package com.jackson.blebridge.core.network

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.jackson.blebridge.core.common.NetworkError
import com.jackson.blebridge.core.common.NeveraResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiCallExecutor @Inject constructor() {
    suspend operator fun <T> invoke(
        block: suspend () -> Response<T>,
    ): NeveraResult<T, NetworkError> =
        try {
            val response = block()
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return NeveraResult.Failure(NetworkError.EmptyBodyError())
                NeveraResult.Success(body)
            } else {
                val headers = response.headers().toMultimap()
                response.errorBody()?.close()
                NeveraResult.Failure(
                    NetworkError.HttpError(
                        code = response.code(),
                        message = response.message(),
                        headers = headers,
                    ),
                )
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            NeveraResult.Failure(NetworkError.TimeoutError(throwable = exception))
        } catch (exception: HttpException) {
            val response = exception.response()
            val headers = response?.headers()?.toMultimap().orEmpty()
            response?.errorBody()?.close()
            NeveraResult.Failure(
                NetworkError.HttpError(
                    code = exception.code(),
                    message = exception.message(),
                    throwable = exception,
                    headers = headers,
                ),
            )
        } catch (exception: MalformedJsonException) {
            NeveraResult.Failure(NetworkError.InvalidResponseError(throwable = exception))
        } catch (exception: EOFException) {
            NeveraResult.Failure(NetworkError.InvalidResponseError(throwable = exception))
        } catch (exception: JsonParseException) {
            NeveraResult.Failure(NetworkError.InvalidResponseError(throwable = exception))
        } catch (exception: IOException) {
            NeveraResult.Failure(NetworkError.NetworkConnectionError(throwable = exception))
        } catch (exception: Exception) {
            NeveraResult.Failure(
                NetworkError.UnknownError(
                    message = exception.message,
                    throwable = exception,
                ),
            )
        }
}
