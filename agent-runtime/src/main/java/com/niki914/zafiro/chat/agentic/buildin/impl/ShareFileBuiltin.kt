package com.niki914.zafiro.chat.agentic.buildin.impl

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.niki914.xposed.api.util.ContextProvider
import com.niki914.zafiro.chat.agentic.buildin.BuiltinTool
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolRequest
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Hands a video, image or text straight to another app's "share to" screen (TikTok, Instagram, Reddit, YouTube, Substack...)
 * with the caption already filled in or copied to the clipboard. This replaces dozens of screen steps (open app, find +, open
 * gallery, scroll, pick the video) with ONE call, so posting costs a handful of taps instead of a long agent loop.
 *
 * The file is copied into the shared media store (Movies/Zafiro or Pictures/Zafiro) and the target app gets a read grant for it.
 * It never taps "Post": the posting step stays with the caller (routine, owner approval gate).
 */
class ShareFileBuiltin : BuiltinTool() {
    override val name: String = "share_file"
    override val defaultEnabled: Boolean = true
    override val description: String =
        "Open another app's share screen with a file already attached (video/image/audio) and a caption ready. " +
            "package = app to share to (e.g. com.zhiliaoapp.musically TikTok, com.instagram.android, com.reddit.frontpage, " +
            "com.google.android.youtube, com.substack.app). path = file in Zafiro's files folder (e.g. outputs/clip.mp4). " +
            "text = caption/post text: it is passed to the app AND copied to the clipboard (some apps ignore it; paste it into the caption field). " +
            "Nothing is posted: afterwards use screen_routine/screen_operation to tap Next/Post. Much faster than navigating the app yourself."
    override val inputSchemaJson: String =
        """{"type":"object","properties":{"package":{"type":"string"},"path":{"type":"string","description":"File under Zafiro's files folder, or an absolute path inside it."},"text":{"type":"string","description":"Caption or post text."},"mime":{"type":"string","description":"Optional, e.g. video/mp4. Guessed from the extension."}},"required":["package"]}"""

    override suspend fun invoke(request: BuiltinToolRequest): BuiltinToolResult {
        val args = runCatching { Json.parseToJsonElement(request.argumentsJson) as? JsonObject }.getOrNull()
            ?: return fail("INVALID_ARGUMENTS", "Arguments must be a JSON object.")
        fun str(k: String) = args[k]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        val pkg = str("package")
        if (pkg.isEmpty()) return fail("INVALID_ARGUMENTS", "package is required.")
        val relPath = str("path")
        val text = str("text")
        if (relPath.isEmpty() && text.isEmpty()) return fail("INVALID_ARGUMENTS", "Give a path, a text, or both.")

        val ctx = ContextProvider.await().applicationContext
        if (ctx.packageManager.getLaunchIntentForPackage(pkg) == null) return fail("APP_NOT_INSTALLED", "$pkg is not installed or cannot be launched.")

        var uri: Uri? = null
        var mime = str("mime").ifEmpty { mimeFor(relPath) }
        if (relPath.isNotEmpty()) {
            val file = resolveInside(ctx.filesDir, relPath) ?: return fail("PATH_NOT_ALLOWED", "path must be inside Zafiro's files folder.")
            if (!file.isFile) return fail("FILE_NOT_FOUND", "No file at ${file.name}.")
            uri = publish(ctx, file, mime) ?: return fail("MEDIA_STORE_FAILED", "Could not make the file shareable (needs Android 10+).")
        }
        if (text.isNotEmpty()) {
            runCatching { (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.setPrimaryClip(ClipData.newPlainText("Zafiro", text)) }
        }
        if (uri == null) mime = "text/plain"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            setPackage(pkg)
            if (uri != null) putExtra(Intent.EXTRA_STREAM, uri)
            if (text.isNotEmpty()) putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (uri != null) runCatching { ctx.grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        return try {
            ctx.startActivity(intent)
            BuiltinToolResult.success(
                message = "Opened $pkg's share screen" + (if (uri != null) " with the file attached" else "") +
                    (if (text.isNotEmpty()) "; caption passed and copied to the clipboard" else "") +
                    ". Nothing was posted. Look at the screen (screen_operation read) and continue: Next/Post.",
            )
        } catch (e: android.content.ActivityNotFoundException) {
            fail("SHARE_NOT_SUPPORTED", "$pkg does not accept shared ${mime}. Use its in-app create flow instead.")
        } catch (e: SecurityException) {
            fail("START_BLOCKED", "Android blocked starting the app from the background (grant Zafiro the 'display over other apps' permission). ${e.message.orEmpty()}")
        }
    }

    private fun publish(ctx: Context, file: File, mime: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val isVideo = mime.startsWith("video")
        val isImage = mime.startsWith("image")
        val collection = when {
            isVideo -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            isImage -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            mime.startsWith("audio") -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else -> return null
        }
        val folder = when { isVideo -> Environment.DIRECTORY_MOVIES; isImage -> Environment.DIRECTORY_PICTURES; else -> Environment.DIRECTORY_MUSIC }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "zafiro-${System.currentTimeMillis()}-${file.name}")
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "$folder/Zafiro")
        }
        val resolver = ctx.contentResolver
        val target = resolver.insert(collection, values) ?: return null
        return try {
            resolver.openOutputStream(target)?.use { out -> file.inputStream().use { it.copyTo(out) } } ?: return null
            target
        } catch (e: java.io.IOException) {
            runCatching { resolver.delete(target, null, null) }
            null
        }
    }

    private fun fail(code: String, message: String) = BuiltinToolResult.failure(code = code, message = message)

    companion object {
        /** The file only if it is inside [root] (no ../ escapes, no symlink tricks). Relative paths are taken from [root]. */
        fun resolveInside(root: File, path: String): File? {
            val f = if (path.startsWith("/")) File(path) else File(root, path)
            val rootPath = runCatching { root.canonicalPath }.getOrNull() ?: return null
            val filePath = runCatching { f.canonicalPath }.getOrNull() ?: return null
            return if (filePath == rootPath || filePath.startsWith(rootPath + File.separator)) f else null
        }

        fun mimeFor(path: String): String = when (path.substringAfterLast('.', "").lowercase()) {
            "mp4", "m4v" -> "video/mp4"
            "mov" -> "video/quicktime"
            "webm" -> "video/webm"
            "3gp" -> "video/3gpp"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "mp3" -> "audio/mpeg"
            "m4a", "aac" -> "audio/mp4"
            "wav" -> "audio/wav"
            else -> "*/*"
        }
    }
}
