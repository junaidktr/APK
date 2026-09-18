package com.example.callrecorder.database

import android.content.Context
import androidx.room.Room

object AppDatabase {
    
    @Volatile
    private var instance: AppDatabase? = null
    
    fun getInstance(context: Context): com.example.callrecorder.database.AppDatabaseSingleton {
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                com.example.callrecorder.database.AppDatabaseSingleton::class.java,
                "call_recorder_database"
            ).build()
            instance = db
            db
        }
    }
}
