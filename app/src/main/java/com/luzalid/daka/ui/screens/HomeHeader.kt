package com.luzalid.daka.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R

@Composable
internal fun HomeHeroHeader(
    dateLabel: String,
    onProfile: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp)
            .debugOutline(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_greeting),
                    color = Color(0xFF3D270A),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 32.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Black,
                    ),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.home_subtitle),
                    color = Color(0xFF4B3B28),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            HomeAvatar(onProfile = onProfile)
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun HomeAvatar(onProfile: () -> Unit) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFEFE2))
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
