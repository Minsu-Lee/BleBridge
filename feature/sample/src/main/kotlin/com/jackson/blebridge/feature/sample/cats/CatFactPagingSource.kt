package com.jackson.blebridge.feature.sample.cats

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.usecase.GetCatFactsPageUseCase
import kotlinx.coroutines.CancellationException

internal class CatFactPagingSource(
    private val getCatFactsPage: GetCatFactsPageUseCase,
) : PagingSource<Int, CatFact>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatFact> {
        val page = params.key ?: SampleCatsDefaults.INITIAL_PAGE
        return try {
            val response = getCatFactsPage(
                page = page,
                limit = SampleCatsDefaults.PAGE_SIZE,
            )
            LoadResult.Page(
                data = response.items,
                prevKey = if (response.currentPage > SampleCatsDefaults.INITIAL_PAGE) {
                    response.currentPage - 1
                } else {
                    null
                },
                nextKey = if (
                    response.items.isNotEmpty() &&
                    response.currentPage < response.lastPage
                ) {
                    response.currentPage + 1
                } else {
                    null
                },
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CatFact>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }
}
