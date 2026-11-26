package com.niki914.zafiro.app.browser

import com.niki914.zafiro.accessibility.table.DisplayNodeTable
import com.niki914.zafiro.api.screen.ActResult
import com.niki914.zafiro.api.screen.BrowserScreen
import com.niki914.zafiro.api.screen.ImageFrame
import com.niki914.zafiro.api.screen.NodeAction
import com.niki914.zafiro.api.screen.PointerAction
import com.niki914.zafiro.api.screen.ScreenAction
import com.niki914.zafiro.api.screen.ScreenId
import com.niki914.zafiro.api.screen.ScreenInfo
import com.niki914.zafiro.api.screen.ScreenKind
import com.niki914.zafiro.api.screen.ScreenSnapshot
import com.niki914.zafiro.api.screen.ShowResult
import com.niki914.zafiro.api.screen.ShowTarget
import com.niki914.zafiro.api.screen.TabInfo
import com.niki914.zafiro.api.screen.Target
import kotlinx.coroutines.CancellationException

/**
 * A browser surface the agent drives like a screen: [snapshot] is the page as numbered nodes (same compact text as the phone),
 * actions return the next snapshot. Owned by one conversation, or by nobody when the owner opened it from the app.
 */
internal class WebBrowserScreen(
    number: Int,
    @Volatile var ownerConversationId: String? = null,
) : BrowserScreen {
    override val id: ScreenId = ScreenId("web-$number")
    override val kind: ScreenKind = ScreenKind.Browser
    override val session = WebBrowserSession(id)
    override val nodes = DisplayNodeTable()

    fun info(): ScreenInfo {
        val (w, h) = session.viewportSize()
        return ScreenInfo(id, kind, w, h)
    }

    override suspend fun snapshot(): ScreenSnapshot {
        if (!BrowserPrefs.isEnabled()) throw com.niki914.zafiro.api.screen.ScreenTreeUnavailable("BROWSER_DISABLED")
        val page = try {
            session.page()
        } catch (error: BrowserActionException) {
            throw com.niki914.zafiro.api.screen.ScreenTreeUnavailable(error.code)
        }
        val version = nodes.mintVersion()
        nodes.remember(version, page.nodes)
        val (w, h) = session.viewportSize()
        return ScreenSnapshot(
            screen = id,
            version = version,
            width = w,
            height = h,
            url = page.url.ifBlank { null },
            nodes = page.nodes,
            yaml = WebYaml.build(page, id.value, w, h, version),
        )
    }

    override suspend fun image(): ImageFrame = session.frame()

    override suspend fun act(action: ScreenAction): ActResult {
        if (!BrowserPrefs.isEnabled()) return ActResult.Failed("BROWSER_DISABLED", null)
        return try {
            val tabAction = when (action) {
                is ScreenAction.NewTab, is ScreenAction.CloseTab, is ScreenAction.SwitchTab, ScreenAction.ListTabs -> true
                else -> false
            }
            run(action)
            if (action != ScreenAction.ListTabs) session.settle()
            val snap = snapshot()
            val tabs = session.tabs()
            ActResult.Done(snap, tabs.takeIf { tabAction || it.size > 1 })
        } catch (error: CancellationException) {
            throw error
        } catch (error: BrowserActionException) {
            ActResult.Failed(error.code, runCatching { snapshot() }.getOrNull())
        } catch (error: com.niki914.zafiro.api.screen.ScreenTreeUnavailable) {
            ActResult.Failed(error.message ?: "SNAPSHOT_FAILED", null)
        }
    }

    private suspend fun run(action: ScreenAction) {
        when (action) {
            is ScreenAction.Tap -> onTarget(action.target, NodeAction.Click) { x, y -> session.hit(PointerAction.Tap(x, y)) }
            is ScreenAction.LongClick -> onTarget(action.target, NodeAction.LongClick) { x, y -> session.hit(PointerAction.LongClick(x, y)) }
            is ScreenAction.Scroll -> when (val target = action.target) {
                is Target.Node -> {
                    requireNode(target)
                    session.perform(target.token, if (action.forward) NodeAction.ScrollForward else NodeAction.ScrollBackward, null)
                }
                is Target.Point -> {
                    val dy = if (action.forward) -300 else 300
                    session.hit(PointerAction.Swipe(target.x, target.y, target.x, target.y + dy, 300L))
                }
            }
            is ScreenAction.SetText -> when (val target = action.target) {
                is Target.Node -> {
                    requireNode(target)
                    session.perform(target.token, NodeAction.SetText, action.text)
                }
                is Target.Point -> throw BrowserActionException("INVALID_ARGUMENTS")
            }
            is ScreenAction.Swipe -> session.hit(PointerAction.Swipe(action.x1, action.y1, action.x2, action.y2, action.durationMs))
            is ScreenAction.Key -> session.hit(PointerAction.Key(action.code))
            is ScreenAction.History -> session.history(action.forward)
            ScreenAction.ListTabs -> Unit
            is ScreenAction.NewTab -> session.newTab(action.url)
            is ScreenAction.CloseTab -> session.closeTab(action.tabId)
            is ScreenAction.SwitchTab -> session.switchTab(action.tabId)
        }
    }

    private fun requireNode(target: Target.Node) {
        nodes.resolve(target.token) ?: throw BrowserActionException("NODE_STALE")
    }

    private suspend fun onTarget(target: Target, action: NodeAction, point: suspend (Int, Int) -> Unit) {
        when (target) {
            is Target.Node -> {
                requireNode(target)
                session.perform(target.token, action, null)
            }
            is Target.Point -> point(target.x, target.y)
        }
    }

    override suspend fun show(target: ShowTarget): ShowResult {
        if (!BrowserPrefs.isEnabled()) return ShowResult.Failed("BROWSER_DISABLED")
        val url = when (target) {
            is ShowTarget.Uri -> target.uri
            is ShowTarget.InAppPreview -> target.url
            is ShowTarget.PackageName -> return ShowResult.Unsupported("package_name")
        }
        return try {
            session.load(url)
            val page = session.page()
            ShowResult.Shown("Loaded ${page.url.ifBlank { url }} (${page.title.ifBlank { "no title" }}). Use screen_operation read on this screen.")
        } catch (error: CancellationException) {
            throw error
        } catch (error: BrowserActionException) {
            ShowResult.Failed(error.code)
        }
    }

    suspend fun tabInfos(): List<TabInfo> = session.tabs()

    suspend fun close() = session.close()
}
