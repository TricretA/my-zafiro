package com.tricreta.zafiro.autonomy.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OutcomesTest {
    private fun o(id: Long, artifact: String, metric: String, value: Double, at: Long, tags: Map<String, String> = emptyMap(), platform: String = "tiktok", lag: String = "24h") =
        OutcomeObs(id, "tiktok", platform, artifact, "post", tags, metric, value, at, lag)

    @Test fun `validation demands evidence, a sane kind, metric and value`() {
        assertNull(OutcomeRules.validate("post", "views", 120.0, "TikTok stats screen", "24h"))
        assertNotNull(OutcomeRules.validate("tweet", "views", 1.0, "x", ""))
        assertNotNull(OutcomeRules.validate("post", "Views!", 1.0, "x", ""))
        assertNotNull(OutcomeRules.validate("post", "views", -1.0, "x", ""))
        assertNotNull(OutcomeRules.validate("post", "views", Double.NaN, "x", ""))
        assertNotNull(OutcomeRules.validate("post", "views", null, "x", ""))
        assertNotNull(OutcomeRules.validate("post", "views", 1.0, "  ", ""))
        assertNotNull(OutcomeRules.validate("post", "views", 1.0, "x", "5y"))
        assertEquals("watch_time", OutcomeRules.normaliseMetric(" Watch Time "))
    }

    @Test fun `the latest lag of an artifact replaces the earlier ones`() {
        val obs = listOf(o(1, "p1", "views", 10.0, 100), o(2, "p1", "views", 90.0, 300, lag = "7d"), o(3, "p1", "views", 40.0, 200), o(4, "p2", "views", 5.0, 150), o(5, "p2", "likes", 1.0, 150))
        val latest = OutcomeAnalysis.latestPerArtifact(obs, "views")
        assertEquals(listOf(5.0, 90.0), latest.map { it.value })
    }

    @Test fun `summary has per metric count, median, mean and max`() {
        val obs = listOf(o(1, "a", "views", 10.0, 1), o(2, "b", "views", 30.0, 2), o(3, "c", "views", 20.0, 3), o(4, "a", "likes", 2.0, 4))
        val s = OutcomeAnalysis.summarize(obs).associateBy { it.metric }
        assertEquals(3, s.getValue("views").artifacts)
        assertEquals(20.0, s.getValue("views").median, 1e-9)
        assertEquals(20.0, s.getValue("views").mean, 1e-9)
        assertEquals(30.0, s.getValue("views").max, 1e-9)
        assertEquals(1, s.getValue("likes").artifacts)
        assertEquals(listOf("b", "c"), OutcomeAnalysis.top(obs, "views", 2).map { it.artifactId })
    }

    @Test fun `rewards use each account's own baseline`() {
        // big account: 10_000 views is normal; small account: 100 is normal. Both beat nothing, both land near 0.5.
        val big = (1..6).map { o(it.toLong(), "b$it", "views", 10_000.0, it * 10L, mapOf("format" to "reel"), platform = "instagram") }
        val small = (1..6).map { o(100L + it, "s$it", "views", 100.0, it * 10L, mapOf("format" to "reel"), platform = "tiktok") }
        val r = OutcomeAnalysis.rewardsByArm(big + small, "views", "format").getValue("reel")
        assertEquals(12, r.size)
        assertTrue(r.all { kotlin.math.abs(it - 0.5) < 1e-9 })
    }

    @Test fun `artifacts without the dimension tag are ignored`() {
        val obs = listOf(o(1, "a", "views", 10.0, 1, mapOf("hook_type" to "Question")), o(2, "b", "views", 10.0, 2))
        val r = OutcomeAnalysis.rewardsByArm(obs, "views", "hook_type")
        assertEquals(setOf("question"), r.keys)
    }
}
