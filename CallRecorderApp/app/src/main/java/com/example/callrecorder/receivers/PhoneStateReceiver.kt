package com.example.callrecorder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.callrecorder.services.CallRecordingService
import com.example.callrecorder.utils.RecordingConstants

class PhoneStateReceiver : BroadcastReceiver() {
    
    companion object {
        const val TAG = "PhoneStateReceiver"
        private var isCallActive = false
        private var currentPhoneNumber: String? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED && 
            intent.action != Intent.ACTION_NEW_OUTGOING_CALL) {
            return
        }

        try {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            currentPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            
            Log.d(TAG, "Phone state: $state, Number: $currentPhoneNumber")
            
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    // Incoming call
                    val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    Log.d(TAG, "Incoming call from: $number")
                    isCallActive = false
                }
                
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    // Call answered or dialing
                    if (!isCallActive) {
                        isCallActive = true
                        
                        // Start recording
                        val serviceIntent = Intent(context, CallRecordingService::class.java).apply {
                            action = RecordingConstants.ACTION_START_RECORDING
                            putExtra(RecordingConstants.EXTRA_CALL_TYPE, 
                                if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) 
                                    RecordingConstants.CALL_TYPE_OUTGOING 
                                else 
                                    RecordingConstants.CALL_TYPE_INCOMING)
                            putExtra(RecordingConstants.EXTRA_PHONE_NUMBER, currentPhoneNumber)
                        }
                        
                        try {
                            context.startForegroundService(serviceIntent)
                            Log.d(TAG, "Started recording regular call")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to start recording service", e)
                        }
                    }
                }
                
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    // Call ended
                    if (isCallActive) {
                        isCallActive = false
                        
                        // Stop recording
                        val serviceIntent = Intent(context, CallRecordingService::class.java).apply {
                            action = RecordingConstants.ACTION_STOP_RECORDING
                        }
                        
                        try {
                            context.startService(serviceIntent)
                            Log.d(TAG, "Stopped recording regular call")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to stop recording service", e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing phone state", e)
        }
    }
}
