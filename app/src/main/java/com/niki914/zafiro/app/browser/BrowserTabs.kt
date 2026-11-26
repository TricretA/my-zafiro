package com.niki914.zafiro.app.browser

/** Which tabs one browser screen has and which is active. Holds handles only; creating and destroying views is the caller's job. */
class TabManager<T>(val maxTabs: Int) {
    data class Entry<T>(val id: String, val handle: T)

    private val items = ArrayList<Entry<T>>()
    private var counter = 0
    private var activeId: String? = null

    val entries: List<Entry<T>> get() = items.toList()
    val active: Entry<T>? get() = items.firstOrNull { it.id == activeId }
    val size: Int get() = items.size

    /** Adds a tab and makes it active. Null when the screen already has [maxTabs]. */
    fun add(handle: T): Entry<T>? {
        if (items.size >= maxTabs) return null
        counter++
        val entry = Entry("t$counter", handle)
        items += entry
        activeId = entry.id
        return entry
    }

    fun get(id: String): Entry<T>? = items.firstOrNull { it.id == id }

    fun switchTo(id: String): Boolean {
        if (get(id) == null) return false
        activeId = id
        return true
    }

    /** Removes the tab. If it was active, the tab before it (else the one after) becomes active. */
    fun close(id: String): Entry<T>? {
        val at = items.indexOfFirst { it.id == id }
        if (at < 0) return null
        val removed = items.removeAt(at)
        if (activeId == id) {
            activeId = items.getOrNull(at - 1)?.id ?: items.getOrNull(at)?.id
        }
        return removed
    }

    fun clear(): List<Entry<T>> {
        val all = items.toList()
        items.clear()
        activeId = null
        return all
    }
}

/** Turns what an agent or the address bar typed into a URL the browser will load, or null when it must be refused. */
object WebUrls {
    private val SCHEME = Regex("^([a-zA-Z][a-zA-Z0-9+.-]*):")
    const val SEARCH = "https://duckduckgo.com/?q="

    fun normalize(input: String): String? {
        val text = input.trim()
        if (text.isEmpty()) return null
        if (text.equals("about:blank", ignoreCase = true)) return "about:blank"
        val scheme = SCHEME.find(text)?.groupValues?.get(1)?.lowercase()
        if (scheme != null && !text.startsWith("localhost:", ignoreCase = true) && !Regex("^[^/\\s]+:\\d+").containsMatchIn(text)) {
            return if ((scheme == "http" || scheme == "https") && text.contains("://")) text else null
        }
        val looksLikeHost = !text.contains(' ') && (text.contains('.') || text.startsWith("localhost", ignoreCase = true))
        if (looksLikeHost) return "https://$text"
        return SEARCH + java.net.URLEncoder.encode(text, "UTF-8")
    }

    /** Navigations inside a page: only http(s) and about:blank stay in the browser; every other scheme is blocked. */
    fun allowedNavigation(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.startsWith("http://") || lower.startsWith("https://") || lower == "about:blank"
    }
}
