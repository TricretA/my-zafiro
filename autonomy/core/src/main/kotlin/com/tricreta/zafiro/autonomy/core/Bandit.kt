package com.tricreta.zafiro.autonomy.core

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

/** Beta posterior of one arm. Rewards are in 0..1 (see [Rewards]); the prior is Beta(1,1). */
data class ArmStats(val arm: String, val n: Int, val alpha: Double, val beta: Double) {
    val mean: Double get() = alpha / (alpha + beta)
}

enum class SuggestMode { UNDERSAMPLED, EXPLORE, THOMPSON }

data class Suggestion(val arm: String, val mode: SuggestMode, val reason: String, val stats: List<ArmStats>, val leader: String?)

/** Turns a raw metric into a 0..1 reward relative to the account's own baseline, so outliers and bigger accounts do not dominate. */
object Rewards {
    /** A post can at most count as this many times the baseline; a single viral hit must not decide the whole experiment. */
    const val MAX_RATIO = 5.0

    fun median(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val s = values.sorted()
        val mid = s.size / 2
        return if (s.size % 2 == 1) s[mid] else (s[mid - 1] + s[mid]) / 2.0
    }

    /** Median of the last [window] values (oldest first). */
    fun trailingMedian(values: List<Double>, window: Int = 30): Double = median(values.takeLast(window))

    /** value == baseline gives 0.5; twice the baseline 0.67; half of it 0.33; five times (the cap) 0.83. */
    fun normalise(value: Double, baseline: Double): Double {
        if (value.isNaN() || baseline.isNaN()) return 0.5
        if (baseline <= 0.0) return if (value > 0.0) 0.75 else 0.5
        val r = min(max(value, 0.0) / baseline, MAX_RATIO)
        return r / (1.0 + r)
    }
}

/**
 * Thompson sampling over named arms (for example hook types). Deterministic for a given [rng].
 * - An arm with fewer than [minSamples] observations is never judged: those arms are tried first (fewest first).
 * - After that, with probability [explorationFloor] (kept inside 10..20%) a uniformly random arm is chosen so a bad early
 *   estimate can recover; otherwise each arm's Beta posterior is sampled and the highest draw wins.
 */
class Bandit(
    private val rng: Random,
    explorationFloor: Double = DEFAULT_FLOOR,
    val minSamples: Int = DEFAULT_MIN_SAMPLES,
) {
    val explorationFloor: Double = explorationFloor.coerceIn(MIN_FLOOR, MAX_FLOOR)

    fun stats(arm: String, rewards: List<Double>): ArmStats {
        val clean = rewards.map { it.coerceIn(0.0, 1.0) }
        return ArmStats(arm, clean.size, 1.0 + clean.sum(), 1.0 + clean.sumOf { 1.0 - it })
    }

    /** The arm with the best mean among arms that have enough samples, or null when none does. */
    fun leader(stats: List<ArmStats>): ArmStats? = stats.filter { it.n >= minSamples }.maxByOrNull { it.mean }

    fun suggest(candidates: Collection<String>, rewardsByArm: Map<String, List<Double>>): Suggestion? {
        val arms = (candidates + rewardsByArm.keys).map { it.trim() }.filter { it.isNotEmpty() }.distinct().sorted()
        if (arms.isEmpty()) return null
        val stats = arms.map { stats(it, rewardsByArm[it].orEmpty()) }
        val leader = leader(stats)?.arm

        val under = stats.filter { it.n < minSamples }
        if (under.isNotEmpty()) {
            val fewest = under.minOf { it.n }
            val tied = under.filter { it.n == fewest }
            val pick = tied[rng.nextInt(tied.size)]
            return Suggestion(pick.arm, SuggestMode.UNDERSAMPLED, "'${pick.arm}' has ${pick.n} of ${minSamples} samples needed before it can be judged.", stats, leader)
        }
        if (rng.nextDouble() < explorationFloor) {
            val pick = stats[rng.nextInt(stats.size)]
            return Suggestion(pick.arm, SuggestMode.EXPLORE, "Exploration draw (${(explorationFloor * 100).toInt()}% of picks are random so an unlucky early estimate can recover).", stats, leader)
        }
        val draws = stats.map { it to sampleBeta(it.alpha, it.beta) }
        val best = draws.maxByOrNull { it.second }!!.first
        return Suggestion(
            best.arm, SuggestMode.THOMPSON,
            "Thompson draw picked '${best.arm}' (mean reward ${"%.2f".format(java.util.Locale.ROOT, best.mean)} over ${best.n} samples; 0.50 equals the account baseline).",
            stats, leader,
        )
    }

    internal fun sampleBeta(a: Double, b: Double): Double {
        val x = sampleGamma(a)
        val y = sampleGamma(b)
        val s = x + y
        return if (s <= 0.0) 0.5 else x / s
    }

    /** Marsaglia-Tsang. */
    private fun sampleGamma(shape: Double): Double {
        if (shape < 1.0) return sampleGamma(shape + 1.0) * rng.nextDouble().coerceAtLeast(1e-12).pow(1.0 / shape)
        val d = shape - 1.0 / 3.0
        val c = 1.0 / sqrt(9.0 * d)
        while (true) {
            var x: Double
            var v: Double
            do {
                x = normal()
                v = 1.0 + c * x
            } while (v <= 0.0)
            v = v * v * v
            val u = rng.nextDouble().coerceAtLeast(1e-12)
            if (u < 1.0 - 0.0331 * x * x * x * x) return d * v
            if (ln(u) < 0.5 * x * x + d * (1.0 - v + ln(v))) return d * v
        }
    }

    private fun normal(): Double {
        val u1 = rng.nextDouble().coerceAtLeast(1e-12)
        val u2 = rng.nextDouble()
        return sqrt(-2.0 * ln(u1)) * cos(2.0 * Math.PI * u2)
    }

    companion object {
        const val MIN_FLOOR = 0.10
        const val MAX_FLOOR = 0.20
        const val DEFAULT_FLOOR = 0.15
        const val DEFAULT_MIN_SAMPLES = 3
    }
}
