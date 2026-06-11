package com.luzalid.daka.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.R
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

@Composable
internal fun HomeRecommendationStage(
    recommendations: List<Recommendation>,
    onPageChanged: (Int) -> Unit,
    onRecord: (Recommendation) -> Unit,
) {
    if (recommendations.isEmpty()) return
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp)
            .debugOutline(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        val itemCount = recommendations.size
        val pageWidth = (maxWidth - 126.dp).coerceIn(238.dp, 270.dp)
        val pagerHeight = 390.dp
        val initialPage = remember(itemCount) {
            if (itemCount <= 1) {
                0
            } else {
                val middle = Int.MAX_VALUE / 2
                middle - middle % itemCount
            }
        }
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { if (itemCount <= 1) 1 else Int.MAX_VALUE },
        )
        val scope = rememberCoroutineScope()
        LaunchedEffect(pagerState.currentPage) {
            onPageChanged(if (itemCount == 0) 0 else pagerState.currentPage % itemCount)
        }
        Box(
            modifier = Modifier
                .width(pageWidth + 112.dp)
                .height(90.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-4).dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x26240F00),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerHeight)
                .align(Alignment.TopCenter)
                .zIndex(1f),
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = (maxWidth - pageWidth) / 2),
            pageSpacing = (-32).dp,
            userScrollEnabled = itemCount > 1,
        ) { page ->
            val itemIndex = if (itemCount == 0) 0 else page % itemCount
            val recommendation = recommendations[itemIndex]
            val offset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                .coerceIn(-1f, 1f)
            val distance = offset.absoluteValue
            val focus = 1f - distance
            val scale = 0.78f + focus * 0.22f
            val alpha = 0.34f + focus * 0.66f
            val rotation = offset * -14f
            val rotationY = offset * 20f
            val xShift = 26.dp * offset
            val yShift = (-68).dp * distance
            val blurRadius = 2.4.dp * distance
            val transformOrigin = when {
                offset > 0f -> TransformOrigin(1f, 1f)
                offset < 0f -> TransformOrigin(0f, 1f)
                else -> TransformOrigin.Center
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 76.dp),
                contentAlignment = Alignment.Center,
            ) {
                DailyWalkCard(
                    recommendation = recommendation,
                    onRecord = {
                        if (page == pagerState.currentPage && pagerState.currentPageOffsetFraction.absoluteValue < 0.35f) {
                            onRecord(recommendation)
                        } else {
                            scope.launch { pagerState.animateScrollToPage(page) }
                        }
                    },
                    modifier = Modifier
                        .width(pageWidth)
                        .height(306.dp)
                        .zIndex(10f - distance)
                        .blur(blurRadius)
                        .graphicsLayer {
                            this.transformOrigin = transformOrigin
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                            rotationZ = rotation
                            this.rotationY = rotationY
                            translationX = xShift.toPx()
                            translationY = yShift.toPx()
                            cameraDistance = 14f * density
                        },
                )
            }
        }
    }
}

