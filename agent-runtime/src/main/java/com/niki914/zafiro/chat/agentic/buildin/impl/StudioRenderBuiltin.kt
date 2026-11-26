package com.niki914.zafiro.chat.agentic.buildin.impl

import com.niki914.xposed.api.util.ContextProvider
import com.niki914.zafiro.api.StudioConfig
import com.niki914.zafiro.api.StudioConfigProvider
import com.niki914.zafiro.chat.agentic.buildin.BuiltinTool
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolRequest
import com.niki914.zafiro.chat.agentic.buildin.BuiltinToolResult
import com.niki914.zafiro.service.findService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Base64

/**
 * Pure validation & URL helpers for [StudioRenderBuiltin] so path/repo checks are unit-tested on JVM.
 */
object StudioRenderSupport {
    private val REPO_REGEX = Regex("^[A-Za-z0-9._-]+/[A-Za-z0-9._-]+$")
    private val TAG_SAFE_REGEX = Regex("[^A-Za-z0-9._-]")

    fun validRepo(repo: String): String? = repo.trim().takeIf { REPO_REGEX.matches(it) }

    fun safeTagPart(id: String): String = id.trim().replace(TAG_SAFE_REGEX, "-").trim('-').ifEmpty { "job" }

    fun validRepoPath(repoPath: String): String? {
        val clean = repoPath.trim().replace('\\', '/').trimStart('/')
        if (clean.isEmpty() || clean.length > 240) return null
        val parts = clean.split('/')
        if (parts.any { it.isEmpty() || it == "." || it == ".." }) return null
        return clean
    }

    fun pickMp4Asset(assets: JsonArray, preferredName: String?): JsonObject? {
        val list = assets.mapNotNull { it as? JsonObject }
        if (!preferredName.isNullOrBlank()) {
            list.firstOrNull { it["name"]?.jsonPrimitive?.contentOrNull == preferredName }?.let { return it }
        }
        return list.firstOrNull {
            it["name"]?.jsonPrimitive?.contentOrNull?.endsWith(".mp4", ignoreCase = true) == true
        } ?: list.firstOrNull()
    }
}

/**
 * Authenticates against the owner's `zafiro-studio` GitHub repository using the encrypted token from
 * Settings -> General -> Video Studio & GitHub (never exposing the token in prompts or logs).
 *
 * Solves two limits of generic tools:
 * 1. Streams large raw videos from the phone (up to 2 GB) to GitHub Release `input-<id>` assets (`upload_input`), bypassing the 20 MB Contents API limit.
 * 2. Streams finished MP4 renders directly into user-visible shared storage (`Download/Zafiro` by default, or any `folder` the user requests).
 */
