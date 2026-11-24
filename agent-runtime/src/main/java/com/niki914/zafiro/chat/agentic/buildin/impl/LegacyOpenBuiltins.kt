package com.niki914.zafiro.chat.agentic.buildin.impl

import com.niki914.xposed.api.util.ContextProvider
import com.niki914.zafiro.api.screen.PhoneScreenId
import com.niki914.zafiro.api.screen.ShowTarget
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolRequest
import com.niki914.zafiro.chat.agentic.buildin.TextResultBuiltinTool
import com.niki914.zafiro.chat.agentic.buildin.TextToolResult

/**
 * `open_uri` and `launch_app` were removed upstream in favour of the single `open` tool. Existing agent prompts, workspaces and
 * recipes (WhatsApp digest, email) call them by name, so they stay as thin aliases over the phone screen's `show`.
 */
class OpenUriBuiltin : TextResultBuiltinTool() {
    override val name = "open_uri"
    override val defaultEnabled = true
    override val description = "Open a web address or deep link (https://, whatsapp://send?phone=...&text=..., tel:, mailto:) on the phone. Same as the open tool with a uri."
    override val inputSchemaJson = """{"type":"object","properties":{"uri":{"type":"string"}},"required":["uri"]}"""

    override suspend fun invokeText(request: BuiltinToolRequest): TextToolResult {
        val uri = parseScreenObject(request.argumentsJson)?.text("uri") ?: return invalidArguments("uri is required.")
        val phone = screenDirectory()?.get(PhoneScreenId) ?: return missingService()
        return showToText(phone.show(ShowTarget.Uri(uri)))
    }
}

class LaunchAppBuiltin : TextResultBuiltinTool() {
    override val name = "launch_app"
    override val defaultEnabled = true
    override val description = "Launch an installed app by app_name (fuzzy match on its label) or exact package_name. Same as the open tool."
    override val inputSchemaJson = """{"type":"object","properties":{"app_name":{"type":"string"},"package_name":{"type":"string"}}}"""

    override suspend fun invokeText(request: BuiltinToolRequest): TextToolResult {
        val a = parseScreenObject(request.argumentsJson) ?: return invalidArguments("Arguments must be a JSON object.")
        val phone = screenDirectory()?.get(PhoneScreenId) ?: return missingService()
        val pkg = a.text("package_name") ?: run {
            val label = a.text("app_name") ?: return invalidArguments("Provide package_name or app_name.")
            val pm = ContextProvider.await().applicationContext.packageManager
            val hits = pm.getInstalledApplications(0).filter { pm.getApplicationLabel(it).toString().contains(label, ignoreCase = true) && pm.getLaunchIntentForPackage(it.packageName) != null }
            when {
                hits.isEmpty() -> return TextToolResult.failure(code = "APP_NOT_FOUND", message = "No installed app matches '$label'.")
                hits.size > 1 -> return TextToolResult.failure(code = SCREEN_AMBIGUOUS_APP, message = "Several apps match '$label'.", payload = hits.joinToString("\n") { "${it.packageName}\t${pm.getApplicationLabel(it)}" })
                else -> hits.single().packageName
            }
        }
        return showToText(phone.show(ShowTarget.PackageName(pkg)))
    }
}