@Composable
private fun DailyWalkCard(
    recommendation: Recommendation,
    onRecord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 36.dp,
                shape = shape,
                clip = false,
                ambientColor = Color(0x2A32200E),
                spotColor = Color(0x2032200E),
            )
            .clip(shape)
            .background(Color(0xFF332500))
            .border(1.dp, Color.White.copy(alpha = 0.14f), shape)
            .clickable(onClick = onRecord)
            .debugOutline(shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x5CFFF5D2),
                            Color(0x18FFF5D2),
                            Color.Transparent,
                        ),
                        center = Offset(0.5f, 0f),
                        radius = 900f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFE8D7A6),
                            Color(0xFFA38F55),
                            Color(0xFF5C4A1F),
                        ),
                    ),
                ),
        )
        ThemeMotionVisual(
            recommendation = recommendation,
            modifier = Modifier
                .fillMaxWidth()
                .height(218.dp)
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Black.copy(alpha = 0.10f),
                            Color(0xFF1E1708).copy(alpha = 0.58f),
                        ),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 14.dp, start = 14.dp, end = 22.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SuggestionPill(
                text = recommendation.category,
                imageAsset = recommendation.imageAsset,
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        ) {
            Text(
                text = recommendation.title,
                color = Color(0xFFFFFCF3),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 23.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = recommendation.description,
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.15.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SuggestionPill(
    text: String,
    imageAsset: String,
) {
    val icon = when (imageAsset) {
        "food" -> Icons.Filled.LocalDining
        "commute" -> Icons.Filled.DirectionsBus
        "sport" -> Icons.Filled.DirectionsRun
        "work" -> Icons.Filled.Work
        "fun" -> Icons.Filled.Movie
        "social" -> Icons.Filled.Groups
        "relax" -> Icons.Filled.Spa
        "home" -> Icons.Filled.Home
        "study" -> Icons.Filled.MenuBook
        else -> Icons.Filled.Explore
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFFFFCF5).copy(alpha = 0.92f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.58f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(horizontal = 11.dp, vertical = 7.dp)
            .debugOutline(RoundedCornerShape(999.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF725018),
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = text,
            color = Color(0xFF604315),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.35.sp,
            ),
        )
    }
}

@Composable
private fun ThemeMotionVisual(
    recommendation: Recommendation,
    modifier: Modifier = Modifier,
) {
    val motion = remember(recommendation.id) { Animatable(1f) }
    LaunchedEffect(recommendation.id) {
        motion.snapTo(0f)
        motion.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing),
        )
    }
    val progress = motion.value
    val enterProgress = (progress / 0.58f).coerceIn(0f, 1f)
    val sparkleProgress = ((progress - 0.14f) / 0.72f).coerceIn(0f, 1f)
    val sparkleAlpha = (1f - sparkleProgress).coerceIn(0f, 1f)
    val iconScale = 0.88f + enterProgress * 0.28f - (progress.coerceIn(0.58f, 1f) - 0.58f) * 0.10f
    val iconAlpha = 0.30f + enterProgress * 0.70f

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.50f, size.height * 0.50f)
            val burst = size.minDimension * (0.18f + sparkleProgress * 0.34f)
            drawCircle(
                color = Color.White.copy(alpha = 0.18f * sparkleAlpha),
                radius = burst,
                center = center,
            )
            listOf(
                Offset(size.width * 0.28f, size.height * 0.34f),
                Offset(size.width * 0.68f, size.height * 0.30f),
                Offset(size.width * 0.76f, size.height * 0.62f),
                Offset(size.width * 0.34f, size.height * 0.72f),
            ).forEachIndexed { index, base ->
                val drift = (index + 1) * 5f * sparkleProgress
                drawCircle(
                    color = Color(0xFFFFE2A5).copy(alpha = 0.58f * sparkleAlpha),
                    radius = 4.dp.toPx() + 2.dp.toPx() * sparkleAlpha,
                    center = Offset(base.x, base.y - drift),
                )
            }
        }
        Image(
            painter = painterResource(categoryMotionImageRes(recommendation.imageAsset)),
            contentDescription = recommendation.category,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .graphicsLayer {
                    alpha = iconAlpha * 0.95f
                    scaleX = iconScale * 1.12f
                    scaleY = iconScale * 1.12f
                    rotationZ = -10f * (1f - enterProgress)
                    translationY = (-22f + 22f * enterProgress).dp.toPx()
                },
        )
    }
}

@Composable
internal fun HomePagerDots(
    count: Int,
    activeIndex: Int,
) {
    if (count <= 1) return
    Row(
        modifier = Modifier.height(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count.coerceAtMost(5)) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == activeIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (index == activeIndex) Color(0xFF563600) else Color(0xFFEDE7DD)),
            )
        }
    }
}

@Composable
internal fun HomeQuoteCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = Color(0x0D4A3520),
                spotColor = Color(0x084A3520),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFEFAF4))
            .border(1.dp, Color(0xFFF0E6D8), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .debugOutline(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.home_quote),
            color = Color(0xFF3A3026),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