class StudioRenderBuiltin(
    private val configProvider: () -> StudioConfig = { findService<StudioConfigProvider>()?.get() ?: StudioConfig() },
) : BuiltinTool() {

    override val name: String = "studio_render"
    override val defaultEnabled: Boolean = true
    override val description: String =
        "Cloud video rendering bridge for zafiro-studio on GitHub Actions. Uses the encrypted GitHub token from Settings > General > Video Studio & GitHub (never ask for or log the token). " +
            "op=status checks whether token & repo are configured and returns default_output_folder. " +
            "op=upload_input (id, local_path, asset_name?) streams a large raw video/audio from the phone directly to GitHub Release input-<id> and returns its download URL for the job JSON. " +
            "op=commit_file (repo_path, content or local_path, message?, branch?) creates/updates a job JSON, HTML film, or asset in the repo. " +
            "op=dispatch (job_path, workflow?, branch?) triggers render.yml and returns run_id. " +
            "op=poll (run_id?) checks workflow progress. " +
            "op=download (id or tag, folder?, filename?, asset_name?) downloads the finished MP4 from release job-<id> directly into the user-visible folder (defaults to configured folder e.g. Download/Zafiro, or any folder the user asks for like 'Movies/Reels' or 'Download/ClientA')."
    override val inputSchemaJson: String =
        """{"type":"object","properties":{"op":{"type":"string","enum":["status","upload_input","commit_file","dispatch","poll","download"]},"id":{"type":"string"},"tag":{"type":"string"},"repo":{"type":"string"},"local_path":{"type":"string"},"asset_name":{"type":"string"},"repo_path":{"type":"string"},"content":{"type":"string"},"message":{"type":"string"},"branch":{"type":"string"},"job_path":{"type":"string"},"workflow":{"type":"string"},"run_id":{"type":"integer"},"folder":{"type":"string"},"filename":{"type":"string"}},"required":["op"]}"""

    override suspend fun invoke(request: BuiltinToolRequest): BuiltinToolResult {
        val args = parseScreenObject(request.argumentsJson)
            ?: return fail("INVALID_ARGUMENTS", "Arguments must be a JSON object.")
        val op = args.text("op") ?: return fail("INVALID_ARGUMENTS", "op is required.")
        val cfg = configProvider()
        val repo = StudioRenderSupport.validRepo(args.text("repo") ?: cfg.repo)
            ?: return fail("INVALID_REPO", "Repository must look like owner/name (e.g. TricretA/zafiro-studio).")

        if (op == "status") {
            return BuiltinToolResult.success(
                message = if (cfg.githubToken.isBlank()) {
                    "GitHub token is not set. Ask the owner to enter it in Settings > General > Video Studio & GitHub."
                } else {
                    "Studio ready (repo=$repo, default_folder=${cfg.outputFolder})."
                },
                data = buildJsonObject {
                    put("configured", cfg.githubToken.isNotBlank())
                    put("repo", repo)
                    put("default_output_folder", cfg.outputFolder)
                },
            )
        }

        val token = cfg.githubToken.trim()
        if (token.isBlank()) {
            return fail(
                "TOKEN_MISSING",
                "GitHub token is not configured. Tell the owner to add it in Settings > General > Video Studio & GitHub.",
            )
        }

        return withContext(Dispatchers.IO) {
            runCatching {
                when (op) {
                    "upload_input" -> uploadInput(repo, token, args)
                    "commit_file" -> commitFile(repo, token, args)
                    "dispatch" -> dispatch(repo, token, args)
                    "poll" -> poll(repo, token, args)
                    "download" -> download(repo, token, cfg, args)
                    else -> fail("INVALID_ARGUMENTS", "Unknown op '$op'.")
                }
            }.getOrElse { t ->
                fail("STUDIO_ERROR", t.message ?: "Studio operation failed.")
            }
        }
    }

    private suspend fun uploadInput(repo: String, token: String, args: JsonObject): BuiltinToolResult {
        val id = args.text("id")?.let(StudioRenderSupport::safeTagPart)
            ?: return fail("INVALID_ARGUMENTS", "id is required for upload_input.")
        val localPath = args.text("local_path")
            ?: return fail("INVALID_ARGUMENTS", "local_path is required for upload_input.")
        val ctx = ContextProvider.await().applicationContext
        val file = PublicFileExporter.resolveReadableFile(ctx, localPath)
            ?: return fail("FILE_NOT_FOUND", "Cannot read local file at $localPath.")
        val assetName = PublicFileExporter.safeFileName(args.text("asset_name") ?: file.name)
        val tag = "input-$id"

        val release = ensureRelease(repo, token, tag)
        val releaseId = release["id"]?.jsonPrimitive?.longOrNull
            ?: error("GitHub release did not return an id.")
        val existingAssets = release["assets"] as? JsonArray
        existingAssets?.mapNotNull { it as? JsonObject }
            ?.firstOrNull { it["name"]?.jsonPrimitive?.contentOrNull == assetName }
            ?.get("id")?.jsonPrimitive?.longOrNull
            ?.let { oldId ->
                apiRequest("DELETE", "$GITHUB_API/repos/$repo/releases/assets/$oldId", token)
            }

        val encodedName = URLEncoder.encode(assetName, "UTF-8").replace("+", "%20")
        val uploadUrl = "$GITHUB_UPLOADS/repos/$repo/releases/$releaseId/assets?name=$encodedName"
        val uploaded = streamUpload(uploadUrl, token, file)
        val browserUrl = uploaded["browser_download_url"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val apiUrl = uploaded["url"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val assetId = uploaded["id"]?.jsonPrimitive?.longOrNull ?: 0L

        return BuiltinToolResult.success(
            message = "Uploaded $assetName (${file.length()} bytes) to release $tag. URL: $browserUrl",
            data = buildJsonObject {
                put("tag", tag)
                put("asset_id", assetId)
                put("asset_name", assetName)
                put("bytes", file.length())
                put("download_url", browserUrl)
                put("api_asset_url", apiUrl)
            },
        )
    }

    private suspend fun commitFile(repo: String, token: String, args: JsonObject): BuiltinToolResult {
        val repoPath = args.text("repo_path")?.let(StudioRenderSupport::validRepoPath)
            ?: return fail("INVALID_ARGUMENTS", "Valid repo_path is required (e.g. jobs/demo.json).")
        val branch = args.text("branch") ?: "main"
        val rawBytes = when {
            args.containsKey("content") -> (args["content"] as? JsonPrimitive)?.content?.toByteArray(Charsets.UTF_8)
                ?: return fail("INVALID_ARGUMENTS", "content must be a string.")
            args.text("local_path") != null -> {
                val ctx = ContextProvider.await().applicationContext
                val file = PublicFileExporter.resolveReadableFile(ctx, args.text("local_path")!!)
                    ?: return fail("FILE_NOT_FOUND", "Cannot read local file.")
                if (file.length() > MAX_CONTENTS_BYTES) {
                    return fail("TOO_LARGE", "Files over 20 MB must use op=upload_input instead of commit_file.")
                }
                file.readBytes()
            }
            else -> return fail("INVALID_ARGUMENTS", "Pass content or local_path.")
        }

        val encodedPath = repoPath.split('/').joinToString("/") { URLEncoder.encode(it, "UTF-8").replace("+", "%20") }
        val contentsUrl = "$GITHUB_API/repos/$repo/contents/$encodedPath"
        val existingSha = runCatching {
            val res = apiJson("GET", "$contentsUrl?ref=$branch", token)
            res["sha"]?.jsonPrimitive?.contentOrNull
        }.getOrNull()

        val message = args.text("message") ?: "zafiro: update $repoPath"
        val body = buildJsonObject {
            put("message", message)
            put("content", Base64.getEncoder().encodeToString(rawBytes))
            put("branch", branch)
            if (!existingSha.isNullOrBlank()) put("sha", existingSha)
        }.toString()

        val resp = apiJson("PUT", contentsUrl, token, body)
        val contentObj = resp["content"] as? JsonObject
        val htmlUrl = contentObj?.get("html_url")?.jsonPrimitive?.contentOrNull.orEmpty()
        val sha = contentObj?.get("sha")?.jsonPrimitive?.contentOrNull.orEmpty()
        return BuiltinToolResult.success(
            message = "Committed $repoPath to $repo ($branch).",
            data = buildJsonObject {
                put("repo_path", repoPath)
                put("sha", sha)
                put("html_url", htmlUrl)
            },
        )
    }

    private suspend fun dispatch(repo: String, token: String, args: JsonObject): BuiltinToolResult {
        val jobPath = args.text("job_path")?.let(StudioRenderSupport::validRepoPath)
            ?: return fail("INVALID_ARGUMENTS", "job_path is required (e.g. jobs/demo.json).")
        val workflow = args.text("workflow") ?: "render.yml"
        val branch = args.text("branch") ?: "main"

        val body = buildJsonObject {
            put("ref", branch)
            put("inputs", buildJsonObject { put("job", jobPath) })
        }.toString()

        val dispatchUrl = "$GITHUB_API/repos/$repo/actions/workflows/$workflow/dispatches"
        val resp = apiRequest("POST", dispatchUrl, token, body)
        if (resp.status !in 200..299) {
            return fail("HTTP_${resp.status}", "Dispatch failed (HTTP ${resp.status}): ${resp.body.take(400)}")
        }

        delay(2_000)
        val runsJson = runCatching {
            apiJson("GET", "$GITHUB_API/repos/$repo/actions/workflows/$workflow/runs?branch=$branch&event=workflow_dispatch&per_page=5", token)
        }.getOrNull()
        val latestRun = (runsJson?.get("workflow_runs") as? JsonArray)?.firstOrNull() as? JsonObject
        val runId = latestRun?.get("id")?.jsonPrimitive?.longOrNull
        val htmlUrl = latestRun?.get("html_url")?.jsonPrimitive?.contentOrNull.orEmpty()

        return BuiltinToolResult.success(
            message = if (runId != null) "Dispatched $workflow for $jobPath (run_id=$runId)." else "Dispatched $workflow for $jobPath.",
            data = buildJsonObject {
                put("job_path", jobPath)
                if (runId != null) put("run_id", runId)
                if (htmlUrl.isNotBlank()) put("html_url", htmlUrl)
            },
        )
    }

    private fun poll(repo: String, token: String, args: JsonObject): BuiltinToolResult {
        val runId = (args["run_id"] as? JsonPrimitive)?.longOrNull
        val runObj: JsonObject = if (runId != null) {
            apiJson("GET", "$GITHUB_API/repos/$repo/actions/runs/$runId", token)
        } else {
            val workflow = args.text("workflow") ?: "render.yml"
            val runs = apiJson("GET", "$GITHUB_API/repos/$repo/actions/workflows/$workflow/runs?event=workflow_dispatch&per_page=1", token)
            (runs["workflow_runs"] as? JsonArray)?.firstOrNull() as? JsonObject
                ?: return fail("NOT_FOUND", "No workflow runs found for $workflow.")
        }
        val id = runObj["id"]?.jsonPrimitive?.longOrNull ?: 0L
        val status = runObj["status"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val conclusion = runObj["conclusion"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val htmlUrl = runObj["html_url"]?.jsonPrimitive?.contentOrNull.orEmpty()
        return BuiltinToolResult.success(
            message = "Run $id: status=$status" + if (conclusion.isNotBlank()) ", conclusion=$conclusion" else "",
            data = buildJsonObject {
                put("run_id", id)
                put("status", status)
                put("conclusion", conclusion)
                put("html_url", htmlUrl)
            },
        )
    }

    private suspend fun download(repo: String, token: String, cfg: StudioConfig, args: JsonObject): BuiltinToolResult {
        val tag = args.text("tag") ?: args.text("id")?.let { "job-${StudioRenderSupport.safeTagPart(it)}" }
            ?: return fail("INVALID_ARGUMENTS", "Pass id or tag to download.")
        val release = apiJson("GET", "$GITHUB_API/repos/$repo/releases/tags/$tag", token)
        val assets = release["assets"] as? JsonArray
            ?: return fail("NO_ASSETS", "Release $tag has no assets.")
        val asset = StudioRenderSupport.pickMp4Asset(assets, args.text("asset_name"))
            ?: return fail("NO_ASSETS", "No matching asset found on release $tag.")
        val assetId = asset["id"]?.jsonPrimitive?.longOrNull
            ?: return fail("NO_ASSETS", "Asset has no id.")
        val assetName = asset["name"]?.jsonPrimitive?.contentOrNull ?: "$tag.mp4"
        val releaseUrl = release["html_url"]?.jsonPrimitive?.contentOrNull.orEmpty()

        val ctx = ContextProvider.await().applicationContext
        val tempFile = File(ctx.cacheDir, "studio-dl-${System.currentTimeMillis()}.tmp")
        try {
            downloadReleaseAsset("$GITHUB_API/repos/$repo/releases/assets/$assetId", token, tempFile)
            val targetFolder = args.text("folder") ?: cfg.outputFolder
            val targetName = args.text("filename") ?: assetName
            val exportRes = PublicFileExporter.exportFile(ctx, tempFile, targetFolder, targetName).getOrThrow()
            return BuiltinToolResult.success(
                message = "Saved ${exportRes.bytes} bytes to ${exportRes.displayPath}",
                data = buildJsonObject {
                    put("display_path", exportRes.displayPath)
                    put("relative_path", exportRes.relativePath)
                    exportRes.contentUri?.let { put("content_uri", it) }
                    put("bytes", exportRes.bytes)
                    put("asset_name", assetName)
                    if (releaseUrl.isNotBlank()) put("release_url", releaseUrl)
                },
            )
        } finally {
            tempFile.delete()
        }
    }

    private fun ensureRelease(repo: String, token: String, tag: String): JsonObject {
        val get = apiRequest("GET", "$GITHUB_API/repos/$repo/releases/tags/$tag", token)
        if (get.status in 200..299) return Json.parseToJsonElement(get.body).jsonObject
        if (get.status != 404) error("Failed to query release $tag: HTTP ${get.status} ${get.body.take(200)}")
        val createBody = buildJsonObject {
            put("tag_name", tag)
            put("name", tag)
            put("prerelease", true)
            put("body", "Input media staged by Zafiro Autonomous.")
        }.toString()
        return apiJson("POST", "$GITHUB_API/repos/$repo/releases", token, createBody)
    }

    private data class ApiResponse(val status: Int, val body: String)

    private fun apiJson(method: String, url: String, token: String, body: String? = null): JsonObject {
        val resp = apiRequest(method, url, token, body)
        if (resp.status !in 200..299) {
            error("GitHub API $method $url failed (HTTP ${resp.status}): ${resp.body.take(300)}")
        }
        return Json.parseToJsonElement(resp.body).jsonObject
    }

    private fun apiRequest(method: String, url: String, token: String, body: String? = null): ApiResponse {
        HttpRequestPolicy.check(url)?.let { error(it) }
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            instanceFollowRedirects = false
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            setRequestProperty("User-Agent", "Zafiro-Autonomous")
            if (body != null) {
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                doOutput = true
                outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
        }
        return try {
            val status = conn.responseCode
            val stream = if (status >= 400) conn.errorStream else conn.inputStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            ApiResponse(status, text)
        } finally {
            conn.disconnect()
        }
    }

    private fun streamUpload(url: String, token: String, file: File): JsonObject {
        HttpRequestPolicy.check(url)?.let { error(it) }
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = TIMEOUT_MS
            readTimeout = TRANSFER_TIMEOUT_MS
            instanceFollowRedirects = false
            doOutput = true
            setFixedLengthStreamingMode(file.length())
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("Content-Type", "application/octet-stream")
            setRequestProperty("User-Agent", "Zafiro-Autonomous")
        }
        return try {
            conn.outputStream.use { out ->
                file.inputStream().use { input ->
                    val buf = ByteArray(BUFFER_SIZE)
                    while (true) {
                        val n = input.read(buf)
                        if (n <= 0) break
                        out.write(buf, 0, n)
                    }
                }
            }
            val status = conn.responseCode
            val stream = if (status >= 400) conn.errorStream else conn.inputStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (status !in 200..299) error("Asset upload failed (HTTP $status): ${text.take(300)}")
            Json.parseToJsonElement(text).jsonObject
        } finally {
            conn.disconnect()
        }
    }

    private fun downloadReleaseAsset(apiAssetUrl: String, token: String, dest: File) {
        var currentUrl = apiAssetUrl
        var sendAuth = true
        repeat(5) {
            HttpRequestPolicy.check(currentUrl)?.let { error(it) }
            val conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TRANSFER_TIMEOUT_MS
                instanceFollowRedirects = false
                setRequestProperty("User-Agent", "Zafiro-Autonomous")
                if (sendAuth) {
                    setRequestProperty("Authorization", "Bearer $token")
                    setRequestProperty("Accept", "application/octet-stream")
                }
            }
            try {
                val status = conn.responseCode
                if (status in 300..399) {
                    val loc = conn.getHeaderField("Location") ?: error("Redirect missing Location header.")
                    // Drop Authorization when GitHub redirects to signed CDN/S3 storage.
                    sendAuth = false
                    currentUrl = loc
                    return@repeat
                }
                if (status !in 200..299) {
                    val err = conn.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                    error("Asset download failed (HTTP $status): ${err.take(300)}")
                }
                conn.inputStream.use { input ->
                    dest.outputStream().use { out -> input.copyTo(out, BUFFER_SIZE) }
                }
                return
            } finally {
                conn.disconnect()
            }
        }
        error("Too many redirects downloading asset.")
    }

    private fun fail(code: String, message: String) = BuiltinToolResult.failure(code = code, message = message)

    private companion object {
        const val GITHUB_API = "https://api.github.com"
        const val GITHUB_UPLOADS = "https://uploads.github.com"
        const val TIMEOUT_MS = 30_000
        const val TRANSFER_TIMEOUT_MS = 180_000
        const val BUFFER_SIZE = 65_536
        const val MAX_CONTENTS_BYTES = 20L * 1024 * 1024
    }
}
