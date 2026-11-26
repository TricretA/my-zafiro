package com.tricreta.zafiro.autonomy.learn

import com.niki914.zafiro.api.LearnService
import com.niki914.zafiro.api.TeamResult
import com.tricreta.zafiro.autonomy.core.Bandit
import com.tricreta.zafiro.autonomy.core.Lesson
import com.tricreta.zafiro.autonomy.core.LessonCurator
import com.tricreta.zafiro.autonomy.core.LessonMath
import com.tricreta.zafiro.autonomy.core.LessonProvider
import com.tricreta.zafiro.autonomy.core.LessonScopes
import com.tricreta.zafiro.autonomy.core.LessonStatus
import com.tricreta.zafiro.autonomy.core.LessonsPrompt
import com.tricreta.zafiro.autonomy.core.OutcomeAnalysis
import com.tricreta.zafiro.autonomy.core.OutcomeRules
import com.tricreta.zafiro.autonomy.data.LearnDao
import com.tricreta.zafiro.autonomy.data.OutcomeEntity
import com.tricreta.zafiro.autonomy.data.toEntity
import com.tricreta.zafiro.autonomy.data.toLesson
import com.tricreta.zafiro.autonomy.data.toObs
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

/**
 * Outcomes (measured results), the bandit suggestion over them, and the lessons store. Every method returns JSON or a short
 * message the agent can read; invalid input is an explicit error, never silently stored. Lessons cannot touch caps,
 * approvals or safety rules (see [com.tricreta.zafiro.autonomy.core.LessonGuard]).
 */
