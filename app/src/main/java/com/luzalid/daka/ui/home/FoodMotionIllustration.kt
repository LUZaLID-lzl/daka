package com.luzalid.daka.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.debugOutline
import kotlin.math.PI
import kotlin.math.sin

@Composable
internal fun FoodMotionIllustration(
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "food-illustration")
    val breathe by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "food-breathe",
    )
    val blink by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3200
                0f at 0
                0f at 2480
                1f at 2560
                0f at 2640
                0f at 3200
            },
        ),
        label = "food-blink",
    )
    val steam by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
        ),
        label = "food-steam",
    )
    val bowlSwing by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "food-bowl-swing",
    )

    Box(
        modifier = modifier
            .then(
                if (contentDescription == null) {
                    Modifier
                } else {
                    Modifier.semantics { this.contentDescription = contentDescription }
                },
            )
            .debugOutline(),
    ) {
        FoodBackgroundLayer(
            motion = breathe,
            modifier = Modifier.fillMaxSize(),
        )
        FoodCharacterLayer(
            motion = breathe,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = breathe * 2.5.dp.toPx()
                    scaleX = 1f + breathe * 0.012f
                    scaleY = 1f - breathe * 0.008f
                },
        )
        FoodFaceLayer(
            blink = blink,
            smile = breathe,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = breathe * 2.5.dp.toPx() },
        )
        FoodBowlLayer(
            swing = bowlSwing,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = bowlSwing * 1.4f
                    translationY = -breathe * 1.5.dp.toPx()
                },
        )
        FoodSteamLayer(
            progress = steam,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun FoodBackgroundLayer(
    motion: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val unit = size.minDimension / 100f
        drawCircle(
            color = Color(0xFF00A86B),
            radius = 13f * unit,
            center = Offset((24f + motion) * unit, 67f * unit),
        )
        drawCircle(
            color = Color(0xFFF8BBD0),
            radius = 8f * unit,
            center = Offset((78f - motion) * unit, 28f * unit),
        )
        drawRoundRect(
            color = Color(0xFF1769E0),
            topLeft = Offset(16f * unit, 26f * unit),
            size = Size(5f * unit, 28f * unit),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * unit),
        )
        repeat(3) { index ->
            drawRoundRect(
                color = Color(0xFF1769E0),
                topLeft = Offset((13f + index * 5f) * unit, 22f * unit),
                size = Size(3f * unit, 13f * unit),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f * unit),
            )
        }
    }
}

@Composable
private fun FoodCharacterLayer(
    motion: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val unit = size.minDimension / 100f
        drawOval(
            color = Color(0xFFFF6D00),
            topLeft = Offset(30f * unit, 24f * unit),
            size = Size(43f * unit, (50f + motion) * unit),
        )
        drawOval(
            color = Color(0xFF00A86B),
            topLeft = Offset(22f * unit, 51f * unit),
            size = Size(25f * unit, 13f * unit),
        )
        drawOval(
            color = Color(0xFF00A86B),
            topLeft = Offset(66f * unit, 48f * unit),
            size = Size(21f * unit, 12f * unit),
        )
    }
}

@Composable
private fun FoodFaceLayer(
    blink: Float,
    smile: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val unit = size.minDimension / 100f
        val eyeHeight = (1f - blink * 0.82f).coerceAtLeast(0.18f)
        listOf(45f, 59f).forEach { x ->
            drawArc(
                color = Color(0xFF161616),
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset((x - 4f) * unit, (39f + (1f - eyeHeight) * 2f) * unit),
                size = Size(8f * unit, 5f * unit * eyeHeight),
                style = Stroke(width = 2.2f * unit, cap = StrokeCap.Round),
            )
        }
        drawArc(
            color = Color(0xFF161616),
            startAngle = 15f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(44f * unit, (48f + smile * 0.5f) * unit),
            size = Size(18f * unit, 10f * unit),
            style = Stroke(width = 2.4f * unit, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun FoodBowlLayer(
    swing: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val unit = size.minDimension / 100f
        drawOval(
            color = Color(0xFFFFE4B5),
            topLeft = Offset(33f * unit, 55f * unit),
            size = Size(39f * unit, 12f * unit),
        )
        drawCircle(Color(0xFFFFC107), 6.5f * unit, Offset(43f * unit, 58f * unit))
        drawCircle(Color(0xFFF5F0DC), 4.1f * unit, Offset(43f * unit, 58f * unit))
        drawCircle(Color(0xFFFFA000), 2.3f * unit, Offset(43f * unit, 58f * unit))
        drawCircle(Color(0xFF66BB3A), 5.2f * unit, Offset(56f * unit, 57f * unit))
        drawCircle(Color(0xFFFF8A35), 4.5f * unit, Offset(65f * unit, 59f * unit))

        val bowl = Path().apply {
            moveTo(29f * unit, 62f * unit)
            quadraticTo(52f * unit, (68f + swing) * unit, 76f * unit, 62f * unit)
            quadraticTo(72f * unit, 84f * unit, 53f * unit, 86f * unit)
            quadraticTo(34f * unit, 84f * unit, 29f * unit, 62f * unit)
            close()
        }
        drawPath(bowl, Color(0xFFFF5C93))
        drawOval(
            color = Color(0xFFFF8AB3),
            topLeft = Offset(29f * unit, 59f * unit),
            size = Size(47f * unit, 8f * unit),
        )
    }
}

@Composable
private fun FoodSteamLayer(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val unit = size.minDimension / 100f
        repeat(2) { index ->
            val localProgress = (progress + index * 0.45f) % 1f
            val alpha = sin(localProgress * PI).toFloat().coerceAtLeast(0f)
            val x = (43f + index * 17f + sin(localProgress * PI * 2).toFloat() * 2f) * unit
            val y = (54f - localProgress * 22f) * unit
            val steamPath = Path().apply {
                moveTo(x, y)
                cubicTo(
                    x - 4f * unit,
                    y - 4f * unit,
                    x + 5f * unit,
                    y - 7f * unit,
                    x,
                    y - 12f * unit,
                )
            }
            drawPath(
                path = steamPath,
                color = Color.White.copy(alpha = alpha * 0.82f),
                style = Stroke(width = 2.6f * unit, cap = StrokeCap.Round),
            )
        }
    }
}

@Preview(name = "Food Motion Illustration", showBackground = true)
@Composable
private fun FoodMotionIllustrationPreview() {
    CompositionLocalProvider(LocalDebugUiOutline provides false) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(Color(0xFFFFC72C)),
        ) {
            FoodMotionIllustration(
                contentDescription = "Food",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
