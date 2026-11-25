package com.niki914.zafiro.chat.compaction

import com.niki914.okia.message.Message
import com.niki914.okia.message.ToolCallOutcome

/**
 * Shrinks what is SENT to the model each step (the stored conversation is not changed).
 *
 * Without this every step re-sends every earlier screen dump and every loaded skill, so the bill grows with the square of the number of
 * steps. Policy (same idea as Artemis `xml_scrub_depth: 1`, `image_scrub_depth`, and Anthropic tool-result clearing):
 *  - only the newest screen result keeps its content, older ones keep just their one-line header;
 *  - only the newest [KEEP_SKILLS] loaded skills stay in full, older ones keep their opening lines and can be re-loaded;
 *  - any other old, large tool result is cut to its head;
 *  - only the newest tool result keeps its images.
 * Results from the last [RECENT_FULL_MESSAGES] messages are never cut (the model is usually still reasoning about them).
 * Deterministic: the same history gives the same output, and older messages stay byte-identical from step to step, so the provider's
 * prompt cache keeps working for everything before the newest screen.
 */
object HistoryScrubber {
    const val KEEP_SKILLS = 6
    const val SKILL_STUB_CHARS = 600
    const val OLD_RESULT_MAX_CHARS = 1800
    const val OLD_RESULT_HEAD_CHARS = 1200
    const val RECENT_FULL_MESSAGES = 8

    private val SCREEN_TOOLS = setOf("screen_operation")

    fun scrub(history: List<Message>): List<Message> {
        if (history.size < 3) return history
        val lastScreen = history.indexOfLast { it.isScreenResult() }
        val skillIdx = history.indices.filter { history[it].toolName() == "load_skill" && history[it].successText() != null }
        val keepSkills = skillIdx.takeLast(KEEP_SKILLS).toSet()
        val lastImageResult = history.indexOfLast { (it as? Message.ToolResult)?.outcome.let { o -> o is ToolCallOutcome.Success && o.images.isNotEmpty() } }
        val recentFrom = history.size - RECENT_FULL_MESSAGES

        var changed = false
        val out = ArrayList<Message>(history.size)
        for ((i, m) in history.withIndex()) {
            val r = m as? Message.ToolResult
            val o = r?.outcome as? ToolCallOutcome.Success
            if (r == null || o == null) { out += m; continue }
            var content = o.content
            var images = o.images
            if (r.toolName in SCREEN_TOOLS && m.isScreenResult() && i != lastScreen) {
                content = "[older screen omitted] " + content.lineSequence().firstOrNull().orEmpty().take(120)
            } else if (r.toolName == "load_skill" && i !in keepSkills) {
                if (content.length > SKILL_STUB_CHARS) content = content.take(SKILL_STUB_CHARS) + "\n...[skill text trimmed; call load_skill again if you need it]"
            } else if (i < recentFrom && content.length > OLD_RESULT_MAX_CHARS && r.toolName !in SCREEN_TOOLS) {
                content = content.take(OLD_RESULT_HEAD_CHARS) + "\n...[${content.length - OLD_RESULT_HEAD_CHARS} chars trimmed from an old result]"
            }
            if (images.isNotEmpty() && i != lastImageResult) images = emptyList()
            if (content == o.content && images === o.images) { out += m; continue }
            changed = true
            out += r.copy(outcome = o.copy(content = content, images = images))
        }
        return if (changed) out else history
    }

    private fun Message.toolName(): String? = (this as? Message.ToolResult)?.toolName
    private fun Message.successText(): String? = ((this as? Message.ToolResult)?.outcome as? ToolCallOutcome.Success)?.content

    /** A screen_operation result that is a screen dump (compact, full tree, search hits), not an error or a status line. */
    private fun Message.isScreenResult(): Boolean {
        if (toolName() !in SCREEN_TOOLS) return false
        val t = successText() ?: return false
        return t.startsWith("app:") || t.startsWith("screen:") || t.startsWith("version:") || t.startsWith("(screen content")
    }
}
