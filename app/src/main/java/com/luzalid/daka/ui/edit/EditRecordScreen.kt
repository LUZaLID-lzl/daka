package com.luzalid.daka.ui.edit

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline
import com.luzalid.daka.ui.home.categoryMotionImageRes
import com.luzalid.daka.ui.home.recommendationPalette

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.MediaAttachmentDraft
import com.luzalid.daka.model.MediaType
import com.luzalid.daka.model.Recommendation
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun EditRecordScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    recordId: String?,
    fallbackRecommendation: Recommendation,
    fromRecommendationCard: Boolean,
    onCancel: () -> Unit,
    onSaved: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val detail by remember(recordId) {
        if (recordId == null) flowOf(null) else repository.observeRecordDetail(recordId)
    }.collectAsState(initial = null)
    val activeRecommendation by produceState(
        initialValue = fallbackRecommendation,
        recordId,
        detail?.recommendationId,
        context.resources.configuration,
    ) {
        value = detail?.recommendationId
            ?.let { repository.getRecommendation(it, context.resources) }
            ?: fallbackRecommendation
    }

    var loadedRecordId by remember(recordId) { mutableStateOf<String?>(null) }
    var title by remember(recordId, fromRecommendationCard) {
        mutableStateOf(if (fromRecommendationCard) fallbackRecommendation.title else "")
    }
    var content by remember(recordId) { mutableStateOf("") }
    var mood by remember(recordId) { mutableStateOf("") }
    var location by remember(recordId) { mutableStateOf("") }
    var tags by remember(recordId) { mutableStateOf("") }
    val media = remember(recordId) { mutableStateListOf<MediaAttachmentDraft>() }
    val moodStrokes = remember(recordId) { mutableStateListOf<DrawnTagStroke>() }
    var saving by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }
    var moodAnimationTrigger by remember { mutableStateOf(0) }
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        uris.forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            val mimeType = context.contentResolver.getType(uri).orEmpty()
            media.add(
                MediaAttachmentDraft(
                    id = UUID.randomUUID().toString(),
                    type = if (mimeType.startsWith("video/")) MediaType.Video else MediaType.Image,
                    uri = uri.toString(),
                    thumbnailUri = uri.toString(),
                ),
            )
        }
    }

    LaunchedEffect(Unit) {
        focusManager.clearFocus(force = true)
    }

    LaunchedEffect(recordId, detail?.id, activeRecommendation.id, fromRecommendationCard) {
        if (recordId == null && loadedRecordId != "new") {
            title = if (fromRecommendationCard) activeRecommendation.title else ""
            content = ""
            mood = ""
            location = ""
            tags = ""
            media.clear()
            moodStrokes.clear()
            loadedRecordId = "new"
        }
        if (recordId != null && detail != null && loadedRecordId != detail!!.id) {
            title = detail!!.title
            content = detail!!.content
            mood = detail!!.mood
            location = detail!!.location
            tags = detail!!.tags
            media.clear()
            media.addAll(repository.getMediaDraftsForVersion(detail!!.currentVersionId))
            moodStrokes.clear()
            loadedRecordId = detail!!.id
        }
    }

    fun save() {
        if (saving || (content.isBlank() && media.isEmpty())) return
        saving = true
        scope.launch {
            val savedId = repository.saveRecord(
                recordId = recordId,
                recommendation = activeRecommendation,
                title = title,
                content = content,
                mood = mood,
                location = location,
                tags = tags,
                media = media.toList(),
            )
            saving = false
            onSaved(savedId)
        }
    }

    val appearance = LocalAppAppearance.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(appearance.backgroundBrush)
            .debugOutline(),
    ) {
        JournalEditContent(
            title = title,
            onTitleChange = { title = it },
            content = content,
            onContentChange = { content = it },
            mood = mood,
            moodStrokes = moodStrokes,
            moodAnimationTrigger = moodAnimationTrigger,
            tags = tags,
            recommendation = activeRecommendation,
            showRecommendationIcon = fromRecommendationCard,
            showTitleField = !fromRecommendationCard,
            onCancel = onCancel,
            onSave = ::save,
            saving = saving,
        )
        if (fromRecommendationCard) {
            RecommendationArtworkFlight(
                recommendation = activeRecommendation,
                modifier = Modifier.fillMaxSize(),
            )
        }
        JournalBottomToolbar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onPickMedia = {
                mediaPicker.launch(arrayOf("image/*", "video/*"))
            },
            onPickTag = { showTagSheet = true },
        )
        if (media.isNotEmpty()) {
            JournalMediaPreviewTray(
                media = media,
                onRemove = { media.remove(it) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 108.dp),
            )
        }
        AnimatedVisibility(
            visible = showTagSheet,
            enter = fadeIn(tween(durationMillis = 180, easing = DrawerGsapEase)) + slideInVertically(
                animationSpec = tween(durationMillis = 520, easing = DrawerGsapEase),
                initialOffsetY = { it },
            ),
            exit = fadeOut(tween(durationMillis = 320, easing = DrawerExitEase)) + slideOutVertically(
                animationSpec = tween(durationMillis = 620, easing = DrawerExitEase),
                targetOffsetY = { (it * 1.08f).toInt() },
            ),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
        ) {
            JournalTagSheet(
                selectedMood = mood,
                onComplete = { selectedMood, strokes ->
                    mood = selectedMood
                    moodStrokes.clear()
                    moodStrokes.addAll(strokes)
                    showTagSheet = false
                    scope.launch {
                        delay(360)
                        moodAnimationTrigger += 1
                    }
                },
                onDismiss = { showTagSheet = false },
            )
        }
    }
}

