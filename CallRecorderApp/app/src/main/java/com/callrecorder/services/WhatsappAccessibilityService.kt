package com.callrecorder.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.callrecorder.models.CallType

class WhatsappAccessibilityService : AccessibilityService() {

    companion object {
        var isWhatsAppCallActive = false
        var currentWhatsAppCallType: CallType? = null
        private var listener: ((Boolean, CallType?) -> Unit)? = null

        fun setCallStateListener(listener: (Boolean, CallType?) -> Unit) {
            this.listener = listener
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.DEFAULT or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                checkForWhatsAppCall(event)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (isWhatsAppCallActive) {
                    checkForCallEnd(event)
                }
            }
        }
    }

    private fun checkForWhatsAppCall(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        // Check if WhatsApp is the active app
        if (!packageName.contains("whatsapp", ignoreCase = true)) {
            return
        }

        event.source?.let { rootNode ->
            // Look for WhatsApp call UI elements
            val callIndicators = listOf(
                "End call",
                "Mute",
                "Speaker",
                "Video call",
                "Audio call"
            )

            val hasCallUI = findTextInTree(rootNode, callIndicators)
            
            if (hasCallUI && !isWhatsAppCallActive) {
                isWhatsAppCallActive = true
                
                // Determine if it's audio or video call
                currentWhatsAppCallType = if (findTextInTree(rootNode, listOf("Video call"))) {
                    CallType.WHATSAPP_VIDEO
                } else {
                    CallType.WHATSAPP_AUDIO
                }

                listener?.invoke(true, currentWhatsAppCallType)
                
                // Start recording service
                startRecordingService()
            }
        }
    }

    private fun checkForCallEnd(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        if (!packageName.contains("whatsapp", ignoreCase = true)) {
            return
        }

        event.source?.let { rootNode ->
            val callIndicators = listOf("End call", "Mute", "Speaker")
            val hasCallUI = findTextInTree(rootNode, callIndicators)

            if (!hasCallUI && isWhatsAppCallActive) {
                // Call has ended
                isWhatsAppCallActive = false
                val callType = currentWhatsAppCallType
                currentWhatsAppCallType = null
                
                listener?.invoke(false, callType)
                
                // Stop recording service
                stopRecordingService()
            }
        }
    }

    private fun findTextInTree(node: AccessibilityNodeInfo, texts: List<String>): Boolean {
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            
            val text = child.text?.toString()
            if (text != null && texts.any { text.contains(it, ignoreCase = true) }) {
                return true
            }

            if (findTextInTree(child, texts)) {
                return true
            }
        }
        return false
    }

    private fun startRecordingService() {
        val intent = Intent(this, CallRecordingService::class.java).apply {
            action = CallRecordingService.ACTION_START_RECORDING
            putExtra(CallRecordingService.EXTRA_PHONE_NUMBER, "WhatsApp_Call")
            putExtra(CallRecordingService.EXTRA_CALL_TYPE, currentWhatsAppCallType)
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopRecordingService() {
        val intent = Intent(this, CallRecordingService::class.java).apply {
            action = CallRecordingService.ACTION_STOP_RECORDING
        }
        startService(intent)
    }

    override fun onInterrupt() {
        // Called when the system wants to interrupt the service
    }

    override fun onDestroy() {
        super.onDestroy()
        isWhatsAppCallActive = false
        currentWhatsAppCallType = null
    }
}
