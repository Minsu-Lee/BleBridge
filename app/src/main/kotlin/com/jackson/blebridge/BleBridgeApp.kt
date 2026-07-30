package com.jackson.blebridge

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jackson.blebridge.feature.main.navigation.MainRoute
import com.jackson.blebridge.feature.main.navigation.mainScreen
import com.jackson.blebridge.feature.splash.navigation.SplashRoute
import com.jackson.blebridge.feature.splash.navigation.splashScreen

@Composable
fun BleBridgeApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        splashScreen(
            onNavigateToMain = {
                navController.navigate(MainRoute) {
                    popUpTo<SplashRoute> { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        mainScreen()
        // debug sample
        debugSampleScreen(navController = navController)
    }
}
