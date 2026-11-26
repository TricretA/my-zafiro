package com.tricreta.zafiro.autonomy.core.signals

/**
 * Decides what a notification is and which platform it belongs to. Pure functions over data tables, so every rule is
 * unit-tested and the keyword table can be replaced from settings without touching code.
 */
object SignalClassifier {

    /** Package -> platform id. Platform ids match workspace ids where a workspace exists. Entries ending ".*" are prefixes. */
    val PLATFORM_BY_PACKAGE: Map<String, String> = linkedMapOf(
        "com.substack.app" to "substack",
        "com.instagram.android" to "instagram",
        "com.instagram.barcelona" to "threads",
        "com.zhiliaoapp.musically" to "tiktok",
        "com.ss.android.ugc.trill" to "tiktok",
        "com.google.android.youtube" to "youtube",
        "com.twitter.android" to "x",
        "com.linkedin.android" to "linkedin",
        "com.facebook.katana" to "facebook",
        "com.facebook.orca" to "facebook",
        "com.reddit.frontpage" to "reddit",
        "com.pinterest" to "pinterest",
        "com.snapchat.android" to "snapchat",
        "org.telegram.messenger" to "telegram",
        "com.discord" to "discord",
        "tv.twitch.android.app" to "twitch",
        "com.whatsapp" to "whatsapp",
        "com.whatsapp.w4b" to "whatsapp",
        "com.google.android.gm" to "email",
        "com.metricool.*" to "metricool",
    )

    /** Platform id -> the workspace (department) that owns it, for platforms that have one. */
    val WORKSPACE_BY_PLATFORM: Map<String, String> = mapOf(
        "substack" to "substack", "instagram" to "instagram", "threads" to "threads", "tiktok" to "tiktok",
        "youtube" to "youtube", "x" to "x", "linkedin" to "linkedin", "facebook" to "facebook", "reddit" to "reddit",
        "pinterest" to "pinterest", "whatsapp" to "whatsapp", "email" to "email",
    )

    /** Chat apps: their message text is conversation, not an activity feed, so engagement keywords never apply to it. */
    val CHAT_PLATFORMS = setOf("whatsapp", "telegram", "discord", "snapchat")

    /** Apps where the owner can command the team by writing to it. */
    val OWNER_COMMAND_PACKAGES = setOf("com.whatsapp", "com.whatsapp.w4b", "org.telegram.messenger")

    val EMAIL_PACKAGES = setOf("com.google.android.gm")

    /** English phrases that mean somebody did something on the owner's content. Matched on word boundaries, case-insensitive. */
    val DEFAULT_ENGAGEMENT_KEYWORDS = listOf(
        "commented", "comment on", "comments on", "replied", "replies", "reply to", "liked", "likes your", "loved your",
        "followed you", "started following", "new follower", "follows you", "followed", "mentioned you", "mentioned", "tagged you",
        "restacked", "restack", "reposted", "repost", "shared your", "subscribed", "new subscriber", "subscriber",
        "sent you a message", "direct message", "new message from", "dm", "reacted to", "upvoted", "new comment", "new reply",
    )

    fun platformOf(pkg: String): String {
        PLATFORM_BY_PACKAGE[pkg]?.let { return it }
        for ((k, v) in PLATFORM_BY_PACKAGE) {
            if (k.endsWith(".*") && pkg.startsWith(k.dropLast(1))) return v
        }
        return "other"
    }

    fun classify(raw: RawNotification, config: SignalConfig, receivedMs: Long): Signal {
        val platform = platformOf(raw.packageName)
        val kind = kindOf(raw, platform, config)
        return Signal(
            receivedMs = receivedMs, whenMs = raw.whenMs, app = raw.packageName, platform = platform, kind = kind,
            title = raw.title.take(MAX_TITLE), text = raw.text.take(MAX_TEXT), subText = raw.subText.take(MAX_TITLE),
            sender = raw.sender.take(MAX_TITLE), conversation = raw.conversation.take(MAX_TITLE),
        )
    }

    fun kindOf(raw: RawNotification, platform: String, config: SignalConfig): SignalKind {
        if (raw.packageName in OWNER_COMMAND_PACKAGES && !raw.isGroup && isOwner(raw, config.ownerSenderNames)) return SignalKind.OWNER_MESSAGE
        if (raw.packageName in EMAIL_PACKAGES) return SignalKind.EMAIL
        if (platform in CHAT_PLATFORMS) return SignalKind.OTHER
        val keywords = config.engagementKeywords.ifEmpty { DEFAULT_ENGAGEMENT_KEYWORDS }
        val haystack = listOf(raw.title, raw.text, raw.subText).joinToString(" ").lowercase()
        return if (keywords.any { containsWord(haystack, it.lowercase()) }) SignalKind.ENGAGEMENT else SignalKind.OTHER
    }

    /**
     * The sender must equal a configured name exactly (ignoring case, spacing and a leading "~"), or, for a configured phone
     * number, have the same digits. Substring matching is deliberately not used: a stranger can choose a display name that
     * contains the owner's name, but cannot make a saved contact or a different number equal it.
     */
    fun isOwner(raw: RawNotification, ownerNames: List<String>): Boolean {
        val who = raw.sender.ifBlank { raw.title }
        if (who.isBlank() || ownerNames.isEmpty()) return false
        val whoName = normName(who)
        val whoDigits = digits(who)
        return ownerNames.any { cfg ->
            val cfgDigits = digits(cfg)
            val isNumber = cfgDigits.length >= MIN_NUMBER_DIGITS && cfg.count { it.isLetter() } == 0
            if (isNumber) {
                whoDigits.length >= MIN_NUMBER_DIGITS && who.count { it.isLetter() } == 0 &&
                    (whoDigits == cfgDigits || (whoDigits.length >= 9 && cfgDigits.length >= 9 && whoDigits.takeLast(9) == cfgDigits.takeLast(9)))
            } else {
                normName(cfg).isNotEmpty() && normName(cfg) == whoName
            }
        }
    }

    private fun normName(s: String) = s.trim().trimStart('~').trim().lowercase().replace(Regex("\\s+"), " ")
    private fun digits(s: String) = s.filter { it.isDigit() }

    internal fun containsWord(haystack: String, needle: String): Boolean {
        if (needle.isBlank()) return false
        var from = 0
        while (true) {
            val at = haystack.indexOf(needle, from)
            if (at < 0) return false
            val before = if (at == 0) ' ' else haystack[at - 1]
            val afterIdx = at + needle.length
            val after = if (afterIdx >= haystack.length) ' ' else haystack[afterIdx]
            if (!before.isLetterOrDigit() && !after.isLetterOrDigit()) return true
            from = at + 1
        }
    }

    const val MAX_TITLE = 200
    const val MAX_TEXT = 600
    private const val MIN_NUMBER_DIGITS = 7
}
