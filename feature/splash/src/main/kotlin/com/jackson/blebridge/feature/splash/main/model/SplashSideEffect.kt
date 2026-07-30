package com.jackson.blebridge.feature.splash.main.model

import com.jackson.blebridge.core.mvi.MviSideEffect

sealed interface SplashSideEffect : MviSideEffect {
    data object NavigateToMain : SplashSideEffect
}
