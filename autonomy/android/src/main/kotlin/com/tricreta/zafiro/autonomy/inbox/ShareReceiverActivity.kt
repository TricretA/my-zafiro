package com.tricreta.zafiro.autonomy.inbox

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.IntentCompat
import com.tricreta.zafiro.autonomy.AutonomyGraph
import com.tricreta.zafiro.autonomy.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Target of Android's "Share" sheet. It has no screen: it copies every shared stream (and text) into the inbox and finishes.
 * It stays open while copying because the sender's temporary read grant ends with this activity. No storage permission needed.
 */
class ShareReceiverActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val graph = AutonomyGraph.get(this)
        val store = graph.inbox
        val intent = intent
        val source = referrer?.takeIf { it.scheme == "android-app" }?.host.orEmpty()
        val uris = streams(intent)
        val text = intent?.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.takeIf { it.isNotBlank() }
        val mimeOfIntent = intent?.type.orEmpty()
        val resolver = contentResolver
        graph.scope.launch {
            var saved = 0
            var problem: String? = null
            withContext(Dispatchers.IO) {
                uris.forEachIndexed { i, uri ->
                    try {
                        val mime = resolver.getType(uri) ?: mimeOfIntent.takeIf { it.isNotEmpty() && !it.endsWith("/*") } ?: "application/octet-stream"
                        val input = resolver.openInputStream(uri) ?: error("The sending app did not allow reading the item.")
                        input.use { store.add(i + 1, displayName(resolver, uri), mime, source, it) }
                        saved++
                    } catch (e: Exception) {
                        problem = e.message ?: e.javaClass.simpleName
                    }
                }
                // Text alongside files (a caption or prompt), or a text-only share.
                if (text != null) {
                    try { store.addText(uris.size + 1, text, source); saved++ } catch (e: Exception) { problem = e.message ?: e.javaClass.simpleName }
                }
            }
            runOnUiThread {
                val msg = when {
                    saved == 0 && problem != null -> getString(R.string.share_failed, problem)
                    saved == 0 -> getString(R.string.share_nothing)
                    saved == 1 -> getString(R.string.share_saved_one)
                    else -> getString(R.string.share_saved_many, saved)
                }
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun streams(intent: Intent?): List<Uri> {
        if (intent == null) return emptyList()
        val list = ArrayList<Uri>()
        if (intent.action == Intent.ACTION_SEND_MULTIPLE) {
            IntentCompat.getParcelableArrayListExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)?.let { list.addAll(it) }
        } else {
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)?.let { list.add(it) }
        }
        if (list.isEmpty()) {
            val clip = intent.clipData
            if (clip != null) for (i in 0 until clip.itemCount) clip.getItemAt(i).uri?.let { list.add(it) }
        }
        return list.filter { it.scheme == ContentResolver.SCHEME_CONTENT }.take(MAX_ITEMS)
    }

    private fun displayName(resolver: ContentResolver, uri: Uri): String? = try {
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }
    } catch (e: Exception) { null }

    private companion object { const val MAX_ITEMS = 20 }
}
