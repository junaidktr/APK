package com.example.callrecorder.utils

object RecordingConstants {
    // Action constants
    const val ACTION_START_RECORDING = "com.example.callrecorder.START_RECORDING"
    const val ACTION_STOP_RECORDING = "com.example.callrecorder.STOP_RECORDING"
    
    // Call types
    const val CALL_TYPE_INCOMING = "INCOMING"
    const val CALL_TYPE_OUTGOING = "OUTGOING"
    const val CALL_TYPE_WHATSAPP = "WHATSAPP"
    const val CALL_TYPE_OTHER_VOIP = "OTHER_VOIP"
    
    // Extra keys
    const val EXTRA_CALL_TYPE = "call_type"
    const val EXTRA_PHONE_NUMBER = "phone_number"
    const val EXTRA_CONTACT_NAME = "contact_name"
    const val EXTRA_START_TIME = "start_time"
    
    // Notification
    const val NOTIFICATION_ID = 1001
    const val CHANNEL_ID = "CALL_RECORDING_CHANNEL"
    
    // Storage
    const val RECORDINGS_FOLDER = "CallRecordings"
    const val FILE_PREFIX = "CALL_"
    const val FILE_EXTENSION = ".mp3"
    
    // Audio settings
    const val AUDIO_SAMPLE_RATE = 44100
    const val AUDIO_CHANNELS = 1 // Mono
    const val AUDIO_BIT_RATE = 128000
    
    // Preferences
    const val PREF_AUTO_RECORD = "auto_record"
    const val PREF_RECORD_WHATSAPP = "record_whatsapp"
    const val PREF_RECORD_UNKNOWN = "record_unknown"
    const val PREF_NOTIFICATION_SOUND = "notification_sound"
}
