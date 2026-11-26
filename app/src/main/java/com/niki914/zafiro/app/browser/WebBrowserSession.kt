package com.niki914.zafiro.app.browser

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.webkit.WebView
import com.niki914.zafiro.api.screen.ImageFrame
import com.niki914.zafiro.api.screen.NodeAction
import com.niki914.zafiro.api.screen.NodeInfo
import com.niki914.zafiro.api.screen.PointerAction
import com.niki914.zafiro.api.screen.ScreenId
import com.niki914.zafiro.api.screen.ScreenImageUnavailable
import com.niki914.zafiro.api.screen.SemanticToken
import com.niki914.zafiro.api.screen.TabInfo
import com.niki914.zafiro.api.screen.BrowserSession
import com.niki914.zafiro.app.screen.applicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonPrimitive
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

/**
 * The tabs of one browser screen. Every method hops to the main thread, where WebView must be used.
 * Failures are thrown as [BrowserActionException] and turned into result codes by the screen.
 */
internal class WebBrowserSession(private val screenId: ScreenId) : BrowserSession {
    private val tabs = TabManager<BrowserTab>(MAX_TABS_PER_SCREEN)

    @Volatile
    var lastPage: WebPage? = null
        private set

    @Volatile
    private var currentUrl: String = ""

    override val url: String get() = currentUrl

    init {
        WebBrowserHost.register(this)
    }

    private suspend fun <T> main(block: suspend () -> T): T = withContext(Dispatchers.Main.immediate) { block() }

    // ------------------------------------------------------------------ script plumbing

    private suspend fun js(tab: BrowserTab, script: String): String? = withTimeoutOrNull(JS_TIMEOUT_MS) {
        suspendCancellableCoroutine { cont ->
            tab.webView.evaluateJavascript(script) { cont.resume(it) }
        }
    }

    private suspend fun call(tab: BrowserTab, expression: String): String? {
        if (!tab.injected) {
            js(tab, scriptSource())
            tab.injected = true
        }
        var raw = js(tab, expression)
        if (WebSnapshotParser.unwrapJsResult(raw) == null) {
            js(tab, scriptSource())
            raw = js(tab, expression)
        }
        return raw
    }

    private suspend fun runAction(tab: BrowserTab, expression: String) {
        val answer = WebSnapshotParser.parseAction(call(tab, expression)) ?: throw BrowserActionException("SCRIPT_UNAVAILABLE")
        if (!answer.ok) throw BrowserActionException(answer.code ?: "ACTION_FAILED")
    }

    // ------------------------------------------------------------------ tabs

    private fun revive(entry: TabManager.Entry<BrowserTab>): BrowserTab {
        val tab = entry.handle
        if (tab.dead) {
            val last = currentUrl
            WebBrowserHost.newWebView(tab)
            tab.dead = false
            tab.injected = false
            applyVisibility()
            if (last.isNotBlank()) tab.webView.loadUrl(last)
        }
        return tab
    }

    private fun createTab(): TabManager.Entry<BrowserTab> {
        if (tabs.size >= MAX_TABS_PER_SCREEN || !WebBrowserHost.canCreateWebView()) throw BrowserActionException("TAB_LIMIT")
        val tab = BrowserTab()
        WebBrowserHost.newWebView(tab)
        val entry = tabs.add(tab) ?: run {
            WebBrowserHost.destroyWebView(tab.webView)
            throw BrowserActionException("TAB_LIMIT")
        }
        tab.id = entry.id
        applyVisibility()
        return entry
    }

    private fun activeEntry(): TabManager.Entry<BrowserTab> = tabs.active ?: createTab()

    private fun applyVisibility() {
        for (entry in tabs.entries) {
            val tab = entry.handle
            if (tab.dead) continue
            val active = entry.id == tabs.active?.id
            tab.webView.visibility = if (active) View.VISIBLE else View.INVISIBLE
            if (active) tab.webView.onResume() else tab.webView.onPause()
        }
        WebBrowserHost.publish()
    }

    fun overview(): List<TabOverview> = tabs.entries.map {
        TabOverview(screenId.value, it.id, it.handle.title, if (it.handle.dead) "" else it.handle.webView.url.orEmpty(), it.id == tabs.active?.id)
    }

