package com.tricreta.zafiro.autonomy.core

import kotlin.math.pow
import kotlin.math.sqrt

enum class LessonStatus { CANDIDATE, ACTIVE, RETIRED;
    companion object { fun parse(s: String?): LessonStatus? = entries.firstOrNull { it.name.equals(s?.trim(), ignoreCase = true) } }
}

/** A short, specific claim learned from measured results. Counts are what agents and the owner reported; confidence is derived. */
data class Lesson(
    val id: Long,
    /** "global" or a workspace id (optionally "<workspace>/<platform>"). */
    val scope: String,
    val claim: String,
    /** Outcome ids, board ids or file paths that support the claim. */
    val evidence: List<String>,
    val helpful: Int,
    val harmful: Int,
    val ttlDays: Int,
    val status: LessonStatus,
    val createdMs: Long,
    val lastConfirmedMs: Long,
)

object LessonMath {
    const val HALF_LIFE_DAYS = 30.0
    private const val Z = 1.28 // about 80% one-sided
    private const val DAY_MS = 86_400_000.0

    fun decayFactor(ageMs: Long, halfLifeDays: Double = HALF_LIFE_DAYS): Double = 0.5.pow(ageMs.coerceAtLeast(0) / DAY_MS / halfLifeDays)

    /** Wilson lower bound of the helpful share, on counts that halve every [halfLifeDays] since the last confirmation. 0 when nothing is known. */
    fun confidence(helpful: Int, harmful: Int, ageSinceConfirmMs: Long, halfLifeDays: Double = HALF_LIFE_DAYS): Double {
        val f = decayFactor(ageSinceConfirmMs, halfLifeDays)
        val h = helpful * f
        val m = harmful * f
        val n = h + m
        if (n <= 0.0) return 0.0
        val p = h / n
        val z2 = Z * Z
        val centre = p + z2 / (2 * n)
        val margin = Z * sqrt(p * (1 - p) / n + z2 / (4 * n * n))
        return ((centre - margin) / (1 + z2 / n)).coerceIn(0.0, 1.0)
    }

    fun confidence(l: Lesson, nowMs: Long): Double = confidence(l.helpful, l.harmful, nowMs - l.lastConfirmedMs)
}

/** Rules that turn counts into a status. Pure, so every threshold is unit-tested. */
object LessonCurator {
    const val PROMOTE_MIN_HELPFUL = 3
    const val PROMOTE_MIN_EVIDENCE = 2
    const val PROMOTE_MIN_CONFIDENCE = 0.5
    const val RETIRE_BELOW_CONFIDENCE = 0.35
    const val CANDIDATE_TTL_DAYS = 21
    const val DEFAULT_TTL_DAYS = 90
    const val MAX_CLAIM_CHARS = 240
    const val DUPLICATE_SIMILARITY = 0.75
    private const val DAY_MS = 86_400_000L

    /** The status [l] should have now. Retired is final: a retired lesson is replaced by a new candidate, never revived. */
    fun evaluate(l: Lesson, nowMs: Long): LessonStatus {
        if (l.status == LessonStatus.RETIRED) return LessonStatus.RETIRED
        val idleDays = (nowMs - l.lastConfirmedMs) / DAY_MS
        if (l.harmful >= l.helpful + 2) return LessonStatus.RETIRED
        if (l.status == LessonStatus.CANDIDATE && idleDays > CANDIDATE_TTL_DAYS) return LessonStatus.RETIRED
        if (l.status == LessonStatus.ACTIVE && idleDays > l.ttlDays) return LessonStatus.RETIRED
        val conf = LessonMath.confidence(l, nowMs)
        if (l.status == LessonStatus.ACTIVE) {
            val decayedN = (l.helpful + l.harmful) * LessonMath.decayFactor(nowMs - l.lastConfirmedMs)
            return if (decayedN >= 3.0 && conf < RETIRE_BELOW_CONFIDENCE) LessonStatus.RETIRED else LessonStatus.ACTIVE
        }
        val promote = l.helpful >= PROMOTE_MIN_HELPFUL && l.evidence.distinct().size >= PROMOTE_MIN_EVIDENCE &&
            conf >= PROMOTE_MIN_CONFIDENCE && l.harmful * 3 <= l.helpful
        return if (promote) LessonStatus.ACTIVE else LessonStatus.CANDIDATE
    }

    private val STOP = setOf("the", "a", "an", "to", "of", "and", "or", "in", "on", "for", "with", "is", "are", "be", "it", "this", "that", "your", "you", "we", "at", "by", "as")

