package com.niki914.zafiro.app.browser

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JsPromptResult
import android.content.Context
import android.widget.FrameLayout
import com.niki914.logging.Logger
import com.niki914.zafiro.app.screen.applicationContext
import com.niki914.zafiro.app.screen.virtualDisplaySize
import com.niki914.zafiro.business.permission.Permission
import com.niki914.zafiro.business.permission.PermissionManager
import com.niki914.zafiro.business.permission.PermissionState
import com.niki914.zafiro.service.findService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.CopyOnWriteArrayList

/** One WebView and what the page last told us. Touched on the main thread only. */
internal class BrowserTab {
    lateinit var webView: WebView
    var id: String = ""
    var loading = false
    var injected = false
    var dead = false
    var title = ""
    private var dialog = ""

    fun noteDialog(text: String) {
        dialog = text.take(200)
    }

    fun takeDialog(): String = dialog.also { dialog = "" }
}

class BrowserActionException(val code: String) : RuntimeException(code)

data class TabOverview(val screenId: String, val tabId: String, val title: String, val url: String, val active: Boolean)

/**
 * Owns every WebView of the agent browser and the one surface they live on.
 *
 * Surface, in order of preference:
 * 1. the "Agent browser" page, while the owner has it open (the container is moved into the page);
 * 2. a full-size, almost transparent, untouchable overlay window, when "Display over other apps" is granted: this is what
 *    lets the browser keep rendering while another app is in front during an unattended run;
 * 3. no window at all: the container is measured by hand. JavaScript and loading work, drawing and screenshots may not.
 */
internal object WebBrowserHost {
    private const val TAG = "niki914_zafiro_WebBrowserHost"
    const val MAX_WEBVIEWS = 4

    private val sessions = CopyOnWriteArrayList<WebBrowserSession>()
    private var container: FrameLayout? = null
    private var pageParent: ViewGroup? = null
    private var overlayAttached = false
    private var webViewCount = 0

    private val _overview = MutableStateFlow<List<TabOverview>>(emptyList())
    val overview: StateFlow<List<TabOverview>> = _overview.asStateFlow()

    private val _surface = MutableStateFlow(Surface.NONE)
    val surface: StateFlow<Surface> = _surface.asStateFlow()

    enum class Surface { NONE, PAGE, OVERLAY, DETACHED }

    fun register(session: WebBrowserSession) {
        sessions += session
        publish()
    }

    fun unregister(session: WebBrowserSession) {
        sessions -= session
        publish()
        if (webViewCount == 0) releaseSurface()
    }

    fun canCreateWebView(): Boolean = webViewCount < MAX_WEBVIEWS

    fun displaySize(): Pair<Int, Int> {
        val (w, h, _) = virtualDisplaySize(applicationContext())
        return w to h
    }

