package com.niki914.zafiro.signals

import android.content.Context
import com.niki914.logging.Logger
import com.niki914.zafiro.api.TeamService
import com.niki914.zafiro.api.util.runCatchingCancellable
import com.niki914.zafiro.service.findService
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.core.signals.BoardBatcher
import com.tricreta.zafiro.autonomy.core.signals.RawNotification
import com.tricreta.zafiro.autonomy.core.signals.Signal
import com.tricreta.zafiro.autonomy.core.signals.SignalClassifier
import com.tricreta.zafiro.autonomy.core.signals.SignalKind
import com.tricreta.zafiro.autonomy.team.TeamServiceImpl
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject

/**
 * One captured notification in, nothing out: classify, store, then react. Reactions are only board items and (for the
 * owner's own messages) the fixed commands in [OwnerCommandRunner]. Third-party text never becomes an instruction; it is
 * stored, shown fenced as untrusted, and summarised on the board with the same fence.
 */
object SignalPipeline {
    private const val TAG = "niki914_zafiro_SignalPipeline"
    private val batcher = BoardBatcher()
    private val gate = Mutex()

    suspend fun handle(context: Context, raw: RawNotification) {
        val config = SignalConfigStore.load(context)
        if (!config.enabled || !config.allows(raw.packageName)) return
        val graph = AutonomyGraph.get(context)
        val signal = SignalClassifier.classify(raw, config, System.currentTimeMillis())
        val stored = graph.signals.add(signal) ?: return
        gate.withLock {
            when (stored.kind) {
                SignalKind.OWNER_MESSAGE -> {
                    val outcome = OwnerCommandRunner.run(context, graph, stored)
                    Logger.i(TAG, "owner command handled: $outcome")
                    graph.signals.ack(setOf(stored.id))
                }
                SignalKind.ENGAGEMENT, SignalKind.EMAIL -> reactWithBoardItem(graph, stored)
                SignalKind.OTHER -> Unit
            }
        }
    }

    private suspend fun reactWithBoardItem(graph: AutonomyGraph, s: Signal) {
        val ws = SignalClassifier.WORKSPACE_BY_PLATFORM[s.platform] ?: return
        val dao = graph.repo.teamDao()
        if (dao.workspace(ws)?.enabled != true) return
        val now = System.currentTimeMillis()
        var offer = batcher.offer(s.platform, now, s)
        if (offer is BoardBatcher.Offer.Amend) {
            val id = offer.batch.itemId ?: return
            val changed = dao.amendTodoBoardItem(id, BoardBatcher.title(offer.batch), BoardBatcher.detail(offer.batch), now)
            if (changed > 0) return
            // The item was claimed or finished meanwhile: start a fresh one for this signal.
            batcher.drop(s.platform)
            offer = batcher.offer(s.platform, now, s)
        }
        val owner = OwnerCommandRunner.directorName(graph, ws)
        val team = findService<TeamService>() ?: TeamServiceImpl(dao)
        val result = team.run(
            "board",
            mapOf(
                "op" to "add", "title" to BoardBatcher.title(offer.batch), "detail" to BoardBatcher.detail(offer.batch),
                "owner" to owner, "by" to "signals", "workspace" to ws,
            ),
        )
        if (!result.ok) {
            Logger.w(TAG, "board item failed: ${result.text}")
            return
        }
        val id = runCatchingCancellable { JSONObject(result.text).getLong("id") }.getOrNull() ?: return
        batcher.created(s.platform, id)
    }
}
