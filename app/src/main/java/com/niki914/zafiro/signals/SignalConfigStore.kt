package com.niki914.zafiro.signals

import android.content.Context
import com.tricreta.zafiro.autonomy.core.signals.SignalConfig
import java.io.File

/** Reads and writes the owner's realtime-signals settings (filesDir/signals/config.json). Reads are cached until the file changes. */
object SignalConfigStore {
    @Volatile private var cached: SignalConfig? = null
    @Volatile private var cachedStamp = -1L

    private fun file(context: Context) = File(context.filesDir, "signals/config.json")

    fun load(context: Context): SignalConfig {
        val f = file(context)
        val stamp = if (f.isFile) f.lastModified() + f.length() else 0L
        cached?.let { if (stamp == cachedStamp) return it }
        val config = SignalConfig.fromJson(if (f.isFile) f.readText() else null)
        cached = config
        cachedStamp = stamp
        return config
    }

    fun save(context: Context, config: SignalConfig) {
        val f = file(context)
        f.parentFile?.mkdirs()
        f.writeText(config.toJson())
        cached = null
    }
}
