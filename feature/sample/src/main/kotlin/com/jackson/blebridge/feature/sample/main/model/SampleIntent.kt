package com.jackson.blebridge.feature.sample.main.model

import com.jackson.blebridge.core.mvi.MviIntent

sealed interface SampleIntent : MviIntent {
    data object Initialize : SampleIntent
    data object RetryClicked : SampleIntent
    data object CatsClicked : SampleIntent
}
