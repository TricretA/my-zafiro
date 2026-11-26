package com.tricreta.zafiro.autonomy.core.signals

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.random.Random

/**
 * Throttle for the reaction to engagement/email signals: at most one board item per platform per [windowMs]. Signals that
 * arrive inside the window are merged into that item (counts and a few samples) instead of creating more.
 */
class BoardBatcher(private val windowMs: Long = DEFAULT_WINDOW_MS, private val maxSamples: Int = 6) {

    class Batch(val platform: String, val startMs: Long) {
        var itemId: Long? = null
        val counts = linkedMapOf<SignalKind, Int>()
        val samples = ArrayList<Signal>()
        val total: Int get() = counts.values.sum()
    }

    sealed interface Offer {
        val batch: Batch
        /** No board item covers this window yet: create one from [batch]. */
        data class Create(override val batch: Batch) : Offer
        /** A board item already covers this window: update it from [batch]. */
        data class Amend(override val batch: Batch) : Offer
    }

    private val lock = ReentrantLock()
    private val batches = HashMap<String, Batch>()

    fun offer(platform: String, nowMs: Long, s: Signal): Offer = lock.withLock {
        val live = batches[platform]?.takeIf { nowMs - it.startMs < windowMs }
        val batch = live ?: Batch(platform, nowMs).also { batches[platform] = it }
        batch.counts[s.kind] = (batch.counts[s.kind] ?: 0) + 1
        if (batch.samples.size < maxSamples) batch.samples.add(s)
        if (batch.itemId != null) Offer.Amend(batch) else Offer.Create(batch)
    }

    /** Remembers the board item created for the platform's current batch so later signals merge into it. */
    fun created(platform: String, itemId: Long) = lock.withLock { batches[platform]?.itemId = itemId }

    /** Forget the platform's batch (its board item was claimed or finished): the next signal starts a fresh item. */
    fun drop(platform: String) = lock.withLock { batches.remove(platform); Unit }

    companion object {
        const val DEFAULT_WINDOW_MS = 10 * 60_000L

        fun title(b: Batch): String {
            val parts = b.counts.entries.joinToString(", ") { "${it.value} ${it.key.name.lowercase().replace('_', ' ')}" }
            return "Realtime: ${b.total} new on ${b.platform} ($parts)".take(200)
        }

        fun detail(b: Batch, random: Random = Random.Default): String {
            val boundary = SignalFormat.newBoundary(random)
            val more = if (b.total > b.samples.size) " (+${b.total - b.samples.size} more, see signals op=unacked)" else ""
            return buildString {
                append("Realtime signals from ${b.platform}, since the first one in this batch. ")
                append("Read them with `signals op=unacked platform=${b.platform}`, handle what matters, then `signals op=ack id=<id>`. ")
                append(SignalFormat.UNTRUSTED_NOTICE.replace("BOUNDARY", boundary)).append('\n')
                b.samples.forEach { append(SignalFormat.line(it, boundary)).append('\n') }
                append(more)
            }.trim().take(3900)
        }
    }
}
