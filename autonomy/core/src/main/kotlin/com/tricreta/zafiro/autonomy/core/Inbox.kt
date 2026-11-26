package com.tricreta.zafiro.autonomy.core

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Limits for the Share-to-Zafiro inbox. */
object InboxLimits {
    /** One shared file larger than this is refused (the phone has little storage and the copy must not fill it). */
    const val MAX_BYTES: Long = 300L * 1024 * 1024
    const val MAX_TEXT_CHARS: Int = 200_000
    const val MAX_STEM_CHARS: Int = 40
}

/** What a shared file is, as agents filter it (`inbox op=list kind=...`). */
object InboxKind {
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val AUDIO = "audio"
    const val TEXT = "text"
    const val DOCUMENT = "document"
    const val OTHER = "other"
    val FILTERS: Set<String> = setOf(IMAGE, VIDEO, AUDIO, TEXT, DOCUMENT)

    fun of(mime: String): String {
        val m = mime.lowercase(Locale.ROOT).substringBefore(';').trim()
        return when {
            m.startsWith("image/") -> IMAGE
            m.startsWith("video/") -> VIDEO
            m.startsWith("audio/") -> AUDIO
            m.startsWith("text/") -> TEXT
            m == "application/pdf" -> DOCUMENT
            else -> OTHER
        }
    }
}

/** Safe file names for shared streams. Everything a sender controls (display name, mime) is untrusted. */
object InboxNames {
    private val MIME_EXT = mapOf(
        "image/jpeg" to "jpg", "image/png" to "png", "image/webp" to "webp", "image/gif" to "gif", "image/heic" to "heic",
        "video/mp4" to "mp4", "video/quicktime" to "mov", "video/webm" to "webm", "video/3gpp" to "3gp",
        "audio/mpeg" to "mp3", "audio/mp4" to "m4a", "audio/aac" to "aac", "audio/wav" to "wav", "audio/x-wav" to "wav",
        "audio/ogg" to "ogg", "audio/webm" to "weba", "audio/flac" to "flac",
        "text/plain" to "txt", "text/html" to "html", "text/markdown" to "md", "text/csv" to "csv",
        "application/pdf" to "pdf",
    )
    private val STAMP: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT)

    /** A name made only of letters, digits, '.', '_' and '-', never empty, never a path, at most [InboxLimits.MAX_STEM_CHARS]. */
    fun stem(raw: String?): String {
        val name = (raw ?: "").substringAfterLast('/').substringAfterLast('\\')
        val base = name.substringBeforeLast('.', name)
        val cleaned = base.replace(Regex("[^A-Za-z0-9._-]+"), "-").replace(Regex("\\.{2,}"), ".").replace(Regex("-{2,}"), "-").trim('-', '.', '_')
        return cleaned.take(InboxLimits.MAX_STEM_CHARS).trim('-', '.', '_').ifEmpty { "shared" }
    }

    /** 1..5 alphanumeric characters from the display name, else from the mime type, else "bin". */
    fun extension(rawName: String?, mime: String): String {
        val fromName = rawName?.substringAfterLast('/')?.substringAfterLast('\\')?.takeIf { '.' in it }?.substringAfterLast('.')?.lowercase(Locale.ROOT)
        if (fromName != null && Regex("[a-z0-9]{1,5}").matches(fromName)) return fromName
        val m = mime.lowercase(Locale.ROOT).substringBefore(';').trim()
        MIME_EXT[m]?.let { return it }
        val sub = m.substringAfter('/', "").filter { it.isLetterOrDigit() }.take(5)
        return sub.ifEmpty { "bin" }
    }

    /** `yyyyMMdd-HHmmss-<n>-<stem>.<ext>`; [n] keeps files of one share apart. */
    fun fileName(nowMs: Long, zone: ZoneId, n: Int, rawName: String?, mime: String): String =
        "${STAMP.format(Instant.ofEpochMilli(nowMs).atZone(zone))}-$n-${stem(rawName)}.${extension(rawName, mime)}"

    /** The id agents use: the file name without its extension. */
    fun idOf(fileName: String): String = fileName.substringBeforeLast('.')
}

/** Copies a stream but refuses to write more than [max] bytes, so one huge share cannot fill the phone. */
class InboxTooLargeException(val max: Long) : IOException("File is larger than the ${max / (1024 * 1024)} MB limit.")

