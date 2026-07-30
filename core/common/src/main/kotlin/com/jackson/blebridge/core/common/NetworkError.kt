package com.jackson.blebridge.core.common

sealed interface NetworkError {
    val code: Int?
    val message: String?
    val throwable: Throwable?

    data class HttpError(
        override val code: Int,
        override val message: String? = null,
        override val throwable: Throwable? = null,
        val headers: Map<String, List<String>> = emptyMap(),
    ) : NetworkError

    data class NetworkConnectionError(
        override val code: Int? = null,
        override val message: String? = "Network connection error",
        override val throwable: Throwable? = null,
    ) : NetworkError

    data class TimeoutError(
        override val code: Int? = null,
        override val message: String? = "Request timed out",
        override val throwable: Throwable? = null,
    ) : NetworkError

    data class EmptyBodyError(
        override val code: Int? = null,
        override val message: String? = "Empty response body",
        override val throwable: Throwable? = null,
    ) : NetworkError

    data class InvalidResponseError(
        override val code: Int? = null,
        override val message: String? = "Invalid response body",
        override val throwable: Throwable? = null,
    ) : NetworkError

    data class UnknownError(
        override val code: Int? = null,
        override val message: String? = "Unknown network error",
        override val throwable: Throwable? = null,
    ) : NetworkError
}
