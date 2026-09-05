package com.callrecorder.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.callrecorder.R
import com.callrecorder.activities.MainActivity
import com.callrecorder.models.CallType
import com.callrecorder.utils.AudioRecorder

class CallRecordingService : Service() {

    companion object {
        const val ACTION_START_RECORDING = "com.callrecorder.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.callrecorder.STOP_RECORDING"
        const val EXTRA_PHONE_NUMBER = "phone_number"
        const val EXTRA_CALL_TYPE = "call_type"
        const val NOTIFICATION_CHANNEL_ID = "call_recording_channel"
        const val NOTIFICATION_ID = 1001
    }

    private var audioRecorder: AudioRecorder? = null
    private var currentPhoneNumber: String = ""
    private var currentCallType: CallType = CallType.INCOMING
    private var isRecording = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        audioRecorder = AudioRecorder(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                currentPhoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: "Unknown"
                currentCallType = intent.getSerializableExtra(EXTRA_CALL_TYPE) as? CallType ?: CallType.INCOMING
                startRecording()
            }
            ACTION_STOP_RECORDING -> {
                stopRecording()
            }
        }
        return START_STICKY
    }

    private fun startRecording() {
        if (isRecording) return

        val filePath = audioRecorder?.startRecording(currentPhoneNumber, currentCallType)
        
        if (filePath != null) {
            isRecording = true
            startForeground(NOTIFICATION_ID, createRecordingNotification())
        }
    }

    private fun stopRecording() {
        if (!isRecording) return

        val duration = audioRecorder?.stopRecording() ?: 0L
        
        // Save recording to database
        saveRecordingToDatabase(duration)
        
        isRecording = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun saveRecordingToDatabase(duration: Long) {
        // This will be implemented with Room database
        // For now, just log the recording details
        android.util.Log.d(
            "CallRecordingService",
            "Recording saved: $currentPhoneNumber, Type: $currentCallType, Duration: ${duration}s"
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Call Recording",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when call recording is in progress"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createRecordingNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Recording Call")
            .setContentText("Recording: $currentPhoneNumber")
            .setSmallIcon(R.drawable.ic_recording)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            audioRecorder?.cancelRecording()
        }
    }
}
