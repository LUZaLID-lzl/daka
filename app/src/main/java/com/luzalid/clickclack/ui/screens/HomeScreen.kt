package com.luzalid.clickclack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.luzalid.clickclack.data.ClickClackRepository
import com.luzalid.clickclack.model.Recommendation

@Composable
internal fun HomeScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    recommendations: List<Recommendation>,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onRecord: (String?, Recommendation) -> Unit,
) {
    val todayRecord by repository.observeTodayRecord().collectAsState(initial = null)
    val homeCards = remember(recommendations) { recommendations.take(3) }
    var currentCardIndex by remember(homeCards) { mutableStateOf(0) }
    val recommendation = homeCards.getOrNull(currentCardIndex)
    val dateLabel = remember { todayDateLabel() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFFFFEFC))
            .safeDrawingPadding()
            .debugOutline(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 112.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeHeroHeader(
                dateLabel = dateLabel,
                onProfile = onProfile,
            )
            if (recommendation != null) {
                HomeRecommendationStage(
                    recommendations = homeCards,
                    onPageChanged = { currentCardIndex = it },
                    onRecord = { selected -> onRecord(todayRecord?.id, selected) },
                )
                HomePagerDots(
                    count = homeCards.size,
                    activeIndex = currentCardIndex,
                )
                Spacer(Modifier.height(12.dp))
                HomeQuoteCard()
                Spacer(Modifier.height(132.dp))
            }
        }
        HomeBottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            onCreate = { recommendation?.let { onRecord(todayRecord?.id, it) } },
            onHistory = onHistory,
            onProfile = onProfile,
        )
    }
}
