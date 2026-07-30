package com.jackson.blebridge.feature.sample.cats

import com.jackson.blebridge.core.common.NeveraResult
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.CatFactPage
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import com.jackson.blebridge.domain.sample.usecase.GetCatFactsPageUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CatFactPagingSourceTest {
    @Test
    fun `첫 페이지는 prevKey 없이 다음 페이지를 반환한다`() = runTest {
        val source = pagingSource(
            FakeCatFactRepository(
                pages = mapOf(1 to page(current = 1, last = 3)),
            ),
        )

        val result = source.load(refreshParams())

        val loaded = assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        assertNull(loaded.prevKey)
        assertEquals(2, loaded.nextKey)
        assertEquals(1, loaded.data.size)
    }

    @Test
    fun `마지막 페이지는 nextKey를 반환하지 않는다`() = runTest {
        val source = pagingSource(
            FakeCatFactRepository(
                pages = mapOf(3 to page(current = 3, last = 3)),
            ),
        )

        val result = source.load(appendParams(key = 3))

        val loaded = assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        assertEquals(2, loaded.prevKey)
        assertNull(loaded.nextKey)
    }

    @Test
    fun `빈 페이지는 lastPage 이전이어도 페이징을 종료한다`() = runTest {
        val source = pagingSource(
            FakeCatFactRepository(
                pages = mapOf(
                    2 to CatFactPage(
                        items = emptyList(),
                        currentPage = 2,
                        lastPage = 3,
                    ),
                ),
            ),
        )

        val result = source.load(appendParams(key = 2))

        val loaded = assertInstanceOf(PagingSource.LoadResult.Page::class.java, result)
        assertNull(loaded.nextKey)
    }

    @Test
    fun `repository 예외를 LoadResult Error로 변환한다`() = runTest {
        val expected = IllegalStateException("network failed")
        val source = pagingSource(FakeCatFactRepository(error = expected))

        val result = source.load(refreshParams())

        val error = assertInstanceOf(PagingSource.LoadResult.Error::class.java, result)
        assertEquals(expected, error.throwable)
    }

    @Test
    fun `anchor 주변 페이지에서 refresh key를 계산한다`() {
        val source = pagingSource(FakeCatFactRepository())
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(CatFact("fact", 4)),
                    prevKey = 1,
                    nextKey = 3,
                ),
            ),
            anchorPosition = 0,
            config = androidx.paging.PagingConfig(pageSize = SampleCatsDefaults.PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

        assertEquals(2, source.getRefreshKey(state))
    }

    private fun pagingSource(repository: CatFactRepository): CatFactPagingSource =
        CatFactPagingSource(GetCatFactsPageUseCase(repository))

    private fun page(current: Int, last: Int): CatFactPage =
        CatFactPage(
            items = listOf(CatFact("fact-$current", current)),
            currentPage = current,
            lastPage = last,
        )

    private fun refreshParams(): PagingSource.LoadParams.Refresh<Int> =
        PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = SampleCatsDefaults.PAGE_SIZE,
            placeholdersEnabled = false,
        )

    private fun appendParams(key: Int): PagingSource.LoadParams.Append<Int> =
        PagingSource.LoadParams.Append(
            key = key,
            loadSize = SampleCatsDefaults.PAGE_SIZE,
            placeholdersEnabled = false,
        )
}

private class FakeCatFactRepository(
    private val pages: Map<Int, CatFactPage> = emptyMap(),
    private val error: Throwable? = null,
) : CatFactRepository {
    override suspend fun getRandomFact(): NeveraResult<CatFact, RandomCatFactFailure> =
        error("Not used in paging source tests")

    override suspend fun getFacts(page: Int, limit: Int): CatFactPage {
        error?.let { throw it }
        return requireNotNull(pages[page])
    }
}