object InboxCopy {
    fun copyLimited(input: InputStream, output: OutputStream, max: Long = InboxLimits.MAX_BYTES): Long {
        val buf = ByteArray(64 * 1024)
        var total = 0L
        while (true) {
            val n = input.read(buf)
            if (n < 0) break
            total += n
            if (total > max) throw InboxTooLargeException(max)
            output.write(buf, 0, n)
        }
        return total
    }
}

/** One line of `outputs/inbox/index.jsonl`. [path] is relative to Zafiro's files folder. */
data class InboxEntry(val id: String, val timeMs: Long, val source: String, val mime: String, val size: Long, val path: String) {
    val kind: String get() = InboxKind.of(mime)
}

object InboxIndex {
    fun formatLine(e: InboxEntry): String = FlatJson.encode(
        listOf("id" to e.id, "t" to e.timeMs, "src" to e.source, "mime" to e.mime, "size" to e.size, "path" to e.path, "kind" to e.kind),
    )

    /** Null for blank or damaged lines: one bad line must not hide the others. */
    fun parseLine(line: String): InboxEntry? {
        val m = FlatJson.parse(line) ?: return null
        val id = m["id"]?.takeIf { it.isNotEmpty() } ?: return null
        val path = m["path"]?.takeIf { it.isNotEmpty() } ?: return null
        return InboxEntry(id, m["t"]?.toLongOrNull() ?: return null, m["src"].orEmpty(), m["mime"].orEmpty(), m["size"]?.toLongOrNull() ?: 0L, path)
    }

    fun parse(text: String): List<InboxEntry> = text.lineSequence().mapNotNull { parseLine(it) }.toList()

    fun format(entries: List<InboxEntry>): String = entries.joinToString("") { formatLine(it) + "\n" }

    /** Newest first, optionally only entries newer than [sinceMs] and of one [kind]. */
    fun filter(entries: List<InboxEntry>, sinceMs: Long = 0, kind: String? = null, limit: Int = 50): List<InboxEntry> =
        entries.filter { it.timeMs > sinceMs && (kind.isNullOrBlank() || it.kind == kind) }.sortedByDescending { it.timeMs }.take(limit)
}

/** Just enough JSON for one-line records of strings and numbers; no dependency, deterministic. */
object FlatJson {
    fun encode(fields: List<Pair<String, Any>>): String = fields.joinToString(",", "{", "}") { (k, v) ->
        "\"" + escape(k) + "\":" + when (v) {
            is Number -> v.toString()
            else -> "\"" + escape(v.toString()) + "\""
        }
    }

    fun escape(s: String): String = buildString {
        s.forEach { c ->
            when {
                c == '"' -> append("\\\"")
                c == '\\' -> append("\\\\")
                c == '\n' -> append("\\n")
                c == '\r' -> append("\\r")
                c == '\t' -> append("\\t")
                c < ' ' -> append("\\u%04x".format(c.code))
                else -> append(c)
            }
        }
    }

    /** Parses one flat object; values come back as text (numbers and booleans as written, null as empty). */
    fun parse(line: String): Map<String, String>? {
        val s = line.trim()
        if (s.length < 2 || s.first() != '{' || s.last() != '}') return null
        val out = LinkedHashMap<String, String>()
        var i = 1
        fun skip() { while (i < s.length && s[i].isWhitespace()) i++ }
        fun readString(): String? {
            if (i >= s.length || s[i] != '"') return null
            i++
            val sb = StringBuilder()
            while (i < s.length) {
                val c = s[i++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (i >= s.length) return null
                        when (val e = s[i++]) {
                            'n' -> sb.append('\n'); 'r' -> sb.append('\r'); 't' -> sb.append('\t')
                            'b' -> sb.append('\b'); 'f' -> sb.append('\u000c')
                            'u' -> {
                                if (i + 4 > s.length) return null
                                sb.append(s.substring(i, i + 4).toIntOrNull(16)?.toChar() ?: return null); i += 4
                            }
                            else -> sb.append(e)
                        }
                    }
                    else -> sb.append(c)
                }
            }
            return null
        }
        skip()
        if (s[i] == '}') return out
        while (true) {
            skip()
            val key = readString() ?: return null
            skip()
            if (i >= s.length || s[i] != ':') return null
            i++; skip()
            val value = if (i < s.length && s[i] == '"') readString() ?: return null else {
                val start = i
                while (i < s.length && s[i] != ',' && s[i] != '}') i++
                s.substring(start, i).trim().let { if (it == "null") "" else it }
            }
            out[key] = value
            skip()
            if (i >= s.length) return null
            if (s[i] == ',') { i++; continue }
            if (s[i] == '}') return out
            return null
        }
    }
}