@Composable
private fun JournalEditContent(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    mood: String,
    moodStrokes: List<DrawnTagStroke>,
    moodAnimationTrigger: Int,
    tags: String,
    recommendation: Recommendation,
    showRecommendationIcon: Boolean,
    showTitleField: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    saving: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 176.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        JournalEditTopBar(
            recommendation = recommendation,
            mood = mood,
            moodStrokes = moodStrokes,
            moodAnimationTrigger = moodAnimationTrigger,
            tags = tags,
            showRecommendationIcon = showRecommendationIcon,
            onCancel = onCancel,
            onSave = onSave,
            saving = saving,
        )
        Spacer(Modifier.height(if (showTitleField) 24.dp else 16.dp))
        if (showTitleField) {
            JournalTitleField(title = title, onTitleChange = onTitleChange)
            Spacer(Modifier.height(22.dp))
        }
        JournalContentField(
            content = content,
            placeholder = if (showRecommendationIcon) recommendation.description else stringResource(R.string.field_content),
            onContentChange = onContentChange,
            modifier = Modifier.weight(1f),
        )
        if (tags.isNotBlank()) {
            Spacer(Modifier.height(18.dp))
            JournalTagsDisplay(tags = tags)
        }
    }
}

private val CardEntryEase = CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f)
private val DrawerGsapEase = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
private val DrawerExitEase = CubicBezierEasing(0.55f, 0f, 0.85f, 0.36f)

@Composable
private fun RecommendationArtworkFlight(
    recommendation: Recommendation,
    modifier: Modifier = Modifier,
) {
    val progress = remember(recommendation.id) { Animatable(0f) }
    val containerSize = LocalWindowInfo.current.containerSize
    val containerHeight = containerSize.height.toFloat()
    val containerWidth = containerSize.width.toFloat()
    val turnDirection = remember(recommendation.id) {
        if (recommendation.id.hashCode() and 1 == 0) 1f else -1f
    }
    LaunchedEffect(recommendation.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 640, easing = LinearEasing),
        )
    }

    val value = progress.value
    if (value < 1f) {
        Box(modifier = modifier) {
            listOf(0.105f to 0.08f, 0.07f to 0.15f, 0.035f to 0.28f, 0f to 1f)
                .forEach { (delay, opacity) ->
                val trailProgress = (value - delay).coerceAtLeast(0f)
                Image(
                    painter = painterResource(categoryMotionImageRes(recommendation.imageAsset)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 18.dp)
                        .size(38.dp)
                        .graphicsLayer {
                            val remaining = 1f - trailProgress
                            val targetHandoff = (remaining / 0.14f).coerceIn(0f, 1f)
                            translationX = artworkFlightX(
                                progress = trailProgress,
                                amplitude = containerWidth * 0.13f,
                                direction = turnDirection,
                            )
                            translationY = containerHeight * 0.32f * remaining
                            scaleX = 1f + 3.35f * remaining
                            scaleY = 1f + 3.35f * remaining
                            rotationZ = artworkFlightRotation(trailProgress, turnDirection)
                            val launchAlpha = (trailProgress / 0.08f).coerceIn(0f, 1f)
                            alpha = targetHandoff * launchAlpha * opacity
                        },
                )
            }
        }
    }
}

