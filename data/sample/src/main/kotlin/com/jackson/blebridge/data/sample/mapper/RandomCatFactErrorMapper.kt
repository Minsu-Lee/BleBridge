package com.jackson.blebridge.data.sample.mapper

import com.jackson.blebridge.core.common.NetworkError
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure

internal fun NetworkError.toRandomCatFactFailure(): RandomCatFactFailure =
    when (this) {
        is NetworkError.HttpError -> when (code) {
            HTTP_TOO_MANY_REQUESTS -> RandomCatFactFailure.RateLimited(
                retryAfterSeconds = headers.header(RETRY_AFTER_HEADER)?.toLongOrNull(),
            )
            in HTTP_CLIENT_ERROR_RANGE -> RandomCatFactFailure.Client(code)
            in HTTP_SERVER_ERROR_RANGE -> RandomCatFactFailure.Server(code)
            else -> RandomCatFactFailure.UnexpectedStatus(code)
        }
        is NetworkError.NetworkConnectionError -> {
            RandomCatFactFailure.Network(
                throwable ?: IllegalStateException(message),
            )
        }
        is NetworkError.TimeoutError -> {
            RandomCatFactFailure.Timeout(
                throwable ?: IllegalStateException(message),
            )
        }
        is NetworkError.EmptyBodyError -> RandomCatFactFailure.EmptyBody
        is NetworkError.InvalidResponseError -> {
            RandomCatFactFailure.InvalidResponse(
                throwable ?: IllegalStateException(message),
            )
        }
        is NetworkError.UnknownError -> {
            RandomCatFactFailure.Unknown(
                throwable ?: IllegalStateException(message),
            )
        }
    }

private fun Map<String, List<String>>.header(name: String): String? =
    entries.firstOrNull { (key, _) -> key.equals(name, ignoreCase = true) }
        ?.value
        ?.firstOrNull()

private const val HTTP_TOO_MANY_REQUESTS = 429
private val HTTP_CLIENT_ERROR_RANGE = 400..499
private val HTTP_SERVER_ERROR_RANGE = 500..599
private const val RETRY_AFTER_HEADER = "Retry-After"
