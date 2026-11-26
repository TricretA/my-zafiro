package com.tricreta.zafiro.autonomy.core

/** One measured result of something the team made (a post, an application, a reply, a video). */
data class OutcomeObs(
    val id: Long,
    val workspace: String,
    val platform: String,
    val artifactId: String,
    val artifactKind: String,
    /** hook_type, format, length_bucket, time_bucket, topic (only those that were given). */
    val tags: Map<String, String>,
    val metric: String,
    val value: Double,
    val observedAtMs: Long,
    /** 1h, 24h, 7d for posts; 3d, 7d, 14d for applications. */
    val lag: String,
)

data class MetricSummary(val metric: String, val artifacts: Int, val median: Double, val mean: Double, val max: Double)

object OutcomeRules {
    val DIMENSIONS: List<String> = listOf("hook_type", "format", "length_bucket", "time_bucket", "topic")
    val KINDS: Set<String> = setOf("post", "application", "reply", "video")
    val LAGS: Set<String> = setOf("1h", "24h", "7d", "3d", "14d", "now")
    const val MAX_METRIC_CHARS = 40
    private val METRIC = Regex("[a-z][a-z0-9_]{0,39}")

    fun normaliseMetric(raw: String): String = raw.trim().lowercase().replace(' ', '_')

    /** An error message or null. Values must be finite and non-negative; evidence is mandatory so a number cannot be made up silently. */
    fun validate(kind: String, metric: String, value: Double?, evidence: String, lag: String): String? = when {
        kind !in KINDS -> "kind must be one of ${KINDS.joinToString(", ")}."
        !METRIC.matches(metric) -> "metric must be a short lower-case name such as views, likes, replies, saves, applications_status."
        value == null || value.isNaN() || value.isInfinite() || value < 0 -> "value must be a number zero or greater."
        evidence.isBlank() -> "evidence is required: say where you read the number (screen routine, stats page, notification) so it can be checked."
        lag.isNotBlank() && lag !in LAGS -> "lag must be one of ${LAGS.joinToString(", ")} (or blank)."
        else -> null
    }
}

object OutcomeAnalysis {
    /** The newest observation of each artifact for [metric] (later lags replace earlier ones), oldest first. */
    fun latestPerArtifact(obs: List<OutcomeObs>, metric: String): List<OutcomeObs> =
        obs.filter { it.metric == metric }
            .groupBy { it.workspace + "/" + it.platform + "/" + it.artifactId.ifBlank { "#" + it.id } }
            .map { (_, rows) -> rows.maxBy { it.observedAtMs } }
            .sortedBy { it.observedAtMs }

    /**
     * Rewards per arm of [dimension] for one [metric]. Each artifact is normalised against the trailing median of its own account
     * (workspace + platform): the median of up to [window] earlier artifacts, or of all of them while there are fewer than 3.
     */
    fun rewardsByArm(obs: List<OutcomeObs>, metric: String, dimension: String, window: Int = 30): Map<String, List<Double>> {
        val latest = latestPerArtifact(obs, metric)
        val out = LinkedHashMap<String, MutableList<Double>>()
        latest.groupBy { it.workspace + "/" + it.platform }.values.forEach { account ->
            val values = account.map { it.value }
            account.forEachIndexed { i, o ->
                val baseline = if (i >= 3) Rewards.trailingMedian(values.subList(0, i), window) else Rewards.median(values.take(window))
                val arm = o.tags[dimension]?.trim()?.lowercase().orEmpty()
                if (arm.isNotEmpty()) out.getOrPut(arm) { mutableListOf() }.add(Rewards.normalise(o.value, baseline))
            }
        }
        return out
    }

    fun summarize(obs: List<OutcomeObs>): List<MetricSummary> =
        obs.map { it.metric }.distinct().sorted().map { m ->
            val v = latestPerArtifact(obs, m).map { it.value }
            MetricSummary(m, v.size, Rewards.median(v), if (v.isEmpty()) 0.0 else v.average(), v.maxOrNull() ?: 0.0)
        }

    fun top(obs: List<OutcomeObs>, metric: String, k: Int): List<OutcomeObs> =
        latestPerArtifact(obs, metric).sortedByDescending { it.value }.take(k)
}
