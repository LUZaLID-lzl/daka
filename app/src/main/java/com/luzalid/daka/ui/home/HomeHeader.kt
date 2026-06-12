package com.luzalid.daka.ui.home

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.AppAppearance
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R

@Composable
internal fun HomeHeroHeader(
    selectedDestination: HomeContentDestination,
    onActive: () -> Unit,
    onPast: () -> Unit,
    onUiLab: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 10.dp)
            .debugOutline(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomePeriodSwitcher(
            activeSelected = selectedDestination == HomeContentDestination.Home,
            onActive = onActive,
            onPast = onPast,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HeaderCircleButton(
                icon = { tint ->
                    Icon(Icons.Filled.Search, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
                },
                onClick = {},
            )
            HeaderCircleButton(
                icon = { tint ->
                    Icon(Icons.Filled.Tune, contentDescription = null, tint = tint, modifier = Modifier.size(23.dp))
                },
                onClick = onUiLab,
            )
        }
    }
}

@Composable
private fun HeaderCircleButton(
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = Color(0x143B342A),
                spotColor = Color(0x103B342A),
            )
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .debugOutline(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        icon(Color(0xFF151515))
    }
}

@Composable
private fun HomePeriodSwitcher(
    activeSelected: Boolean,
    onActive: () -> Unit,
    onPast: () -> Unit,
) {
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = Modifier
            .width(148.dp)
            .height(46.dp)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                clip = false,
                ambientColor = Color(0x103B4760),
                spotColor = Color(0x0C3B4760),
            )
            .clip(shape)
            .background(Color.White.copy(alpha = 0.92f))
            .border(1.dp, Color.White.copy(alpha = 0.84f), shape)
            .padding(5.dp)
            .debugOutline(shape),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PeriodTab(
            text = stringResource(R.string.home_tab_active),
            selected = activeSelected,
            onClick = onActive,
            modifier = Modifier.weight(1f),
        )
        PeriodTab(
            text = stringResource(R.string.home_tab_past),
            selected = !activeSelected,
            onClick = onPast,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PeriodTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .background(if (selected) Color(0xFFE6F0FF) else Color.Transparent)
            .clickable(onClick = onClick)
            .debugOutline(shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF3971B8) else Color(0xFF626974),
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            maxLines = 1,
        )
    }
}

@Preview(name = "Home Header", showBackground = true, widthDp = 360, heightDp = 96)
@Composable
private fun HomeHeroHeaderPreview() {
    androidx.compose.material3.MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F8FA)),
        ) {
            HomeHeroHeader(
                selectedDestination = HomeContentDestination.Home,
                onActive = {},
                onPast = {},
                onUiLab = {},
            )
        }
    }
}
