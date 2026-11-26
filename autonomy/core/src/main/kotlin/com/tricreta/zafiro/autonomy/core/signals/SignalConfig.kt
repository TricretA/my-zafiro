package com.tricreta.zafiro.autonomy.core.signals

/**
 * Owner-editable settings for the realtime signals feature. Stored as one small JSON file by the app
 * (filesDir/signals/config.json); this class only knows how to read, write and apply it.
 */
data class SignalConfig(
    val enabled: Boolean = true,
    /** Package names to capture. An entry ending in ".*" matches that prefix (e.g. "com.metricool.*"). */
    val allowedPackages: List<String> = DEFAULT_PACKAGES,
    /** Names or phone numbers the owner's own WhatsApp/Telegram account shows up as. Only these senders can give commands. */
    val ownerSenderNames: List<String> = emptyList(),
    /** Optional replacement for the built-in engagement keyword table; blank means use [SignalClassifier.DEFAULT_ENGAGEMENT_KEYWORDS]. */
    val engagementKeywords: List<String> = emptyList(),
) {
    fun allows(pkg: String): Boolean = allowedPackages.any { rule ->
        val r = rule.trim()
        if (r.endsWith(".*")) pkg.startsWith(r.dropLast(1)) || pkg == r.dropLast(2) else pkg == r
    }

    fun toJson(): String = MiniJson.write(
        linkedMapOf(
            "enabled" to enabled, "allowed_packages" to allowedPackages, "owner_sender_names" to ownerSenderNames,
            "engagement_keywords" to engagementKeywords,
        ),
    )

    companion object {
        val DEFAULT_PACKAGES = listOf(
            "com.substack.app", "com.instagram.android", "com.zhiliaoapp.musically", "com.ss.android.ugc.trill",
            "com.google.android.youtube", "com.twitter.android", "com.linkedin.android", "com.facebook.katana",
            "com.facebook.orca", "com.reddit.frontpage", "com.instagram.barcelona", "com.pinterest", "com.snapchat.android",
            "org.telegram.messenger", "com.discord", "tv.twitch.android.app", "com.whatsapp", "com.whatsapp.w4b",
            "com.google.android.gm", "com.metricool.*",
        )

        fun fromJson(text: String?): SignalConfig {
            val m = text?.let { MiniJson.parseObject(it) } ?: return SignalConfig()
            fun list(k: String): List<String>? = (m[k] as? List<*>)?.mapNotNull { (it as? String)?.trim()?.takeIf(String::isNotEmpty) }
            return SignalConfig(
                enabled = m["enabled"] as? Boolean ?: true,
                allowedPackages = list("allowed_packages") ?: DEFAULT_PACKAGES,
                ownerSenderNames = list("owner_sender_names") ?: emptyList(),
                engagementKeywords = list("engagement_keywords") ?: emptyList(),
            )
        }

        /** One entry per line (or comma), trimmed, blanks and duplicates dropped; what the settings text boxes use. */
        fun parseLines(text: String): List<String> = text.split('\n', ',', ';').map { it.trim() }.filter { it.isNotEmpty() }.distinct()
    }
}
