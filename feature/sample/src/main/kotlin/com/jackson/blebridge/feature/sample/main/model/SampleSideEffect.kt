package com.jackson.blebridge.feature.sample.main.model

import com.jackson.blebridge.core.mvi.MviSideEffect

sealed interface SampleSideEffect : MviSideEffect {
    data object NavigateToCats : SampleSideEffect
}
