package com.niki914.zafiro.mcp

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

data class McpOAuthRecord(
    val serverName: String,
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAtMs: Long? = null,
    val clientId: String = "",
    val clientSecret: String? = null,
    val issuer: String = "",
    val authorizationEndpoint: String = "",
    val tokenEndpoint: String = "",
    val registrationEndpoint: String? = null,
    val resource: String = "",
    val scopes: List<String> = emptyList(),
    val redirectUri: String = "zafiro-mcp://oauth",
    val status: String = "AUTHORIZED",
)

interface McpSecretCipher {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String?
}

class InMemoryMcpSecretCipher : McpSecretCipher {
    private val values = mutableMapOf<String, String>()
    override fun encrypt(plainText: String): String =
        "memory:${plainText.hashCode()}:${plainText.length}".also { values[it] = plainText }
    override fun decrypt(cipherText: String): String? = values[cipherText]
}

data class McpServerJson(
    val name: String,
    val url: String,
    val transport: String = "streamable-http",
    val headers: Map<String, String> = emptyMap(),
    val auth: String = "none",
)

data class McpCatalogEntry(
    val id: String,
    val name: String,
    val category: String,
    val kind: String,
    val url: String = "",
    val transport: String = "streamable-http",
    val auth: String = "none",
    val needsKey: Boolean = false,
    val free: Boolean = true,
    val writes: Boolean = false,
    val spendsMoney: Boolean = false,
    val verifiedOn: String = "",
    val notes: String = "",
)

object McpCore {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseMcpServersJson(text: String): Result<List<McpServerJson>> = runCatching {
        val root = json.parseToJsonElement(text).jsonObject
        val servers = root["mcpServers"]?.jsonObject?.let { it } ?: return@runCatching emptyList()
        servers.mapNotNull { (name, raw) ->
            val obj = raw as? JsonObject ?: return@mapNotNull null
            val url = (obj["url"] as? JsonPrimitive)?.contentOrNull
                ?: (obj["uri"] as? JsonPrimitive)?.contentOrNull
                ?: return@mapNotNull null
            val rawHeaders = obj["headers"] as? JsonObject
            val headers = rawHeaders?.mapNotNull { (key, value) ->
                (value as? JsonPrimitive)?.contentOrNull?.let { key to it }
            }?.toMap().orEmpty()
            val oauth = (obj["oauth"] as? JsonPrimitive)?.contentOrNull
                ?: if (obj["auth"] != null) (obj["auth"] as? JsonPrimitive)?.contentOrNull else null
            val transport = (obj["type"] as? JsonPrimitive)?.contentOrNull
                ?.let { if (it == "sse") "sse" else "streamable-http" }
                ?: "streamable-http"
            McpServerJson(name, url, transport, headers, oauth ?: "none")
        }
    }

    fun parseCatalog(text: String): Result<List<McpCatalogEntry>> = runCatching {
        val root = json.parseToJsonElement(text)
        val arr = if (root is JsonArray) root else (root as JsonObject)["entries"] as? JsonArray
            ?: throw IllegalArgumentException("Catalog must contain an entries array")
        arr.mapNotNull { element ->
            val o = element as? JsonObject ?: return@mapNotNull null
            fun s(key: String) = (o[key] as? JsonPrimitive)?.contentOrNull.orEmpty()
            fun b(key: String) = (o[key] as? JsonPrimitive)?.contentOrNull?.toBooleanStrictOrNull() ?: false
            val id = s("id")
            val name = s("name")
            if (id.isBlank() || name.isBlank()) null else McpCatalogEntry(
                id = id,
                name = name,
                category = s("category").ifBlank { "APIs" },
                kind = s("kind").ifBlank { "mcp" },
                url = s("url"),
                transport = s("transport").ifBlank { "streamable-http" },
                auth = s("auth").ifBlank { "none" },
                needsKey = b("needsKey"),
                free = o["free"]?.let { it as? JsonPrimitive }?.contentOrNull?.toBooleanStrictOrNull() ?: true,
                writes = b("writes"),
                spendsMoney = b("spendsMoney"),
                verifiedOn = s("verifiedOn"),
                notes = s("notes"),
            )
        }
    }

    fun resolvePlaceholders(text: String, secrets: Map<String, String>): String {
        var resolved = text
        Regex("""\$\{([A-Za-z0-9_.-]+)\}""").findAll(text).forEach { match ->
            val key = match.groupValues[1]
            resolved = resolved.replace(match.value, secrets[key] ?: "")
        }
        return resolved
    }

    fun registrationOrder(
        suppliedClientId: String?,
        suppliedSecret: String?,
        metadataUrl: String?,
        cimdSupported: Boolean,
        dynamicRegistrationEndpoint: String?,
    ): String = when {
        !suppliedClientId.isNullOrBlank() -> "fixed"
        cimdSupported && !metadataUrl.isNullOrBlank() -> "cimd"
        !dynamicRegistrationEndpoint.isNullOrBlank() -> "dcr"
        else -> "none"
    }

    fun needsRefresh(record: McpOAuthRecord, nowMs: Long, skewSeconds: Long = 60): Boolean =
        record.expiresAtMs?.let { nowMs >= it - skewSeconds * 1000 } ?: false

    fun parseInvalidGrant(body: String): Boolean =
        body.contains("invalid_grant", ignoreCase = true)

    fun authorizationServerMetadataUrl(overrideUrl: String?, issuer: String): String? =
        overrideUrl?.takeIf { it.isNotBlank() }
            ?: issuer.trimEnd('/').takeIf { it.isNotBlank() }?.let { "$it/.well-known/oauth-authorization-server" }

    fun protectedResourceMetadataUrl(serverUrl: String): String? = runCatching {
        val u = URI(serverUrl)
        "${u.scheme}://${u.authority}/.well-known/oauth-protected-resource"
    }.getOrNull()

    fun githubSkillUrl(raw: String): Result<String> = runCatching {
        val u = URI(raw.trim())
        require(u.scheme == "https" && u.host.equals("github.com", ignoreCase = true)) {
            "Only https://github.com URLs are allowed"
        }
        val parts = u.path.trim('/').split('/').filter { it.isNotBlank() }
        require(parts.size >= 2) { "GitHub URL must include owner and repository" }
        "https://github.com/${parts[0]}/${parts[1].removeSuffix(".git").trimEnd('/')}"
    }

    fun validSkillFrontmatter(skillMd: String, folderName: String): Boolean {
        val fm = Regex("""(?s)^---\s*\n(.*?)\n---""").find(skillMd)?.groupValues?.get(1).orEmpty()
        val name = Regex("""(?m)^name:\s*(.+?)\s*$""").find(fm)?.groupValues?.get(1)?.trim()
        val license = Regex("""(?m)^license:\s*(.+?)\s*$""").find(fm)?.groupValues?.get(1)?.trim()
        val safeLicense = license == null || license.uppercase() in setOf("MIT", "APACHE-2.0", "BSD", "ISC", "CC0")
        return name == folderName && safeLicense
    }

    fun pkceVerifier(): String {
        val random = ByteArray(32)
        SecureRandom().nextBytes(random)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random)
    }

    fun pkceChallenge(verifier: String): String =
        Base64.getUrlEncoder().withoutPadding()
            .encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII)))

    fun encodeForm(values: Map<String, String>): String =
        values.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }
}
