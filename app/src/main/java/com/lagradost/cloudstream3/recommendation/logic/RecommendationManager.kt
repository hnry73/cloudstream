package com.lagradost.cloudstream3.recommendation.logic

import com.lagradost.cloudstream3.SearchResponse
import org.json.JSONArray
import org.json.JSONObject

private const val CLOUDSTREAM_RECOMMENDATION_SYSTEM_PROMPT = """
Sen bir CloudStream Kişisel Asistanısın. 
Girdi: 
1. Kullanıcı Profili (En çok izlenen türler, son puanlanan filmler).
2. Aday Listesi (Metadata: Tür, Puan, Açıklama).

Görevlerin:
- Aday listesini kullanıcı profiline göre yeniden sırala (Re-rank).
- Kullanıcının \"Cold-start\" (yeni kullanıcı) olup olmadığını kontrol et; öyleyse popüler ama kaliteli içerikleri öne çıkar.
- Her öneri için 1 cümlelik \"Neden?\" açıklaması üret (Örn: \"Daha önce Dark izlediğin için bu gizemli yapımı sevebilirsin\").
- Sadece JSON döndür.
Kodu dikkatli kullanın.
"""

class RecommendationManager {
    fun buildPromptPayload(
        userSummary: RecommendationUserSummary,
        candidates: List<RecommendationCandidate>,
    ): JSONObject {
        val profileJson = JSONObject()
            .put("topGenres", JSONArray(userSummary.topGenres))
            .put(
                "recentRatings",
                JSONArray(
                    userSummary.recentRatings.map {
                        JSONObject().put("title", it.first).put("rating", it.second)
                    }
                )
            )
            .put("isColdStart", userSummary.isColdStart)

        val candidatesJson = JSONArray(candidates.map { candidate ->
            JSONObject()
                .put("name", candidate.item.name)
                .put("type", candidate.item.type?.name)
                .put("score", candidate.item.score?.toString())
                .put("source", candidate.source)
                .put("url", candidate.item.url)
        })

        return JSONObject()
            .put("systemPrompt", CLOUDSTREAM_RECOMMENDATION_SYSTEM_PROMPT.trimIndent())
            .put("userProfile", profileJson)
            .put("candidateList", candidatesJson)
    }

    fun rerankFallback(
        userSummary: RecommendationUserSummary,
        candidates: List<RecommendationCandidate>,
    ): List<RecommendationResult> {
        val sorted = if (userSummary.isColdStart) {
            candidates.sortedByDescending { (it.item.score?.toString()?.toDoubleOrNull() ?: 0.0) + it.score }
        } else {
            candidates.sortedByDescending { it.score + (it.item.score?.toString()?.toDoubleOrNull() ?: 0.0) }
        }

        return sorted.take(10).map {
            RecommendationResult(
                item = it.item,
                explanation = if (userSummary.isColdStart) {
                    "Yeni kullanıcı profiline göre kaliteli ve popüler bir seçenek."
                } else {
                    "Son izleme ve puanlama eğilimine benzer bir içerik."
                }
            )
        }
    }

    fun buildUserSummary(
        ratings: List<Pair<String, Int>>,
        topGenres: List<String>,
    ): RecommendationUserSummary {
        return RecommendationUserSummary(
            topGenres = topGenres,
            recentRatings = ratings.take(5),
            isColdStart = ratings.size < 3,
        )
    }
}
