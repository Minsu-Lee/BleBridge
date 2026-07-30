package com.jackson.blebridge.domain.sample.model

sealed interface RandomCatFactFailure {
    data class Network(val cause: Throwable) : RandomCatFactFailure
    data class Timeout(val cause: Throwable) : RandomCatFactFailure
    data class Client(val statusCode: Int) : RandomCatFactFailure
    data class RateLimited(
        val retryAfterSeconds: Long?,
    ) : RandomCatFactFailure
    data class Server(val statusCode: Int) : RandomCatFactFailure
    data object EmptyBody : RandomCatFactFailure
    data class InvalidResponse(val cause: Throwable) : RandomCatFactFailure
    data class UnexpectedStatus(val statusCode: Int) : RandomCatFactFailure
    data class Unknown(val cause: Throwable) : RandomCatFactFailure
}
