package com.niki914.zafiro.signals

import android.content.Context
import com.niki914.logging.Logger
import com.niki914.zafiro.api.TeamService
import com.niki914.zafiro.api.util.runCatchingCancellable
import com.niki914.zafiro.repo.WorkspaceManager
import com.niki914.zafiro.service.findService
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.core.signals.DeptMatcher
import com.tricreta.zafiro.autonomy.core.signals.OwnerCommand
import com.tricreta.zafiro.autonomy.core.signals.OwnerCommandParser
import com.tricreta.zafiro.autonomy.core.signals.Signal
import com.tricreta.zafiro.autonomy.team.TeamServiceImpl
import com.tricreta.zafiro.autonomy.workspace.WorkspaceDef
import com.tricreta.zafiro.autonomy.workspace.WorkspaceInstaller

/**
 * Carries out the owner's fixed commands. This only changes local state (pause or resume a department, settle an approval,
 * add a board item, ask the scheduler to run a heartbeat now). It never sends a reply: the agents do that, using the
 * WhatsApp recipe, when they read the board item this creates.
 *
 * The message text is the OWNER's instruction because the classifier only produces OWNER_MESSAGE for the configured sender.
 */
object OwnerCommandRunner {
    private const val TAG = "niki914_zafiro_OwnerCommandRunner"
    private const val OWNER_CHANNEL = "whatsapp"
    private val NOT_TEAMS = setOf("toolsmith", "health")

    suspend fun run(context: Context, graph: AutonomyGraph, signal: Signal): String {
        val dao = graph.repo.teamDao()
        return Run(context, graph, signal, WorkspaceManager.defs(context), findService<TeamService>() ?: TeamServiceImpl(dao)).execute()
    }

    suspend fun directorName(graph: AutonomyGraph, ws: String): String {
        val agents = graph.repo.teamDao().agentsOfWorkspace(ws)
        return (agents.firstOrNull { it.id.contains("director") } ?: agents.firstOrNull())?.name.orEmpty()
    }

