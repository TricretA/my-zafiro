package com.niki914.zafiro.chat.agentic.buildin.impl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.niki914.zafiro.api.StudioConfig
import java.io.File
import java.io.InputStream
import java.io.OutputStream

data class PublicExportResult(
    val relativePath: String,
    val displayPath: String,
    val contentUri: String?,
    val bytes: Long,
)

/**
 * Exports files from Zafiro's private sandbox into user-visible shared storage (default `Download/Zafiro`,
 * or any folder requested by the user such as `Download/Reels` or `Movies/Studio`).
 */
object PublicFileExporter {

    private val BAD_CHARS = Regex("[*?\"<>|:\\x00-\\x1F]")

    private const val DIR_DOWNLOAD = "Download"
    private const val DIR_MOVIES = "Movies"
    private const val DIR_PICTURES = "Pictures"
    private const val DIR_DCIM = "DCIM"
    private const val DIR_MUSIC = "Music"
    private const val DIR_DOCUMENTS = "Documents"

    /** Pure folder normalizer (unit-tested). Accepts e.g. `Download/Zafiro`, `/sdcard/Movies/Reels`, `downloads`. */
    fun normalizeFolder(rawFolder: String?, fallback: String = StudioConfig.DEFAULT_OUTPUT_FOLDER): String {
        val cleaned = rawFolder.orEmpty()
            .trim()
            .replace('\\', '/')
            .removePrefix("/storage/emulated/0/")
            .removePrefix("/sdcard/")
            .trim('/')
        if (cleaned.isEmpty()) return fallback
        val parts = cleaned.split('/').filter { it.isNotEmpty() }
        if (parts.any { it == "." || it == ".." || it.startsWith(".") || BAD_CHARS.containsMatchIn(it) }) {
            return fallback
        }
        val first = when (parts.first().lowercase()) {
            "download", "downloads" -> DIR_DOWNLOAD
            "movies", "movie" -> DIR_MOVIES
            "pictures", "picture" -> DIR_PICTURES
            "dcim" -> DIR_DCIM
            "music" -> DIR_MUSIC
            "documents", "document" -> DIR_DOCUMENTS
            else -> parts.first()
        }
        return (listOf(first) + parts.drop(1)).joinToString("/")
    }

    fun safeFileName(rawName: String, fallback: String = "output.bin"): String {
        val base = rawName.trim().substringAfterLast('/').substringAfterLast('\\')
            .replace(BAD_CHARS, "_")
            .trim('.')
        return base.ifEmpty { fallback }
    }

    fun exportFile(
        context: Context,
        source: File,
        folder: String?,
        fileName: String? = null,
        mime: String = OutputPaths.mime(fileName ?: source.name),
    ): Result<PublicExportResult> = runCatching {
        require(source.isFile) { "Source file does not exist: ${source.absolutePath}" }
        val safeName = safeFileName(fileName ?: source.name)
        exportStream(context, safeName, mime, folder) { out ->
            source.inputStream().use { input -> input.copyTo(out) }
        }.getOrThrow()
    }

    fun exportStream(
        context: Context,
        fileName: String,
        mime: String,
        folder: String?,
        writer: (OutputStream) -> Unit,
    ): Result<PublicExportResult> = runCatching {
        val normalizedFolder = normalizeFolder(folder)
        val safeName = safeFileName(fileName)
        val rootSeg = normalizedFolder.substringBefore('/')

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val collection = mediaStoreCollection(rootSeg, mime)
            if (collection != null) {
                return@runCatching writeViaMediaStore(context, collection, normalizedFolder, safeName, mime, writer)
            }
        }

        writeDirectly(normalizedFolder, safeName, writer)
    }

    /** Resolves a local path inside either `filesDir/outputs`, `filesDir`, or external storage if absolute. */
    fun resolveReadableFile(context: Context, path: String): File? {
        val trimmed = path.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.startsWith("/")) {
            return File(trimmed).takeIf { it.isFile && it.canRead() }
        }
        val outputsRoot = File(context.filesDir, OutputPaths.ROOT_NAME)
        OutputPaths.resolve(outputsRoot, trimmed)?.takeIf { it.isFile }?.let { return it }
        val inFilesDir = File(context.filesDir, trimmed)
        val filesCanonical = context.filesDir.canonicalPath
        if (inFilesDir.canonicalPath.startsWith(filesCanonical + File.separator) && inFilesDir.isFile) {
            return inFilesDir
        }
        return null
    }

    private fun mediaStoreCollection(rootSegment: String, mime: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        return when (rootSegment) {
            DIR_DOWNLOAD -> MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            DIR_MOVIES, DIR_DCIM -> when {
                mime.startsWith("image/") -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                mime.startsWith("video/") -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                else -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            DIR_PICTURES -> when {
                mime.startsWith("image/") -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                mime.startsWith("video/") -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                else -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            DIR_MUSIC -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            DIR_DOCUMENTS -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else -> null
        }
    }

    private fun writeViaMediaStore(
        context: Context,
        collection: Uri,
        normalizedFolder: String,
        fileName: String,
        mime: String,
        writer: (OutputStream) -> Unit,
    ): PublicExportResult {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, normalizedFolder)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val target = resolver.insert(collection, values)
            ?: error("Android MediaStore refused to create $normalizedFolder/$fileName")
        try {
            val counting = CountingOutputStream(
                resolver.openOutputStream(target) ?: error("Could not open output stream for $target"),
            )
            counting.use { writer(it) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.update(target, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
            }
            val rel = "$normalizedFolder/$fileName"
            return PublicExportResult(
                relativePath = rel,
                displayPath = "/storage/emulated/0/$rel",
                contentUri = target.toString(),
                bytes = counting.count,
            )
        } catch (t: Throwable) {
            runCatching { resolver.delete(target, null, null) }
            throw t
        }
    }

    private fun writeDirectly(
        normalizedFolder: String,
        fileName: String,
        writer: (OutputStream) -> Unit,
    ): PublicExportResult {
        @Suppress("DEPRECATION")
        val extRoot = Environment.getExternalStorageDirectory()
        val dir = File(extRoot, normalizedFolder)
        if (!dir.exists() && !dir.mkdirs()) {
            error("Cannot create $normalizedFolder. Use a folder under Download/, Movies/, or Documents/, or grant All-Files access.")
        }
        val target = File(dir, fileName)
        val counting = CountingOutputStream(target.outputStream())
        counting.use { writer(it) }
        val rel = "$normalizedFolder/$fileName"
        return PublicExportResult(
            relativePath = rel,
            displayPath = target.absolutePath,
            contentUri = null,
            bytes = counting.count,
        )
    }

    private class CountingOutputStream(private val delegate: OutputStream) : OutputStream() {
        var count = 0L
            private set

        override fun write(b: Int) {
            delegate.write(b)
            count++
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            delegate.write(b, off, len)
            count += len
        }

        override fun flush() = delegate.flush()
        override fun close() = delegate.close()
    }
}
