package com.luzalid.daka.ui.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.model.RecordSummary
import com.luzalid.daka.ui.profile.ProfileScreen
import com.luzalid.daka.ui.records.RecordContent

internal enum class HomeContentDestination {
    Home,
    Records,
    Profile,
}

@Composable
internal fun HomeContentArea(
    selectedContent: HomeContentDestination,
    recommendations: List<Recommendation>,
    activeRecommendation: Recommendation?,
    activeRecommendationIndex: Int,
    dragDistance: Float,
    records: List<RecordSummary>,
    repository: ClickClackRepository?,
    onProfile: () -> Unit,
    onDragDistanceChange: (Float) -> Unit,
    onSwipeRecommendation: (Int) -> Unit,
    onActiveRecommendationChange: (Int) -> Unit,
    onRecommendationRecord: (Recommendation) -> Unit,
    onShuffleRecommendations: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (selectedContent == HomeContentDestination.Profile) Modifier
                else Modifier.padding(bottom = 104.dp),
            ),
    ) {
        val contentTopSpacing = maxHeight * 0.1f
        val recordContentTopSpacing = maxHeight * 0.04f
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (selectedContent != HomeContentDestination.Profile) {
                HomeHeroHeader(onProfile = onProfile)
            }
            when (selectedContent) {
                HomeContentDestination.Home -> {
                    Spacer(Modifier.height(contentTopSpacing))
                    HomeRecommendationContent(
                        recommendations = recommendations,
                        activeRecommendation = activeRecommendation,
                        activeRecommendationIndex = activeRecommendationIndex,
                        dragDistance = dragDistance,
                        onDragDistanceChange = onDragDistanceChange,
                        onSwipeRecommendation = onSwipeRecommendation,
                        onActiveRecommendationChange = onActiveRecommendationChange,
                        onRecommendationRecord = onRecommendationRecord,
                        onShuffleRecommendations = onShuffleRecommendations,
                    )
                }

                HomeContentDestination.Records -> {
                    Spacer(Modifier.height(recordContentTopSpacing))
                    RecordContent(
                        records = records,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                    )
                }

                HomeContentDestination.Profile -> {
                    if (repository != null) {
                        ProfileScreen(
                            padding = PaddingValues(),
                            repository = repository,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeRecommendationContent(
    recommendations: List<Recommendation>,
    activeRecommendation: Recommendation?,
    activeRecommendationIndex: Int,
    dragDistance: Float,
    onDragDistanceChange: (Float) -> Unit,
    onSwipeRecommendation: (Int) -> Unit,
    onActiveRecommendationChange: (Int) -> Unit,
    onRecommendationRecord: (Recommendation) -> Unit,
    onShuffleRecommendations: () -> Unit,
) {
    if (activeRecommendation == null) return

    HomeRecommendationStage(
        recommendations = recommendations,
        activeIndex = activeRecommendationIndex,
        dragDistance = dragDistance,
        onDragDistanceChange = onDragDistanceChange,
        onSwipe = onSwipeRecommendation,
        onActiveIndexChange = onActiveRecommendationChange,
        onRecord = onRecommendationRecord,
    )
    Spacer(Modifier.height(34.dp))
    HomeRecommendationDetail(
        recommendation = activeRecommendation,
        onShuffle = onShuffleRecommendations,
    )
}