private fun artworkFlightX(
    progress: Float,
    amplitude: Float,
    direction: Float,
): Float {
    val radians = progress * PI.toFloat() * 2f
    val envelope = sin(progress * PI.toFloat()).coerceAtLeast(0f)
    return amplitude * direction * sin(radians) * envelope
}

private fun artworkFlightRotation(progress: Float, direction: Float): Float {
    val radians = progress * PI.toFloat() * 2f
    val envelope = sin(progress * PI.toFloat()).coerceAtLeast(0f)
    val envelopeSlope = PI.toFloat() * cos(progress * PI.toFloat())
    val curveSlope = 2f * PI.toFloat() * cos(radians) * envelope +
        sin(radians) * envelopeSlope
    return (-curveSlope * 1.15f * direction).coerceIn(-13f, 13f)
}

@Composable
private fun JournalRecommendationCardTransition(
    recommendation: Recommendation,
    moodText: String,
    moodStrokes: List<DrawnTagStroke>,
    moodAnimationTrigger: Int,
) {
    val progress = remember(recommendation.id) { Animatable(0f) }
    LaunchedEffect(recommendation.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 460, easing = CardEntryEase),
        )
    }
    val value = progress.value
    JournalRecommendationTagPill(
        recommendation = recommendation,
        moodText = moodText,
        moodStrokes = moodStrokes,
        moodAnimationTrigger = moodAnimationTrigger,
        modifier = Modifier.graphicsLayer {
            alpha = ((value - 0.72f) / 0.28f).coerceIn(0f, 1f)
            translationY = (1f - value) * 8.dp.toPx()
            scaleX = 0.92f + value * 0.08f
            scaleY = 0.92f + value * 0.08f
            rotationZ = -4f * (1f - value)
        },
    )
}

@Composable
private fun JournalRecommendationTagPill(
    recommendation: Recommendation,
    moodText: String,
    moodStrokes: List<DrawnTagStroke>,
    moodAnimationTrigger: Int,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)
    val popScale = remember { Animatable(1f) }
    LaunchedEffect(moodAnimationTrigger) {
        if (moodAnimationTrigger > 0) {
            popScale.snapTo(0.72f)
            popScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 680
                    0.72f at 0 using DrawerGsapEase
                    1.58f at 250 using CardEntryEase
                    0.92f at 500 using CardEntryEase
                    1f at 680
                },
            )
        }
    }
    val popValue = popScale.value
    Row(
        modifier = modifier
            .height(46.dp)
            .width(82.dp)
            .shadow(
                elevation = 13.dp,
                shape = shape,
                clip = false,
                ambientColor = Color(0x160B1C30),
                spotColor = Color(0x100B1C30),
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.70f),
                        Color.White.copy(alpha = 0.42f),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.76f), shape)
            .padding(horizontal = 8.dp)
            .debugOutline(shape),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.34f)),
            contentAlignment = Alignment.Center,
        ) {
            if (moodAnimationTrigger > 0 && (moodText.isNotBlank() || moodStrokes.isNotEmpty())) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer {
                            val glow = ((popValue - 1f) / 0.58f).coerceIn(0f, 1f)
                            scaleX = 1f + glow * 0.72f
                            scaleY = 1f + glow * 0.72f
                            alpha = glow * 0.48f
                        }
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.54f)),
                )
            }
            if (moodStrokes.isNotEmpty()) {
                JournalMoodStrokePreview(
                    strokes = moodStrokes,
                    backgroundColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .graphicsLayer {
                            scaleX = popValue
                            scaleY = popValue
                        },
                )
            } else if (moodText.isNotBlank()) {
                Text(
                    text = moodIcon(moodText),
                    fontSize = 19.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        scaleX = popValue
                        scaleY = popValue
                    },
                )
            } else {
                Image(
                    painter = painterResource(categoryMotionImageRes(recommendation.imageAsset)),
                    contentDescription = recommendation.category,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .graphicsLayer {
                            alpha = 0.94f
                            scaleX = 1.18f
                            scaleY = 1.18f
                        },
                )
            }
        }
    }
}

