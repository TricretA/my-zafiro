package com.niki914.zafiro.app.ui.content

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import android.content.ContextWrapper
import com.niki914.uikit.dialog.ZafiroInputDialog
import com.niki914.uikit.infra.component.settings.SettingsPageSpec
import com.niki914.uikit.infra.component.settings.SettingsRowAction
import com.niki914.uikit.infra.component.settings.SettingsRowSpec
import com.niki914.uikit.infra.component.settings.SettingsSectionLayout
import com.niki914.uikit.infra.component.settings.SettingsSectionSpec
import com.niki914.uikit.infra.component.settings.SettingsSpecPageContent
import com.niki914.zafiro.app.R
import com.niki914.zafiro.api.util.runCatchingCancellable
import com.niki914.zafiro.signals.SignalConfigStore
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.core.signals.Signal
import com.tricreta.zafiro.autonomy.core.signals.SignalConfig
import com.tricreta.zafiro.autonomy.core.signals.SignalFormat
import com.tricreta.zafiro.autonomy.core.signals.SignalStore
import com.tricreta.zafiro.autonomy.signals.SignalWatchdog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date
import kotlin.random.Random

private const val ENABLED_ROW = "signals.enabled"
private const val OPEN_ACCESS_ROW = "signals.open_access"
private const val RECONNECT_ROW = "signals.reconnect"
private const val OWNER_ROW = "signals.owner"
private const val PACKAGES_ROW = "signals.packages"
private const val PACKAGES_RESET_ROW = "signals.packages_reset"
private const val LATEST_COUNT = 20

private data class SignalsUi(
    val config: SignalConfig,
    val granted: Boolean,
    val connected: Boolean,
    val stats: SignalStore.Stats,
    val latest: List<Signal>,
)

private enum class Editing { NONE, OWNER, PACKAGES }

