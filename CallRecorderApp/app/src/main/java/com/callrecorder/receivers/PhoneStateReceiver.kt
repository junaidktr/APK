package com.callrecorder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.callrecorder.models.CallType
import com.callrecorder.services.CallRecordingService
import com.callrecorder.utils.ContactHelper

class PhoneStateReceiver : BroadcastReceiver() {

    companion object {
        private var isCallActive = false
        private var currentPhoneNumber: String = ""
        private var callStartTime: Long = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            return
        }

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                // Incoming call
                currentPhoneNumber = incomingNumber ?: "Unknown"
                isCallActive = true
                callStartTime = System.currentTimeMillis()
            }
            
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                // Call answered or outgoing call dialed
                if (!isCallActive && incomingNumber != null) {
                    // Outgoing call
                    currentPhoneNumber = incomingNumber
                    isCallActive = true
                    callStartTime = System.currentTimeMillis()
                } else if (isCallActive) {
                    // Incoming call answered
                    startRecording(context, currentPhoneNumber, CallType.INCOMING)
                }
            }
            
            TelephonyManager.EXTRA_STATE_IDLE -> {
                // Call ended
                if (isCallActive) {
                    stopRecording(context)
                    isCallActive = false
                    currentPhoneNumber = ""
                }
            }
        }
    }

    private fun startRecording(context: Context, phoneNumber: String, callType: CallType) {
        val contactName = ContactHelper.getContactNameByNumber(context, phoneNumber)
        val displayNumber = contactName ?: phoneNumber

        val intent = Intent(context, CallRecordingService::class.java).apply {
            action = CallRecordingService.ACTION_START_RECORDING
            putExtra(CallRecordingService.EXTRA_PHONE_NUMBER, displayNumber)
            putExtra(CallRecordingService.EXTRA_CALL_TYPE, callType)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopRecording(context: Context) {
        val intent = Intent(context, CallRecordingService::class.java).apply {
            action = CallRecordingService.ACTION_STOP_RECORDING
        }
        context.startService(intent)
    }
}
