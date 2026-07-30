package com.jackson.blebridge.feature.splash.main.model

import com.jackson.blebridge.core.mvi.MviState

data class SplashUiState(
    val isLoading: Boolean = true,
    val versionLabel: String = "",
) : MviState
