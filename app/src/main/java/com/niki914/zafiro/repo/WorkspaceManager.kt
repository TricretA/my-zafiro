package com.niki914.zafiro.repo

import android.content.Context
import androidx.core.app.NotificationCompat
import com.niki914.logging.Logger
import com.niki914.zafiro.business.notification.AppNotificationChannel
import com.niki914.zafiro.business.notification.NotificationChannelManager
import com.niki914.zafiro.service.requireService
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.workspace.SocialCatalog
import com.tricreta.zafiro.autonomy.workspace.SocialPlatforms
import com.tricreta.zafiro.autonomy.workspace.SocialSettings
import com.tricreta.zafiro.autonomy.workspace.WorkspaceDef
import com.tricreta.zafiro.autonomy.workspace.WorkspaceInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

/**
 * Bundled workspaces (assets/workspaces): one switchable team per department; the social workspace additionally has per-platform toggles. Switching one ON installs its agents and
 * tasks (first time), turns on its skills, seeds its screen routines and rulebook, and schedules its tasks. OFF pauses them.
 */
object WorkspaceManager {
    private const val TAG = "WorkspaceManager"
    @Volatile private var cache: List<WorkspaceDef>? = null

    private fun deletedFile(context: Context) = File(context.filesDir, "settings/workspaces_deleted.txt")

    fun deletedIds(context: Context): Set<String> = runCatching { deletedFile(context).readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet() }.getOrDefault(emptySet())

    /** Workspaces that ship with the app and are not deleted by the owner. */
    fun defs(context: Context): List<WorkspaceDef> = allDefs(context).filter { it.id !in deletedIds(context) }

    /** Removes a workspace for good (its agents, tasks and state), even a default one; "Restore deleted" brings it back. */
    suspend fun delete(context: Context, def: WorkspaceDef) = withContext(Dispatchers.IO) {
        val graph = AutonomyGraph.require(); val repo = graph.repo; val dao = repo.teamDao()
        dao.agentsOfWorkspace(def.id).forEach { repo.deleteAgent(it.id) }
        dao.tasksOfWorkspace(def.id).forEach { repo.deleteTask(it.id) }
        dao.deleteWorkspace(def.id)
        val f = deletedFile(context).also { it.parentFile?.mkdirs() }
        f.writeText((deletedIds(context) + def.id).joinToString("\n"))
        graph.scheduler.reconcile("workspace_deleted")
    }

    fun restoreDeleted(context: Context) { deletedFile(context).delete() }

    private fun allDefs(context: Context): List<WorkspaceDef> = cache ?: synchronized(this) {
        cache ?: (context.assets.list("workspaces").orEmpty().filter { it.endsWith(".json") }.sorted().mapNotNull { name ->
            runCatching { WorkspaceDef.parse(context.assets.open("workspaces/$name").bufferedReader().readText()) }
                .onFailure { Logger.w(TAG, "bad bundled workspace $name: ${it.message}") }.getOrNull()
        }).also { cache = it }
    }

