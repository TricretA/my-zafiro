package com.tricreta.zafiro.autonomy

import com.tricreta.zafiro.autonomy.core.InboxTooLargeException
import com.tricreta.zafiro.autonomy.inbox.InboxStore
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.File
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class InboxStoreTest {
    private lateinit var root: File
    private lateinit var store: InboxStore
    private var now = 1_800_000_000_000L

    @Before fun setUp() {
        root = File.createTempFile("inbox", "").also { it.delete(); it.mkdirs() }
        store = InboxStore(root, { now }, { ZoneId.of("Africa/Nairobi") }, maxBytes = 1000)
    }

    @After fun tearDown() { root.deleteRecursively() }

    private fun run(vararg kv: Pair<String, String>) = runBlocking { store.run(kv.toMap()) }
    private fun add(name: String?, mime: String, bytes: ByteArray = ByteArray(10) { it.toByte() }) = runBlocking { store.add(1, name, mime, "com.openai.chatgpt", ByteArrayInputStream(bytes)) }

    @Test fun sharedFilesAreCopiedUnderSafeNamesAndIndexed() {
        val e = add("../../etc/evil name.png", "image/png")
        assertTrue(e.path.startsWith("outputs/inbox/") && !e.path.contains(".."))
        assertTrue(File(root, e.path).isFile)
        assertEquals(10, File(root, e.path).length())
        assertEquals("com.openai.chatgpt", e.source)
        assertEquals(1, File(root, "outputs/inbox/index.jsonl").readLines().size)
        assertEquals(setOf("index.jsonl", File(e.path).name), File(root, "outputs/inbox").list()!!.toSet())
    }

    @Test fun listFiltersByKindAndSinceAndHidesFilesThatVanished() {
        val img = add("a.png", "image/png"); now += 1000
        val vid = add("b.mp4", "video/mp4")
        val all = JSONArray(run("op" to "list").text)
        assertEquals(listOf(vid.id, img.id), (0 until all.length()).map { all.getJSONObject(it).getString("id") })
        assertEquals(1, JSONArray(run("op" to "list", "kind" to "video").text).length())
        assertEquals(1, JSONArray(run("op" to "list", "since" to "${img.timeMs}").text).length())
        assertFalse(run("op" to "list", "kind" to "gif").ok)
        File(root, vid.path).delete()
        assertEquals(1, JSONArray(run("op" to "list").text).length())
    }

    @Test fun takeMovesInsideFilesOnlyAndDeleteRemoves() {
        val e = add("c.png", "image/png")
        assertTrue(run("op" to "take", "id" to e.id).ok)
        assertTrue(File(root, e.path).isFile, "no 'to' means no move")
        assertFalse(run("op" to "take", "id" to e.id, "to" to "../outside/").ok)
        assertFalse(run("op" to "take", "id" to e.id, "to" to "/etc/").ok)
        val moved = JSONObject(run("op" to "take", "id" to e.id, "to" to "outputs/studio/").text)
        assertEquals("outputs/studio/${File(e.path).name}", moved.getString("path"))
        assertTrue(File(root, moved.getString("path")).isFile)
        assertFalse(File(root, e.path).exists())
        assertEquals(0, JSONArray(run("op" to "list").text).length())
        val d = add("d.txt", "text/plain")
        assertTrue(run("op" to "delete", "id" to d.id).ok)
        assertFalse(File(root, d.path).exists())
        assertFalse(run("op" to "delete", "id" to "nope").ok)
        assertFalse(run("op" to "bogus").ok)
    }

    @Test fun oversizedStreamsAreRefusedAndLeaveNothingBehind() {
        val endless = object : java.io.InputStream() { override fun read() = 1; override fun read(b: ByteArray, o: Int, l: Int): Int { b.fill(1, o, o + l); return l } }
        assertFailsWith<InboxTooLargeException> { runBlocking { store.add(1, "big.bin", "application/octet-stream", "", endless) } }
        assertEquals(emptyList(), File(root, "outputs/inbox").listFiles()?.map { it.name }.orEmpty())
    }

    @Test fun sharedTextBecomesATextFile() {
        val e = runBlocking { store.addText(2, "a caption", "x") }
        assertEquals("text", e.kind)
        assertEquals("a caption", File(root, e.path).readText())
    }
}
