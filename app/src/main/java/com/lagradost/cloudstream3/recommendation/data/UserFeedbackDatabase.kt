package com.lagradost.cloudstream3.recommendation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserFeedbackEntity::class, UserHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UserFeedbackDatabase : RoomDatabase() {
    abstract fun dao(): UserFeedbackDao

    companion object {
        @Volatile
        private var instance: UserFeedbackDatabase? = null

        fun get(context: Context): UserFeedbackDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserFeedbackDatabase::class.java,
                    "cloudstream_user_feedback.db"
                ).build().also { instance = it }
            }
        }
    }
}
