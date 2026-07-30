package com.jackson.blebridge.feature.sample.cats

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.domain.sample.model.CatFact
import org.junit.Rule
import org.junit.Test

class SampleCatsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun length와_fact를_목록에_표시한다() {
        composeRule.setContent {
            val items = Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = {
                    StaticPagingSource(
                        listOf(CatFact("Cats sleep a lot.", 17)),
                    )
                },
            ).flow.collectAsLazyPagingItems()
            AppTheme {
                SampleCatsScreen(pagingItems = items)
            }
        }

        composeRule.onNodeWithText("17").assertIsDisplayed()
        composeRule.onNodeWithText("Cats sleep a lot.").assertIsDisplayed()
    }

    @Test
    fun 첫_요청_실패_후_retry하면_목록을_표시한다() {
        val source = RetryPagingSource()
        composeRule.setContent {
            val items = Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = { source },
            ).flow.collectAsLazyPagingItems()
            AppTheme {
                SampleCatsScreen(pagingItems = items)
            }
        }

        composeRule.waitUntil {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasTestTag(SampleCatsDefaults.RETRY_TAG),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag(SampleCatsDefaults.RETRY_TAG).performClick()
        composeRule.waitUntil {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasText("Retry succeeded."),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Retry succeeded.").assertIsDisplayed()
    }
}

private class StaticPagingSource(
    private val items: List<CatFact>,
) : PagingSource<Int, CatFact>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatFact> =
        LoadResult.Page(data = items, prevKey = null, nextKey = null)

    override fun getRefreshKey(state: PagingState<Int, CatFact>): Int? = null
}

private class RetryPagingSource : PagingSource<Int, CatFact>() {
    private var attempts = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatFact> {
        attempts += 1
        return if (attempts == 1) {
            LoadResult.Error(IllegalStateException("failed"))
        } else {
            LoadResult.Page(
                data = listOf(CatFact("Retry succeeded.", 16)),
                prevKey = null,
                nextKey = null,
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CatFact>): Int? = null
}