@Composable
private fun JournalBottomToolbar(
    modifier: Modifier = Modifier,
    onPickMedia: () -> Unit,
    onPickTag: () -> Unit,
) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 14.dp)
            .fillMaxWidth(0.62f)
            .widthIn(max = 228.dp)
            .height(74.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false,
                ambientColor = Color(0x160B1C30),
                spotColor = Color(0x0F0B1C30),
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.78f))
            .border(1.dp, Color.White.copy(alpha = 0.54f), RoundedCornerShape(32.dp))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JournalToolButton(label = stringResource(R.string.edit_toolbar_photo), onClick = onPickMedia) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
        }
        JournalToolButton(label = stringResource(R.string.edit_toolbar_tag), onClick = onPickTag) {
            Icon(Icons.Filled.Mood, contentDescription = null)
        }
    }
}

@Composable
private fun JournalToolButton(
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(68.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.24f))
            .clickable(onClick = onClick)
            .debugOutline(RoundedCornerShape(22.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
    ) {
        CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides Color(0xFF464555),
        ) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                icon()
            }
            Text(
                text = label,
                color = Color(0xFF464555),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.55.sp,
                ),
            )
        }
    }
}

private data class JournalMoodOption(
    val emoji: String,
    val label: String,
    val color: Color,
)

private data class DrawnTagStroke(
    val color: Color,
    val width: Float,
    val points: List<Offset>,
)

@Composable
private fun journalMoodOptions() = listOf(
    JournalMoodOption("❤️", stringResource(R.string.mood_love), Color(0xFFE40046)),
    JournalMoodOption("😊", stringResource(R.string.mood_happy), Color(0xFFFFB85F)),
    JournalMoodOption("😌", stringResource(R.string.mood_calm), Color(0xFF2412C9)),
    JournalMoodOption("🥲", stringResource(R.string.mood_moved), Color(0xFF1698D3)),
    JournalMoodOption("🌿", stringResource(R.string.mood_fresh), Color(0xFF3D9B10)),
)

private fun moodIcon(mood: String): String =
    mood.substringBefore(" ").ifBlank { "✍️" }

private fun MutableList<DrawnTagStroke>.recolorMoodStrokes(
    color: Color,
    canvasBackground: Color,
) {
    indices.forEach { index ->
        val stroke = this[index]
        if (stroke.color != canvasBackground) {
            this[index] = stroke.copy(color = color)
        }
    }
}

@Composable
private fun JournalTagSheet(
    selectedMood: String,
    onComplete: (String, List<DrawnTagStroke>) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = journalMoodOptions()
    val sheetScope = rememberCoroutineScope()
    val sketchTag = stringResource(R.string.edit_tag_sketch)
    val defaultMood = "${options[1].emoji} ${options[1].label}"
    var draftMood by remember(selectedMood) { mutableStateOf(selectedMood.ifBlank { defaultMood }) }
    var selectedColor by remember(selectedMood, options) {
        mutableStateOf(options.firstOrNull { it.emoji == moodIcon(draftMood) }?.color ?: options.first().color)
    }
    var erasing by remember { mutableStateOf(false) }
    val strokes = remember { mutableStateListOf<DrawnTagStroke>() }
    val drawerProgress = remember { Animatable(0f) }
    val colorPulse = remember { Animatable(0f) }
    val canvasBackground = Color(0xFFF7F6FA)
    fun selectMoodColor(color: Color, mood: String = "✍️ $sketchTag") {
        selectedColor = color
        draftMood = mood
        erasing = false
        strokes.recolorMoodStrokes(color = color, canvasBackground = canvasBackground)
        sheetScope.launch {
            colorPulse.snapTo(0f)
            colorPulse.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 360, easing = DrawerGsapEase),
            )
        }
    }
    LaunchedEffect(Unit) {
        drawerProgress.snapTo(0f)
        drawerProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 520, easing = DrawerGsapEase),
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.05f),
                        Color(0xFF171226).copy(alpha = 0.22f),
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(518.dp)
                .graphicsLayer {
                    val value = drawerProgress.value
                    alpha = value
                    translationY = (1f - value) * 34.dp.toPx()
                    scaleY = 0.965f + value * 0.035f
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .shadow(
                    elevation = 28.dp,
                    shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp),
                    clip = false,
                    ambientColor = Color(0x1A0B1C30),
                    spotColor = Color(0x140B1C30),
                )
                .clip(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFCFBFE),
                            Color(0xFFF4F2F8),
                            Color(0xFFECE9F2),
                        ),
                    ),
                )
                .border(1.dp, Color.White.copy(alpha = 0.88f), RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {},
                )
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .debugOutline(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFB8B4C3).copy(alpha = 0.72f)),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.edit_tag_sheet_title),
                color = Color(0xFF332B78),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.35.sp,
                ),
            )
            Spacer(Modifier.height(16.dp))
            JournalTagDrawingCanvas(
                strokes = strokes,
                selectedColor = selectedColor,
                erasing = erasing,
                canvasBackground = canvasBackground,
                onDrawingStarted = {
                    if (draftMood.isBlank()) draftMood = "✍️ $sketchTag"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(30.dp),
                        clip = false,
                        ambientColor = Color(0x120B1C30),
                        spotColor = Color(0x0C0B1C30),
                    ),
            )
            Spacer(Modifier.height(16.dp))
            JournalTagActionRow(
                erasing = erasing,
                onToggleErase = { erasing = !erasing },
                onClear = { strokes.clear() },
                onDone = { onComplete(draftMood.ifBlank { "✍️ $sketchTag" }, strokes.toList()) },
            )
            Spacer(Modifier.height(12.dp))
            JournalMoodColorSelector(
                options = options,
                selectedColor = selectedColor,
                colorPulse = colorPulse.value,
                onSelectOption = { option ->
                    selectMoodColor(
                        color = option.color,
                        mood = "${option.emoji} ${option.label}",
                    )
                },
            )
        }
    }
}

