package com.luzalid.daka.ui.app

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import com.luzalid.daka.BuildConfig
import com.luzalid.daka.data.ClickClackRepository
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.ui.edit.EditRecordScreen
import com.luzalid.daka.ui.debug.UiLabScreen
import com.luzalid.daka.ui.home.HomeScreen

private sealed interface AppRoute {
    data object Home : AppRoute
    data object UiLab : AppRoute
    data class Edit(
        val recordId: String?,
        val recommendation: Recommendation,
        val fromRecommendationCard: Boolean,
    ) : AppRoute
}

@Composable
fun ClickClackApp(repository: ClickClackRepository) {
    val initialRoute = if (BuildConfig.OPEN_UI_LAB_ON_LAUNCH) AppRoute.UiLab else AppRoute.Home
    var route by remember { mutableStateOf<AppRoute>(initialRoute) }
    val preferences by repository.observePreferences().collectAsState(initial = emptyList())
    val activityRegistryOwner = LocalContext.current as androidx.activity.result.ActivityResultRegistryOwner
    ProvideAppLanguage(preferences = preferences) {
        val localizedContext = LocalContext.current
        val language = preferenceValue(preferences, "app_language").ifBlank { "system" }
        val homeRecommendations by produceState<List<Recommendation>>(
            initialValue = emptyList(),
            repository,
            language,
            localizedContext,
        ) {
            repository.initialize()
            value = repository.homeRecommendations(localizedContext.resources)
        }
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
                    AnimatedContent(
                        targetState = route,
                        transitionSpec = {
                            val enteringEdit = targetState is AppRoute.Edit
                            val leavingEdit = initialState is AppRoute.Edit
                            when {
                                enteringEdit -> {
                                    (
                                        fadeIn(tween(320)) +
                                            slideInVertically(tween(440)) { it / 14 } +
                                            scaleIn(tween(440), initialScale = 0.985f)
                                        ).togetherWith(
                                        fadeOut(tween(220)) +
                                            slideOutVertically(tween(360)) { -it / 18 } +
                                            scaleOut(tween(360), targetScale = 0.975f),
                                    )
                                }

                                leavingEdit -> {
                                    (
                                        fadeIn(tween(300)) +
                                            slideInVertically(tween(380)) { -it / 18 } +
                                            scaleIn(tween(380), initialScale = 0.975f)
                                        ).togetherWith(
                                        fadeOut(tween(200)) +
                                            slideOutVertically(tween(320)) { it / 14 } +
                                            scaleOut(tween(320), targetScale = 0.985f),
                                    )
                                }

                                else -> fadeIn(tween(220)).togetherWith(fadeOut(tween(160)))
                            }
                        },
                        label = "appRouteTransition",
                    ) { animatedRoute ->
                        AppRouteContent(
                            padding = padding,
                            route = animatedRoute,
                            repository = repository,
                            homeRecommendations = homeRecommendations,
                            onRouteChange = { route = it },
                        )
                    }
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
            onUiLab = { onRouteChange(AppRoute.UiLab) },
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

        AppRoute.UiLab -> UiLabScreen(
            padding = padding,
            onBack = { onRouteChange(AppRoute.Home) },
        )
    }
}
