package com.jackson.blebridge.data.sample.mapper

import com.jackson.blebridge.core.common.NetworkError
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.SocketTimeoutException

class RandomCatFactErrorMapperTest {
    @Test
    fun `429와 retry after를 rate limited로 변환한다`() {
        val mapped = NetworkError.HttpError(
            code = 429,
            headers = mapOf("retry-after" to listOf("30")),
        ).toRandomCatFactFailure()

        val rateLimited = assertInstanceOf(
            RandomCatFactFailure.RateLimited::class.java,
            mapped,
        )
        assertEquals(30L, rateLimited.retryAfterSeconds)
    }

    @Test
    fun `retry after가 숫자가 아니면 대기 시간을 비운다`() {
        val mapped = NetworkError.HttpError(
            code = 429,
            headers = mapOf("Retry-After" to listOf("tomorrow")),
        ).toRandomCatFactFailure()

        val rateLimited = assertInstanceOf(
            RandomCatFactFailure.RateLimited::class.java,
            mapped,
        )
        assertNull(rateLimited.retryAfterSeconds)
    }

    @Test
    fun `http status 범위를 client server unexpected로 구분한다`() {
        assertInstanceOf(
            RandomCatFactFailure.Client::class.java,
            NetworkError.HttpError(404).toRandomCatFactFailure(),
        )
        assertInstanceOf(
            RandomCatFactFailure.Server::class.java,
            NetworkError.HttpError(503).toRandomCatFactFailure(),
        )
        assertInstanceOf(
            RandomCatFactFailure.UnexpectedStatus::class.java,
            NetworkError.HttpError(399).toRandomCatFactFailure(),
        )
    }

    @Test
    fun `공통 네트워크 오류를 random fact 오류로 변환한다`() {
        assertInstanceOf(
            RandomCatFactFailure.Network::class.java,
            NetworkError.NetworkConnectionError(
                throwable = IOException("offline"),
            ).toRandomCatFactFailure(),
        )
        assertInstanceOf(
            RandomCatFactFailure.Timeout::class.java,
            NetworkError.TimeoutError(
                throwable = SocketTimeoutException("timeout"),
            ).toRandomCatFactFailure(),
        )
        assertInstanceOf(
            RandomCatFactFailure.EmptyBody::class.java,
            NetworkError.EmptyBodyError().toRandomCatFactFailure(),
        )
        assertInstanceOf(
            RandomCatFactFailure.InvalidResponse::class.java,
            NetworkError.InvalidResponseError(
                throwable = IllegalStateException("invalid"),
            ).toRandomCatFactFailure(),
        )
    }
}