@Composable
private fun JournalMoodColorSelector(
    options: List<JournalMoodOption>,
    selectedColor: Color,
    colorPulse: Float,
    onSelectOption: (JournalMoodOption) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFE8E5F2).copy(alpha = 0.94f),
                        Color(0xFFDED9EB).copy(alpha = 0.90f),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.78f), RoundedCornerShape(999.dp))
            .padding(horizontal = 20.dp)
            .debugOutline(RoundedCornerShape(999.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val selected = option.color == selectedColor
            JournalMoodColorDot(
                color = option.color,
                selected = selected,
                pulse = if (selected) colorPulse else 0f,
                onClick = { onSelectOption(option) },
            )
        }
    }
}

@Composable
private fun JournalMoodColorDot(
    color: Color,
    selected: Boolean,
    pulse: Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(if (selected) 50.dp else 34.dp)
            .graphicsLayer {
                val bump = if (pulse < 0.5f) pulse / 0.5f else 1f - ((pulse - 0.5f) / 0.5f)
                scaleX = 1f + bump.coerceIn(0f, 1f) * 0.18f
                scaleY = 1f + bump.coerceIn(0f, 1f) * 0.18f
            }
            .clip(CircleShape)
            .background(if (selected) Color.White.copy(alpha = 0.34f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color),
        )
    }
}

