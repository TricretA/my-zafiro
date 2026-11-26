package com.tricreta.zafiro.autonomy.core

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BanditTest {
    @Test fun `normalise is 0_5 at the baseline and monotonic and capped`() {
        assertEquals(0.5, Rewards.normalise(100.0, 100.0), 1e-9)
        assertTrue(Rewards.normalise(200.0, 100.0) > 0.5)
        assertTrue(Rewards.normalise(50.0, 100.0) < 0.5)
        assertEquals(Rewards.normalise(500.0, 100.0), Rewards.normalise(1_000_000.0, 100.0), 1e-9)
        assertEquals(0.5, Rewards.normalise(0.0, 0.0), 1e-9)
        assertEquals(0.75, Rewards.normalise(3.0, 0.0), 1e-9)
        assertEquals(0.5, Rewards.normalise(Double.NaN, 5.0), 1e-9)
    }

    @Test fun `median and trailing median resist an outlier`() {
        assertEquals(10.0, Rewards.median(listOf(9.0, 10.0, 11.0, 1_000_000.0, 8.0)), 1e-9)
        assertEquals(2.5, Rewards.median(listOf(1.0, 2.0, 3.0, 4.0)), 1e-9)
        assertEquals(0.0, Rewards.median(emptyList()), 1e-9)
        assertEquals(5.0, Rewards.trailingMedian(listOf(1000.0, 1000.0, 5.0, 5.0, 5.0), window = 3), 1e-9)
    }

    @Test fun `exploration floor is clamped to ten to twenty percent`() {
        assertEquals(0.10, Bandit(Random(1), explorationFloor = 0.0).explorationFloor, 1e-9)
        assertEquals(0.20, Bandit(Random(1), explorationFloor = 0.9).explorationFloor, 1e-9)
        assertEquals(0.15, Bandit(Random(1), explorationFloor = 0.15).explorationFloor, 1e-9)
    }

    @Test fun `arms below the minimum sample count are tried first, fewest first`() {
        val b = Bandit(Random(7), minSamples = 3)
        val s = assertNotNull(b.suggest(listOf("question", "story", "list"), mapOf("question" to listOf(0.6, 0.6, 0.6), "story" to listOf(0.5), "list" to listOf(0.5, 0.5))))
        assertEquals("story", s.arm)
        assertEquals(SuggestMode.UNDERSAMPLED, s.mode)
        assertEquals("question", s.leader)
    }

    @Test fun `a brand new candidate arm gets sampled before the established ones`() {
        val b = Bandit(Random(3), minSamples = 3)
        val rewards = mapOf("a" to List(10) { 0.9 }, "b" to List(10) { 0.1 })
        assertEquals("fresh", assertNotNull(b.suggest(listOf("fresh"), rewards)).arm)
    }

    @Test fun `empty input gives no suggestion`() {
        assertNull(Bandit(Random(1)).suggest(emptyList(), emptyMap()))
        assertNull(Bandit(Random(1)).suggest(listOf(" ", ""), emptyMap()))
    }

    @Test fun `same seed gives the same sequence`() {
        val rewards = mapOf("a" to List(6) { 0.7 }, "b" to List(6) { 0.4 }, "c" to List(6) { 0.5 })
        fun run(seed: Int) = Bandit(Random(seed)).let { b -> List(30) { b.suggest(emptyList(), rewards)!!.arm } }
        assertEquals(run(42), run(42))
    }

    @Test fun `thompson sampling mostly picks the better arm but the floor keeps exploring`() {
        val rewards = mapOf("good" to List(20) { 0.8 }, "bad" to List(20) { 0.2 }, "mid" to List(20) { 0.5 })
        val b = Bandit(Random(2026), explorationFloor = 0.15)
        val picks = List(2000) { b.suggest(emptyList(), rewards)!! }
        val good = picks.count { it.arm == "good" } / 2000.0
        val explored = picks.count { it.mode == SuggestMode.EXPLORE } / 2000.0
        assertTrue(good > 0.80, "good arm share $good")
        assertTrue(explored in 0.11..0.19, "explore share $explored")
        assertTrue(picks.any { it.arm == "bad" }, "the floor must still try the weak arm")
        assertEquals("good", picks.first().leader)
    }

    @Test fun `one viral outlier does not make a weak arm the leader`() {
        val obs = (1..12).map { i ->
            OutcomeObs(i.toLong(), "tiktok", "tiktok", "p$i", "post", mapOf("hook_type" to if (i % 2 == 0) "question" else "story"), "views", if (i == 1) 1_000_000.0 else 100.0 + i, i * 1000L, "24h")
        }
        val rewards = OutcomeAnalysis.rewardsByArm(obs, "views", "hook_type")
        val b = Bandit(Random(1), minSamples = 3)
        val q = b.stats("question", rewards.getValue("question")).mean
        val s = b.stats("story", rewards.getValue("story")).mean
        assertTrue(s < 0.65, "story mean $s must stay near the baseline despite the 1M outlier")
        assertTrue(q > 0.4)
    }

    @Test fun `beta samples stay in range and match the mean`() {
        val b = Bandit(Random(5))
        val xs = List(5000) { b.sampleBeta(8.0, 2.0) }
        assertTrue(xs.all { it in 0.0..1.0 })
        assertEquals(0.8, xs.average(), 0.02)
        val ys = List(3000) { b.sampleBeta(0.5, 0.5) }
        assertTrue(ys.all { it in 0.0..1.0 })
    }
}
