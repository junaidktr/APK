package com.callrecorder.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startTime: Long = 0
    private var isRecording = false

    fun startRecording(phoneNumber: String, callType: CallType): String? {
        try {
            // Create recording directory
            val recordingsDir = File(context.getExternalFilesDir(null), "CallRecordings")
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
            }

            // Generate filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${callType.name}_$phoneNumber_$timestamp.mp4" // Using MP4 for better compatibility
            outputFile = File(recordingsDir, fileName)

            // Initialize MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile?.absolutePath)
                
                try {
                    prepare()
                    start()
                    startTime = System.currentTimeMillis()
                    isRecording = true
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    return null
                }
            }

            return outputFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun stopRecording(): Long {
        var duration = 0L
        
        try {
            if (isRecording) {
                duration = (System.currentTimeMillis() - startTime) / 1000
                
                mediaRecorder?.apply {
                    try {
                        stop()
                        release()
                    } catch (e: RuntimeException) {
                        // Sometimes stop() throws exception if recording was too short
                        e.printStackTrace()
                    }
                }
                
                isRecording = false
                mediaRecorder = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return duration
    }

    fun cancelRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.apply {
                    try {
                        stop()
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                    release()
                }
                
                isRecording = false
                mediaRecorder = null
                
                // Delete the incomplete recording file
                outputFile?.delete()
                outputFile = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isCurrentlyRecording(): Boolean {
        return isRecording
    }

    fun getRecordingFileSize(): Long {
        return outputFile?.length() ?: 0L
    }
}

// Helper enum (should be in models package, but keeping here for simplicity)
enum class CallType {
    INCOMING,
    OUTGOING,
    WHATSAPP_AUDIO,
    WHATSAPP_VIDEO,
    NETWORK_CALL
}
