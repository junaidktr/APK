package com.example.callrecorder.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(private val context: Context) {
    
    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var startTime: Long = 0
    private var isRecording = false
    
    companion object {
        const val TAG = "AudioRecorder"
    }
    
    fun startRecording(callType: String, phoneNumber: String?): Boolean {
        if (isRecording) {
            Log.w(TAG, "Already recording")
            return false
        }
        
        try {
            // Create recording directory
            val recordingsDir = getRecordingsDirectory()
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
            }
            
            // Generate file name
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${RecordingConstants.FILE_PREFIX}${callType}_${timestamp}${RecordingConstants.FILE_EXTENSION}"
            val file = File(recordingsDir, fileName)
            currentFilePath = file.absolutePath
            
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
                setAudioEncodingBitRate(RecordingConstants.AUDIO_BIT_RATE)
                setAudioSamplingRate(RecordingConstants.AUDIO_SAMPLE_RATE)
                setOutputFile(file.absolutePath)
                
                prepare()
                start()
            }
            
            startTime = System.currentTimeMillis()
            isRecording = true
            
            Log.d(TAG, "Recording started: ${file.absolutePath}")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            return false
        }
    }
    
    fun stopRecording(): Pair<String?, Long> {
        if (!isRecording) {
            Log.w(TAG, "Not recording")
            return Pair(null, 0L)
        }
        
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            
            val duration = (System.currentTimeMillis() - startTime) / 1000
            val filePath = currentFilePath
            
            Log.d(TAG, "Recording stopped: $filePath, Duration: ${duration}s")
            
            mediaRecorder = null
            currentFilePath = null
            isRecording = false
            startTime = 0
            
            return Pair(filePath, duration)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            mediaRecorder?.release()
            mediaRecorder = null
            currentFilePath = null
            isRecording = false
            return Pair(null, 0L)
        }
    }
    
    fun isCurrentlyRecording(): Boolean = isRecording
    
    fun getCurrentFilePath(): String? = currentFilePath
    
    fun getRecordingDuration(): Long {
        return if (isRecording && startTime > 0) {
            (System.currentTimeMillis() - startTime) / 1000
        } else {
            0L
        }
    }
    
    private fun getRecordingsDirectory(): File {
        val externalDir = context.getExternalFilesDir(null)
        return File(externalDir, RecordingConstants.RECORDINGS_FOLDER)
    }
    
    fun getRecordingsList(): List<File> {
        val dir = getRecordingsDirectory()
        return if (dir.exists()) {
            dir.listFiles { file -> 
                file.isFile && file.extension.equals("mp3", ignoreCase = true) ||
                file.extension.equals("m4a", ignoreCase = true) ||
                file.extension.equals("aac", ignoreCase = true)
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun deleteRecording(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete recording", e)
            false
        }
    }
}
