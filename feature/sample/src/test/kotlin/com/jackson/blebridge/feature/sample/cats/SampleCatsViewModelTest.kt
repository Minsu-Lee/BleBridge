package com.jackson.blebridge.feature.sample.cats

import com.jackson.blebridge.core.common.NeveraResult
import androidx.paging.testing.asSnapshot
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.CatFactPage
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import com.jackson.blebridge.domain.sample.usecase.GetCatFactsPageUseCase
import com.jackson.blebridge.feature.sample.MainDispatcherExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SampleCatsViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `스크롤하면 다음 페이지를 이어서 노출한다`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            val repository = object : CatFactRepository {
                override suspend fun getRandomFact(): NeveraResult<CatFact, RandomCatFactFailure> =
                    error("Not used in paging view model tests")

                override suspend fun getFacts(page: Int, limit: Int): CatFactPage =
                    CatFactPage(
                        items = List(limit) { index ->
                            val number = (page - 1) * limit + index
                            CatFact("fact-$number", number)
                        },
                        currentPage = page,
                        lastPage = 2,
                    )
            }
            val viewModel = SampleCatsViewModel(GetCatFactsPageUseCase(repository))

            val snapshot = viewModel.pagingFlow.asSnapshot {
                scrollTo(SampleCatsDefaults.PAGE_SIZE)
            }

            assertEquals(SampleCatsDefaults.PAGE_SIZE * 2, snapshot.size)
            assertEquals("fact-0", snapshot.first().fact)
            assertEquals("fact-19", snapshot.last().fact)
        }
}
