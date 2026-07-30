package com.jackson.blebridge.feature.sample.main.model

import com.jackson.blebridge.core.mvi.MviMutation
import com.jackson.blebridge.domain.sample.model.CatFact

sealed interface SampleMutation : MviMutation {
    data object Loading : SampleMutation
    data class Loaded(val fact: CatFact) : SampleMutation
    data class Failed(val error: SampleError) : SampleMutation
}
