package com.luzalid.daka.recommendation

import android.content.res.Resources
import com.luzalid.daka.data.RecommendationEntity

object RecommendationCatalog {
    private val cases: List<RecommendationCase> by lazy {
        listOf(
            foodRecommendationCases(),
            commuteRecommendationCases(),
            sportRecommendationCases(),
            workRecommendationCases(),
            funRecommendationCases(),
            socialRecommendationCases(),
            relaxRecommendationCases(),
            homeRecommendationCases(),
            studyRecommendationCases(),
            exploreRecommendationCases(),
        ).flatten()
    }

    private val casesById: Map<String, RecommendationCase> by lazy {
        cases.associateBy(RecommendationCase::id)
    }

    fun builtIns(
        resources: Resources,
        now: Long = System.currentTimeMillis(),
    ): List<RecommendationEntity> = cases.map { it.toEntity(resources, now) }

    /**
     * Resolves built-in recommendation copy with the currently selected app locale.
     * Remote recommendations keep their server-provided content.
     */
    fun localized(
        resources: Resources,
        recommendation: RecommendationEntity,
    ): RecommendationEntity {
        if (recommendation.source != "local") return recommendation
        val localized = casesById[recommendation.id]
            ?.toEntity(resources, recommendation.updatedAt)
            ?: return recommendation
        return recommendation.copy(
            title = localized.title,
            description = localized.description,
            category = localized.category,
        )
    }
}