    private class Run(
        val context: Context,
        val graph: AutonomyGraph,
        val signal: Signal,
        val defs: List<WorkspaceDef>,
        val team: TeamService,
    ) {
        private val dao = graph.repo.teamDao()
        private val depts = defs.map { DeptMatcher.Dept(it.id, it.name) }

        suspend fun execute(): String = when (val cmd = OwnerCommandParser.parse(signal.text)) {
            is OwnerCommand.Status -> post(
                "[HIGH] Owner asked STATUS",
                "The owner texted STATUS. Send a short status reply on WhatsApp: what is running, what was done today with evidence, what is blocked on the owner, spend if known. Use the whatsapp-playbook send recipe.",
            )
            is OwnerCommand.StopAll -> stopAll()
            is OwnerCommand.Pause -> setTeam(cmd.dept, false)
            is OwnerCommand.Resume -> setTeam(cmd.dept, true)
            is OwnerCommand.Priority -> request(cmd.text)
            is OwnerCommand.Free -> request(cmd.text)
            is OwnerCommand.Approve -> decide(cmd.code, true)
            is OwnerCommand.Reject -> decide(cmd.code, false)
        }

        private suspend fun stopAll(): String {
            val on = dao.workspaces().filter { it.enabled }
            on.forEach { w ->
                val def = defs.firstOrNull { it.id == w.id } ?: return@forEach
                runCatchingCancellable { WorkspaceManager.setEnabled(context, def, false) }
                    .onFailure { Logger.w(TAG, "could not pause ${w.id}: ${it.message}") }
            }
            graph.notifier.postInfo(signal.id.toInt(), "Zafiro paused everything", "Your STOP ALL message paused ${on.size} team(s). Switch them back on in Teams, or text RESUME <team>.")
            return "stopped ${on.size} team(s)"
        }

        private suspend fun setTeam(query: String, enable: Boolean): String {
            val verb = if (enable) "RESUME" else "PAUSE"
            val dept = DeptMatcher.resolve(query, depts)
                ?: return post("Owner sent $verb for an unknown department", "The owner wrote \"$verb $query\" but no department matches. Known: ${depts.joinToString { it.id }}. Ask which one they meant.")
            val def = defs.first { it.id == dept.id }
            if (!enable && dao.workspace(dept.id)?.enabled != true) return post("Owner paused ${dept.name}: already off", "${dept.name} was already off. Confirm that to the owner.")
            val result = runCatchingCancellable { WorkspaceManager.setEnabled(context, def, enable) }
            return post(
                "Owner ${if (enable) "resumed" else "paused"} ${dept.name}",
                result.fold(
                    { "The owner texted \"$verb ${dept.name}\" and it is done: $it Confirm to the owner in one line." },
                    { "The owner texted \"$verb ${dept.name}\" but it FAILED: ${it.message}. Tell the owner." },
                ),
                if (enable) listOf(dept.id) else emptyList(),
            )
        }

        private suspend fun request(text: String): String {
            val mentioned = DeptMatcher.mentioned(text, depts).map { it.id }
            return post(
                "[HIGH] Owner: ${text.take(80)}",
                "Request from the owner, high priority. Do it before anything else and report back on WhatsApp.\n\nOwner wrote: $text" +
                    if (mentioned.isNotEmpty()) "\n\nIt mentions: ${mentioned.joinToString(", ")}. Hand the relevant part to that department's director (board op=add workspace=<dept>)." else "",
                mentioned,
            )
        }

        private suspend fun decide(code: String, approve: Boolean): String {
            val word = if (approve) "YES" else "NO"
            val actionId = dao.actions("", 200).firstOrNull { it.id.toString() == code }?.id
                ?: return post("Owner answered $word $code: no matching request", "The owner texted \"$word $code\" but no action has that code. Codes are the action numbers from engage. Ask the owner to check the code.")
            val r = team.run("engage", mapOf("op" to if (approve) "approve" else "reject", "id" to actionId.toString(), "reason" to "owner replied $word by message"))
            val detail = if (r.ok) {
                "The owner texted \"$word $code\": action $actionId is now ${if (approve) "APPROVED, do it now and record engage op=done" else "REJECTED, drop it"}. Confirm to the owner."
            } else {
                "The owner texted \"$word $code\" but it could not be applied: ${r.text}. Tell the owner."
            }
            return post("Owner ${if (approve) "approved" else "rejected"} action $actionId", detail)
        }

        /** Adds an owner-made board item for a team and asks the scheduler to run that team's heartbeat now. */
        private suspend fun post(title: String, detail: String, preferred: List<String> = emptyList()): String {
            val ws = chooseWorkspace(preferred) ?: run {
                graph.notifier.postInfo(signal.id.toInt(), "Owner message, but no team is on", signal.text.take(200))
                return "no team on"
            }
            val r = team.run(
                "board",
                mapOf("op" to "add", "title" to title.take(200), "detail" to detail.take(3900), "owner" to directorName(graph, ws), "by" to "owner", "workspace" to ws),
            )
            runCatchingCancellable {
                graph.repo.enqueueManual(WorkspaceInstaller.taskId(ws, "heartbeat"))
                graph.scheduler.reconcile("owner_signal")
            }.onFailure { Logger.w(TAG, "heartbeat not started: ${it.message}") }
            return if (r.ok) "board item in $ws" else "board failed: ${r.text}"
        }

        /** The team to hand an owner item to: a department the text names, else the owner channel (WhatsApp), else any team that is on. */
        private suspend fun chooseWorkspace(preferred: List<String>): String? {
            val enabled = dao.workspaces().filter { it.enabled && it.id !in NOT_TEAMS }.map { it.id }
            return preferred.firstOrNull { it in enabled } ?: OWNER_CHANNEL.takeIf { it in enabled } ?: enabled.firstOrNull()
        }
    }
}
