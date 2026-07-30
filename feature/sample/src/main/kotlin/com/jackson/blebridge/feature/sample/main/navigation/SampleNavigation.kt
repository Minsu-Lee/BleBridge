package com.jackson.blebridge.feature.sample.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import androidx.navigation.compose.composable
import com.jackson.blebridge.feature.sample.main.SampleRoute
import kotlinx.serialization.Serializable

const val SAMPLE_DEEP_LINK = "blebridge-debug://sample"

@Serializable
data object SampleRoute

fun NavGraphBuilder.sampleScreen(
    onNavigateToCats: () -> Unit,
) {
    composable<SampleRoute>(
        deepLinks = listOf(
            navDeepLink { uriPattern = SAMPLE_DEEP_LINK },
        ),
    ) {
        SampleRoute(onNavigateToCats = onNavigateToCats)
    }
}
