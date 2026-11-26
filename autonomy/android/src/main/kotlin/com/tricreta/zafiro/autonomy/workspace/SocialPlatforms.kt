package com.tricreta.zafiro.autonomy.workspace

import org.json.JSONArray
import org.json.JSONObject

/** One platform the Social Media Manager can run (bundled catalogue, assets/social/platforms.json). */
data class SocialPlatform(
    val id: String, val name: String, val app: String, val optional: Boolean, val metricool: String, val caps: String, val skills: List<String>,
)

data class SocialCatalog(
    val metricoolFreeLimit: Int, val warmupDays: Int, val fallbackCaps: String, val platforms: List<SocialPlatform>,
) {
    fun platform(id: String): SocialPlatform? = platforms.firstOrNull { it.id == id }

    companion object {
        fun parse(raw: String): SocialCatalog {
            val o = JSONObject(raw)
            val list = o.getJSONArray("platforms")
            return SocialCatalog(
                metricoolFreeLimit = o.optInt("metricoolFreeLimit", 20), warmupDays = o.optInt("warmupDays", 28), fallbackCaps = o.optString("fallbackCaps", ""),
                platforms = (0 until list.length()).map { i ->
                    val p = list.getJSONObject(i)
                    SocialPlatform(
                        id = p.getString("id"), name = p.getString("name"), app = p.optString("app", ""), optional = p.optBoolean("optional", false),
                        metricool = p.optString("metricool", "none"), caps = p.optString("caps", ""),
                        skills = p.optJSONArray("skills")?.let { a -> (0 until a.length()).map { a.getString(it) } }.orEmpty(),
                    )
                },
            )
        }
    }
}

/** The owner's operating brief. It is deliberately small: everything in it is injected into every social run. */
data class SocialBrief(
    val brandName: String = "",
    val mission: String = "",
    val audience: String = "",
    val voice: String = "",
) {
    fun isComplete(): Boolean = brandName.isNotBlank() && mission.isNotBlank() && audience.isNotBlank()

    fun toJson(): JSONObject = JSONObject()
        .put("brand_name", brandName)
        .put("mission", mission)
        .put("audience", audience)
        .put("voice", voice)

    companion object {
        fun parse(o: JSONObject?): SocialBrief = SocialBrief(
            brandName = o?.optString("brand_name").orEmpty(),
            mission = o?.optString("mission").orEmpty(),
            audience = o?.optString("audience").orEmpty(),
            voice = o?.optString("voice").orEmpty(),
        )
    }
}

