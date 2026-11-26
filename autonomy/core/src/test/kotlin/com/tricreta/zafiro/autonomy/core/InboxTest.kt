package com.tricreta.zafiro.autonomy.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InboxTest {
    @Test fun `stems never contain a path, leading dots or odd characters`() {
        assertEquals("passwd", InboxNames.stem("../../etc/passwd"))
        assertEquals("evil", InboxNames.stem("C:\\Users\\x\\evil.exe"))
        assertEquals("shared", InboxNames.stem(".hidden"))
        assertEquals("shared", InboxNames.stem(null))
        assertEquals("shared", InboxNames.stem("////"))
        assertEquals("My-photo-1", InboxNames.stem("My photo (1).jpg"))
        assertEquals("a.b", InboxNames.stem("a..b.png"))
        assertTrue(InboxNames.stem("x".repeat(500)).length <= InboxLimits.MAX_STEM_CHARS)
        assertFalse(InboxNames.stem("a/../b").contains('/'))
    }

    @Test fun `extension comes from the name, then the mime type, and is always short and alphanumeric`() {
        assertEquals("jpg", InboxNames.extension("IMG_1.JPG", "image/png"))
        assertEquals("png", InboxNames.extension("download", "image/png"))
        assertEquals("mp4", InboxNames.extension(null, "video/mp4; codecs=avc1"))
        assertEquals("bin", InboxNames.extension("file.toolongext", ""))
        assertEquals("bin", InboxNames.extension("x.p/../ng", "application/"))
        assertEquals("zip", InboxNames.extension(null, "application/zip"))
    }

    @Test fun `file name has stamp, counter, stem and extension in the owner's zone`() {
        // 2026-11-26 07:05:09 UTC is 10:05:09 in Nairobi
        val ms = java.time.OffsetDateTime.of(2026, 11, 26, 7, 5, 9, 0, java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
        val name = InboxNames.fileName(ms, ZoneId.of("Africa/Nairobi"), 2, "Cat pic.webp", "image/webp")
        assertEquals("20261126-100509-2-Cat-pic.webp", name)
        assertEquals("20261126-100509-2-Cat-pic", InboxNames.idOf(name))
    }

    @Test fun `kind is derived from the mime type`() {
        assertEquals("image", InboxKind.of("image/png"))
        assertEquals("video", InboxKind.of("VIDEO/mp4"))
        assertEquals("audio", InboxKind.of("audio/mpeg"))
        assertEquals("text", InboxKind.of("text/plain; charset=utf-8"))
        assertEquals("document", InboxKind.of("application/pdf"))
        assertEquals("other", InboxKind.of("application/zip"))
    }

    @Test fun `copy stops at the limit`() {
        val out = ByteArrayOutputStream()
        assertEquals(1000, InboxCopy.copyLimited(ByteArrayInputStream(ByteArray(1000)), out, max = 1000))
        assertFailsWith<InboxTooLargeException> { InboxCopy.copyLimited(ByteArrayInputStream(ByteArray(1001)), ByteArrayOutputStream(), max = 1000) }
        assertEquals(300L * 1024 * 1024, InboxLimits.MAX_BYTES)
    }

    @Test fun `index lines round trip including quotes, unicode and control characters`() {
        val e = InboxEntry("20261126-100509-1-a", 1_700_000_000_123, "com.openai.chatgpt", "image/png", 4096, "outputs/inbox/20261126-100509-1-a.png")
        assertEquals(e, InboxIndex.parseLine(InboxIndex.formatLine(e)))
        val tricky = e.copy(source = "pkg \"quoted\" \\ back\nline\ttab \u0001 \u00e9\u4e2d")
        assertEquals(tricky, InboxIndex.parseLine(InboxIndex.formatLine(tricky)))
        assertFalse(InboxIndex.formatLine(tricky).contains('\n'))
    }

    @Test fun `damaged lines are skipped and do not hide the good ones`() {
        val a = InboxEntry("a", 10, "", "text/plain", 1, "outputs/inbox/a.txt")
        val b = InboxEntry("b", 20, "x", "image/jpeg", 2, "outputs/inbox/b.jpg")
        val text = InboxIndex.formatLine(a) + "\n" + "{not json\n\n{\"id\":\"c\"}\n" + InboxIndex.formatLine(b) + "\n"
        assertEquals(listOf(a, b), InboxIndex.parse(text))
        assertNull(InboxIndex.parseLine(""))
        assertNull(InboxIndex.parseLine("[1,2]"))
        assertEquals(text.lines().count { it.isBlank() } >= 0, true)
    }

    @Test fun `filter returns newest first and honours since, kind and limit`() {
        val es = listOf(
            InboxEntry("a", 10, "", "image/png", 1, "p/a.png"),
            InboxEntry("b", 20, "", "video/mp4", 1, "p/b.mp4"),
            InboxEntry("c", 30, "", "image/png", 1, "p/c.png"),
        )
        assertEquals(listOf("c", "b", "a"), InboxIndex.filter(es).map { it.id })
        assertEquals(listOf("c", "b"), InboxIndex.filter(es, sinceMs = 10).map { it.id })
        assertEquals(listOf("c", "a"), InboxIndex.filter(es, kind = "image").map { it.id })
        assertEquals(listOf("c"), InboxIndex.filter(es, limit = 1).map { it.id })
    }

    @Test fun `flat json parser rejects garbage and reads numbers and null`() {
        assertEquals(mapOf("a" to "1", "b" to "", "c" to "true"), FlatJson.parse("{\"a\": 1, \"b\": null, \"c\": true}"))
        assertEquals(emptyMap(), FlatJson.parse("{}"))
        assertNull(FlatJson.parse("{\"a\":"))
        assertNull(FlatJson.parse("{\"a\" 1}"))
        assertNull(FlatJson.parse("{\"a\":\"unterminated}"))
    }
}
