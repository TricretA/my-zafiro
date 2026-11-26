package com.niki914.zafiro.repo.oauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.niki914.logging.Logger
import com.niki914.zafiro.api.util.runCatchingCancellable
import com.niki914.zafiro.repo.XRepo
import com.niki914.zafiro.mcp.AndroidMcpSecretStore
import com.niki914.zafiro.mcp.McpCore
import com.niki914.zafiro.mcp.McpOAuthRecord
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * "Sign in" for MCP servers that need OAuth, like connecting a connector in Claude: discover the server's sign-in service, register
 * Zafiro, open the browser, catch the redirect (zafiro-mcp://oauth), exchange the code (PKCE) and save the token as the server's
 * Authorization header. Tokens are refreshed in the background before they expire.
 */
object McpOAuth {
    private const val TAG = "niki914_zafiro_McpOAuth"
    private val waiting = ConcurrentHashMap<String, CompletableDeferred<String>>()
    private val refreshLocks = ConcurrentHashMap<String, Mutex>()

    /** Called by MainActivity for zafiro-mcp:// links. @return true when the link was ours. */
    fun onCallback(uri: Uri?): Boolean {
        if (uri == null || uri.scheme != "zafiro-mcp") return false
        val state = uri.getQueryParameter("state")
        val waiter = state?.let { waiting[it] } ?: waiting.values.firstOrNull()
        waiter?.complete(uri.toString())
        return true
    }

    private data class Http(val code: Int, val body: String)

    private fun http(method: String, url: String, body: String? = null, contentType: String = "application/json"): Http {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = method; c.connectTimeout = 15_000; c.readTimeout = 20_000; c.setRequestProperty("Accept", "application/json")
        if (body != null) { c.doOutput = true; c.setRequestProperty("Content-Type", contentType); c.outputStream.use { it.write(body.toByteArray()) } }
        return try {
            val code = c.responseCode
            Http(code, (if (code in 200..299) c.inputStream else c.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty())
        } finally { c.disconnect() }
    }

    private fun getOrNull(url: String): String? = runCatchingCancellable { http("GET", url).takeIf { it.code in 200..299 }?.body }.getOrNull()

    /** Full sign-in. Returns a message for the owner; throws with a plain reason when it cannot finish. */
    suspend fun signIn(context: Context, serverName: String, serverUrl: String): String = withContext(Dispatchers.IO) {
        val origin = McpOAuthLogic.origin(serverUrl)
        val resourceMeta = getOrNull("$origin/.well-known/oauth-protected-resource")
        val authServer = McpOAuthLogic.authorizationServer(resourceMeta, origin)
        val meta = getOrNull("$authServer/.well-known/oauth-authorization-server") ?: getOrNull("$authServer/.well-known/openid-configuration")
        val endpoints = McpOAuthLogic.endpoints(meta, authServer)
        val reg = endpoints.register ?: error("This server does not allow apps to register themselves, so Zafiro cannot sign in automatically. Paste an access token in the Headers field instead.")
        val r = http("POST", reg, McpOAuthLogic.registrationBody())
        val clientId = McpOAuthLogic.clientIdFrom(r.body) ?: error("Could not register Zafiro with the server (HTTP ${r.code}). ${r.body.take(160)}")

        val verifier = McpOAuthLogic.randomUrlSafe()
        val state = McpOAuthLogic.randomUrlSafe(16)
        val url = McpOAuthLogic.authorizeUrl(endpoints, clientId, state, McpOAuthLogic.challenge(verifier), serverUrl)
        val waiter = CompletableDeferred<String>().also { waiting[state] = it }
        try {
            withContext(Dispatchers.Main) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            val callback = try { withTimeout(5 * 60_000L) { waiter.await() } } catch (e: TimeoutCancellationException) { error("Sign-in was not completed within 5 minutes.") }
            val code = McpOAuthLogic.parseCallback(callback, state).getOrThrow()
            val t = http("POST", endpoints.token, McpOAuthLogic.tokenForm(code, verifier, clientId, serverUrl), "application/x-www-form-urlencoded")
            val tokens = McpOAuthLogic.parseTokens(t.body) ?: error("The server did not return a token (HTTP ${t.code}). ${t.body.take(160)}")
            val expiresAt = tokens.expiresInSec?.let { System.currentTimeMillis() + it * 1000 }
            AndroidMcpSecretStore(context).save(serverName, McpOAuthRecord(
                serverName = serverName,
                accessToken = tokens.access,
                refreshToken = tokens.refresh,
                expiresAtMs = expiresAt,
                clientId = clientId,
                issuer = McpOAuthLogic.authorizationServer(null, McpOAuthLogic.origin(serverUrl)),
                authorizationEndpoint = endpoints.authorize,
                tokenEndpoint = endpoints.token,
                registrationEndpoint = endpoints.register,
                resource = serverUrl,
                redirectUri = McpOAuthLogic.REDIRECT_URI,
                status = "AUTHORIZED",
            ))
            XRepo.setMcpAuthorization(serverName, tokens.access)
            "Signed in to $serverName." + if (tokens.refresh == null) " (This server gave no refresh token, so you will need to sign in again when it expires.)" else ""
        } finally { waiting.remove(state) }
    }

    /** Refreshes tokens lazily with one single-flight mutex per MCP server. */
    suspend fun refreshDue(context: Context) = withContext(Dispatchers.IO) {
        val store = AndroidMcpSecretStore(context)
        store.list().filter { it.refreshToken != null }.forEach { record ->
            val lock = refreshLocks.getOrPut(record.serverName) { Mutex() }
            lock.withLock {
                val latest = store.load(record.serverName) ?: return@withLock
                if (!McpCore.needsRefresh(latest, System.currentTimeMillis(), 60)) return@withLock
                runCatchingCancellable {
                    val t = http("POST", latest.tokenEndpoint,
                        McpOAuthLogic.refreshForm(latest.refreshToken!!, latest.clientId, latest.resource),
                        "application/x-www-form-urlencoded")
                    if (McpCore.parseInvalidGrant(t.body)) {
                        store.save(latest.serverName, latest.copy(status = "NEEDS_REAUTH"))
                        XRepo.setMcpAuthorization(latest.serverName, null)
                        Logger.w(TAG, "MCP server ${latest.serverName} needs re-authentication")
                        return@runCatchingCancellable
                    }
                    val tokens = McpOAuthLogic.parseTokens(t.body)
                        ?: error("refresh refused (HTTP ${t.code})")
                    val updated = latest.copy(
                        accessToken = tokens.access,
                        refreshToken = tokens.refresh ?: latest.refreshToken,
                        expiresAtMs = tokens.expiresInSec?.let { System.currentTimeMillis() + it * 1000 },
                        status = "AUTHORIZED",
                    )
                    store.save(updated.serverName, updated)
                    XRepo.setMcpAuthorization(updated.serverName, tokens.access)
                }.onFailure { Logger.w(TAG, "refresh failed for ${latest.serverName}: ${it.message}") }
            }
        }
    }

    fun isSignedIn(context: Context, serverName: String) =
        AndroidMcpSecretStore(context).load(serverName)?.status == "AUTHORIZED"
}
