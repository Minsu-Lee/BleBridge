package com.jackson.blebridge.feature.splash.main

import com.jackson.blebridge.core.mvi.MviViewModel
import com.jackson.blebridge.domain.provider.AppInfoProvider
import com.jackson.blebridge.feature.splash.main.model.SplashIntent
import com.jackson.blebridge.feature.splash.main.model.SplashMutation
import com.jackson.blebridge.feature.splash.main.model.SplashSideEffect
import com.jackson.blebridge.feature.splash.main.model.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class SplashViewModel @Inject constructor(
    appInfoProvider: AppInfoProvider,
) :
    MviViewModel<SplashUiState, SplashSideEffect, SplashIntent, SplashMutation>(
        SplashUiState(
            versionLabel = buildVersionLabel(appInfoProvider),
        ),
    ) {
    private var initializationStarted = false

    override fun handleIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.Initialize -> initialize()
        }
    }

    private fun initialize() {
        if (initializationStarted) return
        initializationStarted = true

        intent {
            delay(SplashDefaults.DURATION_MILLIS)
            applyMutation(SplashMutation.Ready)
            postSideEffect(SplashSideEffect.NavigateToMain)
        }
    }

    override fun reduce(state: SplashUiState, mutation: SplashMutation): SplashUiState =
        when (mutation) {
            SplashMutation.Ready -> state.copy(isLoading = false)
        }
}

private fun buildVersionLabel(appInfoProvider: AppInfoProvider): String {
    return "v${appInfoProvider.versionName} · ${appInfoProvider.packageName}"
}
