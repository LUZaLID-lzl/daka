package com.luzalid.daka.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.luzalid.daka.R
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.MediaAttachment
import com.luzalid.daka.model.MediaAttachmentDraft
import com.luzalid.daka.model.MediaType
import com.luzalid.daka.model.PreferenceItem
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.model.RecordDetail
import com.luzalid.daka.model.RecordSummary
import com.luzalid.daka.model.RecordVersion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.absoluteValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed interface AppRoute {
    data object Home : AppRoute
    data object History : AppRoute
    data object Profile : AppRoute
    data class Edit(val recordId: String?, val recommendation: Recommendation) : AppRoute
    data class Detail(val recordId: String) : AppRoute
    data class Versions(val recordId: String) : AppRoute
}

private enum class HistoryMode {
    List,
    Grid,
    Carousel,
}

private val LocalDebugUiOutline = compositionLocalOf { false }
private val DebugOutlineColor = Color(0xFF1D9BF0)

internal fun Modifier.debugOutline(
    shape: Shape = RoundedCornerShape(0.dp),
    color: Color = DebugOutlineColor,
    width: androidx.compose.ui.unit.Dp = 1.dp,
): Modifier = composed {
    if (LocalDebugUiOutline.current) {
        border(width, color, shape)
    } else {
        this
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickClackApp(repository: ClickClackRepository) {
    var route by remember { mutableStateOf<AppRoute>(AppRoute.Home) }
    val homeRecommendations by produceState<List<Recommendation>>(initialValue = emptyList(), repository) {
        repository.initialize()
        value = repository.homeRecommendations()
    }
    val preferences by repository.observePreferences().collectAsState(initial = emptyList())
    val debugUiOutlineEnabled = preferenceValue(preferences, "debug_ui_outline") == "true"

    MaterialTheme {
        CompositionLocalProvider(LocalDebugUiOutline provides debugUiOutlineEnabled) {
            Scaffold(
                contentWindowInsets = WindowInsets(0.dp),
                topBar = {
                    when (val current = route) {
                        AppRoute.Home -> Unit
                        AppRoute.History -> CenterAlignedTopAppBar(title = { Text(stringResource(R.string.screen_records)) })
                        AppRoute.Profile -> CenterAlignedTopAppBar(title = { Text(stringResource(R.string.screen_profile)) })
                        is AppRoute.Edit -> BackTopBar(stringResource(R.string.screen_check_in)) {
                            route = current.recordId?.let { AppRoute.Detail(it) } ?: AppRoute.Home
                        }
                        is AppRoute.Detail -> BackTopBar(stringResource(R.string.screen_record_detail)) { route = AppRoute.History }
                        is AppRoute.Versions -> BackTopBar(stringResource(R.string.screen_version_history)) { route = AppRoute.Detail(current.recordId) }
                    }
                },
            ) { padding ->
                when (val current = route) {
                        AppRoute.Home -> HomeScreen(
                            padding = padding,
                            repository = repository,
                            recommendations = homeRecommendations,
                            onHistory = { route = AppRoute.History },
                        onProfile = { route = AppRoute.Profile },
                        onRecord = { recordId, recommendation ->
                            route = AppRoute.Edit(recordId, recommendation)
                        },
                    )

                    AppRoute.History -> HistoryScreen(
                        padding = padding,
                        repository = repository,
                        onRecordClick = { route = AppRoute.Detail(it) },
                    )

                    AppRoute.Profile -> ProfileScreen(padding = padding, repository = repository)

                    is AppRoute.Edit -> EditRecordScreen(
                        padding = padding,
                        repository = repository,
                        recordId = current.recordId,
                        fallbackRecommendation = current.recommendation,
                        onCancel = { route = if (current.recordId == null) AppRoute.Home else AppRoute.Detail(current.recordId) },
                        onSaved = { route = AppRoute.Detail(it) },
                    )

                    is AppRoute.Detail -> RecordDetailScreen(
                        padding = padding,
                        repository = repository,
                        recordId = current.recordId,
                        onEdit = { detail ->
                            route = AppRoute.Edit(
                                recordId = detail.id,
                                recommendation = Recommendation(
                                    id = detail.recommendationId,
                                    title = detail.title,
                                    description = "",
                                    category = detail.category,
                                    imageAsset = detail.category,
                                ),
                            )
                        },
                        onVersions = { route = AppRoute.Versions(current.recordId) },
                    )

                    is AppRoute.Versions -> VersionHistoryScreen(
                        padding = padding,
                        repository = repository,
                        recordId = current.recordId,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
        },
    )
}

@Composable
private fun EditRecordScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    recordId: String?,
    fallbackRecommendation: Recommendation,
    onCancel: () -> Unit,
    onSaved: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
    var title by remember(recordId) { mutableStateOf(fallbackRecommendation.title) }
    var content by remember(recordId) { mutableStateOf("") }
    var mood by remember(recordId) { mutableStateOf("") }
    var location by remember(recordId) { mutableStateOf("") }
    var tags by remember(recordId) { mutableStateOf("") }
    val media = remember(recordId) { mutableStateListOf<MediaAttachmentDraft>() }
    var saving by remember { mutableStateOf(false) }

    LaunchedEffect(recordId, detail?.id, activeRecommendation.id) {
        if (recordId == null && loadedRecordId != "new") {
            title = activeRecommendation.title
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

    fun persistReadPermission(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistReadPermission(it)
            media.add(MediaAttachmentDraft(id = UUID.randomUUID().toString(), type = MediaType.Image, uri = it.toString()))
        }
    }
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistReadPermission(it)
            media.add(MediaAttachmentDraft(id = UUID.randomUUID().toString(), type = MediaType.Video, uri = it.toString()))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .debugOutline(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            RecommendationMiniCard(activeRecommendation)
        }
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_title)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
        }
        item {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                label = { Text(stringResource(R.string.field_content)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MediaAddButton(
                    text = stringResource(R.string.action_add_image),
                    icon = { Icon(Icons.Filled.Image, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    onClick = { imageLauncher.launch(arrayOf("image/*")) },
                )
                MediaAddButton(
                    text = stringResource(R.string.action_add_video),
                    icon = { Icon(Icons.Filled.Videocam, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    onClick = { videoLauncher.launch(arrayOf("video/*")) },
                )
            }
        }
        if (media.isNotEmpty()) {
            item {
                SectionTitle(stringResource(R.string.section_added_media))
            }
            items(media, key = { it.id }) { attachment ->
                MediaDraftRow(attachment = attachment, onRemove = { media.remove(attachment) })
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = mood,
                    onValueChange = { mood = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.field_mood)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.field_location)) },
                    singleLine = true,
                )
            }
        }
        item {
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_tags)) },
                singleLine = true,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), enabled = !saving) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = {
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
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !saving && (content.isNotBlank() || media.isNotEmpty()),
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(if (saving) R.string.action_saving else R.string.action_save))
                }
            }
        }
    }
}

