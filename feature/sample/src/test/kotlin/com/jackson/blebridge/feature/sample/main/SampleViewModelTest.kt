package com.jackson.blebridge.feature.sample.main

import com.jackson.blebridge.core.common.NeveraResult
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.CatFactPage
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import com.jackson.blebridge.domain.sample.usecase.GetRandomCatFactUseCase
import com.jackson.blebridge.feature.sample.MainDispatcherExtension
import com.jackson.blebridge.feature.sample.main.model.SampleError
import com.jackson.blebridge.feature.sample.main.model.SampleIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SampleViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `initialize하면 random fact를 한 번 조회한다`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val repository = FakeRandomCatFactRepository(
                NeveraResult.Success(CatFact("Random fact", 11)),
            )
            val viewModel = SampleViewModel(GetRandomCatFactUseCase(repository))

            viewModel.handleIntent(SampleIntent.Initialize)
            viewModel.handleIntent(SampleIntent.Initialize)
            advanceUntilIdle()

            assertEquals(1, repository.requestCount)
            assertFalse(viewModel.state.value.isLoading)
            assertEquals("Random fact", viewModel.state.value.fact?.fact)
            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `실패를 UI 오류로 변환하고 retry하면 다시 조회한다`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val repository = FakeRandomCatFactRepository(
                NeveraResult.Failure(RandomCatFactFailure.Server(503)),
                NeveraResult.Success(CatFact("Recovered", 9)),
            )
            val viewModel = SampleViewModel(GetRandomCatFactUseCase(repository))

            viewModel.handleIntent(SampleIntent.Initialize)
            advanceUntilIdle()
            assertEquals(SampleError.Server(503), viewModel.state.value.error)

            viewModel.handleIntent(SampleIntent.RetryClicked)
            advanceUntilIdle()

            assertEquals(2, repository.requestCount)
            assertEquals("Recovered", viewModel.state.value.fact?.fact)
            assertNull(viewModel.state.value.error)
        }
}

private class FakeRandomCatFactRepository(
    vararg responses: NeveraResult<CatFact, RandomCatFactFailure>,
) : CatFactRepository {
    private val results = ArrayDeque(responses.toList())
    var requestCount: Int = 0
        private set

    override suspend fun getRandomFact(): NeveraResult<CatFact, RandomCatFactFailure> {
        requestCount += 1
        return results.removeFirst()
    }

    override suspend fun getFacts(page: Int, limit: Int): CatFactPage =
        error("Not used in random fact view model tests")
}
