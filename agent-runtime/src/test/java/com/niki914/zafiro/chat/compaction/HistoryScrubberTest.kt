package com.niki914.zafiro.chat.compaction

import com.niki914.okia.message.AssistantMessage
import com.niki914.okia.message.ContentBlock
import com.niki914.okia.message.Message
import com.niki914.okia.message.ToolCallOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

// 保护：历史每步整体重发。旧屏幕/旧技能正文若不裁，费用随步数平方增长；但最新屏幕和近期结果必须原样保留。
class HistoryScrubberTest {
    private fun user(t: String) = Message.User(listOf(ContentBlock.Text(t)))
    private fun asst() = Message.Assistant(AssistantMessage(content = listOf(ContentBlock.Text("ok"))))
    private fun result(tool: String, text: String, id: String = tool) =
        Message.ToolResult(id, tool, ToolCallOutcome.Success(text))

    private fun text(m: Message) = ((m as Message.ToolResult).outcome as ToolCallOutcome.Success).content

    @Test
    fun onlyTheNewestScreenKeepsItsContent() {
        val big = "app:com.x 1080x2400\n" + "[1]btn \"Post\" tap\n".repeat(80)
        val h = listOf(
            user("go"),
            asst(), result("screen_operation", big, "a"),
            asst(), result("screen_operation", big, "b"),
            asst(), result("screen_operation", big, "c"),
        )
        val out = HistoryScrubber.scrub(h)
        assertTrue(text(out[2]).startsWith("[older screen omitted] app:com.x"))
        assertTrue(text(out[2]).length < 200)
        assertTrue(text(out[4]).startsWith("[older screen omitted]"))
        assertEquals(big, text(out[6]))
    }

    @Test
    fun keepsLastThreeSkillsInFullAndStubsOlderOnes() {
        val skill = "# skill\n" + "line\n".repeat(400)
        val h = mutableListOf<Message>(user("go"))
        repeat(5) { h += asst(); h += result("load_skill", skill, "s$it") }
        val out = HistoryScrubber.scrub(h)
        val skills = out.filterIsInstance<Message.ToolResult>()
        assertTrue(text(skills[0]).contains("trimmed"))
        assertTrue(text(skills[1]).contains("trimmed"))
        assertEquals(skill, text(skills[2]))
        assertEquals(skill, text(skills[4]))
    }

    @Test
    fun cutsOldLargeResultsButNeverRecentOnes() {
        val big = "z".repeat(5000)
        val h = mutableListOf<Message>(user("go"), asst(), result("http_request", big, "old"))
        repeat(9) { h += asst(); h += result("memory", "fine", "m$it") }
        h += asst(); h += result("http_request", big, "recent")
        val out = HistoryScrubber.scrub(h)
        assertTrue(text(out[2]).length < 1500)
        assertEquals(big, text(out.last()))
    }

    @Test
    fun returnsTheSameInstanceWhenNothingChanges() {
        val h = listOf(user("hi"), asst(), result("memory", "x"))
        assertSame(h, HistoryScrubber.scrub(h))
    }

    @Test
    fun isDeterministicSoPromptCachePrefixStaysStable() {
        val big = "app:a 1x1\n" + "[1]btn \"x\" tap\n".repeat(50)
        val h = listOf(user("go"), asst(), result("screen_operation", big, "a"), asst(), result("screen_operation", big, "b"))
        val first = HistoryScrubber.scrub(h)
        val second = HistoryScrubber.scrub(h + listOf(asst(), result("screen_operation", big, "c")))
        assertEquals(first[2], second[2])
    }
}
