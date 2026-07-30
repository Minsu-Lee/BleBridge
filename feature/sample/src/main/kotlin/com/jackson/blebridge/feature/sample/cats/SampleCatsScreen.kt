package com.jackson.blebridge.feature.sample.cats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.ui.LoadingContent
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.feature.sample.R
import com.jackson.blebridge.core.ui.component.CatFactItem
import com.jackson.blebridge.feature.sample.cats.component.state.SampleCatsAppendError
import com.jackson.blebridge.feature.sample.cats.component.state.SampleCatsAppendLoading
import com.jackson.blebridge.feature.sample.cats.component.state.SampleCatsErrorContent
import kotlinx.coroutines.flow.flowOf

@Composable
fun SampleCatsRoute(
    modifier: Modifier = Modifier,
    viewModel: SampleCatsViewModel = hiltViewModel(),
) {
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    SampleCatsScreen(
        pagingItems = pagingItems,
        modifier = modifier,
    )
}

@Composable
internal fun SampleCatsScreen(
    pagingItems: LazyPagingItems<CatFact>,
    modifier: Modifier = Modifier,
) {
    val refreshState = pagingItems.loadState.refresh
    val isInitialError = pagingItems.itemCount == 0 && refreshState is LoadState.Error
    val isEmpty = pagingItems.itemCount == 0 && refreshState is LoadState.NotLoading

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .testTag(SampleCatsDefaults.CONTENT_TAG),
    ) {
        Text(
            text = stringResource(R.string.sample_cats_title),
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.screen,
                vertical = AppTheme.spacing.xxl,
            ),
            style = AppTheme.typography.screenTitle,
            color = AppTheme.colors.textPrimary,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isInitialError -> SampleCatsErrorContent(
                    onRetry = pagingItems::retry,
                    modifier = Modifier.align(Alignment.Center),
                )

                isEmpty -> Text(
                    text = stringResource(R.string.sample_empty),
                    modifier = Modifier.align(Alignment.Center),
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.textSecondary,
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(SampleCatsDefaults.FACT_LIST_TAG),
                ) {
                    items(count = pagingItems.itemCount) { index ->
                        val item = pagingItems[index] ?: return@items
                        CatFactItem(
                            fact = item.fact,
                            length = item.length,
                        )
                        if (index < pagingItems.itemCount - 1) {
                            HorizontalDivider(color = AppTheme.colors.borderSubtle)
                        }
                    }

                    when (pagingItems.loadState.append) {
                        is LoadState.Loading -> item { SampleCatsAppendLoading() }
                        is LoadState.Error -> item {
                            SampleCatsAppendError(onRetry = pagingItems::retry)
                        }
                        is LoadState.NotLoading -> Unit
                    }
                }
            }

            if (refreshState is LoadState.Loading) {
                LoadingContent()
            }
        }
    }
}

@Preview
@Composable
private fun SampleCatsScreenPreview() {
    val pagingItems = flowOf(
        PagingData.from(
            listOf(
                CatFact(
                    fact = "Cats sleep for around 13 to 16 hours a day.",
                    length = 48,
                ),
                CatFact(
                    fact = "A group of cats is called a clowder.",
                    length = 39,
                ),
            ),
        ),
    ).collectAsLazyPagingItems()

    AppTheme {
        SampleCatsScreen(pagingItems = pagingItems)
    }
}