/** Realtime signals: whether notification access is on, who the owner is, which apps are watched, and what was captured last. */
@Composable
fun SignalsSettingsContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tick by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf(Editing.NONE) }
    var draft by remember { mutableStateOf("") }

    // Coming back from the system notification-access screen: re-read what the owner may just have changed.
    DisposableEffect(context) {
        val lifecycle = context.findLifecycleOwner()?.lifecycle
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) tick++ }
        lifecycle?.addObserver(observer)
        onDispose { lifecycle?.removeObserver(observer) }
    }

    val ui by produceState<SignalsUi?>(null, tick) { value = withContext(Dispatchers.IO) { loadUi(context) } }
    val state = ui

    val rows = mutableListOf<SettingsSectionSpec>()
    if (state != null) {
        rows += SettingsSectionSpec(
            title = stringResource(R.string.signals_group_status),
            layout = SettingsSectionLayout.GroupedCard,
            rows = listOf(
                SettingsRowSpec.Value(
                    title = stringResource(R.string.signals_access_title),
                    currentState = stringResource(if (state.granted) R.string.signals_access_granted else R.string.signals_access_missing),
                ),
                SettingsRowSpec.Value(
                    title = stringResource(R.string.signals_listener_title),
                    summary = if (state.connected) null else stringResource(R.string.signals_listener_down),
                    currentState = if (state.connected) stringResource(R.string.signals_listener_connected) else null,
                ),
                SettingsRowSpec.Toggle(
                    id = ENABLED_ROW,
                    title = stringResource(R.string.signals_enabled_title),
                    summary = stringResource(R.string.signals_enabled_summary),
                    checked = state.config.enabled,
                ),
                SettingsRowSpec.Action(
                    id = OPEN_ACCESS_ROW,
                    title = stringResource(R.string.signals_open_access_title),
                    summary = stringResource(R.string.signals_open_access_summary),
                ),
                SettingsRowSpec.Action(
                    id = RECONNECT_ROW,
                    title = stringResource(R.string.signals_reconnect_title),
                    summary = stringResource(R.string.signals_reconnect_summary),
                ),
            ),
        )
        rows += SettingsSectionSpec(
            title = stringResource(R.string.signals_group_config),
            layout = SettingsSectionLayout.GroupedCard,
            rows = listOf(
                SettingsRowSpec.Navigation(
                    id = OWNER_ROW,
                    title = stringResource(R.string.signals_owner_title),
                    summary = state.config.ownerSenderNames.joinToString(", ").ifEmpty { stringResource(R.string.signals_owner_summary_empty) },
                ),
                SettingsRowSpec.Navigation(
                    id = PACKAGES_ROW,
                    title = stringResource(R.string.signals_packages_title),
                    summary = stringResource(R.string.signals_packages_summary, state.config.allowedPackages.size),
                ),
                SettingsRowSpec.Action(id = PACKAGES_RESET_ROW, title = stringResource(R.string.signals_packages_reset_title)),
            ),
        )
        rows += SettingsSectionSpec(
            title = stringResource(R.string.signals_group_counts),
            layout = SettingsSectionLayout.GroupedCard,
            rows = listOf(
                SettingsRowSpec.Value(title = stringResource(R.string.signals_today_title), currentState = state.stats.today.toString()),
                SettingsRowSpec.Value(title = stringResource(R.string.signals_unacked_title), currentState = state.stats.unacked.toString()),
                SettingsRowSpec.Value(title = stringResource(R.string.signals_total_title), currentState = state.stats.total.toString()),
            ),
        )
        val time = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        rows += SettingsSectionSpec(
            title = stringResource(R.string.signals_group_latest),
            layout = SettingsSectionLayout.GroupedCard,
            rows = if (state.latest.isEmpty()) {
                listOf(SettingsRowSpec.Message(title = stringResource(R.string.signals_latest_empty)))
            } else {
                state.latest.map { s ->
                    SettingsRowSpec.Value(
                        title = "${s.kind.name} · ${s.platform} · ${time.format(Date(s.receivedMs))}",
                        summary = displayText(s),
                    )
                }
            },
        )
    }

    SettingsSpecPageContent(
        spec = SettingsPageSpec(description = stringResource(R.string.signals_note), sections = rows),
    ) { action ->
        when (action) {
            is SettingsRowAction.ToggleChanged -> if (action.id == ENABLED_ROW && state != null) {
                scope.launch {
                    withContext(Dispatchers.IO) { SignalConfigStore.save(context, state.config.copy(enabled = action.checked)) }
                    tick++
                }
            }
            is SettingsRowAction.Click -> when (action.id) {
                OPEN_ACCESS_ROW -> openAccessSettings(context)
                RECONNECT_ROW -> {
                    val requested = SignalWatchdog.check(context, "settings")
                    Toast.makeText(
                        context,
                        context.getString(if (requested) R.string.signals_reconnect_done else R.string.signals_reconnect_skipped),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                PACKAGES_RESET_ROW -> if (state != null) scope.launch {
                    withContext(Dispatchers.IO) { SignalConfigStore.save(context, state.config.copy(allowedPackages = SignalConfig.DEFAULT_PACKAGES)) }
                    tick++
                }
            }
            is SettingsRowAction.Navigate -> if (state != null) when (action.id) {
                OWNER_ROW -> { draft = state.config.ownerSenderNames.joinToString("\n"); editing = Editing.OWNER }
                PACKAGES_ROW -> { draft = state.config.allowedPackages.joinToString("\n"); editing = Editing.PACKAGES }
            }
            else -> Unit
        }
    }

    val owner = editing == Editing.OWNER
    ZafiroInputDialog(
        visible = editing != Editing.NONE,
        onDismissRequest = { editing = Editing.NONE },
        title = stringResource(if (owner) R.string.signals_owner_title else R.string.signals_packages_title),
        value = draft,
        onValueChange = { draft = it },
        placeholder = stringResource(if (owner) R.string.signals_owner_placeholder else R.string.signals_packages_placeholder),
        hint = stringResource(if (owner) R.string.signals_owner_hint else R.string.signals_packages_hint),
        positiveButtonText = stringResource(R.string.signals_save),
        onPositiveClick = {
            val lines = SignalConfig.parseLines(draft)
            val current = state?.config
            val target = editing
            editing = Editing.NONE
            if (current != null) scope.launch {
                withContext(Dispatchers.IO) {
                    SignalConfigStore.save(
                        context,
                        if (target == Editing.OWNER) current.copy(ownerSenderNames = lines) else current.copy(allowedPackages = lines.ifEmpty { SignalConfig.DEFAULT_PACKAGES }),
                    )
                }
                tick++
            }
        },
        singleLine = false,
        minLines = 4,
    )
}

private fun loadUi(context: Context): SignalsUi {
    val graph = AutonomyGraph.get(context)
    return SignalsUi(
        config = SignalConfigStore.load(context),
        granted = SignalWatchdog.accessGranted(context),
        connected = SignalWatchdog.connected,
        stats = graph.signals.stats(System.currentTimeMillis()),
        latest = graph.signals.recent(limit = LATEST_COUNT),
    )
}

/** Captured text is third-party data: shown as one fenced plain-text line, never interpreted. */
private fun displayText(s: Signal): String {
    val boundary = SignalFormat.newBoundary(Random.Default)
    val body = listOf(s.sender, s.title.takeIf { it != s.sender }.orEmpty(), s.text, s.subText)
        .filter { it.isNotBlank() }
        .joinToString(" | ")
    return SignalFormat.fence(boundary, body, 240)
}

private fun openAccessSettings(context: Context) {
    val detail = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
        .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, SignalWatchdog.component(context).flattenToString())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val general = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val opened = runCatchingCancellable { context.startActivity(detail) }.isSuccess
    if (!opened) runCatchingCancellable { context.startActivity(general) }
}

private tailrec fun Context.findLifecycleOwner(): LifecycleOwner? = when (this) {
    is LifecycleOwner -> this
    is ContextWrapper -> baseContext.findLifecycleOwner()
    else -> null
}
