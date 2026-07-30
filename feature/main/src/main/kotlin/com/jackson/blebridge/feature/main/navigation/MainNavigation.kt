package com.jackson.blebridge.feature.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jackson.blebridge.feature.main.MainRoute
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

fun NavGraphBuilder.mainScreen() {
    composable<MainRoute> {
        MainRoute()
    }
}