    fun tokens(claim: String): Set<String> =
        claim.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 1 && it !in STOP }.toSet()

    fun similarity(a: String, b: String): Double {
        val x = tokens(a)
        val y = tokens(b)
        if (x.isEmpty() || y.isEmpty()) return 0.0
        return x.intersect(y).size.toDouble() / x.union(y).size
    }

    /** A live (not retired) lesson in the same scope that says nearly the same thing. */
    fun findDuplicate(existing: List<Lesson>, scope: String, claim: String): Lesson? =
        existing.filter { it.status != LessonStatus.RETIRED && it.scope.equals(scope, ignoreCase = true) }
            .map { it to similarity(it.claim, claim) }
            .filter { it.second >= DUPLICATE_SIMILARITY }
            .maxByOrNull { it.second }?.first

    /** An error message or null. */
    fun validateNew(scope: String, claim: String, evidence: List<String>): String? = when {
        scope.isBlank() -> "scope is required: 'global' or a workspace id."
        claim.isBlank() -> "claim is required."
        claim.length > MAX_CLAIM_CHARS -> "claim must be one specific sentence of at most $MAX_CLAIM_CHARS characters."
        '\n' in claim -> "claim must be a single line."
        evidence.none { it.isNotBlank() } -> "evidence is required: outcome ids (for example o12,o15), a board id or a file path that shows why."
        else -> LessonGuard.violation(claim)
    }

    fun parseEvidence(raw: String): List<String> = raw.split(',', ';', ' ', '\n').map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}

/**
 * Lessons are advice about content, not authority over safety. A lesson may never touch caps, approvals, budgets or the
 * rules that keep accounts safe: those change only by the owner (the weekly reviewer proposes, the owner decides).
 */
object LessonGuard {
    private val FORBIDDEN = Regex(
        "\\b(quota|quotas|caps?|daily limit|rate limit|per day|approval|approvals|approve|safety|checklist|captcha|2fa|consent|budget|spend|spending|payment|password|credential|credentials|" +
            "ignore (the |all )?(rules?|polic(y|ies)|instructions?)|disable|bypass|override|skip (the )?(review|approval|check)|never ask|without asking|auto-?approve)\\b",
        RegexOption.IGNORE_CASE,
    )

    fun violation(claim: String): String? {
        val hit = FORBIDDEN.find(claim) ?: return null
        return "A lesson cannot touch caps, approvals, budgets or safety rules (found '${hit.value}'). Describe content or timing insights only; " +
            "put rule changes into the weekly proposal file for the owner."
    }
}

object LessonScopes {
    /** "global" and blank lessons apply everywhere; otherwise the lesson scope equals the workspace or starts with "<workspace>/" or "<workspace>:". */
    fun matches(lessonScope: String, workspaceId: String): Boolean {
        val s = lessonScope.trim().lowercase()
        val w = workspaceId.trim().lowercase()
        if (s.isEmpty() || s == "global") return true
        if (w.isEmpty()) return false
        return s == w || s.startsWith("$w/") || s.startsWith("$w:")
    }
}

/** The 'LESSONS' lines injected into a run's prompt: only ACTIVE, unexpired, relevant lessons, best first, one line each. */
object LessonsPrompt {
    const val DEFAULT_K = 5
    const val HEADER = "LESSONS (measured by this team; only active ones; advice on content and timing, they never change caps, approvals or safety rules):"
    private const val LINE_CHARS = 200

    fun top(lessons: List<Lesson>, workspaceId: String, nowMs: Long, k: Int = DEFAULT_K): List<Lesson> =
        lessons.filter { it.status == LessonStatus.ACTIVE && LessonScopes.matches(it.scope, workspaceId) && LessonCurator.evaluate(it, nowMs) == LessonStatus.ACTIVE }
            .sortedWith(compareByDescending<Lesson> { LessonMath.confidence(it, nowMs) }.thenByDescending { it.lastConfirmedMs })
            .take(k)

    fun line(l: Lesson, nowMs: Long): String =
        "- ${l.claim.replace('\n', ' ').trim().take(LINE_CHARS)} (confidence ${(LessonMath.confidence(l, nowMs) * 100).toInt()}%, ${l.evidence.distinct().size} evidence, #${l.id})"

    fun format(lessons: List<Lesson>, workspaceId: String, nowMs: Long, k: Int = DEFAULT_K): String {
        val chosen = top(lessons, workspaceId, nowMs, k)
        if (chosen.isEmpty()) return ""
        return buildString {
            appendLine(HEADER)
            chosen.forEach { appendLine(line(it, nowMs)) }
        }.trimEnd()
    }
}

/** Loads the lessons a run may see. Implemented by the Room store; kept an interface so prompt composition stays pure. */
fun interface LessonProvider {
    suspend fun activeLessons(): List<Lesson>
}
