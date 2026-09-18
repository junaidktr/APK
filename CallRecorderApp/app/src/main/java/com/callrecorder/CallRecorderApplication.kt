package com.callrecorder

import android.app.Application
import androidx.room.Room

class CallRecorderApplication : Application() {

    lateinit var database: CallRecorderDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            CallRecorderDatabase::class.java,
            "call_recorder_database"
        ).build()
    }
}
