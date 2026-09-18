package com.callrecorder

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CallRecording::class], version = 1, exportSchema = false)
abstract class CallRecorderDatabase : RoomDatabase() {
    abstract fun callRecordingDao(): CallRecordingDao
}
