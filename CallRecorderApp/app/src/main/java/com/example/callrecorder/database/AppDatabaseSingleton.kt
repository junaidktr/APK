package com.example.callrecorder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.callrecorder.models.Recording

@Database(entities = [Recording::class], version = 1, exportSchema = false)
abstract class AppDatabaseSingleton : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}
