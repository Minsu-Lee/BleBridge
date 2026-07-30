package com.jackson.blebridge.data.sample.repository

import com.jackson.blebridge.core.common.NeveraResult
import com.jackson.blebridge.core.network.ApiCallExecutor
import com.jackson.blebridge.data.sample.api.CatFactApi
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatFactRepositoryImplTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: CatFactRepositoryImpl

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatFactApi::class.java)
        repository = CatFactRepositoryImpl(api, ApiCallExecutor())
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `page와 limit query를 전달하고 응답을 domain model로 변환한다`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "current_page": 2,
                      "last_page": 4,
                      "data": [
                        {"fact": "Cats sleep a lot.", "length": 17}
                      ]
                    }
                    """.trimIndent(),
                ),
        )

        val result = repository.getFacts(page = 2, limit = 10)

        assertEquals(2, result.currentPage)
        assertEquals(4, result.lastPage)
        assertEquals("Cats sleep a lot.", result.items.single().fact)
        assertEquals(17, result.items.single().length)
        assertEquals("/facts?page=2&limit=10", server.takeRequest().path)
    }

    @Test
    fun `random fact 성공 응답을 domain model로 변환한다`() = runTest {
        enqueueJson(200, """{"fact":"A random cat fact.","length":18}""")

        val result = repository.getRandomFact()

        assertInstanceOf(NeveraResult.Success::class.java, result)
        val success = result as NeveraResult.Success
        assertEquals("A random cat fact.", success.data.fact)
        assertEquals(18, success.data.length)
        assertEquals("/fact", server.takeRequest().path)
    }

    @Test
    fun `429 응답은 Retry-After와 함께 rate limited로 변환한다`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setHeader("Retry-After", "30")
                .setBody("""{"message":"Too many requests"}"""),
        )

        val result = repository.getRandomFact()

        val failure = assertFailure(result)
        val rateLimited = assertInstanceOf(RandomCatFactFailure.RateLimited::class.java, failure)
        assertEquals(30L, rateLimited.retryAfterSeconds)
    }

    @Test
    fun `Retry-After가 숫자가 아니면 대기 시간 없이 rate limited로 변환한다`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setHeader("Retry-After", "tomorrow"),
        )

        val rateLimited = assertInstanceOf(
            RandomCatFactFailure.RateLimited::class.java,
            assertFailure(repository.getRandomFact()),
        )
        assertNull(rateLimited.retryAfterSeconds)
    }

    @Test
    fun `4xx와 5xx 응답을 구분한다`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))
        server.enqueue(MockResponse().setResponseCode(503))

        val client = assertInstanceOf(
            RandomCatFactFailure.Client::class.java,
            assertFailure(repository.getRandomFact()),
        )
        val serverFailure = assertInstanceOf(
            RandomCatFactFailure.Server::class.java,
            assertFailure(repository.getRandomFact()),
        )

        assertEquals(404, client.statusCode)
        assertEquals(503, serverFailure.statusCode)
    }

    @Test
    fun `성공 status의 빈 body를 empty body로 변환한다`() = runTest {
        server.enqueue(MockResponse().setResponseCode(204))

        assertInstanceOf(
            RandomCatFactFailure.EmptyBody::class.java,
            assertFailure(repository.getRandomFact()),
        )
    }

    @Test
    fun `잘못된 JSON을 invalid response로 변환한다`() = runTest {
        enqueueJson(200, """{"fact":""")

        assertInstanceOf(
            RandomCatFactFailure.InvalidResponse::class.java,
            assertFailure(repository.getRandomFact()),
        )
    }

    private fun enqueueJson(statusCode: Int, body: String) {
        server.enqueue(
            MockResponse()
                .setResponseCode(statusCode)
                .setHeader("Content-Type", "application/json")
                .setBody(body),
        )
    }

    private fun assertFailure(
        result: NeveraResult<CatFact, RandomCatFactFailure>,
    ): RandomCatFactFailure {
        assertInstanceOf(NeveraResult.Failure::class.java, result)
        return (result as NeveraResult.Failure).error
    }
}