class LearnServiceImpl(
    private val dao: LearnDao,
    private val nowMs: () -> Long = System::currentTimeMillis,
    private val random: () -> Random = { Random.Default },
) : LearnService {

    /** Serialises lesson read-modify-write so two agents confirming at once cannot lose a count. */
    private val lessonLock = Mutex()

    override suspend fun run(tool: String, args: Map<String, String>): TeamResult = try {
        when (tool) {
            "outcomes" -> outcomes(args)
            "lessons" -> lessons(args)
            else -> TeamResult(false, "Unknown learning tool '$tool'.")
        }
    } catch (e: IllegalArgumentException) {
        TeamResult(false, e.message ?: "Invalid arguments.")
    }

    private fun arg(a: Map<String, String>, k: String) = a[k]?.trim().orEmpty()
    private fun need(a: Map<String, String>, k: String): String = arg(a, k).ifEmpty { throw IllegalArgumentException("'$k' is required.") }
    private fun argL(a: Map<String, String>, k: String) = a[k]?.trim()?.toDoubleOrNull()?.toLong()
    private fun daysAgo(days: Int) = nowMs() - days.coerceIn(1, 3650) * DAY_MS

    // ------------------------------------------------------------ outcomes
    private suspend fun outcomes(a: Map<String, String>): TeamResult = when (need(a, "op")) {
        "record" -> {
            val platform = need(a, "platform").lowercase(Locale.ROOT)
            val kind = arg(a, "kind").lowercase(Locale.ROOT).ifEmpty { "post" }
            val metric = OutcomeRules.normaliseMetric(need(a, "metric"))
            val value = a["value"]?.trim()?.toDoubleOrNull()
            val evidence = arg(a, "evidence")
            val lag = arg(a, "lag").lowercase(Locale.ROOT)
            val problem = OutcomeRules.validate(kind, metric, value, evidence, lag)
            if (problem != null) {
                TeamResult(false, problem)
            } else {
                val observed = argL(a, "observed_at")?.takeIf { it in 1_000_000_000_000L..nowMs() + DAY_MS } ?: nowMs()
                val id = dao.insertOutcome(
                    OutcomeEntity(
                        workspaceId = arg(a, "workspace").ifEmpty { platform }, platform = platform, artifactId = arg(a, "artifact").take(300), artifactKind = kind,
                        hookType = tag(a, "hook_type"), format = tag(a, "format"), lengthBucket = tag(a, "length_bucket"), timeBucket = tag(a, "time_bucket"), topic = tag(a, "topic"),
                        metric = metric, value = value!!, observedAtMs = observed, lagLabel = lag, evidence = evidence.take(500),
                    ),
                )
                TeamResult(true, JSONObject().put("id", "o$id").put("note", "Recorded. Cite o$id as evidence when you add or confirm a lesson.").toString())
            }
        }
        "list" -> {
            val platform = arg(a, "platform").lowercase(Locale.ROOT)
            val kind = arg(a, "kind").lowercase(Locale.ROOT)
            val rows = dao.outcomes(platform, daysAgo((argL(a, "days") ?: 30).toInt()), 400).filter { kind.isEmpty() || it.artifactKind == kind }
                .take((argL(a, "limit") ?: 40).toInt().coerceIn(1, 100))
            TeamResult(true, JSONArray(rows.map { outcomeJson(it) }).toString())
        }
        "summary" -> {
            val platform = arg(a, "platform").lowercase(Locale.ROOT)
            val days = (argL(a, "days") ?: 7).toInt().coerceIn(1, 365)
            TeamResult(true, summary(platform, days).toString())
        }
        "suggest" -> suggest(a)
        else -> TeamResult(false, "op must be record, list, summary or suggest.")
    }

    private fun tag(a: Map<String, String>, k: String) = arg(a, k).lowercase(Locale.ROOT).take(40)

    private fun outcomeJson(o: OutcomeEntity) = JSONObject().put("id", "o${o.id}").put("platform", o.platform).put("kind", o.artifactKind).put("artifact", o.artifactId)
        .put("metric", o.metric).put("value", o.value).put("lag", o.lagLabel).put("at", o.observedAtMs).put("evidence", o.evidence)
        .put("tags", JSONObject().also { t -> o.toObs().tags.forEach { (k, v) -> t.put(k, v) } })

    suspend fun summary(platform: String, days: Int): JSONObject {
        val obs = dao.outcomes(platform, daysAgo(days), 2000).map { it.toObs() }
        val metrics = OutcomeAnalysis.summarize(obs)
        val out = JSONObject().put("platform", platform.ifEmpty { "all" }).put("days", days).put("observations", obs.size)
        out.put(
            "metrics",
            JSONArray(metrics.map { JSONObject().put("metric", it.metric).put("artifacts", it.artifacts).put("median", it.median).put("mean", it.mean).put("max", it.max) }),
        )
        val top = JSONObject()
        metrics.sortedByDescending { it.artifacts }.take(3).forEach { m ->
            top.put(
                m.metric,
                JSONArray(OutcomeAnalysis.top(obs, m.metric, 3).map { JSONObject().put("artifact", it.artifactId.ifEmpty { "o${it.id}" }).put("value", it.value).put("platform", it.platform) }),
            )
        }
        out.put("top", top)
        if (obs.isEmpty()) out.put("note", "No outcomes recorded in this period. Read stats from the platform and call outcomes op=record with evidence.")
        return out
    }

    private suspend fun suggest(a: Map<String, String>): TeamResult {
        val platform = need(a, "platform").lowercase(Locale.ROOT)
        val dimension = need(a, "dimension").lowercase(Locale.ROOT)
        if (dimension !in OutcomeRules.DIMENSIONS) return TeamResult(false, "dimension must be one of ${OutcomeRules.DIMENSIONS.joinToString(", ")}.")
        val metric = OutcomeRules.normaliseMetric(arg(a, "metric").ifEmpty { "views" })
        val extra = arg(a, "arms").split(',').map { it.trim().lowercase(Locale.ROOT) }.filter { it.isNotEmpty() }
        val obs = dao.outcomes(platform, daysAgo((argL(a, "days") ?: 90).toInt()), 2000).map { it.toObs() }.filter { it.metric == metric }
        val rewards = OutcomeAnalysis.rewardsByArm(obs, metric, dimension)
        val s = Bandit(random()).suggest(extra, rewards)
            ?: return TeamResult(false, "Nothing to choose from yet for $dimension on $platform: pass arms=a,b,c (for example the hook types you want to test) and record outcomes with that tag.")
        return TeamResult(
            true,
            JSONObject().put("arm", s.arm).put("mode", s.mode.name).put("reason", s.reason).put("leader", s.leader ?: JSONObject.NULL)
                .put("metric", metric).put("dimension", dimension)
                .put("arms", JSONArray(s.stats.map { JSONObject().put("arm", it.arm).put("samples", it.n).put("mean_reward", Math.round(it.mean * 100) / 100.0) }))
                .put("next", "Write the next piece using '${s.arm}' as its $dimension and record that tag when you record its outcomes.").toString(),
        )
    }

    // ------------------------------------------------------------- lessons
    private suspend fun lessons(a: Map<String, String>): TeamResult = lessonLock.withLock {
        val now = nowMs()
        when (need(a, "op")) {
            "add" -> {
                val scope = need(a, "scope").lowercase(Locale.ROOT)
                val claim = need(a, "claim").replace(Regex("\\s+"), " ")
                val evidence = LessonCurator.parseEvidence(arg(a, "evidence"))
                val problem = LessonCurator.validateNew(scope, claim, evidence)
                if (problem != null) return@withLock TeamResult(false, problem)
                val existing = dao.lessons().map { it.toLesson() }
                val dup = LessonCurator.findDuplicate(existing, scope, claim)
                if (dup != null) {
                    val fresh = evidence.filter { it !in dup.evidence }
                    if (fresh.isEmpty()) {
                        return@withLock TeamResult(true, JSONObject().put("id", dup.id).put("status", dup.status.name).put("note", "Already known as lesson #${dup.id}; no new evidence, nothing changed.").toString())
                    }
                    val merged = bump(dup, fresh, now)
                    return@withLock TeamResult(true, lessonJson(merged, now).put("note", "Merged into the existing lesson #${dup.id} as one more confirmation.").toString())
                }
                val lesson = Lesson(0, scope, claim, evidence, helpful = 0, harmful = 0, ttlDays = LessonCurator.DEFAULT_TTL_DAYS, status = LessonStatus.CANDIDATE, createdMs = now, lastConfirmedMs = now)
                val id = dao.insertLesson(lesson.toEntity())
                TeamResult(true, lessonJson(lesson.copy(id = id), now).put("note", "Stored as a CANDIDATE. It becomes ACTIVE after ${LessonCurator.PROMOTE_MIN_HELPFUL} confirmations with new evidence.").toString())
            }
            "confirm" -> {
                val l = lesson(a) ?: return@withLock TeamResult(false, "No lesson with that id.")
                if (l.status == LessonStatus.RETIRED) return@withLock TeamResult(false, "Lesson #${l.id} is retired. Add it again as a new candidate if new results support it.")
                val evidence = LessonCurator.parseEvidence(need(a, "evidence"))
                val fresh = evidence.filter { it !in l.evidence }
                if (fresh.isEmpty()) return@withLock TeamResult(false, "That evidence is already counted for lesson #${l.id}. Confirm with a NEW outcome id or file.")
                TeamResult(true, lessonJson(bump(l, fresh, now), now).toString())
            }
            "harm" -> {
                val l = lesson(a) ?: return@withLock TeamResult(false, "No lesson with that id.")
                need(a, "reason")
                TeamResult(true, lessonJson(save(l.copy(harmful = l.harmful + 1), now), now).toString())
            }
            "top" -> {
                sweep(now)
                val scope = arg(a, "scope").lowercase(Locale.ROOT)
                val k = (argL(a, "k") ?: LessonsPrompt.DEFAULT_K.toLong()).toInt().coerceIn(1, 20)
                val top = LessonsPrompt.top(dao.lessons().map { it.toLesson() }, scope, now, k)
                TeamResult(
                    true,
                    JSONObject().put("count", top.size).put("lessons", JSONArray(top.map { lessonJson(it, now) }))
                        .put("lines", JSONArray(top.map { LessonsPrompt.line(it, now) })).toString(),
                )
            }
            "list" -> {
                sweep(now)
                val scope = arg(a, "scope").lowercase(Locale.ROOT)
                val status = LessonStatus.parse(a["status"])
                val rows = dao.lessons().map { it.toLesson() }
                    .filter { (scope.isEmpty() || LessonScopes.matches(it.scope, scope)) && (status == null || it.status == status) }.take(60)
                TeamResult(true, JSONArray(rows.map { lessonJson(it, now) }).toString())
            }
            else -> TeamResult(false, "op must be add, confirm, harm, top or list.")
        }
    }

    private suspend fun lesson(a: Map<String, String>): Lesson? {
        val id = need(a, "id").toDoubleOrNull()?.toLong() ?: throw IllegalArgumentException("'id' must be a number.")
        return dao.lesson(id)?.toLesson()
    }

    /** Counts one more confirmation backed by [fresh] evidence, refreshes the clock and lets the curator decide the status. */
    private suspend fun bump(l: Lesson, fresh: List<String>, now: Long): Lesson =
        save(l.copy(helpful = l.helpful + 1, evidence = (l.evidence + fresh).distinct(), lastConfirmedMs = now), now)

    private suspend fun save(l: Lesson, now: Long): Lesson {
        val done = l.copy(status = LessonCurator.evaluate(l, now))
        dao.upsertLesson(done.toEntity())
        return done
    }

    /** Applies decay and expiry: a lesson nobody confirmed for too long retires itself. */
    suspend fun sweep(now: Long = nowMs()): Int {
        var changed = 0
        dao.lessons().map { it.toLesson() }.filter { it.status != LessonStatus.RETIRED }.forEach { l ->
            val s = LessonCurator.evaluate(l, now)
            if (s != l.status) {
                dao.upsertLesson(l.copy(status = s).toEntity())
                changed++
            }
        }
        return changed
    }

    /** Owner decision from the Learning page: the owner is the authority, so confirming activates the lesson. */
    suspend fun ownerConfirm(id: Long): Boolean = lessonLock.withLock {
        val l = dao.lesson(id)?.toLesson() ?: return@withLock false
        if (l.status == LessonStatus.RETIRED) return@withLock false
        val confirmed = l.copy(helpful = l.helpful + 1, evidence = (l.evidence + "owner").distinct(), lastConfirmedMs = nowMs(), status = LessonStatus.ACTIVE)
        dao.upsertLesson(confirmed.toEntity())
        true
    }

    suspend fun ownerRetire(id: Long): Boolean = lessonLock.withLock {
        val l = dao.lesson(id)?.toLesson() ?: return@withLock false
        dao.upsertLesson(l.copy(status = LessonStatus.RETIRED).toEntity())
        true
    }

    /** What a run's prompt may see: active lessons (the prompt builder filters scope, expiry and count). */
    fun provider(): LessonProvider = LessonProvider { dao.lessonsWithStatus(LessonStatus.ACTIVE.name).map { it.toLesson() } }

    private fun lessonJson(l: Lesson, now: Long) = JSONObject().put("id", l.id).put("scope", l.scope).put("claim", l.claim).put("status", l.status.name)
        .put("confidence", Math.round(LessonMath.confidence(l, now) * 100) / 100.0).put("helpful", l.helpful).put("harmful", l.harmful)
        .put("evidence_count", l.evidence.distinct().size)

    private companion object {
        const val DAY_MS = 86_400_000L
    }
}
