package com.luzalid.daka.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.R
import kotlin.math.abs

@Composable
internal fun HomeRecommendationStage(
    recommendations: List<Recommendation>,
    activeIndex: Int,
    dragDistance: Float,
    onDragDistanceChange: (Float) -> Unit,
    onSwipe: (Int) -> Unit,
    onActiveIndexChange: (Int) -> Unit,
    onRecord: (Recommendation) -> Unit,
) {
    if (recommendations.isEmpty()) return
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(326.dp)
            .debugOutline(),
        contentAlignment = Alignment.Center,
    ) {
        val itemCount = recommendations.size
        val stackKey = remember(recommendations) { recommendations.joinToString { it.id } }
        val currentIndex = activeIndex.coerceIn(0, itemCount - 1)

        val pageWidth = (maxWidth - 128.dp).coerceIn(214.dp, 246.dp)
        recommendations
            .mapIndexed { index, recommendation ->
                StackedCardSpec(
                    index = index,
                    depth = cardDepth(index = index, activeIndex = currentIndex, itemCount = itemCount),
                    recommendation = recommendation,
                )
            }
            .sortedByDescending { it.depth }
            .forEach { spec ->
                key(spec.recommendation.id) {
                    AnimatedStackedCard(
                        spec = spec,
                        itemCount = itemCount,
                        pageWidth = pageWidth,
                        stackKey = stackKey,
                        dragDistance = dragDistance,
                        onDragDistanceChange = onDragDistanceChange,
                        onSwipe = onSwipe,
                        onActivate = { onActiveIndexChange(spec.index) },
                        onRecord = { onRecord(spec.recommendation) },
                    )
                }
            }
    }
}

