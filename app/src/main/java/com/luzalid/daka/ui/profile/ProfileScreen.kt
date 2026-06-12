package com.luzalid.daka.ui.profile

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline
import com.luzalid.daka.ui.app.preferenceValue

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzalid.daka.R
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.PreferenceItem
import kotlinx.coroutines.launch

private val ProfileHeroShape = RoundedCornerShape(30.dp)
private val ProfileCardShape = RoundedCornerShape(24.dp)
private val ProfileChipShape = RoundedCornerShape(999.dp)

/**
 * Profile and preference page backed by persisted user preferences.
 */
@Composable
internal fun ProfileScreen(padding: PaddingValues, repository: ClickClackRepository) {
    val preferences by repository.observePreferences().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    ProfileContent(
        padding = padding,
        preferences = preferences,
        onPreferenceChange = { key, value ->
            scope.launch { repository.updatePreference(key, value) }
        },
    )
}

/**
 * Stateless profile surface used by the runtime screen and previews.
 */
@Composable
private fun ProfileContent(
    padding: PaddingValues,
    preferences: List<PreferenceItem>,
    onPreferenceChange: (String, String) -> Unit,
) {
    val appearance = LocalAppAppearance.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(appearance.backgroundBrush)
            .debugOutline(),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val horizontalPadding = maxWidth * 0.06f
            val topSpacing = maxHeight * 0.025f
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    top = topSpacing,
                    end = horizontalPadding,
                    bottom = maxHeight * 0.055f,
                ),
                verticalArrangement = Arrangement.spacedBy(maxHeight * 0.018f),
            ) {
                item { ProfileHeader() }
                item { PreferenceSection(text = stringResource(R.string.preference_section_recommendations)) }
                item {
                    PreferenceSwitchRow(
                        icon = { tint -> Icon(Icons.Filled.Notifications, contentDescription = null, tint = tint) },
                        title = stringResource(R.string.preference_daily_reminder),
                        description = preferenceValue(preferences, "reminder_time")
                            .ifBlank { stringResource(R.string.preference_default_reminder_time) },
                        checked = preferenceValue(preferences, "show_daily_reminder") == "true",
                        onCheckedChange = { checked ->
                            onPreferenceChange("show_daily_reminder", checked.toString())
                        },
                    )
                }
                item { PreferenceSection(text = stringResource(R.string.preference_section_appearance)) }
                item {
                    PreferenceChoiceRow(
                        icon = { tint -> Icon(Icons.Filled.Translate, contentDescription = null, tint = tint) },
                        title = stringResource(R.string.preference_language),
                        selectedValue = preferenceValue(preferences, "app_language").ifBlank { "system" },
                        options = listOf(
                            "system" to stringResource(R.string.preference_follow_system),
                            "zh" to stringResource(R.string.preference_language_chinese),
                            "en" to stringResource(R.string.preference_language_english),
                        ),
                        onSelected = { value -> onPreferenceChange("app_language", value) },
                    )
                }
                item {
                    PreferenceChoiceRow(
                        icon = { tint -> Icon(Icons.Filled.Palette, contentDescription = null, tint = tint) },
                        title = stringResource(R.string.preference_theme),
                        selectedValue = preferenceValue(preferences, "theme_mode").ifBlank { "system" },
                        options = listOf(
                            "system" to stringResource(R.string.preference_follow_system),
                            "light" to stringResource(R.string.preference_light),
                            "dark" to stringResource(R.string.preference_dark),
                        ),
                        onSelected = { value -> onPreferenceChange("theme_mode", value) },
                    )
                }
                item {
                    PreferenceChoiceRow(
                        icon = { tint -> Icon(Icons.Filled.WbSunny, contentDescription = null, tint = tint) },
                        title = stringResource(R.string.preference_background),
                        selectedValue = preferenceValue(preferences, "background_style").ifBlank { "mist" },
                        options = listOf(
                            "mist" to stringResource(R.string.preference_background_mist),
                            "sunrise" to stringResource(R.string.preference_background_sunrise),
                            "forest" to stringResource(R.string.preference_background_forest),
                            "night" to stringResource(R.string.preference_background_night),
                        ),
                        onSelected = { value -> onPreferenceChange("background_style", value) },
                    )
                }
                item {
                    PreferenceSwitchRow(
                        icon = { tint -> Icon(Icons.Filled.GridView, contentDescription = null, tint = tint) },
                        title = stringResource(R.string.preference_debug_outline),
                        description = stringResource(R.string.preference_debug_outline_description),
                        checked = preferenceValue(preferences, "debug_ui_outline") == "true",
                        onCheckedChange = { checked ->
                            onPreferenceChange("debug_ui_outline", checked.toString())
                        },
                    )
                }
            }
        }
    }
}

