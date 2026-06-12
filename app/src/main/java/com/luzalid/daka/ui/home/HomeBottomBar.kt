package com.luzalid.daka.ui.home

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.AppAppearance
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

internal enum class HomeBottomDestination {
    Home,
    Records,
    Profile,
}

@Composable
internal fun HomeBottomNavigation(
    modifier: Modifier = Modifier,
    selectedDestination: HomeBottomDestination = HomeBottomDestination.Home,
    onHome: () -> Unit = {},
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onCreate: () -> Unit,
) {
    val appearance = LocalAppAppearance.current
    val createButtonColor = if (appearance.isDark) Color.White else Color.Black
    val createIconColor = if (appearance.isDark) Color.Black else Color.White
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp)
            .debugOutline(navigationShape),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomDock(
            selectedDestination = selectedDestination,
            onHome = onHome,
            onHistory = onHistory,
            onProfile = onProfile,
            appearance = appearance,
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 18.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color(0x30000000),
                    spotColor = Color(0x24000000),
                )
                .clip(CircleShape)
                .background(createButtonColor)
                .clickable(onClick = onCreate)
                .debugOutline(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = createIconColor, modifier = Modifier.size(28.dp))
        }
    }
}

private val navigationShape = RoundedCornerShape(999.dp)

@Composable
private fun BottomDock(
    selectedDestination: HomeBottomDestination,
    onHome: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    appearance: AppAppearance,
) {
    Box(
        modifier = Modifier
            .width(190.dp)
            .height(64.dp)
            .shadow(
                elevation = 15.dp,
                shape = navigationShape,
                clip = false,
                ambientColor = Color(0x183B342A),
                spotColor = Color(0x123B342A),
            )
            .clip(navigationShape)
            .background(appearance.bottomDockColor)
            .border(1.dp, appearance.bottomDockSelectedColor.copy(alpha = 0.78f), navigationShape)
            .padding(horizontal = 12.dp)
            .debugOutline(navigationShape),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeNavItem(selected = selectedDestination == HomeBottomDestination.Home, hasBadge = false, appearance = appearance, icon = { tint ->
                Icon(Icons.Filled.CalendarViewDay, contentDescription = null, tint = tint, modifier = Modifier.size(27.dp))
            }, onClick = onHome)
            HomeNavItem(selected = selectedDestination == HomeBottomDestination.Records, hasBadge = false, appearance = appearance, icon = { tint ->
                Icon(Icons.Filled.Groups, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
            }, onClick = onHistory)
            HomeNavItem(selected = selectedDestination == HomeBottomDestination.Profile, hasBadge = false, appearance = appearance, icon = { tint ->
                Icon(Icons.Filled.Person, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
            }, onClick = onProfile)
        }
    }
}

@Composable
private fun HomeNavItem(
    selected: Boolean,
    hasBadge: Boolean,
    appearance: AppAppearance,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clickable(onClick = onClick)
            .debugOutline(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (selected) appearance.bottomDockSelectedColor else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            icon(if (selected) appearance.bottomDockContentColor else appearance.bottomDockMutedColor)
        }
        if (hasBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5A52)),
            )
        }
    }
}

@Preview(name = "Home Bottom Navigation", showBackground = true, widthDp = 360, heightDp = 120)
@Composable
private fun HomeBottomNavigationPreview() {
    androidx.compose.material3.MaterialTheme {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAppAppearance provides AppAppearance(
                isDark = false,
                backgroundStyle = "mist",
                backgroundBrush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFF8F9FB), Color(0xFFEFF2F6))),
                surfaceTint = Color(0xFFF7F8FA),
                bottomDockColor = Color.White,
                bottomDockSelectedColor = Color(0xFFF1F3F5),
                bottomDockContentColor = Color(0xFF111111),
                bottomDockMutedColor = Color(0xFF8A8F93),
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(LocalAppAppearance.current.backgroundBrush),
                contentAlignment = Alignment.BottomCenter,
            ) {
                HomeBottomNavigation(onHistory = {}, onProfile = {}, onCreate = {})
            }
        }
    }
}