@Composable
private fun AnimatedStackedCard(
    spec: StackedCardSpec,
    itemCount: Int,
    pageWidth: androidx.compose.ui.unit.Dp,
    stackKey: String,
    dragDistance: Float,
    onDragDistanceChange: (Float) -> Unit,
    onSwipe: (Int) -> Unit,
    onActivate: () -> Unit,
    onRecord: () -> Unit,
) {
    val isFront = spec.depth == 0
    val shape = RoundedCornerShape(if (isFront) 28.dp else 30.dp)
    val baseHeight = 302.dp
    val widthScale = when (spec.depth) {
        1 -> 0.92f
        2 -> 0.90f
        else -> 1f
    }
    val heightScale = when (spec.depth) {
        1 -> 288f / 302f
        2 -> 282f / 302f
        else -> 1f
    }
    val xTarget = when (spec.depth) {
        1 -> -48f
        2 -> 48f
        else -> 0f
    }
    val yTarget = when (spec.depth) {
        1 -> -18f
        2 -> -30f
        else -> 28f
    }
    val rotationTarget = when (spec.depth) {
        1 -> -8f
        2 -> 7f
        else -> 0f
    }
    val alphaTarget = when (spec.depth) {
        1 -> 0.94f
        2 -> 0.88f
        else -> 1f
    }
    val transition = tween<Float>(durationMillis = 360, easing = FastOutSlowInEasing)
    val animatedScaleX by animateFloatAsState(
        targetValue = widthScale,
        animationSpec = transition,
        label = "stacked-card-scale-x",
    )
    val animatedScaleY by animateFloatAsState(
        targetValue = heightScale,
        animationSpec = transition,
        label = "stacked-card-scale-y",
    )
    val animatedX by animateFloatAsState(
        targetValue = xTarget,
        animationSpec = transition,
        label = "stacked-card-x",
    )
    val animatedY by animateFloatAsState(
        targetValue = yTarget,
        animationSpec = transition,
        label = "stacked-card-y",
    )
    val animatedRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = transition,
        label = "stacked-card-rotation",
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = alphaTarget,
        animationSpec = transition,
        label = "stacked-card-alpha",
    )
    val idleTransition = rememberInfiniteTransition(label = "stacked-card-idle-${spec.index}")
    val idleMotion by idleTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2600 + spec.depth * 260,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "stacked-card-idle-float",
    )
    val swipeModifier = if (isFront && itemCount > 1) {
        Modifier.pointerInput(stackKey) {
            var totalDrag = 0f
            detectHorizontalDragGestures(
                onDragStart = {
                    totalDrag = 0f
                    onDragDistanceChange(0f)
                },
                onHorizontalDrag = { _, dragAmount ->
                    totalDrag = (totalDrag + dragAmount).coerceIn(-140f, 140f)
                    onDragDistanceChange(totalDrag)
                },
                onDragEnd = {
                    if (abs(totalDrag) > 42f) {
                        onSwipe(if (totalDrag < 0f) -1 else 1)
                    }
                    totalDrag = 0f
                    onDragDistanceChange(0f)
                },
                onDragCancel = {
                    totalDrag = 0f
                    onDragDistanceChange(0f)
                },
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .width(pageWidth)
            .height(baseHeight)
            .zIndex(10f - spec.depth)
            .graphicsLayer {
                val dragProgress = if (isFront) dragDistance / 140f else 0f
                val idleY = idleMotion * if (isFront) 5f else 2.5f
                val idleRotation = idleMotion * if (isFront) 0.8f else 0.35f
                translationX = animatedX.dp.toPx() + if (isFront) dragDistance else 0f
                translationY = animatedY.dp.toPx() + idleY
                rotationZ = animatedRotation + idleRotation + dragProgress * 7f
                alpha = animatedAlpha
                scaleX = animatedScaleX * if (isFront) 1f - abs(dragProgress) * 0.025f else 1f
                scaleY = animatedScaleY * if (isFront) 1f - abs(dragProgress) * 0.025f else 1f
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .then(swipeModifier),
        contentAlignment = Alignment.Center,
    ) {
        DailyWalkCard(
            recommendation = spec.recommendation,
            onRecord = {
                if (isFront) {
                    onRecord()
                } else {
                    onActivate()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
        )
    }
}

private fun cardDepth(
    index: Int,
    activeIndex: Int,
    itemCount: Int,
): Int {
    if (index == activeIndex) return 0
    if (itemCount <= 1) return 0
    if (index == (activeIndex + 1) % itemCount) return 1
    return 2
}

private data class StackedCardSpec(
    val index: Int,
    val depth: Int,
    val recommendation: Recommendation,
)

@Composable
private fun DailyWalkCard(
    recommendation: Recommendation,
    onRecord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)
    val palette = remember(recommendation.imageAsset) {
        recommendationPalette(recommendation.imageAsset)
    }
    Box(
        modifier = modifier
            .shadow(
                elevation = 36.dp,
                shape = shape,
                clip = false,
                ambientColor = palette.shadow,
                spotColor = palette.shadow.copy(alpha = 0.14f),
            )
            .clip(shape)
            .background(palette.gradient.last())
            .border(1.dp, palette.stroke, shape)
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
                            palette.glow.copy(alpha = 0.42f),
                            palette.glow.copy(alpha = 0.14f),
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
                        palette.gradient,
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
                            Color.Black.copy(alpha = 0.08f),
                            palette.scrim.copy(alpha = 0.66f),
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
                palette = palette,
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
        }
    }
}

@Composable
private fun SuggestionPill(
    text: String,
    imageAsset: String,
    palette: RecommendationPalette,
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
            .background(palette.pillBackground)
            .border(
                width = 1.dp,
                color = palette.pillStroke,
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
            tint = palette.pillForeground,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = text,
            color = palette.pillForeground,
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

@Composable
internal fun HomeRecommendationDetail(
    recommendation: Recommendation,
    onShuffle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 54.dp)
            .debugOutline(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = recommendation.description,
            color = Color(0xFF8F9398),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF2F6CE5))
                .clickable(onClick = onShuffle)
                .padding(horizontal = 24.dp)
                .debugOutline(RoundedCornerShape(999.dp)),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Shuffle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp),
            )
            Text(
                text = stringResource(R.string.home_shuffle_group),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

private fun previewRecommendations() = listOf(
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

@Preview(name = "Home Card Stack", showBackground = true, widthDp = 360, heightDp = 380)
@Composable
private fun HomeRecommendationStagePreview() {
    var activeIndex by remember { mutableIntStateOf(0) }
    var dragDistance by remember { mutableFloatStateOf(0f) }
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(Color(0xFFF7F8FA)),
            contentAlignment = Alignment.Center,
        ) {
            HomeRecommendationStage(
                recommendations = previewRecommendations(),
                activeIndex = activeIndex,
                dragDistance = dragDistance,
                onDragDistanceChange = { dragDistance = it },
                onSwipe = { direction ->
                    activeIndex = if (direction < 0) {
                        (activeIndex + 1) % previewRecommendations().size
                    } else {
                        (activeIndex - 1 + previewRecommendations().size) % previewRecommendations().size
                    }
                },
                onActiveIndexChange = { activeIndex = it },
                onRecord = {},
            )
        }
    }
}

@Preview(name = "Home Detail CTA", showBackground = true, widthDp = 360, heightDp = 180)
@Composable
private fun HomeRecommendationDetailPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F8FA))
                .padding(vertical = 24.dp),
        ) {
            HomeRecommendationDetail(
                recommendation = previewRecommendations().first(),
                onShuffle = {},
            )
        }
    }
}

@Preview(name = "Home Quote", showBackground = true, widthDp = 360, heightDp = 120)
@Composable
private fun HomeQuoteCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F8FA))
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            HomeQuoteCard()
        }
    }
}
