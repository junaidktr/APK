package com.callrecorder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_recordings")
data class CallRecording(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val contactName: String?,
    val callType: CallType, // INCOMING, OUTGOING, WHATSAPP_AUDIO, WHATSAPP_VIDEO
    val recordingPath: String,
    val duration: Long, // in seconds
    val timestamp: Long,
    val fileSize: Long, // in bytes
    val isDeleted: Boolean = false
)

enum class CallType {
    INCOMING,
    OUTGOING,
    WHATSAPP_AUDIO,
    WHATSAPP_VIDEO,
    NETWORK_CALL
}
