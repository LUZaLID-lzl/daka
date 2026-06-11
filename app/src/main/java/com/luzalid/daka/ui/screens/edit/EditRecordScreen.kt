package com.luzalid.daka.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.MediaAttachmentDraft
import com.luzalid.daka.model.Recommendation
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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
    val focusManager = LocalFocusManager.current
    val detail by remember(recordId) {
        if (recordId == null) flowOf(null) else repository.observeRecordDetail(recordId)
    }.collectAsState(initial = null)
    val activeRecommendation by produceState(
        initialValue = fallbackRecommendation,
        recordId,
        detail?.recommendationId,
    ) {
        value = detail?.recommendationId?.let { repository.getRecommendation(it) } ?: fallbackRecommendation
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
    var saving by remember { mutableStateOf(false) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF8F9FF),
                        Color(0xFFF3F6FD),
                        Color(0xFFFFFAF4),
                    ),
                ),
            )
            .debugOutline(),
    ) {
        JournalEditContent(
            title = title,
            onTitleChange = { title = it },
            content = content,
            onContentChange = { content = it },
            tags = tags,
            recommendation = activeRecommendation,
            showRecommendationCard = fromRecommendationCard,
            showTitleField = !fromRecommendationCard,
            onCancel = onCancel,
            onSave = ::save,
            saving = saving,
        )
        JournalBottomToolbar(
            modifier = Modifier.align(Alignment.BottomCenter),
        )
        if (media.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 112.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                media.forEach { attachment ->
                    AssistChip(
                        onClick = { media.remove(attachment) },
                        label = { Text(stringResource(R.string.media_image_attachment)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalEditContent(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    tags: String,
    recommendation: Recommendation,
    showRecommendationCard: Boolean,
    showTitleField: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    saving: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        JournalEditTopBar(onCancel = onCancel, onSave = onSave, saving = saving)
        Spacer(Modifier.height(if (showRecommendationCard) 22.dp else 44.dp))
        if (showRecommendationCard) {
            JournalRecommendationCardTransition(recommendation = recommendation)
            Spacer(Modifier.height(30.dp))
        }
        if (showTitleField) {
            JournalTitleField(title = title, onTitleChange = onTitleChange)
            Spacer(Modifier.height(24.dp))
        }
        JournalContentField(content = content, onContentChange = onContentChange)
        if (tags.isNotBlank()) {
            Spacer(Modifier.height(18.dp))
            JournalTagsDisplay(tags = tags)
        }
    }
}

private data class JournalTag(
    val key: String,
    val label: String,
    val color: Color,
)

private fun journalTags() = listOf(
    JournalTag("food", "Food", Color(0xFFE63B5B)),
    JournalTag("relax", "Calm", Color(0xFFF8A94C)),
    JournalTag("social", "People", Color(0xFF2518C8)),
    JournalTag("explore", "Place", Color(0xFF4AA8E8)),
    JournalTag("home", "Home", Color(0xFF55AA38)),
)

private val CardEntryEase = CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f)

@Composable
private fun JournalRecommendationCardTransition(
    recommendation: Recommendation,
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
    JournalRecommendationCard(
        recommendation = recommendation,
        modifier = Modifier.graphicsLayer {
            alpha = value
            translationY = (1f - value) * 26.dp.toPx()
            scaleX = 0.92f + value * 0.08f
            scaleY = 0.92f + value * 0.08f
            rotationZ = -2.4f * (1f - value)
        },
    )
}

@Composable
private fun JournalRecommendationCard(
    recommendation: Recommendation,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)
    val palette = remember(recommendation.imageAsset) {
        recommendationPalette(recommendation.imageAsset)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 268.dp)
            .height(116.dp)
            .shadow(
                elevation = 14.dp,
                shape = shape,
                clip = false,
                ambientColor = palette.shadow,
                spotColor = palette.shadow.copy(alpha = 0.14f),
            )
            .clip(shape)
            .background(palette.gradient.last())
            .border(1.dp, palette.stroke, shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(palette.gradient)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            palette.glow.copy(alpha = 0.42f),
                            palette.glow.copy(alpha = 0.14f),
                            Color.Transparent,
                        ),
                        center = Offset(0.5f, 0f),
                        radius = 460f,
                    ),
                ),
        )
        Image(
            painter = painterResource(categoryMotionImageRes(recommendation.imageAsset)),
            contentDescription = recommendation.category,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .align(Alignment.TopCenter)
                .padding(top = 0.dp, start = 58.dp, end = 58.dp)
                .graphicsLayer {
                    alpha = 0.86f
                    scaleX = 1.08f
                    scaleY = 1.08f
                    translationY = (-4).dp.toPx()
                },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.02f),
                            Color.Black.copy(alpha = 0.06f),
                            palette.scrim.copy(alpha = 0.68f),
                        ),
                    ),
                ),
        )
        RecommendationPill(
            text = recommendation.category,
            palette = palette,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 12.dp),
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 15.dp, end = 15.dp, bottom = 14.dp),
            text = recommendation.title,
            color = Color(0xFFFFFCF3),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RecommendationPill(
    text: String,
    palette: RecommendationPalette,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.pillBackground)
            .border(1.dp, palette.pillStroke, RoundedCornerShape(999.dp))
            .padding(horizontal = 11.dp, vertical = 7.dp),
        text = text,
        color = palette.pillForeground,
        style = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.SemiBold,
        ),
    )
}

