package com.example.callrecorder.database

import androidx.room.*
import com.example.callrecorder.models.Recording
import java.util.Date

@Dao
interface RecordingDao {
    
    @Query("SELECT * FROM recordings ORDER BY date DESC")
    suspend fun getAllRecordings(): List<Recording>
    
    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long): Recording?
    
    @Query("SELECT * FROM recordings WHERE callType = :callType ORDER BY date DESC")
    suspend fun getRecordingsByType(callType: String): List<Recording>
    
    @Query("SELECT * FROM recordings WHERE phoneNumber = :phoneNumber ORDER BY date DESC")
    suspend fun getRecordingsByPhoneNumber(phoneNumber: String): List<Recording>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: Recording): Long
    
    @Delete
    suspend fun deleteRecording(recording: Recording)
    
    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteRecordingById(id: Long)
    
    @Query("UPDATE recordings SET isPlayed = :played WHERE id = :id")
    suspend fun updatePlayedStatus(id: Long, played: Boolean)
    
    @Query("SELECT COUNT(*) FROM recordings")
    suspend fun getRecordingCount(): Int
    
    @Query("SELECT SUM(fileSize) FROM recordings")
    suspend fun getTotalStorageUsed(): Long?
}
