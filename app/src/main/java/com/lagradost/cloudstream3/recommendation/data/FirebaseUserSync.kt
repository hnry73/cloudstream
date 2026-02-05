package com.lagradost.cloudstream3.recommendation.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Tasks

class FirebaseUserSync(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun syncFeedback(userId: String, feedback: List<UserFeedbackEntity>) {
        if (feedback.isEmpty()) return
        val ratingsCollection = firestore.collection("users").document(userId).collection("ratings")
        feedback.forEach { item ->
            ratingsCollection.document(item.contentKey).set(
                mapOf(
                    "contentName" to item.contentName,
                    "contentUrl" to item.contentUrl,
                    "apiName" to item.apiName,
                    "posterUrl" to item.posterUrl,
                    "genreSummary" to item.genreSummary,
                    "userRating" to item.userRating,
                    "watchIntent" to item.watchIntent.name,
                    "updatedAtMs" to item.updatedAtMs,
                )
            )
        }
        Tasks.await(firestore.waitForPendingWrites())
    }

    suspend fun syncHistory(userId: String, history: List<UserHistoryEntity>) {
        if (history.isEmpty()) return
        val historyCollection = firestore.collection("users").document(userId).collection("history")
        history.forEach { item ->
            historyCollection.document(item.contentKey).set(
                mapOf(
                    "contentName" to item.contentName,
                    "contentUrl" to item.contentUrl,
                    "apiName" to item.apiName,
                    "watchedAtMs" to item.watchedAtMs,
                )
            )
        }
        Tasks.await(firestore.waitForPendingWrites())
    }
}
