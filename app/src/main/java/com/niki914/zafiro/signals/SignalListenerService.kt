package com.niki914.zafiro.signals

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.niki914.logging.Logger
import com.niki914.zafiro.api.util.runCatchingCancellable
import com.tricreta.zafiro.autonomy.core.signals.RawNotification
import com.tricreta.zafiro.autonomy.signals.SignalWatchdog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Captures notifications from the allow-listed apps so the team learns about comments, replies, emails and owner
 * messages as they happen. It only reads: it never replies, dismisses or opens anything. Everything it reads is
 * untrusted third-party text; see [SignalPipeline] for what is done with it.
 */
class SignalListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onListenerConnected() {
        SignalWatchdog.connected = true
        SignalWatchdog.lastConnectedMs = System.currentTimeMillis()
        Logger.i(TAG, "listener connected")
    }

    override fun onListenerDisconnected() {
        SignalWatchdog.connected = false
        Logger.w(TAG, "listener disconnected")
        SignalWatchdog.check(this, "disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val raw = sbn?.let { runCatchingCancellable { extract(it) }.getOrNull() } ?: return
        scope.launch {
            runCatchingCancellable { SignalPipeline.handle(applicationContext, raw) }
                .onFailure { Logger.w(TAG, "signal failed: ${it.message}") }
        }
    }

    private fun extract(sbn: StatusBarNotification): RawNotification? {
        val pkg = sbn.packageName ?: return null
        if (pkg == packageName || pkg.startsWith(OWN_PREFIX)) return null
        val n = sbn.notification ?: return null
        if (n.flags and (Notification.FLAG_GROUP_SUMMARY or Notification.FLAG_ONGOING_EVENT) != 0) return null
        if (!SignalConfigStore.load(applicationContext).let { it.enabled && it.allows(pkg) }) return null

        val extras = n.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        var text = bigText.ifBlank { extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty() }
        val sub = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()
        var sender = ""
        var conversation = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString().orEmpty()
        var whenMs = n.`when`
        var isGroup = false

        val style = runCatchingCancellable { NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(n) }.getOrNull()
        if (style != null) {
            val last = style.messages.lastOrNull()
            if (last != null) {
                text = last.text?.toString().orEmpty().ifBlank { text }
                sender = last.person?.name?.toString().orEmpty()
                if (last.timestamp > 0) whenMs = last.timestamp
            }
            isGroup = style.isGroupConversation
            if (conversation.isBlank()) conversation = style.conversationTitle?.toString().orEmpty()
        }
        if (sender.isBlank() && !isGroup && pkg in OWNER_CHAT_PACKAGES) sender = title
        if (text.isBlank() && title.isBlank()) return null
        return RawNotification(pkg, title, text, sub, whenMs, sender, conversation, isGroup)
    }

    override fun onDestroy() {
        SignalWatchdog.connected = false
        super.onDestroy()
    }

    private companion object {
        const val TAG = "niki914_zafiro_SignalListener"
        const val OWN_PREFIX = "com.niki914.zafiro"
        val OWNER_CHAT_PACKAGES = setOf("com.whatsapp", "com.whatsapp.w4b", "org.telegram.messenger")
    }
}
