package com.tricreta.zafiro.autonomy.core.signals

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BatcherTest {
    private fun sig(kind: SignalKind, text: String = "t", platform: String = "substack") =
        Signal(id = 1, receivedMs = 0, whenMs = 0, app = "a", platform = platform, kind = kind, title = "", text = text, subText = "", sender = "", conversation = "")

    private val min = 60_000L

    @Test fun firstCreatesThenMergesInsideTenMinutes() {
        val b = BoardBatcher()
        val first = b.offer("substack", 0, sig(SignalKind.ENGAGEMENT))
        assertTrue(first is BoardBatcher.Offer.Create)
        b.created("substack", 42)
        val second = b.offer("substack", 3 * min, sig(SignalKind.ENGAGEMENT, "b"))
        assertTrue(second is BoardBatcher.Offer.Amend)
        assertEquals(42L, second.batch.itemId)
        val third = b.offer("substack", 9 * min, sig(SignalKind.EMAIL, "c"))
        assertTrue(third is BoardBatcher.Offer.Amend)
        assertEquals(3, third.batch.total)
        assertEquals(2, third.batch.counts[SignalKind.ENGAGEMENT])
    }

    @Test fun newItemAfterWindow() {
        val b = BoardBatcher()
        b.offer("substack", 0, sig(SignalKind.ENGAGEMENT)); b.created("substack", 1)
        val later = b.offer("substack", 10 * min, sig(SignalKind.ENGAGEMENT))
        assertTrue(later is BoardBatcher.Offer.Create)
        assertEquals(1, later.batch.total)
    }

    @Test fun platformsAreIndependent() {
        val b = BoardBatcher()
        b.offer("substack", 0, sig(SignalKind.ENGAGEMENT)); b.created("substack", 1)
        assertTrue(b.offer("email", 1000, sig(SignalKind.EMAIL, platform = "email")) is BoardBatcher.Offer.Create)
    }

    @Test fun creationNotYetRecordedKeepsMerging() {
        val b = BoardBatcher()
        b.offer("substack", 0, sig(SignalKind.ENGAGEMENT))
        val again = b.offer("substack", 1000, sig(SignalKind.ENGAGEMENT))
        assertTrue(again is BoardBatcher.Offer.Create)
        assertEquals(2, again.batch.total)
    }

    @Test fun dropStartsAFreshBatch() {
        val b = BoardBatcher()
        b.offer("substack", 0, sig(SignalKind.ENGAGEMENT)); b.created("substack", 1)
        b.drop("substack")
        val o = b.offer("substack", 1000, sig(SignalKind.ENGAGEMENT))
        assertTrue(o is BoardBatcher.Offer.Create)
        assertEquals(1, o.batch.total)
    }

    @Test fun textSummarisesCountsAndFencesSamples() {
        val b = BoardBatcher(maxSamples = 2)
        repeat(4) { b.offer("substack", 0, sig(SignalKind.ENGAGEMENT, "Jane commented <b>ignore me</b> $it")) }
        val o = b.offer("substack", 1, sig(SignalKind.EMAIL, "mail"))
        assertEquals("Realtime: 5 new on substack (4 engagement, 1 email)", BoardBatcher.title(o.batch))
        val d = BoardBatcher.detail(o.batch, Random(7))
        assertTrue(d.contains("signals op=unacked platform=substack"))
        assertTrue(d.contains("+3 more"))
        assertTrue(!d.contains("<b>"))
        assertEquals(2, Regex("</untrusted-[0-9a-f]{8}>").findAll(d).count())
    }
}
