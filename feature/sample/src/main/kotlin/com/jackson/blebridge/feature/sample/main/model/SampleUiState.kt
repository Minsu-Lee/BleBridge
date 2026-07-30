package com.jackson.blebridge.feature.sample.main.model

import com.jackson.blebridge.core.mvi.MviState
import com.jackson.blebridge.domain.sample.model.CatFact

data class SampleUiState(
    val isLoading: Boolean = true,
    val fact: CatFact? = null,
    val error: SampleError? = null,
) : MviState

sealed interface SampleError {
    data object Network : SampleError
    data object Timeout : SampleError
    data class Client(val statusCode: Int) : SampleError
    data class RateLimited(val retryAfterSeconds: Long?) : SampleError
    data class Server(val statusCode: Int) : SampleError
    data object EmptyBody : SampleError
    data object InvalidResponse : SampleError
    data class UnexpectedStatus(val statusCode: Int) : SampleError
    data object Unknown : SampleError
}
