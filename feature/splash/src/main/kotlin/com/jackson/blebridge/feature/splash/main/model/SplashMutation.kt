package com.jackson.blebridge.feature.splash.main.model

import com.jackson.blebridge.core.mvi.MviMutation

sealed interface SplashMutation : MviMutation {
    data object Ready : SplashMutation
}
