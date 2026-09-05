package com.example.callrecorder.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val phoneNumber: String?,
    val contactName: String?,
    val callType: String, // "INCOMING", "OUTGOING", "WHATSAPP"
    val recordingPath: String,
    val duration: Long, // in seconds
    val date: Date,
    val fileSize: Long, // in bytes
    val isPlayed: Boolean = false
)
