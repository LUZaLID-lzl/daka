package com.luzalid.daka.ui.debug

import android.animation.ValueAnimator
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.luzalid.daka.R
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.sin

private const val RevealDurationMillis = 1600

private val EnterEase = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
private val SettleEase = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

/**
 * Pixel2Motion-style semantic assembly for the Explore category illustration.
 *
 * Each actor is a masked view of the source artwork. The separate actors choreograph the reveal,
 * then dissolve into the untouched source bitmap so the final-frame contract stays pixel-exact.
 */
@Composable
internal fun ExploreMotionIllustration(
    contentDescription: String?,
    playing: Boolean,
    replayKey: Int,
    modifier: Modifier = Modifier,
) {
    val inspectionMode = LocalInspectionMode.current
    val motionEnabled = inspectionMode ||
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
        ValueAnimator.areAnimatorsEnabled()
    val reveal = remember(replayKey) { Animatable(if (inspectionMode || !motionEnabled) 1f else 0f) }
    val idle = remember(replayKey) { Animatable(0f) }
    var idlePhase by remember(replayKey) { mutableFloatStateOf(0f) }

    LaunchedEffect(playing, replayKey, motionEnabled) {
        if (!playing || !motionEnabled) return@LaunchedEffect

        if (reveal.value < 1f) {
            reveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ((1f - reveal.value) * RevealDurationMillis).toInt(),
                    easing = LinearEasing,
                ),
            )
        }

        while (isActive) {
            idle.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3200, easing = LinearEasing),
            ) {
                idlePhase = value
            }
            idle.snapTo(0f)
            idlePhase = 0f
        }
    }

    val finalFrame = phase(reveal.value, 0.90f, 1f, EnterEase)
    val actorAlpha = 1f - finalFrame
    val breathe = sin(idlePhase * PI * 2).toFloat()

    Box(
        modifier = modifier.then(
            if (contentDescription == null) {
                Modifier
            } else {
                Modifier.semantics { this.contentDescription = contentDescription }
            },
        ),
    ) {
        ExploreActor(
            mask = ExploreMask.Ground,
            progress = phase(reveal.value, 0.02f, 0.42f, EnterEase),
            alpha = actorAlpha,
            fromX = 0f,
            fromY = 46f,
            fromScale = 0.82f,
        )
        ExploreActor(
            mask = ExploreMask.Cloud,
            progress = phase(reveal.value, 0.10f, 0.48f, EnterEase),
            alpha = actorAlpha,
            fromX = -38f,
            fromY = 12f,
            fromScale = 0.78f,
        )
        ExploreActor(
            mask = ExploreMask.Character,
            progress = phase(reveal.value, 0.18f, 0.66f, EnterEase),
            alpha = actorAlpha,
            fromX = 0f,
            fromY = 48f,
            fromScale = 0.72f,
        )
        ExploreActor(
            mask = ExploreMask.Backpack,
            progress = phase(reveal.value, 0.28f, 0.72f, SettleEase),
            alpha = actorAlpha,
            fromX = -44f,
            fromY = 12f,
            fromScale = 0.74f,
            fromRotation = -12f,
        )
        ExploreActor(
            mask = ExploreMask.Hat,
            progress = phase(reveal.value, 0.38f, 0.78f, SettleEase),
            alpha = actorAlpha,
            fromX = -10f,
            fromY = -54f,
            fromScale = 0.76f,
            fromRotation = -9f,
        )
        ExploreActor(
            mask = ExploreMask.Telescope,
            progress = phase(reveal.value, 0.48f, 0.84f, EnterEase),
            alpha = actorAlpha,
            fromX = 58f,
            fromY = -8f,
            fromScale = 0.70f,
            fromRotation = 8f,
        )
        ExploreActor(
            mask = ExploreMask.Pin,
            progress = phase(reveal.value, 0.58f, 0.90f, SettleEase),
            alpha = actorAlpha,
            fromX = 16f,
            fromY = -68f,
            fromScale = 0.58f,
            fromRotation = 5f,
        )

        Image(
            painter = painterResource(R.drawable.category_art_explore),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (motionEnabled) finalFrame else 1f)
                .graphicsLayer {
                    scaleX = 1f + breathe * 0.006f
                    scaleY = 1f - breathe * 0.004f
                    translationY = breathe * 1.4f
                },
        )

        ExploreAccentLayer(
            visible = finalFrame,
            phase = idlePhase,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ExploreActor(
    mask: ExploreMask,
    progress: Float,
    alpha: Float,
    fromX: Float,
    fromY: Float,
    fromScale: Float,
    fromRotation: Float = 0f,
) {
    val inverse = 1f - progress
    Image(
        painter = painterResource(R.drawable.category_art_explore),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha * progress.coerceIn(0f, 1f))
            .graphicsLayer {
                translationX = fromX * inverse
                translationY = fromY * inverse - sin(progress * PI).toFloat() * 8f
                rotationZ = fromRotation * inverse
                val scale = fromScale + (1f - fromScale) * progress
                scaleX = scale * (1f + sin(progress * PI).toFloat() * 0.045f)
                scaleY = scale * (1f - sin(progress * PI).toFloat() * 0.025f)
            }
            .drawWithContent {
                clipPath(mask.path(size.width, size.height)) {
                    this@drawWithContent.drawContent()
                }
            },
    )
}

