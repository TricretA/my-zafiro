package com.tricreta.zafiro.autonomy.signals

import com.niki914.zafiro.api.TeamResult
import com.tricreta.zafiro.autonomy.core.signals.SignalFormat
import com.tricreta.zafiro.autonomy.core.signals.SignalKind
import com.tricreta.zafiro.autonomy.core.signals.SignalStore
import org.json.JSONObject
import java.time.ZoneId

/**
 * The `signals` builtin tool: read-only access (plus ack) to captured notification signals. Everything printed is fenced
 * as untrusted third-party text by [SignalFormat]. Arguments arrive as strings and bad ones throw [IllegalArgumentException],
 * which the team service turns into an error result.
 */
class SignalsTool(
    private val store: SignalStore?,
    private val nowMs: () -> Long,
    private val zone: () -> ZoneId,
) {
    fun run(a: Map<String, String>): TeamResult {
        val store = store ?: return TeamResult(false, "Realtime signals are not available in this process.")
        val platform = arg(a, "platform").ifEmpty { null }
        return when (arg(a, "op")) {
            "recent" -> {
                val kind = arg(a, "kind").ifEmpty { null }?.let { k ->
                    SignalKind.entries.firstOrNull { it.name.equals(k.replace(' ', '_'), true) }
                        ?: throw IllegalArgumentException("'kind' must be OWNER_MESSAGE, ENGAGEMENT, EMAIL or OTHER.")
                }
                val since = arg(a, "since").ifEmpty { null }?.let {
                    SignalFormat.parseSince(it, nowMs(), zone())
                        ?: throw IllegalArgumentException("'since' must look like 30m, 2h, 1d, today, 2026-11-26 or epoch milliseconds.")
                } ?: 0L
                TeamResult(true, SignalFormat.render("Recent signals", store.recent(platform, kind, since, limit(a, 20)), zone = zone()))
            }
            "unacked" -> TeamResult(true, SignalFormat.render("Unacked signals", store.unacked(platform, limit(a, 30)), zone = zone()))
            "ack" -> {
                val raw = arg(a, "id").ifEmpty { throw IllegalArgumentException("'id' is required.") }
                val ids = if (raw.equals("all", true)) {
                    store.unacked(platform, SignalStore.MAX_LIMIT).map { it.id }.toSet()
                } else {
                    raw.split(',', ' ').mapNotNull { it.trim().removePrefix("#").toLongOrNull() }.toSet()
                }
                if (ids.isEmpty()) throw IllegalArgumentException("'id' must be a signal number, a comma list, or all.")
                TeamResult(true, JSONObject().put("acked", store.ack(ids)).toString())
            }
            "stats" -> TeamResult(true, SignalFormat.renderStats(store.stats(nowMs(), zone())))
            else -> TeamResult(false, "op must be recent, unacked, ack or stats.")
        }
    }

    private fun arg(a: Map<String, String>, k: String) = a[k]?.trim().orEmpty()
    private fun limit(a: Map<String, String>, default: Int) = a["limit"]?.toDoubleOrNull()?.toInt() ?: default
}