@Composable
private fun JournalTagDrawingCanvas(
    strokes: MutableList<DrawnTagStroke>,
    selectedColor: Color,
    erasing: Boolean,
    canvasBackground: Color,
    onDrawingStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(canvasBackground)
            .border(1.dp, Color.White.copy(alpha = 0.64f), RoundedCornerShape(28.dp))
            .pointerInput(selectedColor, erasing) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onDrawingStarted()
                        strokes.add(
                            DrawnTagStroke(
                                color = if (erasing) canvasBackground else selectedColor,
                                width = if (erasing) 34f else 12f,
                                points = listOf(offset),
                            ),
                        )
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        if (strokes.isNotEmpty()) {
                            val lastIndex = strokes.lastIndex
                            val last = strokes[lastIndex]
                            strokes[lastIndex] = last.copy(points = last.points + change.position)
                        }
                    },
                )
            }
            .debugOutline(RoundedCornerShape(28.dp)),
    ) {
        strokes.forEach { stroke ->
            if (stroke.points.size == 1) {
                drawCircle(
                    color = stroke.color,
                    radius = stroke.width / 2f,
                    center = stroke.points.first(),
                )
            } else {
                stroke.points.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = stroke.color,
                        start = start,
                        end = end,
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalMoodStrokePreview(
    strokes: List<DrawnTagStroke>,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.background(backgroundColor)) {
        val points = strokes.flatMap { it.points }
        if (points.isEmpty()) return@Canvas
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val sourceWidth = max(1f, maxX - minX)
        val sourceHeight = max(1f, maxY - minY)
        val scale = min(size.width / sourceWidth, size.height / sourceHeight) * 0.74f
        val offset = Offset(
            x = (size.width - sourceWidth * scale) / 2f - minX * scale,
            y = (size.height - sourceHeight * scale) / 2f - minY * scale,
        )
        strokes.forEach { stroke ->
            val width = max(2.2f, stroke.width * scale)
            if (stroke.points.size == 1) {
                drawCircle(
                    color = stroke.color,
                    radius = width / 2f,
                    center = stroke.points.first() * scale + offset,
                )
            } else {
                stroke.points.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = stroke.color,
                        start = start * scale + offset,
                        end = end * scale + offset,
                        strokeWidth = width,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalTagActionRow(
    erasing: Boolean,
    onToggleErase: () -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JournalTagActionButton(
            text = stringResource(R.string.edit_tag_erase),
            selected = erasing,
            onClick = onToggleErase,
        )
        JournalTagActionButton(
            text = stringResource(R.string.edit_tag_clear),
            selected = false,
            onClick = onClear,
        )
        JournalTagActionButton(
            text = stringResource(R.string.edit_tag_done),
            selected = true,
            onClick = onDone,
        )
    }
}

@Composable
private fun JournalTagActionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Color(0xFF3126C8) else Color.White.copy(alpha = 0.58f))
            .border(1.dp, Color.White.copy(alpha = 0.68f), RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        text = text,
        color = if (selected) Color.White else Color(0xFF555765),
        style = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.SemiBold,
        ),
    )
}

@Composable
private fun JournalEditTopBar(
    recommendation: Recommendation,
    mood: String,
    moodStrokes: List<DrawnTagStroke>,
    moodAnimationTrigger: Int,
    tags: String,
    showRecommendationIcon: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    saving: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .debugOutline(),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(44.dp)
                .shadow(
                    elevation = 9.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color(0x123B342A),
                    spotColor = Color(0x0C3B342A),
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.92f))
                .clickable(enabled = !saving, onClick = onCancel)
                .debugOutline(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.action_cancel),
                tint = Color(0xFF151515),
                modifier = Modifier.size(23.dp),
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .debugOutline(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showRecommendationIcon || mood.isNotBlank() || moodStrokes.isNotEmpty()) {
                JournalRecommendationCardTransition(
                    recommendation = recommendation,
                    moodText = mood,
                    moodStrokes = moodStrokes,
                    moodAnimationTrigger = moodAnimationTrigger,
                )
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(999.dp),
                    clip = false,
                    ambientColor = Color(0x0F3B342A),
                    spotColor = Color(0x0A3B342A),
                )
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.86f))
                .clickable(enabled = !saving, onClick = onSave)
                .padding(horizontal = 14.dp, vertical = 9.dp)
                .debugOutline(RoundedCornerShape(999.dp)),
            text = stringResource(if (saving) R.string.action_saving else R.string.action_save),
            color = Color(0xFF2F6CE5),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.28.sp,
            ),
        )
    }
}

@Composable
private fun JournalMediaPreviewTray(
    media: List<MediaAttachmentDraft>,
    onRemove: (MediaAttachmentDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 6.dp)
            .debugOutline(),
        horizontalArrangement = Arrangement.spacedBy((-28).dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        media.forEachIndexed { index, attachment ->
            JournalMediaPreviewCard(
                attachment = attachment,
                onRemove = { onRemove(attachment) },
                modifier = Modifier.graphicsLayer {
                    rotationZ = when (index % 6) {
                        0 -> -7.5f
                        1 -> 4.5f
                        2 -> -2.5f
                        3 -> 6.5f
                        4 -> -5f
                        else -> 2f
                    }
                    translationY = when (index % 4) {
                        0 -> 10.dp.toPx()
                        1 -> 0.dp.toPx()
                        2 -> 7.dp.toPx()
                        else -> 3.dp.toPx()
                    }
                },
            )
        }
    }
}

