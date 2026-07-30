package com.jackson.blebridge.feature.sample.main

import com.jackson.blebridge.core.common.NeveraResult
import com.jackson.blebridge.core.mvi.MviViewModel
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import com.jackson.blebridge.domain.sample.usecase.GetRandomCatFactUseCase
import com.jackson.blebridge.feature.sample.main.model.SampleError
import com.jackson.blebridge.feature.sample.main.model.SampleIntent
import com.jackson.blebridge.feature.sample.main.model.SampleMutation
import com.jackson.blebridge.feature.sample.main.model.SampleSideEffect
import com.jackson.blebridge.feature.sample.main.model.SampleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val getRandomCatFact: GetRandomCatFactUseCase,
) : MviViewModel<SampleUiState, SampleSideEffect, SampleIntent, SampleMutation>(
    SampleUiState(),
) {
    private var initializationStarted = false

    override fun handleIntent(intent: SampleIntent) {
        when (intent) {
            SampleIntent.Initialize -> initialize()
            SampleIntent.RetryClicked -> loadRandomFact()
            SampleIntent.CatsClicked -> intent {
                postSideEffect(SampleSideEffect.NavigateToCats)
            }
        }
    }

    private fun initialize() {
        if (initializationStarted) return
        initializationStarted = true
        loadRandomFact()
    }

    private fun loadRandomFact() = intent {
        applyMutation(SampleMutation.Loading)
        when (val result = getRandomCatFact()) {
            is NeveraResult.Success -> {
                applyMutation(SampleMutation.Loaded(result.data))
            }
            is NeveraResult.Failure -> {
                applyMutation(SampleMutation.Failed(result.error.toUiError()))
            }
        }
    }

    override fun reduce(state: SampleUiState, mutation: SampleMutation): SampleUiState =
        when (mutation) {
            SampleMutation.Loading -> state.copy(
                isLoading = true,
                error = null,
            )
            is SampleMutation.Loaded -> state.copy(
                isLoading = false,
                fact = mutation.fact,
                error = null,
            )
            is SampleMutation.Failed -> state.copy(
                isLoading = false,
                fact = null,
                error = mutation.error,
            )
        }
}

private fun RandomCatFactFailure.toUiError(): SampleError =
    when (this) {
        is RandomCatFactFailure.Network -> SampleError.Network
        is RandomCatFactFailure.Timeout -> SampleError.Timeout
        is RandomCatFactFailure.Client -> SampleError.Client(statusCode)
        is RandomCatFactFailure.RateLimited -> SampleError.RateLimited(retryAfterSeconds)
        is RandomCatFactFailure.Server -> SampleError.Server(statusCode)
        RandomCatFactFailure.EmptyBody -> SampleError.EmptyBody
        is RandomCatFactFailure.InvalidResponse -> SampleError.InvalidResponse
        is RandomCatFactFailure.UnexpectedStatus -> SampleError.UnexpectedStatus(statusCode)
        is RandomCatFactFailure.Unknown -> SampleError.Unknown
    }
