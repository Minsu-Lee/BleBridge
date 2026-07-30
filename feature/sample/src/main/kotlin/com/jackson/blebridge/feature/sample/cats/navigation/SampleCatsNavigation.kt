package com.jackson.blebridge.feature.sample.cats.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jackson.blebridge.feature.sample.cats.SampleCatsRoute
import kotlinx.serialization.Serializable

@Serializable
data object SampleCatsRoute

fun NavGraphBuilder.sampleCatsScreen() {
    composable<SampleCatsRoute> {
        SampleCatsRoute()
    }
}
