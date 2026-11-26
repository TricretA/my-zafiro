package com.tricreta.zafiro.autonomy.core.signals

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CommandTest {
    private fun p(s: String) = OwnerCommandParser.parse(s)

    @Test fun fixedCommands() {
        assertEquals(OwnerCommand.Status, p("STATUS"))
        assertEquals(OwnerCommand.Status, p("  status. "))
        assertEquals(OwnerCommand.StopAll, p("STOP ALL"))
        assertEquals(OwnerCommand.StopAll, p("stop"))
        assertEquals(OwnerCommand.StopAll, p("Pause all"))
        assertEquals(OwnerCommand.Pause("substack"), p("PAUSE substack"))
        assertEquals(OwnerCommand.Pause("video studio"), p("pause   video  studio"))
        assertEquals(OwnerCommand.Resume("LinkedIn"), p("Resume LinkedIn!"))
        assertEquals(OwnerCommand.Priority("reply to the Acme lead first"), p("PRIORITY reply to the Acme lead first"))
    }

    @Test fun approvalsCarryACode() {
        assertEquals(OwnerCommand.Approve("4F2"), p("YES 4f2"))
        assertEquals(OwnerCommand.Reject("17"), p("no 17"))
        assertEquals(OwnerCommand.Free("YES"), p("YES"))
        assertEquals(OwnerCommand.Free("yes please do it"), p("yes please do it"))
        assertEquals(OwnerCommand.Free("NO 12 34"), p("NO 12 34"))
    }

    @Test fun malformedCommandsBecomeFreeTextNeverGuesses() {
        assertEquals(OwnerCommand.Free("PAUSE"), p("PAUSE"))
        assertEquals(OwnerCommand.Free("RESUME"), p("RESUME"))
        assertEquals(OwnerCommand.Free("PRIORITY"), p("PRIORITY"))
        assertEquals(OwnerCommand.Free("STATUS of the invoice please"), p("STATUS of the invoice please"))
        assertEquals(OwnerCommand.Free("STOP posting about politics"), p("STOP posting about politics"))
    }

    @Test fun freeTextAndLeadingName() {
        assertEquals(OwnerCommand.Free("write a post about M-Pesa fees"), p("write a post about M-Pesa fees"))
        assertEquals(OwnerCommand.Status, p("Zafiro, status"))
        assertEquals(OwnerCommand.Free(""), p("   "))
    }

    private val depts = listOf(
        DeptMatcher.Dept("substack", "Substack"), DeptMatcher.Dept("video-studio", "Video Studio"),
        DeptMatcher.Dept("linkedin", "LinkedIn"), DeptMatcher.Dept("jobhunt", "Careers"), DeptMatcher.Dept("x", "X (Twitter)"),
        DeptMatcher.Dept("instagram", "Instagram"), DeptMatcher.Dept("threads", "Threads"), DeptMatcher.Dept("tiktok", "TikTok"),
    )

    @Test fun deptResolution() {
        assertEquals("substack", DeptMatcher.resolve("Substack team", depts)?.id)
        assertEquals("video-studio", DeptMatcher.resolve("video studio", depts)?.id)
        assertEquals("jobhunt", DeptMatcher.resolve("careers", depts)?.id)
        assertEquals("linkedin", DeptMatcher.resolve("link", depts)?.id)
        assertEquals("x", DeptMatcher.resolve("x", depts)?.id)
        assertNull(DeptMatcher.resolve("nonsense", depts))
        assertNull(DeptMatcher.resolve("", depts))
    }

    @Test fun ambiguousPrefixIsNotGuessed() {
        assertNull(DeptMatcher.resolve("t", depts))
    }

    @Test fun mentionedDepartments() {
        val m = DeptMatcher.mentioned("post the video on Instagram and LinkedIn today", depts).map { it.id }
        assertEquals(listOf("linkedin", "instagram"), m)
        assertEquals(emptyList<String>(), DeptMatcher.mentioned("do something useful", depts).map { it.id })
    }
}
