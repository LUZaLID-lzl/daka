package com.luzalid.daka.ui.recommendations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.model.RecordSummary
import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline
import com.luzalid.daka.ui.home.categoryMotionImageRes
import com.luzalid.daka.ui.home.recommendationPalette
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private val categoryOrder = listOf(
    "food",
    "commute",
    "sport",
    "work",
    "fun",
    "social",
    "relax",
    "home",
    "study",
    "explore",
)

@Composable
internal fun AllRecommendationsContent(
    recommendations: List<Recommendation>,
    records: List<RecordSummary>,
    onRecommendationClick: (Recommendation) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryRecommendations = remember(recommendations) {
        recommendations
            .groupBy(Recommendation::imageAsset)
            .mapNotNull { (_, items) -> items.firstOrNull() }
            .sortedBy { recommendation ->
                categoryOrder.indexOf(recommendation.imageAsset).takeIf { it >= 0 } ?: Int.MAX_VALUE
            }
    }
    val recordedIds = remember(records) {
        records.mapTo(mutableSetOf(), RecordSummary::recommendationId)
    }
    val recordedAssets = remember(recommendations, recordedIds) {
        recommendations
            .filter { it.id in recordedIds }
            .mapTo(mutableSetOf(), Recommendation::imageAsset)
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val selectedIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo
                .minByOrNull { item ->
                    (item.offset + item.size / 2 - viewportCenter).absoluteValue
                }
                ?.index
                ?: 0
        }
    }

    BoxWithConstraints(modifier = modifier.debugOutline()) {
        val cardWidth = (maxWidth * 0.72f).coerceIn(252.dp, 272.dp)
        val cardHeight = (cardWidth * 1.62f).coerceIn(408.dp, 440.dp)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.all_recommendations_title),
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color(0xFF19191D),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 27.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.6).sp,
                ),
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .debugOutline(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(
                    items = categoryRecommendations,
                    key = { _, item -> "filter-${item.id}" },
                ) { index, recommendation ->
                    CategoryFilterChip(
                        text = recommendation.category,
                        selected = index == selectedIndex,
                        onClick = {
                            scope.launch { listState.animateScrollToItem(index) }
                        },
                    )
                }
            }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .debugOutline(),
                contentPadding = PaddingValues(start = 30.dp, end = 68.dp, top = 4.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy((-12).dp),
                verticalAlignment = Alignment.Top,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            ) {
                itemsIndexed(
                    items = categoryRecommendations,
                    key = { _, item -> item.id },
                ) { index, recommendation ->
                    val distance = (index - selectedIndex).absoluteValue.coerceAtMost(2)
                    RecommendationGalleryCard(
                        recommendation = recommendation,
                        recorded = recommendation.imageAsset in recordedAssets,
                        selected = index == selectedIndex,
                        depth = distance,
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        onClick = {
                            if (index == selectedIndex) {
                                onRecommendationClick(recommendation)
                            } else {
                                scope.launch { listState.animateScrollToItem(index) }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(999.dp)
    Text(
        text = text,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) Color(0xFFEEF3FF) else Color.White.copy(alpha = 0.64f))
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFF7799E7) else Color(0xFFD9DDE7),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .debugOutline(shape),
        color = if (selected) Color(0xFF416BB9) else Color(0xFF8B909C),
        fontSize = 12.sp,
        lineHeight = 15.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
    )
}

@Composable
private fun RecommendationGalleryCard(
    recommendation: Recommendation,
    recorded: Boolean,
    selected: Boolean,
    depth: Int,
    cardWidth: androidx.compose.ui.unit.Dp,
    cardHeight: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val palette = remember(recommendation.imageAsset) {
        recommendationPalette(recommendation.imageAsset)
    }
    val cardShape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .graphicsLayer {
                val depthScale = when (depth) {
                    0 -> 1f
                    1 -> 0.96f
                    else -> 0.93f
                }
                scaleX = depthScale
                scaleY = depthScale
                translationY = (depth * 9).dp.toPx()
                alpha = if (depth > 1) 0.84f else 1f
                shadowElevation = if (selected) 24.dp.toPx() else 12.dp.toPx()
                shape = cardShape
                clip = false
            }
            .shadow(
                elevation = if (selected) 22.dp else 10.dp,
                shape = cardShape,
                clip = false,
                ambientColor = palette.shadow.copy(alpha = 0.20f),
                spotColor = palette.shadow.copy(alpha = 0.15f),
            )
            .clip(cardShape)
            .background(Brush.verticalGradient(palette.gradient))
            .border(1.dp, Color.White.copy(alpha = 0.72f), cardShape)
            .clickable(onClick = onClick)
            .debugOutline(cardShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.58f),
                            palette.glow.copy(alpha = 0.24f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = recommendation.category,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.78f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                color = palette.pillForeground,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            if (recorded) {
                Box(
                    modifier = Modifier
                        .size(27.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.78f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.recommendation_recorded_count, 1),
                        tint = palette.pillForeground,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Image(
            painter = painterResource(categoryMotionImageRes(recommendation.imageAsset)),
            contentDescription = recommendation.category,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(start = 26.dp, end = 26.dp, top = 66.dp, bottom = 24.dp),
        )
    }
}

@Preview(name = "All Recommendation Gallery", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun AllRecommendationsContentPreview() {
    val appearance = appAppearance(isDark = false, backgroundStyle = "mist")
    MaterialTheme(colorScheme = appColorScheme(false)) {
        CompositionLocalProvider(
            LocalAppAppearance provides appearance,
            LocalDebugUiOutline provides false,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(appearance.backgroundBrush)
                    .padding(top = 16.dp),
            ) {
                AllRecommendationsContent(
                    recommendations = previewRecommendations(),
                    records = listOf(
                        RecordSummary(
                            id = "record-food",
                            recommendationId = "local-food-001",
                            title = "Food record",
                            dateKey = "2026-06-13",
                            category = "Food",
                            updatedAt = 0L,
                            content = "",
                            thumbnailUri = null,
                            mediaType = null,
                        ),
                    ),
                    onRecommendationClick = {},
                )
            }
        }
    }
}

private fun previewRecommendations() = listOf(
    Recommendation(
        id = "local-food-001",
        title = "Enjoy a comforting meal",
        description = "Save a flavorful moment from today.",
        category = "Food",
        imageAsset = "food",
    ),
    Recommendation(
        id = "local-sport-001",
        title = "Take a 20-minute walk",
        description = "Record your pace and how your body feels.",
        category = "Fitness",
        imageAsset = "sport",
    ),
    Recommendation(
        id = "local-social-001",
        title = "Message a friend",
        description = "Reach out and save the moment.",
        category = "Social",
        imageAsset = "social",
    ),
)
