package com.luzalid.daka.ui.home

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.AppAppearance
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline

import androidx.compose.ui.graphics.Color
import java.util.Locale

internal data class RecommendationPalette(
    val gradient: List<Color>,
    val glow: Color,
    val scrim: Color,
    val stroke: Color,
    val shadow: Color,
    val pillBackground: Color,
    val pillStroke: Color,
    val pillForeground: Color,
)

internal fun recommendationPalette(asset: String): RecommendationPalette = when (asset.lowercase(Locale.getDefault())) {
    "food" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFF1C7), Color(0xFFFFD5A6), Color(0xFFF2A07D)),
        glow = Color(0xFFFFE9A8),
        scrim = Color(0xFF542015),
        stroke = Color(0x66FFE5A6),
        shadow = Color(0x209A3B25),
        pillBackground = Color(0xFFFFF0D5).copy(alpha = 0.95f),
        pillStroke = Color(0xFFFFB75C).copy(alpha = 0.62f),
        pillForeground = Color(0xFF9A3E20),
    )
    "commute" -> RecommendationPalette(
        gradient = listOf(Color(0xFFEAF8FF), Color(0xFFC5E8FA), Color(0xFF83B9DF)),
        glow = Color(0xFFE1F6FF),
        scrim = Color(0xFF092841),
        stroke = Color(0x66D6F5FF),
        shadow = Color(0x20155F91),
        pillBackground = Color(0xFFF0F9FF).copy(alpha = 0.95f),
        pillStroke = Color(0xFF9BD6F5).copy(alpha = 0.64f),
        pillForeground = Color(0xFF185783),
    )
    "sport" -> RecommendationPalette(
        gradient = listOf(Color(0xFFF1FAD9), Color(0xFFCBEAB8), Color(0xFF8FCB9E)),
        glow = Color(0xFFECFFC5),
        scrim = Color(0xFF0B3324),
        stroke = Color(0x66E5FFC3),
        shadow = Color(0x20176D47),
        pillBackground = Color(0xFFF2FFE8).copy(alpha = 0.95f),
        pillStroke = Color(0xFFB9E99A).copy(alpha = 0.64f),
        pillForeground = Color(0xFF287448),
    )
    "work" -> RecommendationPalette(
        gradient = listOf(Color(0xFFF0F5FF), Color(0xFFD2DFF6), Color(0xFF96ACD8)),
        glow = Color(0xFFE8F0FF),
        scrim = Color(0xFF111D40),
        stroke = Color(0x66E1EAFF),
        shadow = Color(0x20263F83),
        pillBackground = Color(0xFFF1F5FF).copy(alpha = 0.95f),
        pillStroke = Color(0xFFB8C9F4).copy(alpha = 0.64f),
        pillForeground = Color(0xFF345294),
    )
    "fun" -> RecommendationPalette(
        gradient = listOf(Color(0xFFF5F0FF), Color(0xFFDDD2F5), Color(0xFFB29DD9)),
        glow = Color(0xFFF0E7FF),
        scrim = Color(0xFF251943),
        stroke = Color(0x66EEE4FF),
        shadow = Color(0x2049347E),
        pillBackground = Color(0xFFF7F1FF).copy(alpha = 0.95f),
        pillStroke = Color(0xFFD4C0FA).copy(alpha = 0.64f),
        pillForeground = Color(0xFF60449D),
    )
    "social" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFF1E7), Color(0xFFF8D0CC), Color(0xFFD99AA6)),
        glow = Color(0xFFFFE8CF),
        scrim = Color(0xFF42172A),
        stroke = Color(0x66FFE4CF),
        shadow = Color(0x2087324F),
        pillBackground = Color(0xFFFFF0EA).copy(alpha = 0.95f),
        pillStroke = Color(0xFFF4B0B2).copy(alpha = 0.64f),
        pillForeground = Color(0xFF963E58),
    )
    "relax" -> RecommendationPalette(
        gradient = listOf(Color(0xFFEDFCF8), Color(0xFFC9ECE3), Color(0xFF8CC6BE)),
        glow = Color(0xFFE3FFF6),
        scrim = Color(0xFF102F31),
        stroke = Color(0x66DFFFF7),
        shadow = Color(0x20246B65),
        pillBackground = Color(0xFFECFFF9).copy(alpha = 0.95f),
        pillStroke = Color(0xFFA9E3D6).copy(alpha = 0.66f),
        pillForeground = Color(0xFF287365),
    )
    "home" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFF5DE), Color(0xFFF5D8AE), Color(0xFFD6AA77)),
        glow = Color(0xFFFFF0CE),
        scrim = Color(0xFF40230F),
        stroke = Color(0x66FFEBC4),
        shadow = Color(0x20805026),
        pillBackground = Color(0xFFFFF5E6).copy(alpha = 0.95f),
        pillStroke = Color(0xFFEFC58B).copy(alpha = 0.64f),
        pillForeground = Color(0xFF855321),
    )
    "study" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFF1FA), Color(0xFFEBD1E7), Color(0xFFC9A2C8)),
        glow = Color(0xFFFFE4F5),
        scrim = Color(0xFF301C42),
        stroke = Color(0x66FFE6F7),
        shadow = Color(0x20633A78),
        pillBackground = Color(0xFFFFF1FA).copy(alpha = 0.95f),
        pillStroke = Color(0xFFE2B2D7).copy(alpha = 0.64f),
        pillForeground = Color(0xFF784885),
    )
    "explore" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFF6CE), Color(0xFFD8EBC9), Color(0xFF9BC9B7)),
        glow = Color(0xFFFFF0B8),
        scrim = Color(0xFF0E3639),
        stroke = Color(0x66FFF0B8),
        shadow = Color(0x201D706D),
        pillBackground = Color(0xFFF1FFF9).copy(alpha = 0.95f),
        pillStroke = Color(0xFFA9DDD0).copy(alpha = 0.66f),
        pillForeground = Color(0xFF24756C),
    )
    else -> RecommendationPalette(
        gradient = listOf(Color(0xFFF0FAF3), Color(0xFFCBE8D5), Color(0xFF91C2A4)),
        glow = Color(0xFFD9F7E4),
        scrim = Color(0xFF0E261A),
        stroke = Color(0x4DCDF5D8),
        shadow = Color(0x2A235E3B),
        pillBackground = Color(0xFFEFFFF4).copy(alpha = 0.94f),
        pillStroke = Color(0xFFB9E4C8).copy(alpha = 0.62f),
        pillForeground = Color(0xFF2B6A45),
    )
}
