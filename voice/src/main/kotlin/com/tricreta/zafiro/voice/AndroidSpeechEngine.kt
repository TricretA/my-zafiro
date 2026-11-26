package com.tricreta.zafiro.voice

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.niki914.logging.Logger
import com.tricreta.zafiro.voice.dictation.DictationSession.Fault
import com.tricreta.zafiro.voice.dictation.SpeechEngine

/**
 * [SpeechEngine] over the system recogniser. ONE SpeechRecognizer instance is reused for every session (recreated only on
 * [Fault.FATAL] or consecutive [Fault.TRANSIENT] errors), because destroying and recreating it on every pause causes BUSY/CLIENT
 * loops and repeated chimes. Briefly mutes the non-DND music stream around segment startup so Android's recognition beep does
 * not interrupt dictation. Main thread only (SpeechRecognizer requirement).
 */
class AndroidSpeechEngine(private val context: Context) : SpeechEngine {

    private var recognizer: SpeechRecognizer? = null
    private var recreateBeforeNextListen = false
    private var consecutiveTransientErrors = 0
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val handler = Handler(Looper.getMainLooper())
    private var mutedMusicForChime = false
    private val unmuteRunnable = Runnable { unmuteChimeStream() }

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    override fun listen(languageTag: String?, listener: SpeechEngine.Listener) {
        if (!isAvailable()) {
            listener.onError(
                Fault.FATAL, -2,
                "No speech recognition service is installed on this phone (install or enable Google's speech services).",
            )
            return
        }
        if (recreateBeforeNextListen) {
            destroy()
            recreateBeforeNextListen = false
        }
        val r = recognizer ?: SpeechRecognizer.createSpeechRecognizer(context).also { recognizer = it }
        r.setRecognitionListener(object : RecognitionListener {
            override fun onPartialResults(partialResults: Bundle?) {
                val text = bestText(partialResults)
                if (text.isNotEmpty()) {
                    consecutiveTransientErrors = 0
                    listener.onPartial(text)
                }
            }

            override fun onResults(results: Bundle?) {
                consecutiveTransientErrors = 0
                unmuteChimeStream()
                listener.onResult(bestText(results))
            }

            override fun onError(error: Int) {
                unmuteChimeStream()
                val mapped = SpeechErrors.map(error)
                Logger.w(LOG_TAG, "recognition error code=$error fault=${mapped.fault}")
                when (mapped.fault) {
                    Fault.FATAL -> {
                        consecutiveTransientErrors = 0
                        recreateBeforeNextListen = true
                    }
                    Fault.TRANSIENT -> {
                        if (++consecutiveTransientErrors >= 2) {
                            recreateBeforeNextListen = true
                        }
                    }
                    Fault.NO_SPEECH, Fault.BUSY -> consecutiveTransientErrors = 0
                }
                listener.onError(mapped.fault, error, mapped.message)
            }

            override fun onReadyForSpeech(params: Bundle?) {
                handler.postDelayed(unmuteRunnable, CHIME_UNMUTE_AFTER_READY_MS)
            }

            override fun onBeginningOfSpeech() {
                consecutiveTransientErrors = 0
                unmuteChimeStream()
            }

            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            // Hints only: many recognisers ignore them, which is why the restart loop exists.
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, MIN_SPEECH_HINT_MS)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, SILENCE_HINT_MS)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, POSSIBLY_COMPLETE_SILENCE_HINT_MS)
            languageTag?.let { putExtra(RecognizerIntent.EXTRA_LANGUAGE, it) }
        }
        muteChimeStreamBriefly()
        r.startListening(intent)
    }

    override fun stop() {
        muteChimeStreamBriefly()
        recognizer?.stopListening()
    }

    override fun cancel() {
        unmuteChimeStream()
        runCatching { recognizer?.cancel() }
    }

    override fun release() {
        unmuteChimeStream()
        destroy()
    }

    private fun muteChimeStreamBriefly() {
        val am = audioManager ?: return
        handler.removeCallbacks(unmuteRunnable)
        if (!mutedMusicForChime) {
            val alreadyMuted = runCatching { am.isStreamMute(AudioManager.STREAM_MUSIC) }.getOrDefault(false)
            if (!alreadyMuted && !am.isMusicActive) {
                val muted = runCatching {
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
                }.isSuccess
                if (muted) mutedMusicForChime = true
            }
        }
        handler.postDelayed(unmuteRunnable, CHIME_UNMUTE_FALLBACK_MS)
    }

    private fun unmuteChimeStream() {
        handler.removeCallbacks(unmuteRunnable)
        if (!mutedMusicForChime) return
        mutedMusicForChime = false
        runCatching {
            audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
        }
    }

    private fun destroy() {
        recognizer?.let {
            it.setRecognitionListener(null)
            runCatching { it.cancel() }
            runCatching { it.destroy() }
        }
        recognizer = null
    }

    private fun bestText(bundle: Bundle?): String =
        bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()

    private companion object {
        const val LOG_TAG = "AndroidSpeechEngine"
        const val MIN_SPEECH_HINT_MS = 30_000L
        const val SILENCE_HINT_MS = 5_000L
        const val POSSIBLY_COMPLETE_SILENCE_HINT_MS = 3_500L
        const val CHIME_UNMUTE_AFTER_READY_MS = 200L
        const val CHIME_UNMUTE_FALLBACK_MS = 900L
    }
}
