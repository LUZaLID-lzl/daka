package com.luzalid.daka.ui.app

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.ui.edit.EditRecordScreen
import com.luzalid.daka.ui.home.HomeScreen

private sealed interface AppRoute {
    data object Home : AppRoute
    data class Edit(
        val recordId: String?,
        val recommendation: Recommendation,
        val fromRecommendationCard: Boolean,
    ) : AppRoute
}

@Composable
fun ClickClackApp(repository: ClickClackRepository) {
    var route by remember { mutableStateOf<AppRoute>(AppRoute.Home) }
    val homeRecommendations by produceState<List<Recommendation>>(initialValue = emptyList(), repository) {
        repository.initialize()
        value = repository.homeRecommendations()
    }
    val preferences by repository.observePreferences().collectAsState(initial = emptyList())
    val activityRegistryOwner = LocalContext.current as androidx.activity.result.ActivityResultRegistryOwner
    ProvideAppLanguage(preferences = preferences) {
        val appearance = rememberAppAppearance(preferences)
        val debugUiOutlineEnabled = preferenceValue(preferences, "debug_ui_outline") == "true"

        MaterialTheme(colorScheme = appColorScheme(appearance.isDark)) {
            CompositionLocalProvider(
                LocalDebugUiOutline provides debugUiOutlineEnabled,
                LocalAppAppearance provides appearance,
                LocalActivityResultRegistryOwner provides activityRegistryOwner,
            ) {
                Scaffold(
                    contentWindowInsets = WindowInsets(0.dp),
                    containerColor = appearance.surfaceTint,
                ) { padding ->
                    AppRouteContent(
                        padding = padding,
                        route = route,
                        repository = repository,
                        homeRecommendations = homeRecommendations,
                        onRouteChange = { route = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRouteContent(
    padding: PaddingValues,
    route: AppRoute,
    repository: ClickClackRepository,
    homeRecommendations: List<Recommendation>,
    onRouteChange: (AppRoute) -> Unit,
) {
    when (route) {
        AppRoute.Home -> HomeScreen(
            padding = padding,
            repository = repository,
            recommendations = homeRecommendations,
            onHistory = {},
            onRecord = { recordId, recommendation, fromRecommendationCard ->
                onRouteChange(AppRoute.Edit(recordId, recommendation, fromRecommendationCard))
            },
        )

        is AppRoute.Edit -> EditRecordScreen(
            padding = padding,
            repository = repository,
            recordId = route.recordId,
            fallbackRecommendation = route.recommendation,
            fromRecommendationCard = route.fromRecommendationCard,
            onCancel = { onRouteChange(AppRoute.Home) },
            onSaved = { onRouteChange(AppRoute.Home) },
        )
    }
}
