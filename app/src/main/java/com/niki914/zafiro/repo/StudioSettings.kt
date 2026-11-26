package com.niki914.zafiro.repo

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.niki914.zafiro.api.StudioConfig
import com.niki914.zafiro.api.util.runCatchingCancellable
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypted storage for Video Studio settings (GitHub token encrypted with AndroidKeystore AES-GCM,
 * plus render repo and default public output folder).
 */
object StudioSettings {
    private const val PREFS_NAME = "studio_encrypted_settings"
    private const val KEY_TOKEN = "github_token"
    private const val KEY_REPO = "repo"
    private const val KEY_OUTPUT_FOLDER = "output_folder"
    private const val KEYSTORE_ALIAS = "zafiro_studio_aes_gcm"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun read(context: Context): StudioConfig {
        val p = prefs(context)
        val rawToken = p.getString(KEY_TOKEN, null).orEmpty()
        val token = decrypt(rawToken).trim()
        val repo = p.getString(KEY_REPO, null)?.trim().orEmpty().ifBlank { StudioConfig.DEFAULT_REPO }
        val folder = p.getString(KEY_OUTPUT_FOLDER, null)?.trim().orEmpty().ifBlank { StudioConfig.DEFAULT_OUTPUT_FOLDER }
        return StudioConfig(
            githubToken = token,
            repo = repo,
            outputFolder = folder,
        )
    }

    fun write(context: Context, config: StudioConfig) {
        val token = config.githubToken.trim()
        val repo = config.repo.trim().ifBlank { StudioConfig.DEFAULT_REPO }
        val folder = config.outputFolder.trim().ifBlank { StudioConfig.DEFAULT_OUTPUT_FOLDER }
        prefs(context).edit()
            .putString(KEY_TOKEN, if (token.isEmpty()) "" else encrypt(token))
            .putString(KEY_REPO, repo)
            .putString(KEY_OUTPUT_FOLDER, folder)
            .apply()
    }

    private fun encrypt(plainText: String): String =
        runCatchingCancellable {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, keystoreKey())
            val combined = cipher.iv + cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            "enc:" + Base64.encodeToString(combined, Base64.NO_WRAP)
        }.getOrElse { "plain:$plainText" }

    private fun decrypt(value: String): String = when {
        value.isEmpty() -> ""
        value.startsWith("enc:") -> runCatchingCancellable {
            val combined = Base64.decode(value.removePrefix("enc:"), Base64.NO_WRAP)
            val iv = combined.copyOfRange(0, 12)
            val encrypted = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, keystoreKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        }.getOrDefault("")
        value.startsWith("plain:") -> value.removePrefix("plain:")
        else -> ""
    }

    private fun keystoreKey(): java.security.Key {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        ks.getKey(KEYSTORE_ALIAS, null)?.let { return it }
        val generator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }
}