    /** Copies bundled routines into files/routines. A file the owner edited (different from the last seeded copy) is never overwritten. */
    suspend fun seedRoutines(context: Context) = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "routines").also { it.mkdirs() }
        context.assets.list("routines").orEmpty().filter { it.endsWith(".json") }.forEach { name ->
            val bundled = context.assets.open("routines/$name").bufferedReader().readText()
            val target = File(dir, name)
            val seed = File(dir, ".$name.seed")
            val lastSeeded = seed.takeIf { it.isFile }?.readText()
            // New file, or untouched since last seeding -> take the bundled version.
            if (!target.isFile || (lastSeeded != null && target.readText() == lastSeeded)) {
                target.writeText(bundled); seed.writeText(bundled)
            }
        }
    }

    private fun ensureRulebook(context: Context, def: WorkspaceDef) {
        val f = File(context.filesDir, "outputs/${def.id}/RULES.md")
        if (f.isFile) return
        f.parentFile?.mkdirs()
        f.writeText(
            "# ${def.name} rulebook\n\nThis is the single source of rules for this workspace. Newest corrections are at the top; they override anything older.\n\n" +
                "- Never invent facts, results or experience. Say \"not available\" instead.\n" +
                "- Daily caps: ${def.quotas.replace(",", ", ")}. Stop at the cap.\n" +
                "- The owner switched this workspace on, so the team works alone. Only serious actions (direct messages, sending email, payments, deletions, applications) wait for approval; 10 approvals in a row make a type automatic, one rejection makes it ask again.\n" +
                "- Act like a person: pause between actions, vary what you do, stop on any warning or verification.\n" +
                "- Never bypass locks, CAPTCHAs, 2FA, consent screens or payments.\n",
        )
    }

    suspend fun setEnabled(context: Context, def: WorkspaceDef, enabled: Boolean): String = withContext(Dispatchers.IO) {
        val graph = AutonomyGraph.require()
        val installer = WorkspaceInstaller(graph.repo.teamDao())
        if (enabled && def.id == SocialPlatforms.WORKSPACE_ID && enabledPlatforms(context).isEmpty()) {
            writePlatformsFile(context)
            runCatching {
                requireService<NotificationChannelManager>().post(AppNotificationChannel.Alerts, NO_PLATFORMS_NOTIFICATION_ID) {
                    setSmallIcon(context.applicationInfo.icon.takeIf { it != 0 } ?: android.R.drawable.ic_dialog_info)
                    setContentTitle("Pick your platforms")
                    setContentText("Open Workspaces > Social Media Manager and switch on the platforms you use.")
                    setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    setAutoCancel(true)
                }
            }
            return@withContext NO_PLATFORMS_MESSAGE
        }
        if (enabled) {
            seedRoutines(context)
            ensureRulebook(context, def)
            val r = installer.install(def)
            if (def.id == SocialPlatforms.WORKSPACE_ID) applySocialConfig(context, switchSkills = true)
            val scheduled = installer.setEnabled(def, true)
            def.skills.forEach { id -> runCatching { XRepo.skills.setEnabled(id, true) } }
            graph.scheduler.reconcile("workspace_on")
            buildString {
                append("${def.name} is ON: ${def.agents.size} agents, $scheduled scheduled tasks.")
                if (r.retiredLegacyTasks > 0) append(" ${r.retiredLegacyTasks} older hand-made task(s) for the same agents were switched off (kept, not deleted).")
            }
        } else {
            if (graph.repo.teamDao().workspace(def.id) != null) installer.setEnabled(def, false)
            graph.scheduler.reconcile("workspace_off")
            "${def.name} is OFF. Its tasks are paused."
        }
    }

    // ---------------------------------------------------------------- social platforms

    private const val NO_PLATFORMS_NOTIFICATION_ID = 91401
    private const val NO_PLATFORMS_MESSAGE = "Pick at least one platform first, then switch the Social Media Manager on."
    private fun socialSettingsFile(context: Context) = File(context.filesDir, "settings/social_platforms.json")
    private fun platformsFile(context: Context) = File(context.filesDir, "outputs/social/PLATFORMS.json")
    private fun migrationFlag(context: Context) = File(context.filesDir, "settings/social_migrated.flag")

    fun socialCatalog(context: Context): SocialCatalog = socialCatalogCache
        ?: SocialCatalog.parse(context.assets.open("social/platforms.json").bufferedReader().readText()).also { socialCatalogCache = it }
    @Volatile private var socialCatalogCache: SocialCatalog? = null

    fun socialSettings(context: Context): SocialSettings = SocialSettings.parse(runCatching { socialSettingsFile(context).readText() }.getOrNull())

    private fun saveSocialSettings(context: Context, s: SocialSettings) {
        socialSettingsFile(context).also { it.parentFile?.mkdirs() }.writeText(s.toJson())
    }

    fun enabledPlatforms(context: Context): Set<String> = SocialPlatforms.enabledIds(socialCatalog(context), socialSettings(context)).toSet()

    /** Rewrites the file the agents read (outputs/social/PLATFORMS.json) from the saved choices. */
    private fun writePlatformsFile(context: Context) {
        platformsFile(context).also { it.parentFile?.mkdirs() }.writeText(SocialPlatforms.platformsJson(socialCatalog(context), socialSettings(context)))
    }

    /** Writes PLATFORMS.json, merges the per-platform caps into the workspace quotas and (when [switchSkills]) turns on the skills of enabled platforms. */
    private suspend fun applySocialConfig(context: Context, switchSkills: Boolean) {
        val catalog = socialCatalog(context); val settings = socialSettings(context)
        writePlatformsFile(context)
        val dao = AutonomyGraph.require().repo.teamDao()
        dao.workspace(SocialPlatforms.WORKSPACE_ID)?.let { dao.upsertWorkspace(it.copy(quotas = SocialPlatforms.quotas(catalog, settings), updatedAtMs = System.currentTimeMillis())) }
        if (switchSkills) SocialPlatforms.enabledIds(catalog, settings).forEach { id -> SocialPlatforms.skillsOf(catalog, id).forEach { runCatching { XRepo.skills.setEnabled(it, true) } } }
    }

    private fun socialDef(context: Context): WorkspaceDef? = allDefs(context).firstOrNull { it.id == SocialPlatforms.WORKSPACE_ID }

    /**
     * Switches one platform on or off for the Social Media Manager. The team only works on enabled platforms. Turning the last platform
     * off pauses the workspace. Returns a short message for the owner.
     */
    suspend fun setPlatformEnabled(context: Context, id: String, on: Boolean): String = withContext(Dispatchers.IO) {
        val catalog = socialCatalog(context)
        val platform = catalog.platform(id) ?: return@withContext "Unknown platform $id."
        val before = socialSettings(context)
        saveSocialSettings(context, SocialPlatforms.setEnabled(catalog, before, id, on, LocalDate.now().toString()))
        val dao = AutonomyGraph.require().repo.teamDao()
        val socialOn = dao.workspace(SocialPlatforms.WORKSPACE_ID)?.enabled == true
        if (!on) {
            val left = enabledPlatforms(context)
            SocialPlatforms.exclusiveSkills(catalog, id, left + id, socialDef(context)?.skills.orEmpty()).forEach { runCatching { XRepo.skills.setEnabled(it, false) } }
        }
        applySocialConfig(context, switchSkills = socialOn && on)
        if (socialOn && enabledPlatforms(context).isEmpty()) {
            socialDef(context)?.let { setEnabled(context, it, false) }
            return@withContext "${platform.name} is off. No platform is left, so the Social Media Manager is paused."
        }
        if (on) "${platform.name} is on." else "${platform.name} is off."
    }

    /** Owner-edited daily caps for one platform (action to number); 0 stops that action. */
    suspend fun setPlatformCaps(context: Context, id: String, caps: Map<String, Int>) = withContext(Dispatchers.IO) {
        saveSocialSettings(context, SocialPlatforms.withCaps(socialSettings(context), id, caps))
        applySocialConfig(context, switchSkills = false)
    }

    /** Switches every platform whose app is installed on this phone. Returns the ids that were switched on. */
    suspend fun enableInstalledPlatforms(context: Context): List<String> {
        val installed = socialCatalog(context).platforms.filter { p -> p.app.isNotBlank() && runCatching { context.packageManager.getPackageInfo(p.app, 0) }.isSuccess && !p.optional }.map { it.id }
        installed.forEach { setPlatformEnabled(context, it, true) }
        return installed
    }

    /**
     * Runs once after the update that replaced the per-platform workspaces by the single social one: the old workspaces are paused
     * (their agents, tasks, board items and history stay in the database), and the ones that were ON become platform toggles of the social workspace.
     */
    suspend fun migrateSupersededWorkspaces(context: Context) = withContext(Dispatchers.IO) {
        if (migrationFlag(context).isFile) return@withContext
        val graph = AutonomyGraph.require(); val dao = graph.repo.teamDao()
        val now = System.currentTimeMillis()
        val old = SocialPlatforms.SUPERSEDED.mapNotNull { dao.workspace(it) }
        old.forEach { ws ->
            dao.setWorkspaceTasksEnabled(ws.id, false, now); dao.setWorkspaceAgentsEnabled(ws.id, false, now)
            dao.upsertWorkspace(ws.copy(enabled = false, updatedAtMs = now))
        }
        val catalog = socialCatalog(context)
        val carry = SocialPlatforms.migratedPlatforms(catalog, old.filter { it.enabled }.map { it.id })
        carry.forEach { setPlatformEnabled(context, it, true) }
        if (carry.isNotEmpty()) socialDef(context)?.let { setEnabled(context, it, true) }
        graph.scheduler.reconcile("workspace_superseded")
        migrationFlag(context).also { it.parentFile?.mkdirs() }.writeText("migrated ${carry.joinToString(",")}")
        Logger.i(TAG, "superseded workspaces paused: ${old.map { it.id }}, carried to social: $carry")
    }
}
