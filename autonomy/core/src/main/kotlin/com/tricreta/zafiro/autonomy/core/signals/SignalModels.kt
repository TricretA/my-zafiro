package com.tricreta.zafiro.autonomy.core.signals

/** What a captured notification means for the team. */
enum class SignalKind { OWNER_MESSAGE, ENGAGEMENT, EMAIL, OTHER }

/** A notification as Android handed it over, before any judgement. All text is untrusted third-party data. */
data class RawNotification(
    val packageName: String,
    val title: String = "",
    val text: String = "",
    val subText: String = "",
    val whenMs: Long = 0L,
    /** The person who wrote the message, when the notification is a messaging style one; blank otherwise. */
    val sender: String = "",
    /** The chat or thread name, when known. */
    val conversation: String = "",
    val isGroup: Boolean = false,
)

/** One captured, classified notification. [id] is assigned by the store. */
data class Signal(
    val id: Long = 0,
    val receivedMs: Long,
    val whenMs: Long,
    val app: String,
    val platform: String,
    val kind: SignalKind,
    val title: String,
    val text: String,
    val subText: String,
    val sender: String,
    val conversation: String,
    val acked: Boolean = false,
) {
    /** Identity for duplicate detection: the same notification re-posted by the app carries the same key. */
    val dedupeKey: String get() = "$app|$sender|$title|$text|$whenMs"

    fun toMap(): Map<String, Any?> = linkedMapOf(
        "id" to id, "rx" to receivedMs, "when" to whenMs, "app" to app, "platform" to platform, "kind" to kind.name,
        "title" to title, "text" to text, "sub" to subText, "sender" to sender, "conv" to conversation, "acked" to acked,
    )

    companion object {
        fun fromMap(m: Map<String, Any?>): Signal? {
            val kind = (m["kind"] as? String)?.let { k -> SignalKind.entries.firstOrNull { it.name == k } } ?: return null
            return Signal(
                id = (m["id"] as? Number)?.toLong() ?: return null,
                receivedMs = (m["rx"] as? Number)?.toLong() ?: 0L,
                whenMs = (m["when"] as? Number)?.toLong() ?: 0L,
                app = m["app"] as? String ?: return null,
                platform = m["platform"] as? String ?: "other",
                kind = kind,
                title = m["title"] as? String ?: "",
                text = m["text"] as? String ?: "",
                subText = m["sub"] as? String ?: "",
                sender = m["sender"] as? String ?: "",
                conversation = m["conv"] as? String ?: "",
                acked = m["acked"] as? Boolean ?: false,
            )
        }
    }
}
