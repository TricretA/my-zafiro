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

/** What the owner chose: enabled platforms with the day each was switched on, and per-platform cap overrides ("post=2,comment=10"). */
data class SocialSettings(val enabledSince: Map<String, String> = emptyMap(), val capOverrides: Map<String, String> = emptyMap()) {
    fun toJson(): String = JSONObject()
        .put("enabled", JSONObject(enabledSince))
        .put("caps", JSONObject(capOverrides))
        .toString(1)

    companion object {
        fun parse(raw: String?): SocialSettings {
            if (raw.isNullOrBlank()) return SocialSettings()
            val o = runCatching { JSONObject(raw) }.getOrNull() ?: return SocialSettings()
            fun map(k: String): Map<String, String> = o.optJSONObject(k)?.let { j -> j.keys().asSequence().associateWith { j.optString(it) } }.orEmpty()
            return SocialSettings(map("enabled"), map("caps"))
        }
    }
}

/** Pure rules of the single "social" workspace: platform toggles, caps, the file the agents read, skills per platform, and the migration from the old per-platform workspaces. */
object SocialPlatforms {
    const val WORKSPACE_ID = "social"

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

    /** The workspace quota string: per-platform keys ("instagram.post=1") for enabled platforms plus the generic fallback keys. */
    fun quotas(catalog: SocialCatalog, s: SocialSettings): String {
        val perPlatform = catalog.platforms.filter { it.id in s.enabledSince }.flatMap { p -> caps(p, s).map { (k, v) -> "${p.id}.$k=$v" } }
        return (perPlatform + catalog.fallbackCaps.split(',').filter { it.isNotBlank() }).joinToString(",")
    }

    /** Content of social/PLATFORMS.json that every agent reads at the start of a run. */
    fun platformsJson(catalog: SocialCatalog, s: SocialSettings): String {
        val enabled = catalog.platforms.filter { it.id in s.enabledSince }
        val o = JSONObject()
            .put("enabled", JSONArray(enabled.map { it.id }))
            .put("enabled_since", JSONObject(enabled.associate { it.id to s.enabledSince.getValue(it.id) }))
            .put("caps", JSONObject(enabled.associate { p -> p.id to JSONObject(caps(p, s)) }))
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
