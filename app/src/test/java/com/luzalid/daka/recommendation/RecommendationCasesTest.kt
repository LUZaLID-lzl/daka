package com.luzalid.daka.recommendation

import org.junit.Assert.assertEquals
import org.junit.Test

class RecommendationCasesTest {
    @Test
    fun eachCategoryContainsOneHundredCasesWithUniqueIds() {
        val categories = listOf(
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
        )
        val allCases = categories.flatten()

        categories.forEach { assertEquals(100, it.size) }
        assertEquals(1_000, allCases.size)
        assertEquals(allCases.size, allCases.map(RecommendationCase::id).toSet().size)
    }
}
