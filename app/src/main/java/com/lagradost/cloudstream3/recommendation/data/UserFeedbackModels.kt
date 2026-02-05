package com.lagradost.cloudstream3.recommendation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class WatchIntent {
    WATCHED,
    PLAN_TO_WATCH
}

@Entity(tableName = "user_feedback")
data class UserFeedbackEntity(
    @PrimaryKey val contentKey: String,
    val contentName: String,
    val contentUrl: String,
    val apiName: String,
    val posterUrl: String?,
    val genreSummary: String?,
    val userRating: Int,
    val watchIntent: WatchIntent,
    val updatedAtMs: Long,
    val syncedToFirebase: Boolean,
)

@Entity(tableName = "user_watch_history")
data class UserHistoryEntity(
    @PrimaryKey val contentKey: String,
    val contentName: String,
    val contentUrl: String,
    val apiName: String,
    val watchedAtMs: Long,
    val syncedToFirebase: Boolean,
)
