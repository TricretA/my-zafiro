package com.niki914.zafiro.app.ui.content

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.niki914.uikit.infra.component.settings.SettingsPageSpec
import com.niki914.uikit.infra.component.settings.SettingsRowAction
import com.niki914.uikit.infra.component.settings.SettingsRowSpec
import com.niki914.uikit.infra.component.settings.SettingsSectionLayout
import com.niki914.uikit.infra.component.settings.SettingsSectionSpec
import com.niki914.uikit.infra.component.settings.SettingsSpecPageContent
import com.niki914.zafiro.app.R
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.core.LessonMath
import com.tricreta.zafiro.autonomy.core.LessonStatus
import com.tricreta.zafiro.autonomy.core.OutcomeAnalysis
import com.tricreta.zafiro.autonomy.data.toLesson
import com.tricreta.zafiro.autonomy.data.toObs
import kotlinx.coroutines.launch
import java.util.Locale

private const val WEEK_MS = 7L * 24 * 3_600_000

private fun fmt(v: Double): String = if (v >= 100 || v == Math.floor(v)) v.toLong().toString() else String.format(Locale.ROOT, "%.1f", v)

/** Lessons with their confidence and evidence count (confirm or retire), and the last week's results per platform. */
@Composable
fun LearningPageContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graph = AutonomyGraph.require()
    val dao = graph.db.learnDao()
    val since = remember { System.currentTimeMillis() - WEEK_MS }
    val lessons by dao.observeLessons().collectAsState(initial = emptyList())
    val outcomes by dao.observeOutcomes(since, 2000).collectAsState(initial = emptyList())
    val now = System.currentTimeMillis()

    val lessonRows = lessons.map { it.toLesson() }.flatMap { l ->
        val summary = stringResource(
            R.string.learning_lesson_summary, l.scope, (LessonMath.confidence(l, now) * 100).toInt(), l.evidence.distinct().size, l.helpful, l.harmful,
        )
        buildList<SettingsRowSpec> {
            add(SettingsRowSpec.Value(title = l.claim, summary = summary, currentState = l.status.name))
            if (l.status != LessonStatus.RETIRED) {
                if (l.status == LessonStatus.CANDIDATE) {
                    add(SettingsRowSpec.Action(id = "ok:${l.id}", title = stringResource(R.string.learning_confirm, l.id.toInt()), summary = stringResource(R.string.learning_confirm_summary)))
                }
                add(SettingsRowSpec.Destructive(id = "no:${l.id}", title = stringResource(R.string.learning_retire, l.id.toInt())))
            }
        }
    }.ifEmpty { listOf(SettingsRowSpec.Message(stringResource(R.string.learning_empty_lessons))) }

    val byPlatform = outcomes.map { it.toObs() }.groupBy { it.platform }.toSortedMap()
    val outcomeRows = byPlatform.flatMap { (platform, obs) ->
        OutcomeAnalysis.summarize(obs).map { m ->
            SettingsRowSpec.Value(
                title = "$platform · ${m.metric}",
                summary = stringResource(R.string.learning_outcome_summary, m.artifacts, fmt(m.median), fmt(m.max)),
            )
        }
    }.ifEmpty { listOf(SettingsRowSpec.Message(stringResource(R.string.learning_empty_outcomes))) }

    val confirmed = stringResource(R.string.learning_done_confirmed)
    val retired = stringResource(R.string.learning_done_retired)
    val spec = SettingsPageSpec(
        description = stringResource(R.string.learning_intro),
        sections = listOf(
            SettingsSectionSpec(title = stringResource(R.string.learning_section_lessons, lessons.size), layout = SettingsSectionLayout.GroupedCard, rows = lessonRows),
            SettingsSectionSpec(title = stringResource(R.string.learning_section_outcomes), layout = SettingsSectionLayout.GroupedCard, rows = outcomeRows),
        ),
    )
    SettingsSpecPageContent(spec = spec, onAction = { a ->
        if (a is SettingsRowAction.Click) {
            val id = a.id.substringAfter(':').toLongOrNull() ?: return@SettingsSpecPageContent
            scope.launch {
                val ok = if (a.id.startsWith("ok:")) graph.learn.ownerConfirm(id) else graph.learn.ownerRetire(id)
                if (ok) Toast.makeText(context, if (a.id.startsWith("ok:")) confirmed else retired, Toast.LENGTH_SHORT).show()
            }
        }
    })
}
