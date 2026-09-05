package com.example.callrecorder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.callrecorder.services.CallRecordingService

class BootCompletedReceiver : BroadcastReceiver() {
    
    companion object {
        const val TAG = "BootCompletedReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            Log.d(TAG, "Boot completed, starting recording service")
            
            // Note: We don't auto-start recording on boot
            // The service will be triggered by phone state changes
            // This receiver just ensures the app is ready
            
            try {
                // Optionally start the service in idle mode
                val serviceIntent = Intent(context, CallRecordingService::class.java)
                context.startForegroundService(serviceIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start service on boot", e)
            }
        }
    }
}
