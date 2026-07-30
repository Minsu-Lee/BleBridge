package com.jackson.blebridge.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jackson.blebridge.feature.splash.main.SplashRoute
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashScreen(onNavigateToMain: () -> Unit) {
    composable<SplashRoute> {
        SplashRoute(onNavigateToMain = onNavigateToMain)
    }
}
