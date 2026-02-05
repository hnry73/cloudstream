package com.lagradost.cloudstream3.recommendation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserFeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedback(feedback: UserFeedbackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(history: UserHistoryEntity)

    @Query("SELECT * FROM user_feedback ORDER BY updatedAtMs DESC")
    suspend fun getAllFeedback(): List<UserFeedbackEntity>

    @Query("SELECT * FROM user_feedback WHERE syncedToFirebase = 0")
    suspend fun getPendingFeedback(): List<UserFeedbackEntity>

    @Query("SELECT * FROM user_watch_history ORDER BY watchedAtMs DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int = 50): List<UserHistoryEntity>

    @Query("SELECT * FROM user_watch_history WHERE syncedToFirebase = 0")
    suspend fun getPendingHistory(): List<UserHistoryEntity>

    @Query("UPDATE user_feedback SET syncedToFirebase = 1 WHERE contentKey IN (:keys)")
    suspend fun markFeedbackSynced(keys: List<String>)

    @Query("UPDATE user_watch_history SET syncedToFirebase = 1 WHERE contentKey IN (:keys)")
    suspend fun markHistorySynced(keys: List<String>)
}
