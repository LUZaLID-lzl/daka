package com.luzalid.daka.ui.app

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.luzalid.daka.model.PreferenceItem

private val DebugOutlineColor = Color(0xFF1D9BF0)

internal fun preferenceValue(preferences: List<PreferenceItem>, key: String): String =
    preferences.firstOrNull { it.key == key }?.value.orEmpty()

internal data class AppAppearance(
    val isDark: Boolean,
    val backgroundStyle: String,
    val backgroundBrush: Brush,
    val surfaceTint: Color,
    val bottomDockColor: Color,
    val bottomDockSelectedColor: Color,
    val bottomDockContentColor: Color,
    val bottomDockMutedColor: Color,
)

internal val LocalDebugUiOutline = compositionLocalOf { false }

internal val LocalAppAppearance = compositionLocalOf {
    AppAppearance(
        isDark = false,
        backgroundStyle = "mist",
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF8F9FB), Color(0xFFF0F3F6))),
        surfaceTint = Color(0xFFF7F8FA),
        bottomDockColor = Color.White,
        bottomDockSelectedColor = Color(0xFFF2F3F4),
        bottomDockContentColor = Color(0xFF111111),
        bottomDockMutedColor = Color(0xFF8A8F93),
    )
}

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

@Composable
internal fun rememberAppAppearance(preferences: List<PreferenceItem>): AppAppearance {
    val systemDark = isSystemInDarkTheme()
    val themeMode = preferenceValue(preferences, "theme_mode").ifBlank { "system" }
    val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> systemDark
    }
    return appAppearance(
        isDark = isDark,
        backgroundStyle = preferenceValue(preferences, "background_style").ifBlank { "mist" },
    )
}

internal fun appAppearance(
    isDark: Boolean,
    backgroundStyle: String,
): AppAppearance {
    val style = if (isDark && backgroundStyle == "mist") "night" else backgroundStyle
    return when (style) {
        "sunrise" -> AppAppearance(
            isDark = isDark,
            backgroundStyle = style,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFFFF6EC), Color(0xFFFFE8D3), Color(0xFFF8F3EF))),
            surfaceTint = Color(0xFFFFF3EA),
            bottomDockColor = Color(0xFFFFFBF6),
            bottomDockSelectedColor = Color(0xFFFFE6CC),
            bottomDockContentColor = Color(0xFF2A1710),
            bottomDockMutedColor = Color(0xFF98725C),
        )
        "forest" -> AppAppearance(
            isDark = isDark,
            backgroundStyle = style,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF1FBF4), Color(0xFFDDEFE5), Color(0xFFF6FAF7))),
            surfaceTint = Color(0xFFF1FAF4),
            bottomDockColor = Color(0xFFFBFFFC),
            bottomDockSelectedColor = Color(0xFFDFF2E5),
            bottomDockContentColor = Color(0xFF102419),
            bottomDockMutedColor = Color(0xFF6E8C78),
        )
        "night" -> AppAppearance(
            isDark = true,
            backgroundStyle = style,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF12151C), Color(0xFF1B202A), Color(0xFF101217))),
            surfaceTint = Color(0xFF151922),
            bottomDockColor = Color(0xE6222630),
            bottomDockSelectedColor = Color(0xFF343A46),
            bottomDockContentColor = Color(0xFFF7F8FA),
            bottomDockMutedColor = Color(0xFFA7AFBA),
        )
        else -> AppAppearance(
            isDark = isDark,
            backgroundStyle = "mist",
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF8F9FB), Color(0xFFEFF2F6), Color(0xFFF8F9FB))),
            surfaceTint = Color(0xFFF7F8FA),
            bottomDockColor = Color.White,
            bottomDockSelectedColor = Color(0xFFF1F3F5),
            bottomDockContentColor = Color(0xFF111111),
            bottomDockMutedColor = Color(0xFF8A8F93),
        )
    }
}

internal fun appColorScheme(isDark: Boolean) = if (isDark) {
    darkColorScheme(
        primary = Color(0xFFA8C7FA),
        secondary = Color(0xFFB9C8DA),
        tertiary = Color(0xFFD8BDE8),
        background = Color(0xFF111318),
        surface = Color(0xFF191C20),
        surfaceVariant = Color(0xFF41474D),
        primaryContainer = Color(0xFF284777),
        secondaryContainer = Color(0xFF3E4754),
    )
} else {
    lightColorScheme(
        primary = Color(0xFF2F6CE5),
        secondary = Color(0xFF58677A),
        tertiary = Color(0xFF76538A),
        background = Color(0xFFF8F9FB),
        surface = Color(0xFFFFFBFF),
        surfaceVariant = Color(0xFFE7ECF2),
        primaryContainer = Color(0xFFDCE7FF),
        secondaryContainer = Color(0xFFDCE4F0),
    )
}