@Composable
private fun HistoryScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    onRecordClick: (String) -> Unit,
) {
    val records by repository.observeRecordSummaries().collectAsState(initial = emptyList())
    var mode by remember { mutableStateOf(HistoryMode.List) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .debugOutline(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ModeChip(stringResource(R.string.history_mode_list), Icons.AutoMirrored.Filled.FormatListBulleted, mode == HistoryMode.List) { mode = HistoryMode.List }
            ModeChip(stringResource(R.string.history_mode_grid), Icons.Filled.GridView, mode == HistoryMode.Grid) { mode = HistoryMode.Grid }
            ModeChip(stringResource(R.string.history_mode_carousel), Icons.Filled.ViewCarousel, mode == HistoryMode.Carousel) { mode = HistoryMode.Carousel }
        }
        if (records.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.empty_records_title),
                body = stringResource(R.string.empty_records_body),
                modifier = Modifier.fillMaxSize().padding(24.dp),
            )
        } else {
            when (mode) {
                HistoryMode.List -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(records, key = { it.id }) { record ->
                        RecordListCard(record = record, onClick = { onRecordClick(record.id) })
                    }
                }

                HistoryMode.Grid -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(156.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(records, key = { it.id }) { record ->
                        RecordGridCard(record = record, onClick = { onRecordClick(record.id) })
                    }
                }

                HistoryMode.Carousel -> Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    records.forEach { record ->
                        RecordCarouselCard(record = record, onClick = { onRecordClick(record.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordDetailScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    recordId: String,
    onEdit: (RecordDetail) -> Unit,
    onVersions: () -> Unit,
) {
    val detail by remember(recordId) { repository.observeRecordDetail(recordId) }.collectAsState(initial = null)
    val versionId = detail?.currentVersionId.orEmpty()
    val media by remember(versionId) {
        if (versionId.isBlank()) flowOf(emptyList()) else repository.observeMediaForVersion(versionId)
    }.collectAsState(initial = emptyList())

    if (detail == null) {
        EmptyState(
            title = stringResource(R.string.record_not_found_title),
            body = stringResource(R.string.record_not_found_body),
            modifier = Modifier.fillMaxSize().padding(padding),
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .debugOutline(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            CategoryVisual(
                asset = detail!!.category,
                category = detail!!.category,
                modifier = Modifier.fillMaxWidth().height(180.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(detail!!.category) })
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.version_number, detail!!.versionNumber)) },
                )
            }
        }
        item {
            Text(detail!!.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(
                    R.string.record_updated,
                    detail!!.dateKey,
                    formatTimestamp(
                        timestamp = detail!!.updatedAt,
                        pattern = stringResource(R.string.date_format_timestamp),
                    ),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Text(detail!!.content, style = MaterialTheme.typography.bodyLarge)
        }
        if (detail!!.mood.isNotBlank() || detail!!.location.isNotBlank() || detail!!.tags.isNotBlank()) {
            item {
                MetadataRow(detail = detail!!)
            }
        }
        if (media.isNotEmpty()) {
            item { SectionTitle(stringResource(R.string.section_media)) }
            items(media, key = { it.id }) { attachment ->
                MediaAttachmentCard(attachment)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onVersions, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.screen_version_history))
                }
                Button(onClick = { onEdit(detail!!) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_edit))
                }
            }
        }
    }
}