@Composable
private fun JournalBottomToolbar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .fillMaxWidth(0.84f)
            .widthIn(max = 304.dp)
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
        JournalToolButton(label = "Photo") { Icon(Icons.Filled.PhotoCamera, contentDescription = null) }
        JournalToolButton(label = "Place") { Icon(Icons.Filled.LocationOn, contentDescription = null) }
        JournalToolButton(label = "Tag") { Icon(Icons.Filled.LocalOffer, contentDescription = null) }
    }
}

@Composable
private fun JournalToolButton(
    label: String,
    icon: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(68.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.24f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
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

@Composable
private fun JournalEditTopBar(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    saving: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onCancel,
            enabled = !saving,
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.action_cancel),
                tint = Color(0xFF464555),
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Today",
            color = Color(0xFF3525CD),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.28.sp,
            ),
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(18.dp))
                .clickable(enabled = !saving, onClick = onSave)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            text = stringResource(if (saving) R.string.action_saving else R.string.action_save),
            color = Color(0xFF3525CD),
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
private fun JournalTitleField(
    title: String,
    onTitleChange: (String) -> Unit,
) {
    BasicTextField(
        value = title,
        onValueChange = onTitleChange,
        singleLine = true,
        textStyle = TextStyle(
            color = Color(0xFF0B1C30),
            fontFamily = FontFamily.SansSerif,
            fontSize = 30.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 340.dp)
            .height(56.dp),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (title.isBlank()) {
                    Text(
                        text = "Journal Title",
                        color = Color(0xFF464555).copy(alpha = 0.60f),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 30.sp,
                            lineHeight = 38.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
                inner()
            }
        },
    )
}

@Composable
private fun JournalContentField(
    content: String,
    onContentChange: (String) -> Unit,
) {
    BasicTextField(
        value = content,
        onValueChange = onContentChange,
        textStyle = TextStyle(
            color = Color(0xFF0B1C30),
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 340.dp)
            .height(260.dp),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.TopStart,
            ) {
                if (content.isBlank()) {
                    Text(
                        text = "Write your thoughts here...",
                        color = Color(0xFF464555).copy(alpha = 0.60f),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                        ),
                    )
                }
                inner()
            }
        },
    )
}

@Composable
private fun JournalMetadataRow(
    mood: String,
    onMoodChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        JournalSmallField(
            value = mood,
            onValueChange = onMoodChange,
            placeholder = stringResource(R.string.field_mood),
            modifier = Modifier.weight(1f),
        )
        JournalSmallField(
            value = location,
            onValueChange = onLocationChange,
            placeholder = stringResource(R.string.field_location),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun JournalTagsDisplay(tags: String) {
    val parsedTags = remember(tags) {
        tags.split(",", "，", " ")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
    }
}

@Composable
private fun JournalTagsField(
    tags: String,
    onTagsChange: (String) -> Unit,
) {
    JournalSmallField(
        value = tags,
        onValueChange = onTagsChange,
        placeholder = stringResource(R.string.field_tags),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    )
}

@Composable
private fun JournalSmallField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = Color(0xFF4D4D59), fontSize = 14.sp, textAlign = TextAlign.Center),
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.44f))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isBlank()) {
                    Text(placeholder, color = Color(0xFF9B9BA8), fontSize = 14.sp)
                }
                inner()
            }
        },
    )
}


