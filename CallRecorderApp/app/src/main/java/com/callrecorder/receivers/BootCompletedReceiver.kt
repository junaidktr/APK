package com.callrecorder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.callrecorder.services.CallRecordingService

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            // Optionally start the recording service or show notification
            // that the app is ready to record calls
            android.util.Log.d("BootCompletedReceiver", "Device boot completed")
        }
    }
}
