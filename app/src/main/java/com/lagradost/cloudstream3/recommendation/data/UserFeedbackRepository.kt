package com.lagradost.cloudstream3.recommendation.data

import android.content.Context
import android.provider.Settings
import com.lagradost.cloudstream3.SearchResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserFeedbackRepository(
    context: Context,
    private val sync: FirebaseUserSync = FirebaseUserSync(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val dao = UserFeedbackDatabase.get(context).dao()
    private val userId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        ?: "anonymous"

    suspend fun upsertRating(
        item: SearchResponse,
        rating: Int,
        watchIntent: WatchIntent,
        genreSummary: String? = null,
    ) = withContext(ioDispatcher) {
        val key = "${item.apiName}:${item.url}"
        dao.upsertFeedback(
            UserFeedbackEntity(
                contentKey = key,
                contentName = item.name,
                contentUrl = item.url,
                apiName = item.apiName,
                posterUrl = item.posterUrl,
                genreSummary = genreSummary,
                userRating = rating.coerceIn(1, 10),
                watchIntent = watchIntent,
                updatedAtMs = System.currentTimeMillis(),
                syncedToFirebase = false,
            )
        )
        if (watchIntent == WatchIntent.WATCHED) {
            dao.upsertHistory(
                UserHistoryEntity(
                    contentKey = key,
                    contentName = item.name,
                    contentUrl = item.url,
                    apiName = item.apiName,
                    watchedAtMs = System.currentTimeMillis(),
                    syncedToFirebase = false,
                )
            )
        }
    }

    suspend fun getAllRatings(): List<UserFeedbackEntity> = withContext(ioDispatcher) { dao.getAllFeedback() }

    suspend fun getRecentHistory(limit: Int = 50): List<UserHistoryEntity> = withContext(ioDispatcher) {
        dao.getRecentHistory(limit)
    }

    suspend fun syncPendingToFirebase() = withContext(ioDispatcher) {
        val pendingRatings = dao.getPendingFeedback()
        val pendingHistory = dao.getPendingHistory()

        runCatching {
            sync.syncFeedback(userId, pendingRatings)
            sync.syncHistory(userId, pendingHistory)
            dao.markFeedbackSynced(pendingRatings.map { it.contentKey })
            dao.markHistorySynced(pendingHistory.map { it.contentKey })
        }
    }
}
