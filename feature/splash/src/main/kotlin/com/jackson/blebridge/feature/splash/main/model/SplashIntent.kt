package com.jackson.blebridge.feature.splash.main.model

import com.jackson.blebridge.core.mvi.MviIntent

sealed interface SplashIntent : MviIntent {
    data object Initialize : SplashIntent
}