@Composable
private fun VersionHistoryScreen(
    padding: PaddingValues,
    repository: ClickClackRepository,
    recordId: String,
) {
    val versions by remember(recordId) { repository.observeVersions(recordId) }.collectAsState(initial = emptyList())
    if (versions.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.empty_versions_title),
            body = stringResource(R.string.empty_versions_body),
            modifier = Modifier.fillMaxSize().padding(padding),
        )
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .debugOutline(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(versions, key = { it.id }) { version ->
            VersionCard(version)
        }
    }
}

@Composable
private fun ProfileScreen(padding: PaddingValues, repository: ClickClackRepository) {
    val preferences by repository.observePreferences().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .debugOutline(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ProfileHeader()
        }
        item {
            PreferenceSection(stringResource(R.string.preference_section_recommendations))
        }
        item {
            PreferenceSwitchRow(
                icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                title = stringResource(R.string.preference_daily_reminder),
                description = preferenceValue(preferences, "reminder_time"),
                checked = preferenceValue(preferences, "show_daily_reminder") == "true",
                onCheckedChange = { checked ->
                    scope.launch { repository.updatePreference("show_daily_reminder", checked.toString()) }
                },
            )
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                title = stringResource(R.string.preference_categories),
                description = localizedPreferenceValue(preferences, "preferred_categories"),
            )
        }
        item {
            PreferenceSection(stringResource(R.string.preference_section_appearance))
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Palette, contentDescription = null) },
                title = stringResource(R.string.preference_theme),
                description = localizedPreferenceValue(preferences, "theme_mode"),
            )
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Storage, contentDescription = null) },
                title = stringResource(R.string.preference_media_strategy),
                description = localizedPreferenceValue(preferences, "media_strategy"),
            )
        }
        item {
            PreferenceSwitchRow(
                icon = { Icon(Icons.Filled.GridView, contentDescription = null) },
                title = stringResource(R.string.preference_debug_outline),
                description = stringResource(R.string.preference_debug_outline_description),
                checked = preferenceValue(preferences, "debug_ui_outline") == "true",
                onCheckedChange = { checked ->
                    scope.launch { repository.updatePreference("debug_ui_outline", checked.toString()) }
                },
            )
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                title = stringResource(R.string.preference_about),
                description = stringResource(R.string.preference_about_description),
            )
        }
    }
}

