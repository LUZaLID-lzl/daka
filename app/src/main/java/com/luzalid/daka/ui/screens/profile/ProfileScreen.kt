package com.luzalid.daka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luzalid.daka.R
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.PreferenceItem
import kotlinx.coroutines.launch

@Composable
internal fun ProfileScreen(padding: PaddingValues, repository: ClickClackRepository) {
    val preferences by repository.observePreferences().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .debugOutline(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ProfileHeader() }
        item { PreferenceSection(stringResource(R.string.preference_section_recommendations)) }
        item {
            PreferenceSwitchRow(
                icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                title = stringResource(R.string.preference_daily_reminder),
                description = preferenceValue(preferences, "reminder_time"),
                checked = preferenceValue(preferences, "show_daily_reminder") == "true",
                onCheckedChange = { checked ->
                    scope.launch { repository.updatePreference("show_daily_reminder", checked.toString()) }
                },
            )
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                title = stringResource(R.string.preference_categories),
                description = localizedPreferenceValue(preferences, "preferred_categories"),
            )
        }
        item { PreferenceSection(stringResource(R.string.preference_section_appearance)) }
        item {
            PreferenceChoiceRow(
                icon = { Icon(Icons.Filled.Palette, contentDescription = null) },
                title = stringResource(R.string.preference_theme),
                selectedValue = preferenceValue(preferences, "theme_mode").ifBlank { "system" },
                options = listOf(
                    "system" to stringResource(R.string.preference_follow_system),
                    "light" to stringResource(R.string.preference_light),
                    "dark" to stringResource(R.string.preference_dark),
                ),
                onSelected = { value ->
                    scope.launch { repository.updatePreference("theme_mode", value) }
                },
            )
        }
        item {
            PreferenceChoiceRow(
                icon = { Icon(Icons.Filled.WbSunny, contentDescription = null) },
                title = stringResource(R.string.preference_background),
                selectedValue = preferenceValue(preferences, "background_style").ifBlank { "mist" },
                options = listOf(
                    "mist" to stringResource(R.string.preference_background_mist),
                    "sunrise" to stringResource(R.string.preference_background_sunrise),
                    "forest" to stringResource(R.string.preference_background_forest),
                    "night" to stringResource(R.string.preference_background_night),
                ),
                onSelected = { value ->
                    scope.launch { repository.updatePreference("background_style", value) }
                },
            )
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Storage, contentDescription = null) },
                title = stringResource(R.string.preference_media_strategy),
                description = localizedPreferenceValue(preferences, "media_strategy"),
            )
        }
        item {
            PreferenceSwitchRow(
                icon = { Icon(Icons.Filled.GridView, contentDescription = null) },
                title = stringResource(R.string.preference_debug_outline),
                description = stringResource(R.string.preference_debug_outline_description),
                checked = preferenceValue(preferences, "debug_ui_outline") == "true",
                onCheckedChange = { checked ->
                    scope.launch { repository.updatePreference("debug_ui_outline", checked.toString()) }
                },
            )
        }
        item {
            PreferenceStaticRow(
                icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                title = stringResource(R.string.preference_about),
                description = stringResource(R.string.preference_about_description),
            )
        }
    }
}

@Composable
private fun ProfileHeader() {
    Card(
        modifier = Modifier.debugOutline(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.profile_local_first),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.profile_description))
        }
    }
}

@Composable
private fun PreferenceSection(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        HorizontalDivider()
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp)) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(description) },
            leadingContent = icon,
            trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        )
    }
}

@Composable
private fun PreferenceStaticRow(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp)) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(description) },
            leadingContent = icon,
        )
    }
}

@Composable
private fun PreferenceChoiceRow(
    icon: @Composable () -> Unit,
    title: String,
    selectedValue: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit,
) {
    Card(modifier = Modifier.debugOutline(RoundedCornerShape(22.dp)), shape = RoundedCornerShape(22.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                options.forEach { (value, label) ->
                    FilterChip(
                        selected = selectedValue == value,
                        onClick = { onSelected(value) },
                        label = { Text(label) },
                    )
                }
            }
        }
    }
}

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
        else -> value
    }

@Preview(name = "Profile Settings", showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun ProfileSettingsPreview() {
    val preferences = listOf(
        PreferenceItem("reminder_time", "21:30"),
        PreferenceItem("show_daily_reminder", "true"),
        PreferenceItem("preferred_categories", "all_categories"),
        PreferenceItem("theme_mode", "system"),
        PreferenceItem("background_style", "mist"),
        PreferenceItem("media_strategy", "local_uri"),
        PreferenceItem("debug_ui_outline", "false"),
    )
    MaterialTheme(colorScheme = appColorScheme(false)) {
        CompositionLocalProvider(
            LocalDebugUiOutline provides false,
            LocalAppAppearance provides appAppearance(isDark = false, backgroundStyle = "mist"),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalAppAppearance.current.backgroundBrush),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { ProfileHeader() }
                item { PreferenceSection(stringResource(R.string.preference_section_recommendations)) }
                item {
                    PreferenceSwitchRow(
                        icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        title = stringResource(R.string.preference_daily_reminder),
                        description = preferenceValue(preferences, "reminder_time"),
                        checked = true,
                        onCheckedChange = {},
                    )
                }
                item {
                    PreferenceStaticRow(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        title = stringResource(R.string.preference_categories),
                        description = localizedPreferenceValue(preferences, "preferred_categories"),
                    )
                }
                item { PreferenceSection(stringResource(R.string.preference_section_appearance)) }
                item {
                    PreferenceChoiceRow(
                        icon = { Icon(Icons.Filled.Palette, contentDescription = null) },
                        title = stringResource(R.string.preference_theme),
                        selectedValue = "system",
                        options = listOf(
                            "system" to stringResource(R.string.preference_follow_system),
                            "light" to stringResource(R.string.preference_light),
                            "dark" to stringResource(R.string.preference_dark),
                        ),
                        onSelected = {},
                    )
                }
                item {
                    PreferenceChoiceRow(
                        icon = { Icon(Icons.Filled.WbSunny, contentDescription = null) },
                        title = stringResource(R.string.preference_background),
                        selectedValue = "mist",
                        options = listOf(
                            "mist" to stringResource(R.string.preference_background_mist),
                            "sunrise" to stringResource(R.string.preference_background_sunrise),
                            "forest" to stringResource(R.string.preference_background_forest),
                            "night" to stringResource(R.string.preference_background_night),
                        ),
                        onSelected = {},
                    )
                }
                item {
                    PreferenceSwitchRow(
                        icon = { Icon(Icons.Filled.GridView, contentDescription = null) },
                        title = stringResource(R.string.preference_debug_outline),
                        description = stringResource(R.string.preference_debug_outline_description),
                        checked = false,
                        onCheckedChange = {},
                    )
                }
            }
        }
    }
}

@Preview(name = "Preference Choice Row", showBackground = true, widthDp = 360, heightDp = 140)
@Composable
private fun PreferenceChoiceRowPreview() {
    MaterialTheme(colorScheme = appColorScheme(false)) {
        PreferenceChoiceRow(
            icon = { Icon(Icons.Filled.Palette, contentDescription = null) },
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
