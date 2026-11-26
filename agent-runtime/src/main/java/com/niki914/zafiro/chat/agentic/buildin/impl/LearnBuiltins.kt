package com.niki914.zafiro.chat.agentic.buildin.impl

import com.niki914.zafiro.api.InboxService
import com.niki914.zafiro.api.LearnService
import com.niki914.zafiro.api.TeamResult
import com.niki914.zafiro.chat.agentic.buildin.BuiltinTool
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolRequest
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolResult
import com.niki914.zafiro.service.findService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/** Parses the arguments of a forwarding tool and wraps the service's [TeamResult]; the tools differ only in the service they call. */
abstract class ForwardingBuiltin(
    override val name: String,
    override val description: String,
    override val inputSchemaJson: String,
    private val unavailableMessage: String,
) : BuiltinTool() {
    override val defaultEnabled: Boolean = true

    protected abstract suspend fun forward(args: Map<String, String>): TeamResult?

    override suspend fun invoke(request: BuiltinToolRequest): BuiltinToolResult {
        val obj = runCatching { Json.parseToJsonElement(request.argumentsJson) as? JsonObject }.getOrNull()
            ?: return BuiltinToolResult.failure(code = "INVALID_ARGUMENTS", message = "Arguments must be a JSON object.")
        val args = obj.mapValues { (_, v) -> (v as? JsonPrimitive)?.contentOrNull ?: v.toString() }
        val r = forward(args) ?: return BuiltinToolResult.failure(code = "SERVICE_UNAVAILABLE", message = unavailableMessage)
        val data = buildJsonObject { put("text", r.text) }
        return if (r.ok) BuiltinToolResult.success(message = r.text.take(MAX_MESSAGE), data = data)
        else BuiltinToolResult.failure(code = "LEARN_ERROR", message = r.text.take(MAX_MESSAGE), data = data)
    }

    private companion object { const val MAX_MESSAGE = 6_000 }
}

/** Measured results of what the team made, and the next arm to try. */
class OutcomesBuiltin : ForwardingBuiltin(
    name = "outcomes",
    description = "Record and read measured results (success = evidence, never 'the run finished'). " +
        "op=record (workspace, platform, kind=post|application|reply|video, artifact=<id or url>, metric=views|likes|replies|saves|..., value, lag=1h|24h|7d for posts or 3d|7d|14d for applications, " +
        "evidence=<where you read the number>, optional tags hook_type, format, length_bucket, time_bucket, topic) stores one observation; evidence is mandatory. " +
        "op=list (platform, kind, days, limit) shows recent observations. op=summary (platform, days) gives per-metric medians and the top items. " +
        "op=suggest (platform, dimension=hook_type|format|length_bucket|time_bucket|topic, metric, arms=a,b,c optional new arms) returns the arm to use next and why; use exactly the arm it returns.",
    inputSchemaJson = """{"type":"object","properties":{"op":{"type":"string","enum":["record","list","summary","suggest"]},"workspace":{"type":"string"},"platform":{"type":"string"},"kind":{"type":"string"},"artifact":{"type":"string"},"metric":{"type":"string"},"value":{"type":"number"},"lag":{"type":"string"},"evidence":{"type":"string"},"hook_type":{"type":"string"},"format":{"type":"string"},"length_bucket":{"type":"string"},"time_bucket":{"type":"string"},"topic":{"type":"string"},"observed_at":{"type":"string"},"days":{"type":"number"},"limit":{"type":"number"},"dimension":{"type":"string"},"arms":{"type":"string"}},"required":["op"]}""",
    unavailableMessage = "The learning store is not running yet.",
) {
    override suspend fun forward(args: Map<String, String>): TeamResult? = findService<LearnService>()?.run("outcomes", args)
}

/** The lessons store: short, evidenced claims that are promoted by confirmations and retired by harm or age. */
class LessonsBuiltin : ForwardingBuiltin(
    name = "lessons",
    description = "Lessons learned from measured results. op=add (scope=global|<workspace id>, claim=<one specific sentence>, evidence=<outcome ids like o12,o15 or a file path>) proposes a CANDIDATE; " +
        "a near duplicate is merged instead. op=confirm (id, evidence) when a later result agrees; op=harm (id, reason) when it contradicts. Enough confirmations promote it to ACTIVE, harm or age retires it. " +
        "op=top (scope, k) returns the best active one-line lessons for that scope: read them before writing. op=list (scope, status) lists all. " +
        "A lesson can never change caps, approvals, budgets or safety rules; such ideas go into the weekly proposal file for the owner.",
    inputSchemaJson = """{"type":"object","properties":{"op":{"type":"string","enum":["add","confirm","harm","top","list"]},"id":{"type":"number"},"scope":{"type":"string"},"claim":{"type":"string"},"evidence":{"type":"string"},"reason":{"type":"string"},"k":{"type":"number"},"status":{"type":"string"}},"required":["op"]}""",
    unavailableMessage = "The learning store is not running yet.",
) {
    override suspend fun forward(args: Map<String, String>): TeamResult? = findService<LearnService>()?.run("lessons", args)
}

/** Files that arrived through the Android share sheet ("Share to Zafiro"). */
class InboxBuiltin : ForwardingBuiltin(
    name = "inbox",
    description = "Files received through 'Share to Zafiro' (the Android share sheet), stored in outputs/inbox. " +
        "op=list (since=<epoch ms taken BEFORE you pressed share>, kind=image|video|audio|text|document, limit) lists newest first with id, kind, size, source app and path. " +
        "op=take (id, optional to=<folder or file under Zafiro's files folder, e.g. outputs/studio/>) returns the path and, with 'to', moves the file there. " +
        "op=delete (id) removes it. Always check the size and kind of what you take.",
    inputSchemaJson = """{"type":"object","properties":{"op":{"type":"string","enum":["list","take","delete"]},"id":{"type":"string"},"since":{"type":"number"},"kind":{"type":"string"},"limit":{"type":"number"},"to":{"type":"string"}},"required":["op"]}""",
    unavailableMessage = "The share inbox is not running yet.",
) {
    override suspend fun forward(args: Map<String, String>): TeamResult? = findService<InboxService>()?.run(args)
}