@Composable
private fun JournalMediaPreviewCard(
    attachment: MediaAttachmentDraft,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .width(132.dp)
            .height(166.dp)
            .shadow(
                elevation = 18.dp,
                shape = shape,
                clip = false,
                ambientColor = Color(0x260B1C30),
                spotColor = Color(0x1D0B1C30),
            )
            .clip(shape)
            .background(Color.White.copy(alpha = 0.92f))
            .border(2.dp, Color.White.copy(alpha = 0.92f), shape)
            .debugOutline(shape),
    ) {
        JournalDraftMediaThumbnail(
            attachment = attachment,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clip(RoundedCornerShape(19.dp)),
        )
        if (attachment.type == MediaType.Video) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.34f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.90f))
                .clickable(onClick = onRemove)
                .debugOutline(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.action_remove),
                tint = Color(0xFF151515),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun JournalDraftMediaThumbnail(
    attachment: MediaAttachmentDraft,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, attachment.uri, attachment.type, context) {
        value = if (attachment.type == MediaType.Image) {
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(attachment.uri))?.use { input ->
                        BitmapFactory.decodeStream(input)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        } else {
            null
        }
    }

    Box(
        modifier = modifier.background(Color(0xFFE8ECF2)),
        contentAlignment = Alignment.Center,
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = if (attachment.type == MediaType.Video) Icons.Filled.PlayArrow else Icons.Filled.PhotoCamera,
                contentDescription = null,
                tint = Color(0xFF58677A),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun JournalTitleField(
    title: String,
    onTitleChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 340.dp),
    ) {
        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color(0xFF0B1C30),
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (title.isBlank()) {
                        Text(
                            text = stringResource(R.string.field_title),
                            color = Color(0xFF464555).copy(alpha = 0.40f),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 28.sp,
                                lineHeight = 36.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                            ),
                        )
                    }
                    inner()
                }
            },
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF0B1C30).copy(alpha = 0.08f)),
        )
    }
}

@Composable
private fun JournalContentField(
    content: String,
    placeholder: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = content,
        onValueChange = onContentChange,
        textStyle = TextStyle(
            color = Color(0xFF0B1C30),
            fontFamily = FontFamily.SansSerif,
            fontSize = 17.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 340.dp)
            .padding(horizontal = 4.dp),
        decorationBox = { inner ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart,
            ) {
                if (content.isBlank()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF58677A).copy(alpha = 0.40f),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 17.sp,
                            lineHeight = 29.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
                inner()
            }
        },
    )
}

@Composable
private fun JournalTagsDisplay(tags: String) {
    val parsedTags = remember(tags) {
        tags.split(",", "，", " ")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .debugOutline(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        parsedTags.forEach { tag ->
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.54f))
                    .border(1.dp, Color.White.copy(alpha = 0.62f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .debugOutline(RoundedCornerShape(999.dp)),
                text = "#$tag",
                color = Color(0xFF58677A),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Preview(name = "Journal Edit - Card Entry", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun JournalEditCardEntryPreview() {
    JournalEditPreviewContent(fromRecommendationCard = true)
}

@Preview(name = "Journal Edit - Plus Entry", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun JournalEditPlusEntryPreview() {
    JournalEditPreviewContent(fromRecommendationCard = false)
}

@Composable
private fun JournalEditPreviewContent(fromRecommendationCard: Boolean) {
    val previewAppearance = appAppearance(isDark = false, backgroundStyle = "mist")
    var title by remember { mutableStateOf(if (fromRecommendationCard) "A quiet morning..." else "") }
    var content by remember { mutableStateOf("") }
    var tagText by remember { mutableStateOf("relax") }
    var moodText by remember { mutableStateOf("😊 Happy") }
    val recommendation = Recommendation(
        id = "preview",
        title = "A quiet morning...",
        description = "Capture one small detail that made today feel different.",
        category = "Calm",
        imageAsset = "relax",
    )

    MaterialTheme(colorScheme = appColorScheme(false)) {
        CompositionLocalProvider(
            LocalAppAppearance provides previewAppearance,
            LocalDebugUiOutline provides false,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FF)),
            ) {
                JournalEditContent(
                    title = title,
                    onTitleChange = { title = it },
                    content = content,
                    onContentChange = { content = it },
                    mood = moodText,
                    moodStrokes = emptyList(),
                    moodAnimationTrigger = 0,
                    tags = tagText,
                    recommendation = recommendation,
                    showRecommendationIcon = fromRecommendationCard,
                    showTitleField = !fromRecommendationCard,
                    onCancel = {},
                    onSave = {},
                    saving = false,
                )
                JournalBottomToolbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onPickMedia = {},
                    onPickTag = {},
                )
            }
        }
    }
}
