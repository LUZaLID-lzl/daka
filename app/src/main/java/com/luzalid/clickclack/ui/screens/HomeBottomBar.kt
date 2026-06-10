package com.luzalid.clickclack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeBottomNavigation(
    modifier: Modifier = Modifier,
    onCreate: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                clip = false,
                ambientColor = Color(0x104A3520),
                spotColor = Color(0x0A4A3520),
            )
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(Color(0xFFF2ECE3).copy(alpha = 0.98f))
            .padding(horizontal = 22.dp, vertical = 6.dp)
            .debugOutline(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeNavItem("Discover", selected = true, icon = { tint ->
                Icon(Icons.Filled.Explore, contentDescription = null, tint = tint, modifier = Modifier.size(25.dp))
            }, onClick = {})
            HomeNavItem("Create", selected = false, icon = { tint ->
                Icon(Icons.Filled.AddCircle, contentDescription = null, tint = tint, modifier = Modifier.size(25.dp))
            }, onClick = onCreate)
            HomeNavItem("Reflect", selected = false, icon = { tint ->
                Icon(Icons.Filled.SelfImprovement, contentDescription = null, tint = tint, modifier = Modifier.size(25.dp))
            }, onClick = onHistory)
            HomeNavItem("Archive", selected = false, icon = { tint ->
                Icon(Icons.Filled.GridView, contentDescription = null, tint = tint, modifier = Modifier.size(25.dp))
            }, onClick = onHistory)
        }
    }
}

@Composable
private fun HomeNavItem(
    label: String,
    selected: Boolean,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(68.dp)
            .clickable(onClick = onClick)
            .debugOutline(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .width(if (selected) 48.dp else 38.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (selected) Color(0xFFF7DFA9) else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            icon(if (selected) Color(0xFF6A4B05) else Color(0xFF4A4032))
        }
        Text(
            text = label,
            color = Color(0xFF2E281F),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