@Composable
private fun HandDrawnTagIcon(
    tag: JournalTag,
    modifier: Modifier = Modifier,
    strokeScale: Float = 1f,
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.minDimension * 0.035f * strokeScale, cap = StrokeCap.Round)
        val color = tag.color
        val cx = size.width * 0.5f
        val top = size.height * 0.18f
        val bottom = size.height * 0.82f
        drawLine(color, Offset(cx, top), Offset(cx, bottom), strokeWidth = stroke.width, cap = StrokeCap.Round)
        when (tag.key) {
            "food" -> {
                repeat(4) { index ->
                    val x = size.width * (0.28f + index * 0.14f)
                    drawCircle(color, size.minDimension * 0.055f, Offset(x, top + index % 2 * size.height * 0.06f), style = stroke)
                    drawLine(color, Offset(x, top + size.height * 0.08f), Offset(cx, size.height * 0.42f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                }
                drawCircle(color, size.minDimension * 0.09f, Offset(cx, top), style = stroke)
            }
            "social" -> {
                repeat(5) { index ->
                    val angle = index / 5f * 6.28f
                    val x = cx + kotlin.math.cos(angle) * size.width * 0.20f
                    val y = top + size.height * 0.08f + kotlin.math.sin(angle) * size.height * 0.10f
                    drawCircle(color, size.minDimension * 0.052f, Offset(x, y), style = stroke)
                    drawLine(color, Offset(x, y + size.height * 0.06f), Offset(cx, size.height * 0.44f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                }
            }
            "explore" -> {
                repeat(3) { index ->
                    val x = size.width * (0.34f + index * 0.16f)
                    drawCircle(color, size.minDimension * 0.052f, Offset(x, top + size.height * 0.04f), style = stroke)
                    drawLine(color, Offset(x, top + size.height * 0.10f), Offset(cx, size.height * 0.46f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                }
                drawCircle(color, size.minDimension * 0.035f, Offset(size.width * 0.72f, size.height * 0.28f), style = stroke)
            }
            "home" -> {
                drawLine(color, Offset(size.width * 0.28f, size.height * 0.40f), Offset(cx, size.height * 0.20f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(color, Offset(cx, size.height * 0.20f), Offset(size.width * 0.72f, size.height * 0.40f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawCircle(color, size.minDimension * 0.08f, Offset(size.width * 0.35f, size.height * 0.62f), style = stroke)
                drawCircle(color, size.minDimension * 0.08f, Offset(size.width * 0.65f, size.height * 0.62f), style = stroke)
            }
            else -> {
                val left = Path().apply {
                    moveTo(cx, size.height * 0.50f)
                    cubicTo(size.width * 0.20f, size.height * 0.22f, size.width * 0.22f, size.height * 0.72f, size.width * 0.36f, size.height * 0.70f)
                }
                val right = Path().apply {
                    moveTo(cx, size.height * 0.50f)
                    cubicTo(size.width * 0.82f, size.height * 0.20f, size.width * 0.86f, size.height * 0.72f, size.width * 0.64f, size.height * 0.70f)
                }
                drawPath(left, color, style = stroke)
                drawPath(right, color, style = stroke)
            }
        }
        drawCircle(color, size.minDimension * 0.11f, Offset(size.width * 0.30f, size.height * 0.82f), style = stroke)
        drawCircle(color, size.minDimension * 0.12f, Offset(size.width * 0.50f, size.height * 0.76f), style = stroke)
        drawCircle(color, size.minDimension * 0.13f, Offset(size.width * 0.70f, size.height * 0.82f), style = stroke)
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
                    tags = tagText,
                    recommendation = recommendation,
                    showRecommendationCard = fromRecommendationCard,
                    showTitleField = !fromRecommendationCard,
                    onCancel = {},
                    onSave = {},
                    saving = false,
                )
                JournalBottomToolbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}