    /** The visible WebView, for the owner's page. Main thread. */
    fun activeWebView(): WebView? = tabs.active?.handle?.takeIf { !it.dead }?.webView

    override suspend fun tabs(): List<TabInfo> = main { tabInfos() }

    private fun tabInfos(): List<TabInfo> = tabs.entries.map {
        val tab = it.handle
        TabInfo(it.id, if (tab.dead) "" else tab.webView.url.orEmpty(), tab.title, it.id == tabs.active?.id)
    }

    override suspend fun newTab(url: String?): TabInfo = main {
        val target = url?.let { WebUrls.normalize(it) ?: throw BrowserActionException("INVALID_URL") }
        val entry = createTab()
        if (target != null) startLoad(entry.handle, target) else currentUrl = ""
        tabInfos().first { it.tabId == entry.id }
    }

    override suspend fun closeTab(tabId: String): Unit = main {
        val removed = tabs.close(tabId) ?: throw BrowserActionException("UNKNOWN_TAB")
        if (!removed.handle.dead) WebBrowserHost.destroyWebView(removed.handle.webView)
        currentUrl = activeWebView()?.url.orEmpty()
        applyVisibility()
    }

    override suspend fun switchTab(tabId: String): Unit = main {
        if (!tabs.switchTo(tabId)) throw BrowserActionException("UNKNOWN_TAB")
        val tab = revive(tabs.active!!)
        currentUrl = tab.webView.url.orEmpty()
        applyVisibility()
        tab.webView.bringToFront()
    }

    // ------------------------------------------------------------------ navigation

    private fun startLoad(tab: BrowserTab, url: String) {
        tab.loading = true
        tab.injected = false
        currentUrl = url
        tab.webView.loadUrl(url)
    }

    override suspend fun load(url: String): Unit = main {
        val target = WebUrls.normalize(url) ?: throw BrowserActionException("INVALID_URL")
        val tab = revive(activeEntry())
        startLoad(tab, target)
        settle()
    }

    override suspend fun history(forward: Boolean): Unit = main {
        val tab = revive(activeEntry())
        val ok = if (forward) tab.webView.canGoForward() else tab.webView.canGoBack()
        if (!ok) throw BrowserActionException(if (forward) "NO_FORWARD_HISTORY" else "NO_BACK_HISTORY")
        tab.loading = true
        tab.injected = false
        if (forward) tab.webView.goForward() else tab.webView.goBack()
        settle()
    }

    /** Lets a click or a load finish: starts a beat late (navigations begin asynchronously), waits while loading, then lets scripts paint. */
    suspend fun settle() = main {
        val tab = tabs.active?.handle ?: return@main
        delay(150)
        var waited = 0L
        while (tab.loading && waited < LOAD_TIMEOUT_MS) {
            delay(100)
            waited += 100
        }
        delay(250)
        currentUrl = if (tab.dead) currentUrl else tab.webView.url.orEmpty()
    }

    // ------------------------------------------------------------------ reading

    private fun deviceWidth(tab: BrowserTab): Int =
        tab.webView.width.takeIf { it > 0 } ?: WebBrowserHost.displaySize().first

    override suspend fun document(): List<NodeInfo> = page().nodes

    /** The page with nodes in device pixels. A session with no tab yet reads as a blank page. */
    suspend fun page(): WebPage = main {
        val entry = tabs.active
        if (entry == null) {
            return@main WebPage("", "", 0, 0, 0, 0, false, "", emptyList()).also { lastPage = it }
        }
        val tab = revive(entry)
        val raw = WebSnapshotParser.unwrapJsResult(call(tab, "window.__zb.snapshot()")) ?: throw BrowserActionException("SNAPSHOT_FAILED")
        val parsed = WebSnapshotParser.parse(raw, deviceWidth(tab)) ?: throw BrowserActionException("SNAPSHOT_FAILED")
        currentUrl = parsed.url
        parsed.copy(dialog = tab.takeDialog()).also { lastPage = it }
    }

    fun viewportSize(): Pair<Int, Int> {
        val view = activeWebView()
        if (view != null && view.width > 0 && view.height > 0) return view.width to view.height
        return WebBrowserHost.displaySize()
    }

