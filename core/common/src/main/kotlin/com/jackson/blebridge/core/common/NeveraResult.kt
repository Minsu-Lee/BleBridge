package com.jackson.blebridge.core.common

sealed interface NeveraResult<out T, out E> {
    data class Success<T>(val data: T) : NeveraResult<T, Nothing>
    data class Failure<E>(val error: E) : NeveraResult<Nothing, E>
}

inline fun <T, E, R> NeveraResult<T, E>.mapSuccess(
    transform: (T) -> R,
): NeveraResult<R, E> =
    when (this) {
        is NeveraResult.Success -> NeveraResult.Success(transform(data))
        is NeveraResult.Failure -> NeveraResult.Failure(error)
    }

inline fun <T, E, F> NeveraResult<T, E>.mapFailure(
    transform: (E) -> F,
): NeveraResult<T, F> =
    when (this) {
        is NeveraResult.Success -> NeveraResult.Success(data)
        is NeveraResult.Failure -> NeveraResult.Failure(transform(error))
    }

inline fun <T, E, R, F> NeveraResult<T, E>.map(
    transformSuccess: (T) -> R,
    transformFailure: (E) -> F,
): NeveraResult<R, F> =
    when (this) {
        is NeveraResult.Success -> NeveraResult.Success(transformSuccess(data))
        is NeveraResult.Failure -> NeveraResult.Failure(transformFailure(error))
    }
