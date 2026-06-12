package com.luzalid.daka.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R
import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline
import com.luzalid.daka.ui.home.FoodMotionIllustration

private enum class LabPreviewSize(val dimension: Dp) {
    Small(132.dp),
    Medium(196.dp),
    Large(260.dp),
}

@Composable
internal fun UiLabScreen(
    padding: PaddingValues,
    onBack: () -> Unit,
) {
    var darkPreview by remember { mutableStateOf(false) }
    var previewSize by remember { mutableStateOf(LabPreviewSize.Medium) }
    var replayKey by remember { mutableIntStateOf(0) }
    var playing by remember { mutableStateOf(true) }
    val appearance = LocalAppAppearance.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appearance.backgroundBrush)
            .padding(padding)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 12.dp)
            .debugOutline(),
    ) {
        UiLabTopBar(onBack = onBack)
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.screen_ui_lab),
            color = if (appearance.isDark) Color.White else Color(0xFF1E2026),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.ui_lab_description),
            color = if (appearance.isDark) Color(0xFFB7BEC9) else Color(0xFF737984),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 21.sp),
        )
        Spacer(Modifier.height(24.dp))
        LabSectionTitle(stringResource(R.string.ui_lab_preview))
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .shadow(18.dp, RoundedCornerShape(30.dp), clip = false)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    if (darkPreview) {
                        Brush.verticalGradient(listOf(Color(0xFF161A22), Color(0xFF242A35)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFFFFD34F), Color(0xFFFFB928)))
                    },
                )
                .border(1.dp, Color.White.copy(alpha = 0.34f), RoundedCornerShape(30.dp))
                .debugOutline(RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center,
        ) {
            key(replayKey) {
                RiveMotionPreview(
                    contentDescription = stringResource(R.string.category_food),
                    playing = playing,
                    replayKey = replayKey,
                    modifier = Modifier.size(previewSize.dimension),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        LabControlCard {
            LabControlRow(
                title = stringResource(R.string.ui_lab_background),
                options = listOf(
                    stringResource(R.string.ui_lab_background_light),
                    stringResource(R.string.ui_lab_background_dark),
                ),
                selectedIndex = if (darkPreview) 1 else 0,
                onSelected = { darkPreview = it == 1 },
            )
            Spacer(Modifier.height(18.dp))
            LabControlRow(
                title = stringResource(R.string.ui_lab_size),
                options = listOf(
                    stringResource(R.string.ui_lab_size_small),
                    stringResource(R.string.ui_lab_size_medium),
                    stringResource(R.string.ui_lab_size_large),
                ),
                selectedIndex = previewSize.ordinal,
                onSelected = { previewSize = LabPreviewSize.entries[it] },
            )
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LabActionButton(
                    text = stringResource(if (playing) R.string.ui_lab_pause else R.string.ui_lab_play),
                    onClick = { playing = !playing },
                    icon = {
                        Icon(
                            imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                )
                LabActionButton(
                    text = stringResource(R.string.ui_lab_replay),
                    onClick = {
                        playing = true
                        replayKey += 1
                    },
                    icon = {
                        Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                    },
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        LabControlCard {
            LabSectionTitle(stringResource(R.string.ui_lab_layers))
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ui_lab_layers_body),
                color = if (appearance.isDark) Color(0xFFCAD0DA) else Color(0xFF666D78),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 22.sp),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.ui_lab_rive_note),
                color = if (appearance.isDark) Color(0xFF9EA8B7) else Color(0xFF7A828E),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LabActionButton(
    text: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF2F6CE5))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp)
            .debugOutline(RoundedCornerShape(999.dp)),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun UiLabTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .clickable(onClick = onBack)
            .debugOutline(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.action_back),
            tint = Color(0xFF151515),
        )
    }
}

@Composable
private fun LabControlCard(content: @Composable ColumnScope.() -> Unit) {
    val appearance = LocalAppAppearance.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                if (appearance.isDark) Color(0xFF252A34).copy(alpha = 0.92f)
                else Color.White.copy(alpha = 0.86f),
            )
            .border(1.dp, Color.White.copy(alpha = 0.38f), RoundedCornerShape(26.dp))
            .padding(18.dp)
            .debugOutline(RoundedCornerShape(26.dp)),
        content = content,
    )
}

@Composable
private fun LabControlRow(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    LabSectionTitle(title)
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) Color(0xFF2F6CE5) else Color(0xFFE9EDF3))
                    .clickable { onSelected(index) }
                    .padding(vertical = 10.dp)
                    .debugOutline(RoundedCornerShape(999.dp)),
                text = option,
                color = if (selected) Color.White else Color(0xFF525966),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LabSectionTitle(text: String) {
    val appearance = LocalAppAppearance.current
    Text(
        text = text,
        color = if (appearance.isDark) Color.White else Color(0xFF252830),
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Preview(name = "UI Motion Lab", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun UiLabScreenPreview() {
    val appearance = appAppearance(isDark = false, backgroundStyle = "mist")
    MaterialTheme(colorScheme = appColorScheme(false)) {
        CompositionLocalProvider(
            LocalAppAppearance provides appearance,
            LocalDebugUiOutline provides false,
        ) {
            UiLabScreen(padding = PaddingValues(), onBack = {})
        }
    }
}
