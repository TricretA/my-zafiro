package com.niki914.zafiro.app.ui.content

import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.niki914.uikit.infra.component.settings.SettingsPageSpec
import com.niki914.uikit.infra.component.settings.SettingsRowAction
import com.niki914.uikit.infra.component.settings.SettingsRowSpec
import com.niki914.uikit.infra.component.settings.SettingsSectionLayout
import com.niki914.uikit.infra.component.settings.SettingsSectionSpec
import com.niki914.uikit.infra.component.settings.SettingsSpecPageContent
import com.niki914.zafiro.app.R
import com.niki914.zafiro.app.browser.BrowserActionException
import com.niki914.zafiro.app.browser.BrowserPrefs
import com.niki914.zafiro.app.browser.WebBrowserHost
import com.niki914.zafiro.app.browser.WebBrowserScreen
import com.niki914.zafiro.app.screen.AppScreens
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Settings of the agent browser: the global switch, desktop mode, what is open now, and clearing logins. */
@Composable
fun AgentBrowserSettingsContent(onOpenBrowser: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val enabled by BrowserPrefs.enabled.collectAsState()
    val desktop by BrowserPrefs.desktop.collectAsState()
    val tabs by WebBrowserHost.overview.collectAsState()
    val surface by WebBrowserHost.surface.collectAsState()
    val surfaceText = stringResource(
        when (surface) {
            WebBrowserHost.Surface.NONE -> R.string.browser_surface_none
            WebBrowserHost.Surface.PAGE -> R.string.browser_surface_page
            WebBrowserHost.Surface.OVERLAY -> R.string.browser_surface_overlay
            WebBrowserHost.Surface.DETACHED -> R.string.browser_surface_detached
        },
    )
    val clearedText = stringResource(R.string.browser_cleared)

    fun section(title: String, rows: List<SettingsRowSpec>) =
        SettingsSectionSpec(title = title, layout = SettingsSectionLayout.GroupedCard, rows = rows)

    val spec = SettingsPageSpec(
        description = stringResource(R.string.browser_settings_description),
        sections = listOf(
            section(
                stringResource(R.string.browser_settings_title),
                listOf(
                    SettingsRowSpec.Toggle(id = "enabled", title = stringResource(R.string.browser_enabled_title), summary = stringResource(R.string.browser_enabled_summary), checked = enabled),
                    SettingsRowSpec.Toggle(id = "desktop", title = stringResource(R.string.browser_desktop_title), summary = stringResource(R.string.browser_desktop_summary), checked = desktop, enabled = enabled),
                    SettingsRowSpec.Navigation(id = "open", title = stringResource(R.string.browser_open_title), summary = stringResource(R.string.browser_open_summary), enabled = enabled),
                    SettingsRowSpec.Value(title = stringResource(R.string.browser_surface_title), summary = surfaceText),
                    SettingsRowSpec.Message(stringResource(R.string.browser_google_note)),
                ),
            ),
            section(
                stringResource(R.string.browser_tabs_section),
                tabs.map { SettingsRowSpec.Value(title = "${it.screenId} · ${it.tabId}${it.title.takeIf { t -> t.isNotBlank() }?.let { t -> " · $t" }.orEmpty()}", summary = it.url, currentState = if (it.active) "active" else null) }
                    .ifEmpty { listOf(SettingsRowSpec.Message(stringResource(R.string.browser_tabs_empty))) },
            ),
            section(
                stringResource(R.string.browser_data_section),
                listOf(SettingsRowSpec.Destructive(id = "clear", title = stringResource(R.string.browser_clear_title), summary = stringResource(R.string.browser_clear_summary))),
            ),
        ),
    )
    SettingsSpecPageContent(spec = spec, onAction = { action ->
        when (action) {
            is SettingsRowAction.ToggleChanged -> when (action.id) {
                "enabled" -> {
                    BrowserPrefs.setEnabled(action.checked)
                    if (!action.checked) scope.launch { AppScreens.closeAllBrowsers() }
                }
                "desktop" -> WebBrowserHost.setDesktopMode(action.checked)
            }
            is SettingsRowAction.Navigate -> if (action.id == "open") onOpenBrowser()
            is SettingsRowAction.Click -> if (action.id == "clear") {
                WebBrowserHost.clearData()
                Toast.makeText(context, clearedText, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    })
}

/** The browser the agents use, shown to the owner: address bar, tab buttons, and the live page. */
@Composable
fun AgentBrowserPageContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val enabled by BrowserPrefs.enabled.collectAsState()
    var screen by remember { mutableStateOf<WebBrowserScreen?>(null) }
    LaunchedEffect(enabled) { screen = if (enabled) AppScreens.userBrowser() else null }

    val current = screen
    Column(Modifier.fillMaxSize().padding(top = topChromePadding(), bottom = bottomChromePadding())) {
        if (!enabled || current == null) {
            Text(stringResource(R.string.browser_off_message), Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            return@Column
        }
        val overview by WebBrowserHost.overview.collectAsState()
        val mine = overview.filter { it.screenId == current.id.value }
        val activeUrl = mine.firstOrNull { it.active }?.url.orEmpty()
        var address by remember(activeUrl) { mutableStateOf(activeUrl) }
        val failed = stringResource(R.string.browser_load_failed, "%s")

        fun run(block: suspend () -> Unit) {
            scope.launch {
                try {
                    block()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: BrowserActionException) {
                    Toast.makeText(context, failed.replace("%s", error.code), Toast.LENGTH_SHORT).show()
                }
            }
        }

        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.browser_address_hint)) },
            )
            Button(onClick = { run { current.session.load(address) } }) { Text(stringResource(R.string.browser_go)) }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
            TextButton(onClick = { run { current.session.history(false) } }) { Text(stringResource(R.string.browser_back)) }
            TextButton(onClick = { run { current.session.history(true) } }) { Text(stringResource(R.string.browser_forward)) }
            TextButton(onClick = { run { current.session.load(activeUrl) } }, enabled = activeUrl.isNotBlank()) { Text(stringResource(R.string.browser_reload)) }
            TextButton(onClick = { run { current.session.newTab(null) } }) { Text(stringResource(R.string.browser_new_tab)) }
            mine.forEach { tab ->
                TextButton(onClick = { run { current.session.switchTab(tab.tabId) } }) {
                    Text((if (tab.active) "[${tab.tabId}] " else "${tab.tabId} ") + tab.title.take(14))
                }
            }
            TextButton(
                onClick = { mine.firstOrNull { it.active }?.let { tab -> run { current.session.closeTab(tab.tabId) } } },
                enabled = mine.isNotEmpty(),
            ) { Text(stringResource(R.string.browser_close_tab)) }
        }
        AndroidView(
            factory = { ctx -> FrameLayout(ctx).also { WebBrowserHost.attachToPage(it) } },
            onRelease = { WebBrowserHost.detachFromPage(it) },
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
    }
}
