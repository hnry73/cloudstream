package com.lagradost.cloudstream3.recommendation.logic

import com.lagradost.cloudstream3.SearchResponse

data class RecommendationCandidate(
    val item: SearchResponse,
    val source: String,
    val score: Double,
)

data class RecommendationResult(
    val item: SearchResponse,
    val explanation: String,
)

data class RecommendationUserSummary(
    val topGenres: List<String>,
    val recentRatings: List<Pair<String, Int>>,
    val isColdStart: Boolean,
)
