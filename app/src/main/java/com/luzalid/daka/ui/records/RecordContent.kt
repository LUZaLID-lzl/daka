package com.luzalid.daka.ui.records

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.AppAppearance
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline
import com.luzalid.daka.ui.home.categoryMotionImageRes
import com.luzalid.daka.ui.home.recommendationPalette

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R
import com.luzalid.daka.model.RecordSummary
import java.util.Locale

@Composable
internal fun RecordContent(
    records: List<RecordSummary>,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val filteredRecords = remember(records, query) {
        val needle = query.trim().lowercase(Locale.getDefault())
        if (needle.isBlank()) {
            records
        } else {
            records.filter { record ->
                record.title.lowercase(Locale.getDefault()).contains(needle) ||
                    record.content.lowercase(Locale.getDefault()).contains(needle) ||
                    record.category.lowercase(Locale.getDefault()).contains(needle) ||
                    record.dateKey.contains(needle)
            }
        }
    }
    val categories = remember(filteredRecords) {
        filteredRecords
            .groupBy { it.category }
            .entries
            .sortedByDescending { it.value.size }
            .take(4)
    }

    LazyColumn(
        modifier = modifier.debugOutline(),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            RecordSearchRow(
                query = query,
                onQueryChange = { query = it },
            )
        }

        item {
            PopularRecordCard(
                categories = categories.map { it.key to it.value.size },
            )
        }

        item {
            RecordSectionHeader(
                title = stringResource(R.string.records_recent_title),
                trailing = stringResource(R.string.action_show_all),
            )
        }

        if (filteredRecords.isEmpty()) {
            item {
                EmptyRecordContent()
            }
        } else {
            items(filteredRecords, key = { it.id }) { record ->
                RecordSummaryCard(
                    record = record,
                )
            }
        }
    }
}

@Composable
private fun RecordSearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color(0xFF242128),
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(18.dp),
                    clip = false,
                    ambientColor = Color(0x14302F2B),
                    spotColor = Color(0x0F302F2B),
                )
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .debugOutline(RoundedCornerShape(18.dp)),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color(0xFFAEB2BA),
                        modifier = Modifier.size(25.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isBlank()) {
                            Text(
                                text = stringResource(R.string.records_search_hint),
                                color = Color(0xFFAEB2BA),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )

        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 56.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(18.dp),
                    clip = false,
                    ambientColor = Color(0x2A4D46C8),
                    spotColor = Color(0x1F4D46C8),
                )
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF4D46C8))
                .debugOutline(RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Tune,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(27.dp),
            )
        }
    }
}

@Composable
private fun PopularRecordCard(categories: List<Pair<String, Int>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false,
                ambientColor = Color(0x14302F2B),
                spotColor = Color(0x0F302F2B),
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(vertical = 18.dp)
            .debugOutline(RoundedCornerShape(28.dp)),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        RecordSectionHeader(
            title = stringResource(R.string.records_popular_title),
            trailing = stringResource(R.string.action_show_all),
            modifier = Modifier.padding(horizontal = 18.dp),
        )

        if (categories.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_records_title),
                color = Color(0xFF858894),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                ),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                categories.forEach { (category, count) ->
                    RecordCategoryCard(
                        category = category,
                        count = count,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordSectionHeader(
    title: String,
    trailing: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color(0xFF242128),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 23.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = trailing,
            color = Color(0xFF8A7CA5),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
private fun RecordCategoryCard(
    category: String,
    count: Int,
) {
    val asset = remember(category) { recordCategoryAsset(category) }
    val palette = remember(asset) { recommendationPalette(asset) }
    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    clip = false,
                    ambientColor = palette.shadow,
                    spotColor = palette.shadow.copy(alpha = 0.12f),
                )
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(palette.gradient))
                .debugOutline(RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(categoryMotionImageRes(asset)),
                contentDescription = stringResource(R.string.record_category_count, category, count),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            )
        }
        Text(
            text = category,
            color = Color(0xFF2A2836),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RecordSummaryCard(
    record: RecordSummary,
) {
    val asset = remember(record.category) { recordCategoryAsset(record.category) }
    val palette = remember(asset) { recommendationPalette(asset) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(30.dp),
                clip = false,
                ambientColor = palette.shadow,
                spotColor = Color(0x12302F2B),
            )
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .padding(14.dp)
            .debugOutline(RoundedCornerShape(30.dp)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.verticalGradient(palette.gradient))
                .debugOutline(RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                palette.glow.copy(alpha = 0.42f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            Image(
                painter = painterResource(categoryMotionImageRes(asset)),
                contentDescription = record.category,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(46.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(14.dp),
                        clip = false,
                        ambientColor = Color(0x18302F2B),
                        spotColor = Color(0x12302F2B),
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.84f))
                    .debugOutline(RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color(0xFF4D46C8),
                    modifier = Modifier.size(25.dp),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = record.title,
                color = Color(0xFF242128),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.record_card_meta, record.dateKey, record.category),
                color = Color(0xFF666A76),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EmptyRecordContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false,
                ambientColor = Color(0x12302F2B),
                spotColor = Color(0x0D302F2B),
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.90f))
            .padding(horizontal = 24.dp, vertical = 34.dp)
            .debugOutline(RoundedCornerShape(28.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.empty_records_title),
            color = Color(0xFF242128),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = stringResource(R.string.empty_records_body),
            color = Color(0xFF727681),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        )
    }
}

private fun recordCategoryAsset(category: String): String {
    val value = category.lowercase(Locale.getDefault())
    return when {
        value.contains("美食") || value.contains("food") -> "food"
        value.contains("通勤") || value.contains("commute") -> "commute"
        value.contains("运动") || value.contains("fitness") || value.contains("sport") -> "sport"
        value.contains("工作") || value.contains("work") -> "work"
        value.contains("娱乐") || value.contains("fun") || value.contains("entertainment") -> "fun"
        value.contains("社交") || value.contains("social") -> "social"
        value.contains("放松") || value.contains("relax") -> "relax"
        value.contains("家务") || value.contains("home") -> "home"
        value.contains("学习") || value.contains("learning") || value.contains("study") -> "study"
        value.contains("探索") || value.contains("explore") -> "explore"
        else -> "food"
    }
}

private fun previewRecordSummaries() = listOf(
    RecordSummary(
        id = "record-sport",
        recommendationId = "local-sport-001",
        title = "饭后散步 20 分钟",
        dateKey = "2026-06-11",
        category = "运动",
        updatedAt = 1_781_151_200_000,
        content = "傍晚的风很舒服，路上看到一家新开的咖啡店。",
        thumbnailUri = null,
        mediaType = null,
    ),
    RecordSummary(
        id = "record-food",
        recommendationId = "local-food-001",
        title = "吃一份黄焖鸡米饭",
        dateKey = "2026-06-10",
        category = "美食",
        updatedAt = 1_781_064_800_000,
        content = "今天的米饭很香，汤汁拌饭刚刚好。",
        thumbnailUri = null,
        mediaType = null,
    ),
    RecordSummary(
        id = "record-social",
        recommendationId = "local-social-001",
        title = "给一位朋友发一条问候",
        dateKey = "2026-06-09",
        category = "社交",
        updatedAt = 1_780_978_400_000,
        content = "聊到了最近的工作，也约了周末见面。",
        thumbnailUri = null,
        mediaType = null,
    ),
)

@Preview(name = "Record Content", showBackground = true, widthDp = 360, heightDp = 520)
@Composable
private fun RecordContentPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
                .padding(24.dp),
        ) {
            RecordContent(
                records = previewRecordSummaries(),
            )
        }
    }
}