@Composable
private fun RecommendationMiniCard(recommendation: Recommendation) {
    Card(
        modifier = Modifier.debugOutline(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryVisual(asset = recommendation.imageAsset, category = recommendation.category, modifier = Modifier.size(72.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(recommendation.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(recommendation.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(recommendation.description, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MediaAddButton(
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(52.dp).debugOutline(RoundedCornerShape(999.dp))) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun MediaDraftRow(attachment: MediaAttachmentDraft, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaThumbnail(
                uri = attachment.uri,
                mediaType = attachment.type,
                modifier = Modifier.size(68.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(
                        if (attachment.type == MediaType.Image) R.string.media_image else R.string.media_video,
                    ),
                    fontWeight = FontWeight.Medium,
                )
                Text(attachment.uri, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = onRemove) {
                Text(stringResource(R.string.action_remove))
            }
        }
    }
}

@Composable
private fun RecordListCard(record: RecordSummary, onClick: () -> Unit) {
    val mediaOnlyText = stringResource(R.string.record_media_only)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).debugOutline(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MediaThumbnail(record.thumbnailUri, record.mediaType, Modifier.size(82.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(record.dateKey, style = MaterialTheme.typography.labelLarge)
                    AssistChip(onClick = {}, label = { Text(record.category) })
                }
                Text(record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    record.content.ifBlank { mediaOnlyText },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RecordGridCard(record: RecordSummary, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick).debugOutline(RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp)) {
        Column {
            MediaThumbnail(record.thumbnailUri, record.mediaType, Modifier.fillMaxWidth().aspectRatio(1.1f))
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(record.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                Text(record.dateKey, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun RecordCarouselCard(record: RecordSummary, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.width(280.dp).clickable(onClick = onClick).debugOutline(RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MediaThumbnail(record.thumbnailUri, record.mediaType, Modifier.fillMaxWidth().height(220.dp))
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AssistChip(onClick = {}, label = { Text(record.category) })
                Text(record.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(record.content, maxLines = 3, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MediaAttachmentCard(attachment: MediaAttachment) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            MediaThumbnail(
                uri = attachment.thumbnailUri ?: attachment.uri,
                mediaType = attachment.type,
                modifier = Modifier.fillMaxWidth().height(220.dp),
            )
            Text(
                text = stringResource(
                    if (attachment.type == MediaType.Image) {
                        R.string.media_image_attachment
                    } else {
                        R.string.media_video_attachment
                    },
                ),
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun VersionCard(version: RecordVersion) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.version_number, version.versionNumber), fontWeight = FontWeight.Bold)
                if (version.isCurrent) {
                    AssistChip(onClick = {}, label = { Text(stringResource(R.string.current_version)) })
                }
            }
            Text(
                formatTimestamp(
                    timestamp = version.editedAt,
                    pattern = stringResource(R.string.date_format_timestamp),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(version.title, style = MaterialTheme.typography.titleMedium)
            Text(version.content, maxLines = 3, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MediaThumbnail(uri: String?, mediaType: MediaType?, modifier: Modifier) {
    val context = LocalContext.current
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, uri, mediaType, context) {
        value = if (uri != null && mediaType == MediaType.Image) {
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(uri))?.use { input ->
                        BitmapFactory.decodeStream(input)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        } else {
            null
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .debugOutline(RoundedCornerShape(20.dp)),
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
            val isVideo = mediaType == MediaType.Video
            Icon(
                imageVector = if (isVideo) Icons.Filled.PlayArrow else Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun CategoryVisual(
    asset: String,
    category: String,
    modifier: Modifier = Modifier,
    decorated: Boolean = true,
) {
    val colors = categoryColors(asset)
    if (!decorated) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(categoryImageRes(asset)),
                contentDescription = category,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
            )
        }
        return
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(38.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.34f),
                        colors[1].copy(alpha = 0.34f),
                        colors[0].copy(alpha = 0.18f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.26f),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.50f, size.height * 0.55f),
            )
            drawCircle(
                color = colors[1].copy(alpha = 0.16f),
                radius = size.minDimension * 0.45f,
                center = Offset(size.width * 0.50f, size.height * 0.54f),
            )
        }
        Image(
            painter = painterResource(categoryImageRes(asset)),
            contentDescription = category,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
        )
    }
}

@DrawableRes
private fun categoryImageRes(asset: String): Int = when (asset.lowercase(Locale.getDefault())) {
    "food" -> R.drawable.illustration_food
    "sport" -> R.drawable.illustration_sport
    "commute" -> R.drawable.illustration_commute
    "social" -> R.drawable.illustration_social
    "explore" -> R.drawable.illustration_explore
    "work", "study" -> R.drawable.illustration_explore
    "fun", "relax" -> R.drawable.illustration_social
    "home" -> R.drawable.illustration_food
    else -> R.drawable.illustration_food
}

private fun categoryColors(asset: String): List<Color> = when (asset.lowercase(Locale.getDefault())) {
    "food" -> listOf(Color(0xFFB54626), Color(0xFFE8A54E))
    "sport" -> listOf(Color(0xFF1B7A5A), Color(0xFF7BC6A4))
    "commute" -> listOf(Color(0xFF2E5C83), Color(0xFFA7C7E7))
    "work" -> listOf(Color(0xFF48515E), Color(0xFF98A2B3))
    "fun" -> listOf(Color(0xFF8A3FFC), Color(0xFFFF7AA2))
    "social" -> listOf(Color(0xFFB83280), Color(0xFFF2A0C8))
    "relax" -> listOf(Color(0xFF2F6F73), Color(0xFFB5D8CC))
    "home" -> listOf(Color(0xFF7A5B37), Color(0xFFD9BF8F))
    "study" -> listOf(Color(0xFF334E8A), Color(0xFF9DB7F5))
    else -> listOf(Color(0xFF4B5563), Color(0xFF94A3B8))
}

@Composable
private fun MetadataRow(detail: RecordDetail) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
        if (detail.mood.isNotBlank()) {
            AssistChip(onClick = {}, label = { Text(stringResource(R.string.metadata_mood, detail.mood)) })
        }
        if (detail.location.isNotBlank()) {
            AssistChip(onClick = {}, label = { Text(stringResource(R.string.metadata_location, detail.location)) })
        }
        if (detail.tags.isNotBlank()) {
            AssistChip(onClick = {}, label = { Text(stringResource(R.string.metadata_tags, detail.tags)) })
        }
    }
}

@Composable
private fun ModeChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun EmptyState(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileHeader() {
    Card(
        modifier = Modifier.debugOutline(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.profile_local_first),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.profile_description))
        }
    }
}

@Composable
private fun PreferenceSection(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        HorizontalDivider()
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp)) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(description) },
            leadingContent = icon,
            trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        )
    }
}

@Composable
private fun PreferenceStaticRow(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp)) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(description) },
            leadingContent = icon,
        )
    }
}

private fun preferenceValue(preferences: List<PreferenceItem>, key: String): String =
    preferences.firstOrNull { it.key == key }?.value.orEmpty()

@Composable
private fun localizedPreferenceValue(preferences: List<PreferenceItem>, key: String): String =
    when (val value = preferenceValue(preferences, key)) {
        "all_categories" -> stringResource(R.string.preference_all_categories)
        "system" -> stringResource(R.string.preference_follow_system)
        "local_uri" -> stringResource(R.string.preference_local_uri)
        else -> value
    }

private fun formatTimestamp(timestamp: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
