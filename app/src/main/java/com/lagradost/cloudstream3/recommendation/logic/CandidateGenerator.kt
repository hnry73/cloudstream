package com.lagradost.cloudstream3.recommendation.logic

import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.recommendation.data.UserFeedbackEntity
import com.lagradost.cloudstream3.recommendation.data.UserHistoryEntity

class CandidateGenerator {
    fun generateCandidates(
        ratings: List<UserFeedbackEntity>,
        history: List<UserHistoryEntity>,
        popularItems: List<SearchResponse>,
        limit: Int = 20,
    ): List<RecommendationCandidate> {
        val watchedKeys = history.map { "${it.apiName}:${it.contentUrl}" }.toSet()

        val contentBased = popularItems
            .filter { item -> "${item.apiName}:${item.url}" !in watchedKeys }
            .filter { item ->
                val genreHint = item.type?.name.orEmpty().lowercase()
                ratings.any { it.genreSummary?.lowercase()?.contains(genreHint) == true }
            }
            .take(limit / 2)
            .mapIndexed { idx, item ->
                RecommendationCandidate(item = item, source = "content", score = 100.0 - idx)
            }

        val collaborative = popularItems
            .filter { item -> "${item.apiName}:${item.url}" !in watchedKeys }
            .take(limit)
            .mapIndexed { idx, item ->
                RecommendationCandidate(item = item, source = "collaborative", score = 90.0 - idx)
            }

        return (contentBased + collaborative)
            .distinctBy { "${it.item.apiName}:${it.item.url}" }
            .take(limit)
    }
}
