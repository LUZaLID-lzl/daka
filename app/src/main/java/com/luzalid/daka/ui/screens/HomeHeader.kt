package com.luzalid.daka.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeHeroHeader(
    onProfile: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 18.dp)
            .debugOutline(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomeAvatar(onProfile = onProfile)
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
                onClick = {},
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
private fun HomeAvatar(onProfile: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = Color(0x123B342A),
                spotColor = Color(0x0C3B342A),
            )
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onProfile)
            .padding(3.dp)
            .debugOutline(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color(0xFFF8DAC5)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF3B1D11),
                    radius = size.minDimension * 0.22f,
                    center = Offset(size.width * 0.50f, size.height * 0.30f),
                )
                drawCircle(
                    color = Color(0xFFFFD7BF),
                    radius = size.minDimension * 0.19f,
                    center = Offset(size.width * 0.50f, size.height * 0.38f),
                )
                drawOval(
                    color = Color(0xFFFFF7EF),
                    topLeft = Offset(size.width * 0.28f, size.height * 0.58f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.44f, size.height * 0.30f),
                )
            }
        }
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
            HomeHeroHeader(onProfile = {})
        }
    }
}
