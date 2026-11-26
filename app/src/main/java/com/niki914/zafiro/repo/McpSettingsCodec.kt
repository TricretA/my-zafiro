package com.niki914.zafiro.repo

import com.niki914.zafiro.repo.SettingsJsonCodecUtils.array
import com.niki914.zafiro.repo.SettingsJsonCodecUtils.enabledForAgent
import com.niki914.zafiro.repo.SettingsJsonCodecUtils.obj
import com.niki914.zafiro.repo.SettingsJsonCodecUtils.orEmptyObjects
import com.niki914.zafiro.repo.SettingsJsonCodecUtils.parseObject
import com.niki914.zafiro.repo.SettingsJsonCodecUtils.stringNonBlankOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.longOrNull
import com.niki914.zafiro.settings.model.RuntimeMcpServer as McpServer

internal object McpSettingsCodec {
    fun normalizeServerId(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return null
        val normalized = trimmed
            .lowercase()
            .replace(Regex("[^a-z0-9_-]+"), "_")
            .trim('_')
            .take(SERVER_ID_MAX_LENGTH)
        return normalized
            .ifBlank { "server_${Integer.toHexString(trimmed.hashCode())}" }
            .takeIf { SAFE_SERVER_ID.matches(it) }
    }

    fun parseServers(json: String, agentId: String = MAIN_AGENT_ID): List<McpServer> {
        return parseObject(json)
            .array(SERVERS_KEY)
            .orEmptyObjects()
            .mapNotNull { obj ->
                val id = obj.stringNonBlankOrNull(ID_KEY) ?: return@mapNotNull null
                val name = obj.stringNonBlankOrNull(NAME_KEY) ?: return@mapNotNull null
                val url = obj.stringNonBlankOrNull(URL_KEY) ?: return@mapNotNull null
                McpServer(
                    name = name,
                    url = url,
                    enabled = enabledForAgent(obj[ENABLED_FOR_AGENTS_KEY], agentId) == true,
                    transport = obj.stringNonBlankOrNull(TRANSPORT_KEY) ?: "streamable-http",
                    authMode = obj.stringNonBlankOrNull(AUTH_MODE_KEY) ?: "none",
                    disabledTools = (obj[DISABLED_TOOLS_KEY] as? JsonObject)?.keys ?: emptySet(),
                    status = obj.stringNonBlankOrNull(STATUS_KEY) ?: "UNKNOWN",
                    lastError = obj.stringNonBlankOrNull(LAST_ERROR_KEY).orEmpty(),
                    toolCount = (obj[TOOL_COUNT_KEY] as? JsonPrimitive)?.longOrNull?.toInt() ?: 0,
                    tokenExpiresAtMs = obj.stringNonBlankOrNull(TOKEN_EXPIRY_KEY)?.toLongOrNull(),
                    headers = obj.obj(HEADERS_KEY)
                        ?.mapNotNull { (key, value) ->
                            val primitive = value as? JsonPrimitive ?: return@mapNotNull null
                            if (!primitive.isString) return@mapNotNull null
                            primitive.contentOrNull?.let { headerValue -> key to headerValue }
                        }
                        ?.toMap()
                        ?: emptyMap(),
                )
            }
    }

    fun encodeServers(servers: List<McpServer>, agentId: String = MAIN_AGENT_ID): String {
        return JsonObject(
            mapOf(
                SERVERS_KEY to JsonArray(
                    servers.mapNotNull { server ->
                        val id = normalizeServerId(server.name) ?: return@mapNotNull null
                        JsonObject(
                            mapOf(
                                ID_KEY to JsonPrimitive(id),
                                NAME_KEY to JsonPrimitive(server.name),
                                URL_KEY to JsonPrimitive(server.url),
                                HEADERS_KEY to JsonObject(
                                    server.headers
                                        .filterKeys(String::isNotBlank)
                                        .mapValues { (_, value) -> JsonPrimitive(value) }
                                ),
                                ENABLED_FOR_AGENTS_KEY to JsonArray(
                                    if (server.enabled) listOf(JsonPrimitive(agentId)) else emptyList()
                                ),
                                TRANSPORT_KEY to JsonPrimitive(server.transport),
                                AUTH_MODE_KEY to JsonPrimitive(server.authMode),
                                DISABLED_TOOLS_KEY to JsonObject(server.disabledTools.associateWith { JsonPrimitive(true) }),
                                STATUS_KEY to JsonPrimitive(server.status),
                                LAST_ERROR_KEY to JsonPrimitive(server.lastError),
                                TOOL_COUNT_KEY to JsonPrimitive(server.toolCount),
                                TOKEN_EXPIRY_KEY to (server.tokenExpiresAtMs?.let { JsonPrimitive(it.toString()) } ?: JsonPrimitive("")),
                            )
                        )
                    }
                )
            )
        ).toString()
    }

    private const val SERVER_ID_MAX_LENGTH = 64
    private val SAFE_SERVER_ID = Regex("[a-z0-9_-]{1,$SERVER_ID_MAX_LENGTH}")

    private const val MAIN_AGENT_ID = "main"
    private const val SERVERS_KEY = "servers"
    private const val ID_KEY = "id"
    private const val NAME_KEY = "name"
    private const val URL_KEY = "url"
    private const val HEADERS_KEY = "headers"
    private const val ENABLED_FOR_AGENTS_KEY = "enabled_for_agents"
    private const val DESCRIPTION_KEY = "description"
    private const val INPUT_SCHEMA_KEY = "input_schema"
    private const val TRANSPORT_KEY = "transport"
    private const val AUTH_MODE_KEY = "auth_mode"
    private const val DISABLED_TOOLS_KEY = "disabled_tools"
    private const val STATUS_KEY = "status"
    private const val LAST_ERROR_KEY = "last_error"
    private const val TOOL_COUNT_KEY = "tool_count"
    private const val TOKEN_EXPIRY_KEY = "token_expires_at"
}
