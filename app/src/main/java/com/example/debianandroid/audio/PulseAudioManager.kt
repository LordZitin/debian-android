package com.example.debianandroid.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object PulseAudioManager {

    private var currentTrack: AudioTrack? = null
    private var isPlaying = false

    /**
     * Generates a smooth melodic chime tone using AudioTrack
     * to verify the PulseAudio sound pipeline output.
     */
    suspend fun playTestSound(sampleRate: Int = 44100, volume: Float = 0.8f): Boolean = withContext(Dispatchers.IO) {
        try {
            stopAudio()

            val durationSeconds = 0.8
            val numSamples = (durationSeconds * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            // Play pleasant dual tone chord (523.25Hz C5 + 659.25Hz E5)
            val freq1 = 523.25
            val freq2 = 659.25

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = when {
                    t < 0.05 -> t / 0.05
                    t > 0.6 -> (0.8 - t) / 0.2
                    else -> 1.0
                }
                val sampleValue = ((sin(2.0 * Math.PI * freq1 * t) * 0.5 + sin(2.0 * Math.PI * freq2 * t) * 0.5) * envelope * volume * Short.MAX_VALUE)
                buffer[i] = sampleValue.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBufferSize))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            currentTrack = track
            track.write(buffer, 0, buffer.size)
            track.play()
            isPlaying = true

            kotlinx.coroutines.delay(850)
            track.stop()
            track.release()
            currentTrack = null
            isPlaying = false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun stopAudio() {
        try {
            currentTrack?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
        } catch (_: Exception) {
        } finally {
            currentTrack = null
            isPlaying = false
        }
    }
}