    override suspend fun frame(): ImageFrame {
        val (bitmap, w, h) = main {
            val view = activeWebView() ?: throw ScreenImageUnavailable("NO_PAGE", "The browser has no open page.")
            WebBrowserHost.ensureSurface()
            val (width, height) = if (view.width > 0 && view.height > 0) view.width to view.height else WebBrowserHost.displaySize()
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bmp))
            Triple(bmp, width, height)
        }
        val png = withContext(Dispatchers.Default) {
            ByteArrayOutputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.toByteArray()
            }
        }
        bitmap.recycle()
        return ImageFrame(screenId, w, h, png)
    }

    // ------------------------------------------------------------------ acting

    override suspend fun perform(token: SemanticToken, action: NodeAction, text: String?): Unit = main {
        val tab = revive(activeEntry())
        val i = token.index
        val expression = when (action) {
            NodeAction.Click -> "window.__zb.tap($i)"
            NodeAction.LongClick -> "window.__zb.longTap($i)"
            NodeAction.SetText -> "window.__zb.setText($i, ${JsonPrimitive(text.orEmpty())})"
            NodeAction.ScrollForward -> "window.__zb.scrollNode($i, true)"
            NodeAction.ScrollBackward -> "window.__zb.scrollNode($i, false)"
        }
        runAction(tab, expression)
    }

    /** Device pixels per css pixel as of the last snapshot, so `x`/`y` from the model land where the page drew the element. */
    private fun cssScale(tab: BrowserTab): Double {
        val vw = lastPage?.viewportCssWidth ?: 0
        return if (vw > 0) deviceWidth(tab).toDouble() / vw else applicationContext().resources.displayMetrics.density.toDouble()
    }

    override suspend fun hit(action: PointerAction): Unit = main {
        val tab = revive(activeEntry())
        val s = cssScale(tab)
        when (action) {
            is PointerAction.Tap -> runAction(tab, "window.__zb.tapAt(${action.x / s}, ${action.y / s}, false)")
            is PointerAction.LongClick -> runAction(tab, "window.__zb.tapAt(${action.x / s}, ${action.y / s}, true)")
            is PointerAction.Swipe -> runAction(
                tab,
                "window.__zb.scrollAt(${action.x1 / s}, ${action.y1 / s}, ${(action.x1 - action.x2) / s}, ${(action.y1 - action.y2) / s})",
            )
            is PointerAction.Key -> when (val name = keyName(action.code)) {
                "Back" -> history(false)
                null -> throw BrowserActionException("UNSUPPORTED_KEY")
                else -> runAction(tab, "window.__zb.key('$name')")
            }
        }
    }

    // ------------------------------------------------------------------ housekeeping

    fun reapplyUserAgent() {
        for (entry in tabs.entries) {
            if (entry.handle.dead) continue
            WebBrowserHost.applyUserAgent(entry.handle.webView)
            entry.handle.webView.reload()
        }
    }

    fun clearCaches() {
        for (entry in tabs.entries) {
            if (entry.handle.dead) continue
            entry.handle.webView.clearCache(true)
            entry.handle.webView.clearFormData()
        }
    }

    suspend fun close(): Unit = main { destroy() }

    /** Main thread. */
    fun destroy() {
        for (entry in tabs.clear()) {
            if (!entry.handle.dead) WebBrowserHost.destroyWebView(entry.handle.webView)
        }
        WebBrowserHost.unregister(this)
    }

    companion object {
        const val MAX_TABS_PER_SCREEN = 3
        private const val JS_TIMEOUT_MS = 10_000L
        private const val LOAD_TIMEOUT_MS = 20_000L

        @Volatile
        private var script: String? = null

        private fun scriptSource(): String = script ?: applicationContext().assets.open("browser/zbrowser.js")
            .bufferedReader().use { it.readText() }.also { script = it }

        /** Android key codes the page can act on. */
        fun keyName(code: Int): String? = when (code) {
            4 -> "Back"
            66 -> "Enter"
            111 -> "Escape"
            61 -> "Tab"
            92 -> "PageUp"
            93 -> "PageDown"
            else -> null
        }
    }
}
