package com.example.callrecorder.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import com.example.callrecorder.utils.RecordingConstants

/**
 * Accessibility Service to detect WhatsApp calls
 * Note: This requires user to enable accessibility permission manually
 */
class WhatsappAccessibilityService : AccessibilityService() {

    private var isWhatsAppCallActive = false
    private var callStartTime: Long = 0
    
    companion object {
        const val TAG = "WhatsAppAccessibility"
        var instance: WhatsappAccessibilityService? = null
        
        // Callbacks for call events
        var onWhatsAppCallStart: (() -> Unit)? = null
        var onWhatsAppCallEnd: (() -> Unit)? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        serviceInfo = info
        
        Log.d(TAG, "WhatsApp Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        try {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    checkForWhatsAppCall(event)
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    checkForCallButtons(event)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing accessibility event", e)
        }
    }

    private fun checkForWhatsAppCall(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        if (packageName.contains("whatsapp", ignoreCase = true)) {
            val source = event.source ?: return
            
            // Look for call-related UI elements
            val text = event.text?.joinToString(" ") ?: ""
            
            // Check for common WhatsApp call indicators
            if (text.contains("call", ignoreCase = true) || 
                text.contains("ended", ignoreCase = true) ||
                hasCallButton(source)) {
                
                if (!isWhatsAppCallActive && hasActiveCallIndicator(source)) {
                    isWhatsAppCallActive = true
                    callStartTime = System.currentTimeMillis()
                    Log.d(TAG, "WhatsApp call started")
                    onWhatsAppCallStart?.invoke()
                    
                    // Start recording service
                    startRecordingService()
                } else if (isWhatsAppCallActive && hasCallEndedIndicator(source, text)) {
                    Log.d(TAG, "WhatsApp call ended")
                    onWhatsAppCallEnd?.invoke()
                    stopRecordingService()
                }
            }
        }
    }

    private fun checkForCallButtons(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        if (packageName.contains("whatsapp", ignoreCase = true)) {
            event.source?.let { node ->
                findEndCallButton(node)?.let { endButton ->
                    if (!isWhatsAppCallActive) {
                        isWhatsAppCallActive = true
                        callStartTime = System.currentTimeMillis()
                        Log.d(TAG, "WhatsApp call detected via end button")
                        onWhatsAppCallStart?.invoke()
                        startRecordingService()
                    }
                }
            }
        }
    }

    private fun hasCallButton(node: AccessibilityNodeInfo): Boolean {
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val text = child.text?.toString()?.lowercase() ?: ""
            val className = child.className?.toString() ?: ""
            
            if (text.contains("end") || text.contains("hang") || 
                className.contains("ImageButton") && child.isClickable) {
                recycleNode(child)
                return true
            }
            
            if (hasCallButton(child)) {
                recycleNode(child)
                return true
            }
            recycleNode(child)
        }
        return false
    }

    private fun findEndCallButton(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val text = child.text?.toString()?.lowercase() ?: ""
            val contentDesc = child.contentDescription?.toString()?.lowercase() ?: ""
            
            if ((text.contains("end") || contentDesc.contains("end")) && child.isClickable) {
                return child
            }
            
            val found = findEndCallButton(child)
            if (found != null) {
                return found
            }
        }
        return null
    }

    private fun hasActiveCallIndicator(node: AccessibilityNodeInfo): Boolean {
        val text = node.text?.toString()?.lowercase() ?: ""
        val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
        
        return text.contains("calling") || text.contains("on hold") ||
               contentDesc.contains("calling") || text.contains("\\d+:\\d+".toRegex())
    }

    private fun hasCallEndedIndicator(node: AccessibilityNodeInfo, text: String): Boolean {
        return text.contains("ended", ignoreCase = true) || 
               text.contains("call finished", ignoreCase = true)
    }

    private fun startRecordingService() {
        val intent = Intent(this, CallRecordingService::class.java).apply {
            action = RecordingConstants.ACTION_START_RECORDING
            putExtra(RecordingConstants.EXTRA_CALL_TYPE, RecordingConstants.CALL_TYPE_WHATSAPP)
        }
        try {
            startForegroundService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording service", e)
        }
    }

    private fun stopRecordingService() {
        isWhatsAppCallActive = false
        val intent = Intent(this, CallRecordingService::class.java).apply {
            action = RecordingConstants.ACTION_STOP_RECORDING
        }
        try {
            startService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording service", e)
        }
    }

    private fun recycleNode(node: AccessibilityNodeInfo?) {
        try {
            node?.recycle()
        } catch (e: Exception) {
            // Ignore recycling errors
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "WhatsApp Accessibility Service Destroyed")
    }
}
