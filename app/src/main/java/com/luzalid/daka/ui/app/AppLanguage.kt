package com.luzalid.daka.ui.app

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.luzalid.daka.model.PreferenceItem
import java.util.Locale

@Composable
internal fun ProvideAppLanguage(
    preferences: List<PreferenceItem>,
    content: @Composable () -> Unit,
) {
    val language = preferenceValue(preferences, "app_language").ifBlank { "system" }
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val localizedConfiguration = remember(baseConfiguration, language) {
        baseConfiguration.localizedCopy(language)
    }
    val localizedContext = remember(baseContext, localizedConfiguration, language) {
        baseContext.localizedContext(localizedConfiguration, language)
    }

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfiguration,
        LocalContext provides localizedContext,
        content = content,
    )
}

private fun Configuration.localizedCopy(language: String): Configuration =
    Configuration(this).apply {
        language.localeOrNull()?.let(::setLocale)
    }

private fun Context.localizedContext(configuration: Configuration, language: String): Context =
    if (language == "system") {
        this
    } else {
        createConfigurationContext(configuration)
    }

private fun String.localeOrNull(): Locale? =
    when (this) {
        "zh" -> Locale.SIMPLIFIED_CHINESE
        "en" -> Locale.ENGLISH
        else -> null
    }
