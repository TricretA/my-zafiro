package com.tricreta.zafiro.autonomy.signals

import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import com.niki914.logging.Logger
import com.niki914.zafiro.api.util.runCatchingCancellable

/**
 * Keeps the notification listener bound. Tecno (HiOS) and Samsung kill listeners and often do not rebind them, so this is
 * called at app start and every time a scheduled run starts (the scheduler heartbeat). Realtime is best-effort: when the
 * listener is down, scheduled polling still finds the same information later.
 *
 * The listener class lives in the app module; it is addressed by name so this module does not depend on it.
 */
object SignalWatchdog {
    private const val TAG = "niki914_zafiro_SignalWatchdog"
    const val LISTENER_CLASS = "com.niki914.zafiro.signals.SignalListenerService"

    /** Set by the listener in onListenerConnected / onListenerDisconnected. Process-local: a fresh process starts false. */
    @Volatile var connected: Boolean = false
    @Volatile var lastConnectedMs: Long = 0L
    @Volatile var lastRebindRequestMs: Long = 0L

    fun component(context: Context) = ComponentName(context.packageName, LISTENER_CLASS)

    /** True when the owner granted notification access to this app (system setting, readable without any permission). */
    fun accessGranted(context: Context): Boolean = runCatchingCancellable {
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    }.getOrDefault(false)

    /** Asks Android to bind the listener again if access is granted but nothing is connected. Never throws. Returns true if a rebind was requested. */
    fun check(context: Context, reason: String): Boolean {
        if (!accessGranted(context) || connected) return false
        // A just-started process has not had time to be bound yet, and the system binds it by itself; do not nag it.
        val now = System.currentTimeMillis()
        if (now - lastRebindRequestMs < MIN_REBIND_GAP_MS) return false
        lastRebindRequestMs = now
        val result = runCatchingCancellable { NotificationListenerService.requestRebind(component(context)) }
        result.onFailure { Logger.w(TAG, "rebind failed: ${it.message}") }
        if (result.isSuccess) Logger.i(TAG, "requested rebind ($reason)")
        return result.isSuccess
    }

    private const val MIN_REBIND_GAP_MS = 60_000L
}
