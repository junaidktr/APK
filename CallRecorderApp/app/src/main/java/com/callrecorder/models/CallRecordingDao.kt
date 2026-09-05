package com.callrecorder

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CallRecordingDao {

    @Query("SELECT * FROM call_recordings WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<CallRecording>>

    @Query("SELECT * FROM call_recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long): CallRecording?

    @Query("SELECT * FROM call_recordings WHERE phoneNumber = :phoneNumber AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getRecordingsByPhoneNumber(phoneNumber: String): Flow<List<CallRecording>>

    @Query("SELECT * FROM call_recordings WHERE callType = :callType AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getRecordingsByType(callType: CallType): Flow<List<CallRecording>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: CallRecording): Long

    @Update
    suspend fun updateRecording(recording: CallRecording)

    @Delete
    suspend fun deleteRecording(recording: CallRecording)

    @Query("UPDATE call_recordings SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteRecording(id: Long)

    @Query("DELETE FROM call_recordings WHERE id = :id")
    suspend fun hardDeleteRecording(id: Long)

    @Query("SELECT COUNT(*) FROM call_recordings WHERE isDeleted = 0")
    fun getTotalRecordingsCount(): Flow<Int>

    @Query("SELECT SUM(fileSize) FROM call_recordings WHERE isDeleted = 0")
    suspend fun getTotalStorageUsed(): Long?
}
