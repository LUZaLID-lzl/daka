package com.luzalid.daka.recommendation

import android.content.res.Resources
import com.luzalid.daka.R
import com.luzalid.daka.data.RecommendationEntity

object RecommendationCatalog {
    fun builtIns(
        resources: Resources,
        now: Long = System.currentTimeMillis(),
    ): List<RecommendationEntity> = listOf(
        RecommendationEntity(
            id = "local-food-001",
            title = resources.getString(R.string.recommendation_food_title),
            description = resources.getString(R.string.recommendation_food_description),
            category = resources.getString(R.string.category_food),
            imageAsset = "food",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-commute-001",
            title = resources.getString(R.string.recommendation_commute_title),
            description = resources.getString(R.string.recommendation_commute_description),
            category = resources.getString(R.string.category_commute),
            imageAsset = "commute",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-sport-001",
            title = resources.getString(R.string.recommendation_sport_title),
            description = resources.getString(R.string.recommendation_sport_description),
            category = resources.getString(R.string.category_sport),
            imageAsset = "sport",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-work-001",
            title = resources.getString(R.string.recommendation_work_title),
            description = resources.getString(R.string.recommendation_work_description),
            category = resources.getString(R.string.category_work),
            imageAsset = "work",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-fun-001",
            title = resources.getString(R.string.recommendation_fun_title),
            description = resources.getString(R.string.recommendation_fun_description),
            category = resources.getString(R.string.category_fun),
            imageAsset = "fun",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-social-001",
            title = resources.getString(R.string.recommendation_social_title),
            description = resources.getString(R.string.recommendation_social_description),
            category = resources.getString(R.string.category_social),
            imageAsset = "social",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-relax-001",
            title = resources.getString(R.string.recommendation_relax_title),
            description = resources.getString(R.string.recommendation_relax_description),
            category = resources.getString(R.string.category_relax),
            imageAsset = "relax",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-home-001",
            title = resources.getString(R.string.recommendation_home_title),
            description = resources.getString(R.string.recommendation_home_description),
            category = resources.getString(R.string.category_home),
            imageAsset = "home",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-study-001",
            title = resources.getString(R.string.recommendation_study_title),
            description = resources.getString(R.string.recommendation_study_description),
            category = resources.getString(R.string.category_study),
            imageAsset = "study",
            createdAt = now,
            updatedAt = now,
        ),
        RecommendationEntity(
            id = "local-explore-001",
            title = resources.getString(R.string.recommendation_explore_title),
            description = resources.getString(R.string.recommendation_explore_description),
            category = resources.getString(R.string.category_explore),
            imageAsset = "explore",
            createdAt = now,
            updatedAt = now,
        ),
    )

    /**
     * Resolves built-in recommendation copy with the currently selected app locale.
     * Remote recommendations keep their server-provided content.
     */
    fun localized(
        resources: Resources,
        recommendation: RecommendationEntity,
    ): RecommendationEntity {
        if (recommendation.source != "local") return recommendation
        val localized = builtIns(resources, now = recommendation.updatedAt)
            .firstOrNull { it.id == recommendation.id }
            ?: return recommendation
        return recommendation.copy(
            title = localized.title,
            description = localized.description,
            category = localized.category,
        )
    }
}
