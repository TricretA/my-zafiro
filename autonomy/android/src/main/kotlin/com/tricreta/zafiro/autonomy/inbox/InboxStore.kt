package com.tricreta.zafiro.autonomy.inbox

import com.niki914.zafiro.api.InboxService
import com.niki914.zafiro.api.TeamResult
import com.tricreta.zafiro.autonomy.core.InboxCopy
import com.tricreta.zafiro.autonomy.core.InboxEntry
import com.tricreta.zafiro.autonomy.core.InboxIndex
import com.tricreta.zafiro.autonomy.core.InboxKind
import com.tricreta.zafiro.autonomy.core.InboxLimits
import com.tricreta.zafiro.autonomy.core.InboxNames
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.time.ZoneId

/**
 * Files received through the share sheet live in `files/outputs/inbox` with a small `index.jsonl`. No storage permission is
 * involved: the sender grants a temporary read on its content:// uri and we copy the bytes. Names are sanitised, so nothing a
 * sender controls can leave the inbox folder.
 */
class InboxStore(
    private val filesDir: File,
    private val nowMs: () -> Long = System::currentTimeMillis,
    private val zone: () -> ZoneId = { ZoneId.systemDefault() },
    private val maxBytes: Long = InboxLimits.MAX_BYTES,
) : InboxService {
    private val dir = File(filesDir, "outputs/inbox")
    private val indexFile = File(dir, "index.jsonl")
    private val lock = Mutex()

    private fun relative(f: File) = f.relativeTo(filesDir).path.replace(File.separatorChar, '/')

    private fun readIndex(): List<InboxEntry> = if (indexFile.isFile) InboxIndex.parse(indexFile.readText()) else emptyList()

    private fun writeIndex(entries: List<InboxEntry>) {
        dir.mkdirs()
        val tmp = File(dir, "index.jsonl.tmp")
        tmp.writeText(InboxIndex.format(entries))
        if (!tmp.renameTo(indexFile)) { indexFile.writeText(InboxIndex.format(entries)); tmp.delete() }
    }

    /** Copies [input] into the inbox. Throws [com.tricreta.zafiro.autonomy.core.InboxTooLargeException] or IOException; a partial file is removed. */
    suspend fun add(n: Int, displayName: String?, mime: String, source: String, input: InputStream): InboxEntry {
        val name = InboxNames.fileName(nowMs(), zone(), n, displayName, mime)
        return lock.withLock {
            dir.mkdirs()
            val target = File(dir, name)
            require(target.canonicalFile.parentFile == dir.canonicalFile) { "Unsafe file name." }
            val part = File(dir, "$name.part")
            try {
                val size = part.outputStream().use { InboxCopy.copyLimited(input, it, maxBytes) }
                check(part.renameTo(target)) { "Could not finish writing the file." }
                val entry = InboxEntry(InboxNames.idOf(name), nowMs(), source, mime, size, relative(target))
                writeIndex(readIndex() + entry)
                entry
            } catch (e: Exception) {
                part.delete()
                throw e
            }
        }
    }

    /** Shared text becomes a .txt file so agents read every kind of share the same way. */
    suspend fun addText(n: Int, text: String, source: String): InboxEntry =
        add(n, "shared-text", "text/plain", source, text.take(InboxLimits.MAX_TEXT_CHARS).byteInputStream())

    override suspend fun run(args: Map<String, String>): TeamResult = try {
        when (val op = args["op"]?.trim().orEmpty()) {
            "list" -> lock.withLock {
                val since = args["since"]?.trim()?.toDoubleOrNull()?.toLong() ?: 0L
                val kind = args["kind"]?.trim()?.lowercase().orEmpty()
                if (kind.isNotEmpty() && kind !in InboxKind.FILTERS) return@withLock TeamResult(false, "kind must be one of ${InboxKind.FILTERS.joinToString(", ")}.")
                val limit = (args["limit"]?.trim()?.toDoubleOrNull()?.toInt() ?: 20).coerceIn(1, 100)
                val rows = InboxIndex.filter(readIndex().filter { File(filesDir, it.path).isFile }, since, kind, limit)
                TeamResult(true, JSONArray(rows.map { json(it) }).toString())
            }
            "take" -> lock.withLock {
                val e = find(args) ?: return@withLock TeamResult(false, "No inbox item with that id.")
                val src = File(filesDir, e.path)
                if (!src.isFile) return@withLock TeamResult(false, "The file is gone.")
                val to = args["to"]?.trim().orEmpty()
                if (to.isEmpty()) return@withLock TeamResult(true, json(e).put("note", "Not moved. Copy it where you need it, then call inbox op=delete.").toString())
                val dest = resolveTarget(to, src.name) ?: return@withLock TeamResult(false, "'to' must stay inside Zafiro's files folder.")
                dest.parentFile?.mkdirs()
                if (dest.exists()) return@withLock TeamResult(false, "${relative(dest)} already exists.")
                if (!src.renameTo(dest)) { src.copyTo(dest); src.delete() }
                writeIndex(readIndex().filter { it.id != e.id })
                TeamResult(true, json(e.copy(path = relative(dest))).put("moved", true).toString())
            }
            "delete" -> lock.withLock {
                val e = find(args) ?: return@withLock TeamResult(false, "No inbox item with that id.")
                File(filesDir, e.path).delete()
                writeIndex(readIndex().filter { it.id != e.id })
                TeamResult(true, JSONObject().put("deleted", e.id).toString())
            }
            else -> TeamResult(false, "op must be list, take or delete (got '$op').")
        }
    } catch (e: java.io.IOException) {
        TeamResult(false, "File error: ${e.message}")
    }

    private fun find(args: Map<String, String>): InboxEntry? {
        val id = args["id"]?.trim().orEmpty()
        return if (id.isEmpty()) null else readIndex().firstOrNull { it.id == id }
    }

    /** A folder (ends with '/' or exists) keeps the file name; otherwise the last segment is the new name. Never leaves [filesDir]. */
    private fun resolveTarget(to: String, name: String): File? {
        val base = filesDir.canonicalFile
        val raw = File(base, to.trimStart('/'))
        val f = if (to.endsWith("/") || raw.isDirectory) File(raw, name) else raw
        val c = f.canonicalFile
        return if (c.path.startsWith(base.path + File.separator)) c else null
    }

    private fun json(e: InboxEntry) = JSONObject().put("id", e.id).put("kind", e.kind).put("mime", e.mime).put("size", e.size)
        .put("source", e.source).put("time", e.timeMs).put("path", e.path)
}