/** One social identity the owner has deliberately made available to the team. No passwords are ever stored here. */
data class SocialAccount(
    val handle: String = "",
    val purpose: String = "",
    /** NOT_CREATED | LOGIN_REQUIRED | VERIFIED */
    val accessState: String = "NOT_CREATED",
    /** draft plus zero or more outward actions the owner allows on this account. */
    val allowedActions: Set<String> = emptySet(),
) {
    fun isReady(): Boolean = handle.isNotBlank() && purpose.isNotBlank() && accessState == "VERIFIED" && allowedActions.isNotEmpty()

    fun toJson(): JSONObject = JSONObject()
        .put("handle", handle)
        .put("purpose", purpose)
        .put("access", accessState)
        .put("allowed_actions", JSONArray(allowedActions.sorted()))

    companion object {
        fun parse(o: JSONObject?): SocialAccount = SocialAccount(
            handle = o?.optString("handle").orEmpty(),
            purpose = o?.optString("purpose").orEmpty(),
            accessState = o?.optString("access", "NOT_CREATED").orEmpty().ifBlank { "NOT_CREATED" },
            allowedActions = o?.optJSONArray("allowed_actions")?.let { a ->
                (0 until a.length()).map { a.optString(it).trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
            }.orEmpty(),
        )
    }
}

/** What the owner chose: platforms, their accounts, limits, and the tasks that are allowed to run. */
data class SocialSettings(
    val enabledSince: Map<String, String> = emptyMap(),
    val capOverrides: Map<String, String> = emptyMap(),
    val brief: SocialBrief = SocialBrief(),
    val accounts: Map<String, SocialAccount> = emptyMap(),
    val scheduledTaskKeys: Set<String> = emptySet(),
) {
    fun toJson(): String = JSONObject()
        .put("enabled", JSONObject(enabledSince))
        .put("caps", JSONObject(capOverrides))
        .put("brief", brief.toJson())
        .put("accounts", JSONObject(accounts.mapValues { it.value.toJson() }))
        .put("scheduled_tasks", JSONArray(scheduledTaskKeys.sorted()))
        .toString(1)

    companion object {
        fun parse(raw: String?): SocialSettings {
            if (raw.isNullOrBlank()) return SocialSettings()
            val o = runCatching { JSONObject(raw) }.getOrNull() ?: return SocialSettings()
            fun map(k: String): Map<String, String> = o.optJSONObject(k)?.let { j -> j.keys().asSequence().associateWith { j.optString(it) } }.orEmpty()
            val accounts = o.optJSONObject("accounts")?.let { entries ->
                entries.keys().asSequence().associateWith { SocialAccount.parse(entries.optJSONObject(it)) }
            }.orEmpty()
            val scheduled = o.optJSONArray("scheduled_tasks")?.let { a ->
                (0 until a.length()).map { a.optString(it).trim() }.filter { it.isNotEmpty() }.toSet()
            }.orEmpty()
            return SocialSettings(map("enabled"), map("caps"), SocialBrief.parse(o.optJSONObject("brief")), accounts, scheduled)
        }
    }
}

/** Pure rules of the single "social" workspace: platform toggles, caps, the file the agents read, skills per platform, and the migration from the old per-platform workspaces. */
object SocialPlatforms {
    const val WORKSPACE_ID = "social"
    const val VIDEO_STUDIO_ID = "video-studio"
    val OUTWARD_ACTIONS = setOf("post", "comment", "reply", "like", "follow", "dm", "note", "restack")
    val CONFIGURABLE_ACTIONS = listOf("draft", "post", "comment", "reply", "like", "follow", "dm")

    /** Per-platform workspaces that the single social workspace replaced. */
    val SUPERSEDED = listOf("substack", "x", "linkedin", "instagram", "facebook", "tiktok", "youtube", "pinterest", "reddit", "threads", "whatsapp")

    fun enabledIds(catalog: SocialCatalog, s: SocialSettings): List<String> = catalog.platforms.map { it.id }.filter { it in s.enabledSince }

    fun setEnabled(catalog: SocialCatalog, s: SocialSettings, id: String, on: Boolean, today: String): SocialSettings {
        if (catalog.platform(id) == null) return s
        return if (on) s.copy(enabledSince = if (id in s.enabledSince) s.enabledSince else s.enabledSince + (id to today))
        else s.copy(enabledSince = s.enabledSince - id)
    }

    /** Daily caps of one platform: the bundled defaults, overridden by the owner's values. */
    fun caps(platform: SocialPlatform, s: SocialSettings): Map<String, Int> {
        val merged = LinkedHashMap<String, Int>()
        parseCaps(platform.caps).forEach { (k, v) -> merged[k] = v }
        parseCaps(s.capOverrides[platform.id].orEmpty()).forEach { (k, v) -> if (k in merged) merged[k] = v }
        return merged
    }

    fun withCaps(s: SocialSettings, platformId: String, caps: Map<String, Int>): SocialSettings =
        s.copy(capOverrides = s.capOverrides + (platformId to caps.entries.joinToString(",") { "${it.key}=${it.value.coerceAtLeast(0)}" }))

    fun withAccount(s: SocialSettings, platformId: String, account: SocialAccount): SocialSettings =
        s.copy(accounts = s.accounts + (platformId to account))

    fun withSchedule(s: SocialSettings, taskKey: String, on: Boolean): SocialSettings =
        s.copy(scheduledTaskKeys = if (on) s.scheduledTaskKeys + taskKey else s.scheduledTaskKeys - taskKey)

    /** The caps agents receive: an action not explicitly allowed is impossible even if a prompt gets it wrong. */
    fun effectiveCaps(platform: SocialPlatform, s: SocialSettings): Map<String, Int> {
        val allowed = s.accounts[platform.id]?.allowedActions.orEmpty()
        return caps(platform, s).mapValues { (action, cap) -> if (action in OUTWARD_ACTIONS && action !in allowed) 0 else cap }
    }

    /** A social team can only be armed after every selected account identifies an accessible account and its purpose. */
    fun configurationIssue(catalog: SocialCatalog, s: SocialSettings): String? = when {
        enabledIds(catalog, s).isEmpty() -> "Select at least one platform."
        !s.brief.isComplete() -> "Add the brand, mission and audience."
        enabledIds(catalog, s).firstOrNull { !s.accounts[it].let { account -> account?.isReady() == true } } != null ->
            "Configure every selected account: handle, purpose, verified access and allowed actions."
        s.scheduledTaskKeys.isEmpty() -> "Choose at least one work-plan task."
        else -> null
    }

    /** The workspace quota string. Generic social actions are zero so a missing platform prefix cannot bypass account consent. */
    fun quotas(catalog: SocialCatalog, s: SocialSettings): String {
        val perPlatform = catalog.platforms.filter { it.id in s.enabledSince }.flatMap { p -> effectiveCaps(p, s).map { (k, v) -> "${p.id}.$k=$v" } }
        val genericDeny = OUTWARD_ACTIONS.sorted().map { "$it=0" }
        return (perPlatform + genericDeny).joinToString(",")
    }

    /** Content of social/PLATFORMS.json that every agent reads at the start of a run. */
    fun platformsJson(catalog: SocialCatalog, s: SocialSettings): String {
        val enabled = catalog.platforms.filter { it.id in s.enabledSince }
        val o = JSONObject()
            .put("enabled", JSONArray(enabled.map { it.id }))
            .put("enabled_since", JSONObject(enabled.associate { it.id to s.enabledSince.getValue(it.id) }))
            .put("caps", JSONObject(enabled.associate { p -> p.id to JSONObject(effectiveCaps(p, s)) }))
            .put("brief", s.brief.toJson())
            .put("accounts", JSONObject(enabled.associate { p -> p.id to (s.accounts[p.id] ?: SocialAccount()).toJson() }))
            .put("scheduled_tasks", JSONArray(s.scheduledTaskKeys.sorted()))
            .put("ready", configurationIssue(catalog, s) == null)
            .put("owner_contract", "Use brief as the business objective and each account's purpose as its local objective. Work only on enabled accounts and only take an outward action in allowed_actions; draft and research are allowed when selected. If a video is needed, add a board item to workspace video-studio for Video Producer with title 'Social video: <name>' and a complete production brief. Video Studio produces and verifies media; Social decides whether to publish it.")
            .put("apps", JSONObject(enabled.associate { it.id to it.app }))
            .put("metricool", JSONObject().put("plan", "free").put("monthly_limit", catalog.metricoolFreeLimit).put("support", JSONObject(enabled.associate { it.id to it.metricool })))
            .put("warmup_days", catalog.warmupDays)
        return o.toString(1)
    }

    /** Skills a platform needs: its playbook and the skills it lists. */
    fun skillsOf(catalog: SocialCatalog, id: String): Set<String> = catalog.platform(id)?.skills.orEmpty().toSet()

    /** Skills used only by [id] among the enabled platforms and not part of the shared set: safe to switch off when [id] is turned off. */
    fun exclusiveSkills(catalog: SocialCatalog, id: String, stillEnabled: Collection<String>, shared: Collection<String>): Set<String> {
        val others = stillEnabled.filter { it != id }.flatMap { skillsOf(catalog, it) }.toSet()
        return skillsOf(catalog, id) - others - shared.toSet()
    }

    /** Old per-platform workspaces that were ON, as the platform toggles to turn on in the social workspace. */
    fun migratedPlatforms(catalog: SocialCatalog, oldEnabledWorkspaces: Collection<String>): List<String> =
        catalog.platforms.map { it.id }.filter { it in oldEnabledWorkspaces && it in SUPERSEDED }

    private fun parseCaps(raw: String): Map<String, Int> = raw.split(',', ';').mapNotNull {
        val p = it.split('=').map { x -> x.trim() }
        if (p.size == 2) p[1].toIntOrNull()?.let { n -> p[0].lowercase() to n } else null
    }.toMap(LinkedHashMap())
}
