package com.niki914.zafiro.api

/**
 * Configuration for Zafiro's cloud Video Studio (GitHub Actions render repo) and default public output folder.
 * Provided by [StudioConfigProvider] so `agent-runtime` tools can authenticate without exposing tokens in prompts.
 */
data class StudioConfig(
    val githubToken: String = "",
    val repo: String = DEFAULT_REPO,
    val outputFolder: String = DEFAULT_OUTPUT_FOLDER,
) {
    companion object {
        const val DEFAULT_REPO = "TricretA/zafiro-studio"
        const val DEFAULT_OUTPUT_FOLDER = "Download/Zafiro"
    }
}

interface StudioConfigProvider {
    fun get(): StudioConfig
}
