package com.tricreta.zafiro.autonomy.core.signals

import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import java.time.Instant
import java.time.ZoneId

/**
 * Captured signals in one JSONL file (one JSON object per line), newest last. The whole file is kept in memory (it is
 * capped at [maxEntries] / [maxBytes], so a few hundred KB); appends go straight to disk, while rotation and acks rewrite
 * the file through a temp file so a crash never leaves half of it.
 */
class SignalStore(
    private val file: File,
    private val maxEntries: Int = DEFAULT_MAX_ENTRIES,
    private val maxBytes: Long = DEFAULT_MAX_BYTES,
    private val dedupeWindowMs: Long = DEDUPE_WINDOW_MS,
) {
    private val lock = ReentrantLock()
    private var loaded = false
    private val items = ArrayList<Signal>()
    private var nextId = 1L
    private var bytes = 0L

    /** Adds [s] unless it duplicates a recent signal. Returns the stored signal (with its id), or null for a duplicate. */
    fun add(s: Signal): Signal? = lock.withLock {
        load()
        if (isDuplicate(s)) return null
        val stored = s.copy(id = nextId++)
        items.add(stored)
        val line = encode(stored)
        bytes += line.length + 1
        if (items.size > maxEntries || bytes > maxBytes) {
            rotate()
        } else {
            file.parentFile?.mkdirs()
            file.appendText(line + "\n")
        }
        stored
    }

    fun recent(platform: String? = null, kind: SignalKind? = null, sinceMs: Long = 0L, limit: Int = 20): List<Signal> = lock.withLock {
        load()
        items.asReversed().asSequence().filter { matches(it, platform, kind) && it.receivedMs >= sinceMs }.take(limit.coerceIn(1, MAX_LIMIT)).toList()
    }

    /** Oldest first, so an agent handles them in the order they happened. */
    fun unacked(platform: String? = null, limit: Int = 30): List<Signal> = lock.withLock {
        load()
        items.asSequence().filter { !it.acked && matches(it, platform, null) }.take(limit.coerceIn(1, MAX_LIMIT)).toList()
    }

    /** Marks the given ids handled. Returns how many changed. */
    fun ack(ids: Set<Long>): Int = lock.withLock {
        load()
        var n = 0
        for (i in items.indices) {
            val s = items[i]
            if (s.id in ids && !s.acked) { items[i] = s.copy(acked = true); n++ }
        }
        if (n > 0) rewrite()
        n
    }

    fun get(id: Long): Signal? = lock.withLock { load(); items.firstOrNull { it.id == id } }

    data class Stats(val total: Int, val unacked: Int, val today: Int, val byKindToday: Map<SignalKind, Int>, val byPlatformToday: Map<String, Int>)

    fun stats(nowMs: Long, zone: ZoneId = ZoneId.systemDefault()): Stats = lock.withLock {
        load()
        val day = Instant.ofEpochMilli(nowMs).atZone(zone).toLocalDate()
        val today = items.filter { Instant.ofEpochMilli(it.receivedMs).atZone(zone).toLocalDate() == day }
        Stats(
            total = items.size, unacked = items.count { !it.acked }, today = today.size,
            byKindToday = today.groupingBy { it.kind }.eachCount(), byPlatformToday = today.groupingBy { it.platform }.eachCount(),
        )
    }

    fun size(): Int = lock.withLock { load(); items.size }

    fun clear() = lock.withLock { load(); items.clear(); bytes = 0; rewrite() }

    private fun matches(s: Signal, platform: String?, kind: SignalKind?) =
        (platform.isNullOrBlank() || s.platform.equals(platform, true) || s.app.equals(platform, true)) && (kind == null || s.kind == kind)

    private fun isDuplicate(s: Signal): Boolean {
        val key = s.dedupeKey
        for (i in items.indices.reversed().take(DEDUPE_SCAN)) {
            val o = items[i]
            if (o.dedupeKey == key) return true
            if (s.receivedMs - o.receivedMs <= dedupeWindowMs && o.app == s.app && o.title == s.title && o.text == s.text && o.sender == s.sender) return true
        }
        return false
    }

    private fun load() {
        if (loaded) return
        loaded = true
        if (!file.isFile) return
        var maxId = 0L
        file.useLines { lines ->
            for (line in lines) {
                val s = MiniJson.parseObject(line)?.let { Signal.fromMap(it) } ?: continue
                items.add(s); maxId = maxOf(maxId, s.id)
            }
        }
        nextId = maxId + 1
        bytes = items.sumOf { encode(it).length + 1L }
        if (items.size > maxEntries || bytes > maxBytes) rotate()
    }

    private fun rotate() {
        while (items.size > maxEntries) items.removeAt(0)
        bytes = items.sumOf { encode(it).length + 1L }
        while (bytes > maxBytes && items.isNotEmpty()) {
            bytes -= encode(items.removeAt(0)).length + 1L
        }
        rewrite()
    }

    private fun rewrite() {
        file.parentFile?.mkdirs()
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(items.joinToString("") { encode(it) + "\n" })
        if (!tmp.renameTo(file)) {
            file.writeText(tmp.readText())
            tmp.delete()
        }
        bytes = items.sumOf { encode(it).length + 1L }
    }

    private fun encode(s: Signal) = MiniJson.write(s.toMap())

    companion object {
        const val DEFAULT_MAX_ENTRIES = 2000
        const val DEFAULT_MAX_BYTES = 2L * 1024 * 1024
        const val DEDUPE_WINDOW_MS = 60_000L
        const val MAX_LIMIT = 100
        private const val DEDUPE_SCAN = 300
    }
}
