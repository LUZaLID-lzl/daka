package com.luzalid.daka.ui.home

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.Recommendation
import kotlin.math.abs
import kotlin.random.Random

@Composable
internal fun HomeScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    recommendations: List<Recommendation>,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onRecord: (String?, Recommendation, Boolean) -> Unit,
) {
    val todayRecord by repository.observeTodayRecord().collectAsState(initial = null)
    val records by repository.observeRecordSummaries().collectAsState(initial = emptyList())
    val appearance = LocalAppAppearance.current
    var selectedContent by remember { mutableStateOf(HomeContentDestination.Home) }
    var groupSeed by remember(recommendations) { mutableStateOf(0) }
    val homeCards = remember(recommendations, groupSeed) {
        if (recommendations.size <= 3) {
            recommendations.shuffled(Random(groupSeed))
        } else {
            recommendations.shuffled(Random(groupSeed)).take(3)
        }
    }
    var currentCardIndex by remember(homeCards) { mutableStateOf(0) }
    var dragDistance by remember(homeCards) { mutableFloatStateOf(0f) }
    val recommendation = homeCards.getOrNull(currentCardIndex)
    val moveCard: (Int) -> Unit = { direction ->
        if (homeCards.size > 1) {
            currentCardIndex = if (direction < 0) {
                (currentCardIndex + 1) % homeCards.size
            } else {
                (currentCardIndex - 1 + homeCards.size) % homeCards.size
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(appearance.backgroundBrush)
            .then(
                if (selectedContent == HomeContentDestination.Home) {
                    Modifier.pointerInput(homeCards) {
                        var totalDrag = 0f
                        detectHorizontalDragGestures(
                            onDragStart = {
                                totalDrag = 0f
                                dragDistance = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                totalDrag = (totalDrag + dragAmount).coerceIn(-140f, 140f)
                                dragDistance = totalDrag
                            },
                            onDragEnd = {
                                if (abs(totalDrag) > 42f) {
                                    moveCard(if (totalDrag < 0f) -1 else 1)
                                }
                                totalDrag = 0f
                                dragDistance = 0f
                            },
                            onDragCancel = {
                                totalDrag = 0f
                                dragDistance = 0f
                            },
                        )
                    }
                } else {
                    Modifier
                },
            )
            .debugOutline(),
    ) {
        HomeContentArea(
            selectedContent = selectedContent,
            recommendations = homeCards,
            activeRecommendation = recommendation,
            activeRecommendationIndex = currentCardIndex,
            dragDistance = dragDistance,
            records = records,
            onProfile = onProfile,
            onDragDistanceChange = { dragDistance = it },
            onSwipeRecommendation = moveCard,
            onActiveRecommendationChange = { currentCardIndex = it },
            onRecommendationRecord = { selected -> onRecord(null, selected, true) },
            onShuffleRecommendations = { groupSeed += 1 },
        )
        HomeBottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedDestination = when (selectedContent) {
                HomeContentDestination.Home -> HomeBottomDestination.Home
                HomeContentDestination.Records -> HomeBottomDestination.Records
            },
            onHome = { selectedContent = HomeContentDestination.Home },
            onHistory = { selectedContent = HomeContentDestination.Records },
            onProfile = onProfile,
            onCreate = { recommendation?.let { onRecord(null, it, false) } },
        )
    }
}

private fun previewHomeRecommendations() = listOf(
    Recommendation(
        id = "food",
        title = "吃一份黄焖鸡米饭",
        description = "给今天留一个有味道的片段，记录这顿饭和当时的心情。",
        category = "美食",
        imageAsset = "food",
    ),
    Recommendation(
        id = "sport",
        title = "饭后散步 20 分钟",
        description = "把脚步、空气和身体状态记录下来。",
        category = "运动",
        imageAsset = "sport",
    ),
    Recommendation(
        id = "social",
        title = "给一位朋友发一条问候",
        description = "记录你们聊到了什么，也记录主动联系的感受。",
        category = "社交",
        imageAsset = "social",
    ),
)

@Preview(name = "Home Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    val cards = previewHomeRecommendations()
    var activeIndex by remember { mutableStateOf(0) }
    var dragDistance by remember { mutableFloatStateOf(0f) }
    val appearance = appAppearance(isDark = false, backgroundStyle = "mist")
    androidx.compose.material3.MaterialTheme(colorScheme = appColorScheme(false)) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAppAppearance provides appearance,
            LocalDebugUiOutline provides false,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(appearance.backgroundBrush),
            ) {
                HomeContentArea(
                    selectedContent = HomeContentDestination.Home,
                    recommendations = cards,
                    activeRecommendation = cards[activeIndex],
                    activeRecommendationIndex = activeIndex,
                    dragDistance = dragDistance,
                    records = emptyList(),
                    onProfile = {},
                    onDragDistanceChange = { dragDistance = it },
                    onSwipeRecommendation = { direction ->
                        activeIndex = if (direction < 0) {
                            (activeIndex + 1) % cards.size
                        } else {
                            (activeIndex - 1 + cards.size) % cards.size
                        }
                    },
                    onActiveRecommendationChange = { activeIndex = it },
                    onRecommendationRecord = {},
                    onShuffleRecommendations = {},
                )
                HomeBottomNavigation(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onHistory = {},
                    onProfile = {},
                    onCreate = {},
                )
            }
        }
    }
}
