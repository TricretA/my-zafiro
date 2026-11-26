package com.niki914.zafiro.app.browser

import android.content.Context
import com.niki914.zafiro.app.screen.applicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Global switches of the agent browser. Kept in their own small preferences file. */
object BrowserPrefs {
    private const val FILE = "agent_browser"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_DESKTOP = "desktop"

    private val _enabled = MutableStateFlow(true)
    private val _desktop = MutableStateFlow(false)
    private var loaded = false

    val enabled: StateFlow<Boolean> get() = load { _enabled.asStateFlow() }
    val desktop: StateFlow<Boolean> get() = load { _desktop.asStateFlow() }

    private fun prefs() = applicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE)

    @Synchronized
    private fun <T> load(block: () -> T): T {
        if (!loaded) {
            val p = prefs()
            _enabled.value = p.getBoolean(KEY_ENABLED, true)
            _desktop.value = p.getBoolean(KEY_DESKTOP, false)
            loaded = true
        }
        return block()
    }

    fun isEnabled(): Boolean = load { _enabled.value }

    fun isDesktop(): Boolean = load { _desktop.value }

    fun setEnabled(value: Boolean) {
        load { }
        prefs().edit().putBoolean(KEY_ENABLED, value).apply()
        _enabled.value = value
    }

    fun setDesktop(value: Boolean) {
        load { }
        prefs().edit().putBoolean(KEY_DESKTOP, value).apply()
        _desktop.value = value
    }
}