/**
 * Top profile summary card with local-first status and current appearance state.
 */
@Composable
private fun ProfileHeader() {
    val appearance = LocalAppAppearance.current
    val isNight = appearance.isDark
    val heroColors = if (isNight) {
        listOf(Color(0xFF252B38), Color(0xFF171B24))
    } else {
        listOf(Color(0xFFFFFFFF), appearance.surfaceTint, Color(0xFFEAF1FF))
    }
    val titleColor = if (isNight) Color(0xFFF7F8FA) else Color(0xFF171717)
    val bodyColor = if (isNight) Color(0xFFC7CED8) else Color(0xFF626974)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = ProfileHeroShape,
                clip = false,
                ambientColor = Color(0x173B342A),
                spotColor = Color(0x123B342A),
            )
            .clip(ProfileHeroShape)
            .background(Brush.linearGradient(heroColors))
            .debugOutline(ProfileHeroShape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Text(
                    text = stringResource(R.string.profile_local_first),
                    color = bodyColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.app_name),
                    color = titleColor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.profile_description),
                    color = bodyColor,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar() {
    Box(
        modifier = Modifier
            .size(58.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = Color(0x183B342A),
                spotColor = Color(0x123B342A),
            )
            .clip(CircleShape)
            .background(Color.White)
            .padding(4.dp)
            .debugOutline(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFE1D0), Color(0xFFDCE7FF)))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.app_name).take(1),
                color = Color(0xFF171717),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun PreferenceSection(text: String) {
    Text(
        modifier = Modifier
            .padding(start = 4.dp, top = 4.dp)
            .debugOutline(),
        text = text,
        color = if (LocalAppAppearance.current.isDark) Color(0xFFE6EAF0) else Color(0xFF20242A),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun PreferenceSwitchRow(
    icon: @Composable (Color) -> Unit,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    PreferenceCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PreferenceIcon(icon = icon)
            PreferenceText(
                modifier = Modifier.weight(1f),
                title = title,
                description = description,
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

@Composable
private fun PreferenceChoiceRow(
    icon: @Composable (Color) -> Unit,
    title: String,
    selectedValue: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit,
) {
    PreferenceCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PreferenceIcon(icon = icon)
                Text(
                    text = title,
                    color = preferenceTitleColor(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                options.forEach { (value, label) ->
                    FilterChip(
                        selected = selectedValue == value,
                        onClick = { onSelected(value) },
                        shape = ProfileChipShape,
                        label = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = preferenceChipContainerColor(),
                            labelColor = preferenceDescriptionColor(),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceCard(content: @Composable () -> Unit) {
    val appearance = LocalAppAppearance.current
    val container = if (appearance.isDark) Color(0xE6222630) else Color.White.copy(alpha = 0.86f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = ProfileCardShape,
                clip = false,
                ambientColor = Color(0x123B342A),
                spotColor = Color(0x0D3B342A),
            )
            .clip(ProfileCardShape)
            .background(container)
            .debugOutline(ProfileCardShape),
    ) {
        content()
    }
}

@Composable
private fun PreferenceIcon(icon: @Composable (Color) -> Unit) {
    val appearance = LocalAppAppearance.current
    val container = if (appearance.isDark) Color(0xFF343B48) else Color(0xFFF1F5FA)
    val tint = if (appearance.isDark) Color(0xFFE8EDF4) else Color(0xFF151515)

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .debugOutline(RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.size(23.dp), contentAlignment = Alignment.Center) {
            icon(tint)
        }
    }
}

@Composable
private fun PreferenceText(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = preferenceTitleColor(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = description,
            color = preferenceDescriptionColor(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun preferenceTitleColor(): Color =
    if (LocalAppAppearance.current.isDark) Color(0xFFF3F6FA) else Color(0xFF20242A)

@Composable
private fun preferenceDescriptionColor(): Color =
    if (LocalAppAppearance.current.isDark) Color(0xFFB7C0CB) else Color(0xFF68717C)

@Composable
private fun preferenceChipContainerColor(): Color =
    if (LocalAppAppearance.current.isDark) Color(0xFF303744) else Color(0xFFF4F6F9)

@Composable
private fun localizedPreferenceValue(preferences: List<PreferenceItem>, key: String): String =
    when (val value = preferenceValue(preferences, key)) {
        "all_categories" -> stringResource(R.string.preference_all_categories)
        "system" -> stringResource(R.string.preference_follow_system)
        "light" -> stringResource(R.string.preference_light)
        "dark" -> stringResource(R.string.preference_dark)
        "mist" -> stringResource(R.string.preference_background_mist)
        "sunrise" -> stringResource(R.string.preference_background_sunrise)
        "forest" -> stringResource(R.string.preference_background_forest)
        "night" -> stringResource(R.string.preference_background_night)
        "local_uri" -> stringResource(R.string.preference_local_uri)
        "zh" -> stringResource(R.string.preference_language_chinese)
        "en" -> stringResource(R.string.preference_language_english)
        else -> value
    }

private fun previewProfilePreferences() = listOf(
    PreferenceItem("reminder_time", "21:30"),
    PreferenceItem("show_daily_reminder", "true"),
    PreferenceItem("preferred_categories", "all_categories"),
    PreferenceItem("theme_mode", "system"),
    PreferenceItem("app_language", "system"),
    PreferenceItem("background_style", "mist"),
    PreferenceItem("media_strategy", "local_uri"),
    PreferenceItem("debug_ui_outline", "false"),
)

@Preview(name = "Profile Settings", showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun ProfileSettingsPreview() {
    val appearance = appAppearance(isDark = false, backgroundStyle = "mist")
    MaterialTheme(colorScheme = appColorScheme(false)) {
        CompositionLocalProvider(
            LocalDebugUiOutline provides false,
            LocalAppAppearance provides appearance,
        ) {
            ProfileContent(
                padding = PaddingValues(),
                preferences = previewProfilePreferences(),
                onPreferenceChange = { _, _ -> },
            )
        }
    }
}

@Preview(name = "Profile Settings Dark", showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun ProfileSettingsDarkPreview() {
    val appearance = appAppearance(isDark = true, backgroundStyle = "night")
    MaterialTheme(colorScheme = appColorScheme(true)) {
        CompositionLocalProvider(
            LocalDebugUiOutline provides true,
            LocalAppAppearance provides appearance,
        ) {
            ProfileContent(
                padding = PaddingValues(),
                preferences = previewProfilePreferences().map {
                    if (it.key == "background_style") it.copy(value = "night") else it
                },
                onPreferenceChange = { _, _ -> },
            )
        }
    }
}

@Preview(name = "Preference Choice Row", showBackground = true, widthDp = 360, heightDp = 150)
@Composable
private fun PreferenceChoiceRowPreview() {
    val appearance = appAppearance(isDark = false, backgroundStyle = "mist")
    MaterialTheme(colorScheme = appColorScheme(false)) {
        CompositionLocalProvider(
            LocalDebugUiOutline provides false,
            LocalAppAppearance provides appearance,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(appearance.backgroundBrush)
                    .padding(18.dp),
            ) {
                PreferenceChoiceRow(
                    icon = { tint -> Icon(Icons.Filled.Palette, contentDescription = null, tint = tint) },
                    title = stringResource(R.string.preference_theme),
                    selectedValue = "light",
                    options = listOf(
                        "system" to stringResource(R.string.preference_follow_system),
                        "light" to stringResource(R.string.preference_light),
                        "dark" to stringResource(R.string.preference_dark),
                    ),
                    onSelected = {},
                )
            }
        }
    }
}
