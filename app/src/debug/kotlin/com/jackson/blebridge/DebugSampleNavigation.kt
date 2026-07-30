package com.jackson.blebridge

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavController
import com.jackson.blebridge.feature.sample.cats.navigation.SampleCatsRoute
import com.jackson.blebridge.feature.sample.cats.navigation.sampleCatsScreen
import com.jackson.blebridge.feature.sample.main.navigation.sampleScreen

internal fun NavGraphBuilder.debugSampleScreen(navController: NavController) {
    sampleScreen(
        onNavigateToCats = {
            navController.navigate(SampleCatsRoute) {
                launchSingleTop = true
            }
        },
    )
    sampleCatsScreen()
}