    private fun container(): FrameLayout {
        container?.let { return it }
        val c = FrameLayout(applicationContext())
        c.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        container = c
        return c
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun newWebView(tab: BrowserTab): WebView {
        val context = applicationContext()
        val wv = WebView(context)
        webViewCount++
        tab.webView = wv
        with(wv.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = false
            mediaPlaybackRequiresUserGesture = true
            setGeolocationEnabled(false)
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            safeBrowsingEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        applyUserAgent(wv)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)
        wv.setDownloadListener { url, _, _, _, _ -> tab.noteDialog("download blocked: $url") }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                tab.title = title.orEmpty()
                publish()
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult): Boolean {
                tab.noteDialog("alert: $message")
                result.confirm()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult): Boolean {
                tab.noteDialog("confirm declined: $message")
                result.cancel()
                return true
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult): Boolean {
                tab.noteDialog("prompt declined: $message")
                result.cancel()
                return true
            }

            override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult): Boolean {
                result.confirm()
                return true
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<android.net.Uri>>?,
                fileChooserParams: FileChooserParams?,
            ): Boolean {
                tab.noteDialog("file chooser blocked")
                filePathCallback?.onReceiveValue(null)
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                request.deny()
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                callback?.invoke(origin, false, false)
            }
        }
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (WebUrls.allowedNavigation(url)) return false
                tab.noteDialog("blocked non-web link: ${request.url.scheme}")
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                tab.loading = true
                tab.injected = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                tab.loading = false
                runCatching { CookieManager.getInstance().flush() }
                publish()
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) tab.noteDialog("page failed: ${error.description}")
            }

            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                Logger.w(TAG, "render process gone crashed=${detail.didCrash()}")
                tab.dead = true
                tab.loading = false
                destroyWebView(view)
                return true
            }
        }
        container().addView(wv, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        ensureSurface()
        return wv
    }

    fun destroyWebView(wv: WebView) {
        runCatching {
            wv.stopLoading()
            (wv.parent as? ViewGroup)?.removeView(wv)
            wv.destroy()
        }
        webViewCount = (webViewCount - 1).coerceAtLeast(0)
    }

    fun applyUserAgent(wv: WebView) {
        wv.settings.userAgentString = if (BrowserPrefs.isDesktop()) desktopUserAgent() else null
    }

    private fun desktopUserAgent(): String {
        val default = WebSettings.getDefaultUserAgent(applicationContext())
        val chrome = Regex("Chrome/([\\d.]+)").find(default)?.groupValues?.get(1) ?: "120.0.0.0"
        return "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/$chrome Safari/537.36"
    }

    fun setDesktopMode(enabled: Boolean) {
        BrowserPrefs.setDesktop(enabled)
        sessions.forEach { it.reapplyUserAgent() }
    }

    fun clearData(): Unit {
        val cookies = CookieManager.getInstance()
        cookies.removeAllCookies(null)
        cookies.flush()
        WebStorage.getInstance().deleteAllData()
        sessions.forEach { it.clearCaches() }
    }

    fun closeAll() {
        sessions.toList().forEach { it.destroy() }
    }

    fun publish() {
        _overview.value = sessions.flatMap { it.overview() }
    }

    // ------------------------------------------------------------------ surface

    private fun overlayGranted(): Boolean {
        val manager = findService<PermissionManager>() ?: return false
        return manager.status(Permission.OVERLAY) == PermissionState.GRANTED
    }

    fun ensureSurface() {
        val c = container()
        if (pageParent != null) {
            _surface.value = Surface.PAGE
            return
        }
        if (overlayAttached) {
            _surface.value = Surface.OVERLAY
            return
        }
        if (overlayGranted() && addOverlay(c)) {
            _surface.value = Surface.OVERLAY
            return
        }
        val (w, h) = displaySize()
        c.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
        c.layout(0, 0, w, h)
        _surface.value = Surface.DETACHED
    }

    private fun addOverlay(c: FrameLayout): Boolean {
        val context = applicationContext()
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return false
        val (w, h) = displaySize()
        val lp = WindowManager.LayoutParams(
            w,
            h,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            alpha = 0.01f
            windowAnimations = 0
        }
        return try {
            (c.parent as? ViewGroup)?.removeView(c)
            manager.addView(c, lp)
            overlayAttached = true
            true
        } catch (error: Exception) {
            Logger.w(TAG, "overlay window not added: ${error.message}")
            false
        }
    }

    private fun removeOverlay() {
        val c = container ?: return
        if (!overlayAttached) return
        val manager = applicationContext().getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        runCatching { manager?.removeViewImmediate(c) }
        overlayAttached = false
    }

    private fun releaseSurface() {
        removeOverlay()
        (container?.parent as? ViewGroup)?.removeView(container)
        _surface.value = Surface.NONE
    }

    /** The "Agent browser" page takes the container while it is on screen. Main thread. */
    fun attachToPage(parent: FrameLayout) {
        val c = container()
        removeOverlay()
        (c.parent as? ViewGroup)?.removeView(c)
        parent.addView(c, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        pageParent = parent
        _surface.value = Surface.PAGE
    }

    fun detachFromPage(parent: FrameLayout) {
        if (pageParent !== parent) return
        pageParent = null
        container?.let { parent.removeView(it) }
        if (webViewCount > 0) ensureSurface() else _surface.value = Surface.NONE
    }
}