@Composable
private fun ExploreAccentLayer(
    visible: Float,
    phase: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        if (visible <= 0f) return@Canvas

        val unit = size.minDimension / 500f
        val pulse = sin(phase * PI).toFloat().coerceAtLeast(0f)
        val lensCenter = Offset(371f * unit, 160f * unit)
        val pinCenter = Offset(381f * unit, 278f * unit)

        drawCircle(
            color = Color.White.copy(alpha = visible * pulse * 0.42f),
            radius = (20f + pulse * 4f) * unit,
            center = lensCenter,
            style = Stroke(width = 2.4f * unit),
        )
        drawLine(
            color = Color.White.copy(alpha = visible * pulse * pulse * 0.72f),
            start = lensCenter.copy(x = lensCenter.x - 8f * unit),
            end = lensCenter.copy(x = lensCenter.x + 8f * unit),
            strokeWidth = 2.2f * unit,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = Color(0xFFFF8A93).copy(alpha = visible * pulse * 0.32f),
            radius = (31f + pulse * 7f) * unit,
            center = pinCenter,
            style = Stroke(width = 2.2f * unit),
        )
    }
}

private fun phase(
    timeline: Float,
    start: Float,
    end: Float,
    easing: CubicBezierEasing,
): Float {
    val local = ((timeline - start) / (end - start)).coerceIn(0f, 1f)
    return easing.transform(local)
}

private enum class ExploreMask {
    Ground,
    Cloud,
    Character,
    Backpack,
    Hat,
    Telescope,
    Pin;

    fun path(width: Float, height: Float): Path {
        fun x(value: Float) = width * value / 500f
        fun y(value: Float) = height * value / 500f

        return Path().apply {
            when (this@ExploreMask) {
                Ground -> {
                    moveTo(x(110f), y(335f))
                    lineTo(x(432f), y(335f))
                    lineTo(x(455f), y(500f))
                    lineTo(x(82f), y(500f))
                    close()
                }

                Cloud -> {
                    moveTo(x(77f), y(332f))
                    lineTo(x(278f), y(332f))
                    lineTo(x(278f), y(450f))
                    lineTo(x(77f), y(450f))
                    close()
                }

                Character -> {
                    moveTo(x(116f), y(136f))
                    lineTo(x(307f), y(136f))
                    lineTo(x(326f), y(371f))
                    lineTo(x(116f), y(371f))
                    close()
                }

                Backpack -> {
                    moveTo(x(70f), y(195f))
                    lineTo(x(198f), y(195f))
                    lineTo(x(198f), y(353f))
                    lineTo(x(70f), y(353f))
                    close()
                }

                Hat -> {
                    moveTo(x(86f), y(66f))
                    lineTo(x(307f), y(66f))
                    lineTo(x(307f), y(198f))
                    lineTo(x(86f), y(198f))
                    close()
                }

                Telescope -> {
                    moveTo(x(248f), y(108f))
                    lineTo(x(415f), y(108f))
                    lineTo(x(415f), y(205f))
                    lineTo(x(248f), y(205f))
                    close()
                }

                Pin -> {
                    moveTo(x(315f), y(214f))
                    lineTo(x(446f), y(214f))
                    lineTo(x(446f), y(381f))
                    lineTo(x(315f), y(381f))
                    close()
                }
            }
        }
    }
}
