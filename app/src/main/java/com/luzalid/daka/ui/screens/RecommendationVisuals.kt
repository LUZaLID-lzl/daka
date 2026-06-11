package com.luzalid.daka.ui.screens

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
        gradient = listOf(Color(0xFFFFD086), Color(0xFFE87532), Color(0xFF6A2E13)),
        glow = Color(0xFFFFE0A8),
        scrim = Color(0xFF351406),
        stroke = Color(0x4DFFE1B5),
        shadow = Color(0x2A7B2F12),
        pillBackground = Color(0xFFFFF1DC).copy(alpha = 0.94f),
        pillStroke = Color(0xFFFFC77E).copy(alpha = 0.58f),
        pillForeground = Color(0xFF8A3E11),
    )
    "commute" -> RecommendationPalette(
        gradient = listOf(Color(0xFF9CE5FF), Color(0xFF2C8EC8), Color(0xFF123C62)),
        glow = Color(0xFFC6F1FF),
        scrim = Color(0xFF071D33),
        stroke = Color(0x4DBFEFFF),
        shadow = Color(0x2A0D4E76),
        pillBackground = Color(0xFFE8F8FF).copy(alpha = 0.94f),
        pillStroke = Color(0xFF9EDFFF).copy(alpha = 0.58f),
        pillForeground = Color(0xFF135476),
    )
    "sport" -> RecommendationPalette(
        gradient = listOf(Color(0xFFD5F28A), Color(0xFF66AA47), Color(0xFF234A22)),
        glow = Color(0xFFE9FFB0),
        scrim = Color(0xFF10230F),
        stroke = Color(0x4DE2FFB4),
        shadow = Color(0x2A245F1D),
        pillBackground = Color(0xFFF1FFE2).copy(alpha = 0.94f),
        pillStroke = Color(0xFFC8EB8E).copy(alpha = 0.62f),
        pillForeground = Color(0xFF3B6E20),
    )
    "work" -> RecommendationPalette(
        gradient = listOf(Color(0xFFA8C8FF), Color(0xFF5177D6), Color(0xFF1C2E66)),
        glow = Color(0xFFD3E1FF),
        scrim = Color(0xFF0B1535),
        stroke = Color(0x4DD4E0FF),
        shadow = Color(0x2A1D3B86),
        pillBackground = Color(0xFFEFF4FF).copy(alpha = 0.94f),
        pillStroke = Color(0xFFB8CBFF).copy(alpha = 0.60f),
        pillForeground = Color(0xFF27458E),
    )
    "fun" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFB9D4), Color(0xFFD75186), Color(0xFF642244)),
        glow = Color(0xFFFFD8E7),
        scrim = Color(0xFF351020),
        stroke = Color(0x4DFFD4E5),
        shadow = Color(0x2A7B2450),
        pillBackground = Color(0xFFFFEEF5).copy(alpha = 0.94f),
        pillStroke = Color(0xFFFFB7D2).copy(alpha = 0.60f),
        pillForeground = Color(0xFF8B2853),
    )
    "social" -> RecommendationPalette(
        gradient = listOf(Color(0xFFD8BBFF), Color(0xFF8A5DD4), Color(0xFF39245F)),
        glow = Color(0xFFE9D8FF),
        scrim = Color(0xFF1D0F36),
        stroke = Color(0x4DE9D8FF),
        shadow = Color(0x2A4B287A),
        pillBackground = Color(0xFFF6EEFF).copy(alpha = 0.94f),
        pillStroke = Color(0xFFD9BFFF).copy(alpha = 0.62f),
        pillForeground = Color(0xFF59328A),
    )
    "relax" -> RecommendationPalette(
        gradient = listOf(Color(0xFFB2F0E1), Color(0xFF3DAA99), Color(0xFF174F4D)),
        glow = Color(0xFFD2FFF2),
        scrim = Color(0xFF082624),
        stroke = Color(0x4DC5FFF2),
        shadow = Color(0x2A17665F),
        pillBackground = Color(0xFFE9FFF9).copy(alpha = 0.94f),
        pillStroke = Color(0xFFAEEBDF).copy(alpha = 0.64f),
        pillForeground = Color(0xFF226A60),
    )
    "home" -> RecommendationPalette(
        gradient = listOf(Color(0xFFFFDCA6), Color(0xFFB98043), Color(0xFF563519)),
        glow = Color(0xFFFFECC8),
        scrim = Color(0xFF2B1607),
        stroke = Color(0x4DFFE3B8),
        shadow = Color(0x2A694015),
        pillBackground = Color(0xFFFFF4E6).copy(alpha = 0.94f),
        pillStroke = Color(0xFFE8C18B).copy(alpha = 0.62f),
        pillForeground = Color(0xFF73501D),
    )
    "study" -> RecommendationPalette(
        gradient = listOf(Color(0xFFB9D5FF), Color(0xFF5D79B9), Color(0xFF26345E)),
        glow = Color(0xFFD8E8FF),
        scrim = Color(0xFF101934),
        stroke = Color(0x4DD8E8FF),
        shadow = Color(0x2A263A72),
        pillBackground = Color(0xFFF0F5FF).copy(alpha = 0.94f),
        pillStroke = Color(0xFFBDCEEF).copy(alpha = 0.62f),
        pillForeground = Color(0xFF354C84),
    )
    else -> RecommendationPalette(
        gradient = listOf(Color(0xFFAEEAC3), Color(0xFF4F9D74), Color(0xFF244D38)),
        glow = Color(0xFFD9F7E4),
        scrim = Color(0xFF0E261A),
        stroke = Color(0x4DCDF5D8),
        shadow = Color(0x2A235E3B),
        pillBackground = Color(0xFFEFFFF4).copy(alpha = 0.94f),
        pillStroke = Color(0xFFB9E4C8).copy(alpha = 0.62f),
        pillForeground = Color(0xFF2B6A45),
    )
}
