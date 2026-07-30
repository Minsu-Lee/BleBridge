package com.jackson.blebridge.core.network

import com.google.gson.stream.MalformedJsonException
import com.jackson.blebridge.core.common.NetworkError
import com.jackson.blebridge.core.common.NeveraResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class ApiCallExecutorTest {
    private val apiCall = ApiCallExecutor()

    @Test
    fun `body가 있으면 success를 반환한다`() = runTest {
        val result = apiCall { Response.success("body") }

        val success = assertInstanceOf(NeveraResult.Success::class.java, result)
        assertEquals("body", success.data)
    }

    @Test
    fun `body가 null이면 empty body error를 반환한다`() = runTest {
        val result = apiCall { Response.success<String>(null) }

        assertInstanceOf(
            NetworkError.EmptyBodyError::class.java,
            assertFailure(result),
        )
    }

    @Test
    fun `http error response의 status를 보존한다`() = runTest {
        val response = Response.error<String>(
            429,
            """{"message":"Too many requests"}""".toResponseBody(),
        )
        val result = apiCall { response }

        val error = assertInstanceOf(
            NetworkError.HttpError::class.java,
            assertFailure(result),
        )
        assertEquals(429, error.code)
    }

    @Test
    fun `timeout과 connection error를 구분한다`() = runTest {
        val timeout = apiCall<String> { throw SocketTimeoutException("timeout") }
        val connection = apiCall<String> { throw IOException("offline") }

        assertInstanceOf(NetworkError.TimeoutError::class.java, assertFailure(timeout))
        assertInstanceOf(
            NetworkError.NetworkConnectionError::class.java,
            assertFailure(connection),
        )
    }

    @Test
    fun `malformed json은 invalid response error로 변환한다`() = runTest {
        val result = apiCall<String> {
            throw MalformedJsonException("invalid json")
        }

        assertInstanceOf(
            NetworkError.InvalidResponseError::class.java,
            assertFailure(result),
        )
    }

    @Test
    fun `cancellation exception은 재전파한다`() {
        assertThrows(CancellationException::class.java) {
            runTest {
                apiCall<String> { throw CancellationException("cancelled") }
            }
        }
    }

    private fun assertFailure(
        result: NeveraResult<*, NetworkError>,
    ): NetworkError =
        assertInstanceOf(NeveraResult.Failure::class.java, result).error as NetworkError
}
