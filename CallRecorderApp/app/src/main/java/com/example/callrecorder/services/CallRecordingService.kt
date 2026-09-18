package com.example.callrecorder.services

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.callrecorder.R
import com.example.callrecorder.database.AppDatabase
import com.example.callrecorder.models.Recording
import com.example.callrecorder.utils.AudioRecorder
import com.example.callrecorder.utils.RecordingConstants
import kotlinx.coroutines.*
import java.io.File
import java.util.Date

class CallRecordingService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var audioRecorder: AudioRecorder? = null
    private var currentCallType: String = RecordingConstants.CALL_TYPE_INCOMING
    private var currentPhoneNumber: String? = null
    private var currentContactName: String? = null
    
    companion object {
        const val TAG = "CallRecordingService"
        var isRunning: Boolean = false
    }

    inner class LocalBinder : android.os.Binder() {
        fun getService(): CallRecordingService = this@CallRecordingService
    }

    override fun onCreate() {
        super.onCreate()
        audioRecorder = AudioRecorder(this)
        isRunning = true
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with action: ${intent?.action}")
        
        when (intent?.action) {
            RecordingConstants.ACTION_START_RECORDING -> {
                currentCallType = intent.getStringExtra(RecordingConstants.EXTRA_CALL_TYPE) 
                    ?: RecordingConstants.CALL_TYPE_INCOMING
                currentPhoneNumber = intent.getStringExtra(RecordingConstants.EXTRA_PHONE_NUMBER)
                currentContactName = intent.getStringExtra(RecordingConstants.EXTRA_CONTACT_NAME)
                
                startForegroundService()
                startRecording()
            }
            
            RecordingConstants.ACTION_STOP_RECORDING -> {
                stopRecording()
                stopForegroundService()
            }
        }
        
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = createNotification("Recording call...")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(RecordingConstants.NOTIFICATION_ID, notification)
        } else {
            @Suppress("DEPRECATION")
            startForeground(RecordingConstants.NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, com.example.callrecorder.activities.MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, RecordingConstants.CHANNEL_ID)
            .setContentTitle("Call Recorder")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun startRecording() {
        serviceScope.launch {
            try {
                val success = audioRecorder?.startRecording(currentCallType, currentPhoneNumber)
                
                if (success == true) {
                    Log.d(TAG, "Recording started for $currentCallType call")
                    
                    // Update notification
                    val notification = createNotification("Recording $currentCallType call")
                    val notificationManager = getSystemService(NotificationManager::class.java)
                    notificationManager.notify(RecordingConstants.NOTIFICATION_ID, notification)
                } else {
                    Log.e(TAG, "Failed to start recording")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting recording", e)
            }
        }
    }

    private fun stopRecording() {
        serviceScope.launch {
            try {
                val (filePath, duration) = audioRecorder?.stopRecording() ?: Pair(null, 0L)
                
                if (filePath != null && duration > 0) {
                    // Save recording to database
                    saveRecordingToDatabase(filePath, duration)
                    Log.d(TAG, "Recording saved: $filePath")
                }
                
                // Update notification
                val notification = createNotification("Recording stopped")
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(RecordingConstants.NOTIFICATION_ID, notification)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recording", e)
            }
        }
    }

    private suspend fun saveRecordingToDatabase(filePath: String, duration: Long) {
        withContext(Dispatchers.IO) {
            try {
                val database = AppDatabase.getInstance(applicationContext)
                val recording = Recording(
                    phoneNumber = currentPhoneNumber,
                    contactName = currentContactName,
                    callType = currentCallType,
                    recordingPath = filePath,
                    duration = duration,
                    date = Date(),
                    fileSize = File(filePath).length()
                )
                
                database.recordingDao().insertRecording(recording)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving recording to database", e)
            }
        }
    }

    private fun stopForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        audioRecorder = null
        isRunning = false
        Log.d(TAG, "Service destroyed")
    }
}
