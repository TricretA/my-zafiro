package com.niki914.zafiro.mcp

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

interface McpSecretStore {
    fun save(serverName: String, record: McpOAuthRecord)
    fun load(serverName: String): McpOAuthRecord?
    fun list(): List<McpOAuthRecord>
    fun delete(serverName: String)
    fun saveHeader(name: String, headers: Map<String, String>)
    fun loadHeaders(name: String): Map<String, String>
}

class AndroidMcpSecretStore(context: Context) : McpSecretStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mcp_encrypted_secrets", Context.MODE_PRIVATE)
    private val cipher = AndroidKeystoreMcpCipher()

    override fun save(serverName: String, record: McpOAuthRecord) {
        val json = McpOAuthStoreCodec.encode(record)
        prefs.edit().putString("oauth:$serverName", safeEncrypt(json)).apply()
    }

    override fun load(serverName: String): McpOAuthRecord? =
        prefs.getString("oauth:$serverName", null)?.let { value ->
            safeDecrypt(value)?.let(McpOAuthStoreCodec::decode)
        }

    override fun list(): List<McpOAuthRecord> =
        prefs.all.keys.filter { it.startsWith("oauth:") }.mapNotNull { key ->
            val value = prefs.getString(key, null) ?: return@mapNotNull null
            safeDecrypt(value)?.let(McpOAuthStoreCodec::decode)
        }

    override fun delete(serverName: String) {
        prefs.edit().remove("oauth:$serverName").remove("headers:$serverName").apply()
    }

    override fun saveHeader(name: String, headers: Map<String, String>) {
        val json = McpOAuthStoreCodec.encodeHeaders(headers)
        prefs.edit().putString("headers:$name", safeEncrypt(json)).apply()
    }

    override fun loadHeaders(name: String): Map<String, String> =
        prefs.getString("headers:$name", null)?.let { value ->
            safeDecrypt(value)?.let(McpOAuthStoreCodec::decodeHeaders).orEmpty()
        }.orEmpty()

    private fun safeEncrypt(value: String): String =
        runCatching { "enc:" + cipher.encrypt(value) }.getOrElse { "plain:" + value }

    private fun safeDecrypt(value: String): String? = when {
        value.startsWith("enc:") -> runCatching { cipher.decrypt(value.removePrefix("enc:")) }.getOrNull()
        value.startsWith("plain:") -> value.removePrefix("plain:")
        else -> null
    }

    private class AndroidKeystoreMcpCipher : McpSecretCipher {
        private val keyAlias = "zafiro_mcp_aes_gcm"
        private fun key() =
            (KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.getKey(keyAlias, null)
                ?: run {
                    val generator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
                    generator.init(android.security.keystore.KeyGenParameterSpec.Builder(
                        keyAlias,
                        android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build())
                    generator.generateKey()
                })

        override fun encrypt(plainText: String): String {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val combined = iv + encrypted
            return Base64.encodeToString(combined, Base64.NO_WRAP)
        }

        override fun decrypt(cipherText: String): String? = runCatching {
            val combined = Base64.decode(cipherText, Base64.NO_WRAP)
            val iv = combined.copyOfRange(0, 12)
            val encrypted = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        }.getOrNull()
    }
}

internal object McpOAuthStoreCodec {
    private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
    fun encode(record: McpOAuthRecord): String = kotlinx.serialization.json.JsonObject(buildMap {
        put("serverName", kotlinx.serialization.json.JsonPrimitive(record.serverName))
        put("accessToken", kotlinx.serialization.json.JsonPrimitive(record.accessToken))
        record.refreshToken?.let { put("refreshToken", kotlinx.serialization.json.JsonPrimitive(it)) }
        record.expiresAtMs?.let { put("expiresAtMs", kotlinx.serialization.json.JsonPrimitive(it)) }
        put("clientId", kotlinx.serialization.json.JsonPrimitive(record.clientId))
        record.clientSecret?.let { put("clientSecret", kotlinx.serialization.json.JsonPrimitive(it)) }
        put("issuer", kotlinx.serialization.json.JsonPrimitive(record.issuer))
        put("authorizationEndpoint", kotlinx.serialization.json.JsonPrimitive(record.authorizationEndpoint))
        put("tokenEndpoint", kotlinx.serialization.json.JsonPrimitive(record.tokenEndpoint))
        record.registrationEndpoint?.let { put("registrationEndpoint", kotlinx.serialization.json.JsonPrimitive(it)) }
        put("resource", kotlinx.serialization.json.JsonPrimitive(record.resource))
        put("scopes", kotlinx.serialization.json.JsonArray(record.scopes.map { kotlinx.serialization.json.JsonPrimitive(it) }))
        put("redirectUri", kotlinx.serialization.json.JsonPrimitive(record.redirectUri))
        put("status", kotlinx.serialization.json.JsonPrimitive(record.status))
    }).toString()
    fun decode(text: String): McpOAuthRecord? = runCatching {
        val o = json.parseToJsonElement(text).jsonObject
        fun s(key: String) = (o[key] as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull
        fun l(key: String) = s(key)?.toLongOrNull()
        val serverName = s("serverName").orEmpty()
        val accessToken = s("accessToken").orEmpty()
        if (serverName.isBlank() || accessToken.isBlank()) null else McpOAuthRecord(
            serverName = serverName,
            accessToken = accessToken,
            refreshToken = s("refreshToken"),
            expiresAtMs = l("expiresAtMs"),
            clientId = s("clientId").orEmpty(),
            clientSecret = s("clientSecret"),
            issuer = s("issuer").orEmpty(),
            authorizationEndpoint = s("authorizationEndpoint").orEmpty(),
            tokenEndpoint = s("tokenEndpoint").orEmpty(),
            registrationEndpoint = s("registrationEndpoint"),
            resource = s("resource").orEmpty(),
            scopes = (o["scopes"] as? kotlinx.serialization.json.JsonArray)
                ?.mapNotNull { (it as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull }.orEmpty(),
            redirectUri = s("redirectUri").orEmpty().ifBlank { "zafiro-mcp://oauth" },
            status = s("status").orEmpty().ifBlank { "AUTHORIZED" },
        )
    }.getOrNull()

    fun encodeHeaders(headers: Map<String, String>): String =
        kotlinx.serialization.json.JsonObject(headers.mapValues { kotlinx.serialization.json.JsonPrimitive(it.value) }).toString()

    fun decodeHeaders(text: String): Map<String, String> =
        runCatching {
            val obj = json.parseToJsonElement(text) as? kotlinx.serialization.json.JsonObject ?: return@runCatching emptyMap()
            obj.mapNotNull { (key, value) ->
                (value as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull?.let { key to it }
            }.toMap()
        }.getOrDefault(emptyMap())

}
