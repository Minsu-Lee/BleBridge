package com.jackson.blebridge.feature.sample.cats

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.jackson.blebridge.core.mvi.MviViewModel
import com.jackson.blebridge.domain.sample.usecase.GetCatFactsPageUseCase
import com.jackson.blebridge.feature.sample.cats.model.SampleCatsIntent
import com.jackson.blebridge.feature.sample.cats.model.SampleCatsMutation
import com.jackson.blebridge.feature.sample.cats.model.SampleCatsSideEffect
import com.jackson.blebridge.feature.sample.cats.model.SampleCatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SampleCatsViewModel @Inject constructor(
    getCatFactsPage: GetCatFactsPageUseCase,
) : MviViewModel<SampleCatsUiState, SampleCatsSideEffect, SampleCatsIntent, SampleCatsMutation>(
    SampleCatsUiState,
) {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = SampleCatsDefaults.PAGE_SIZE,
            initialLoadSize = SampleCatsDefaults.PAGE_SIZE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { CatFactPagingSource(getCatFactsPage) },
    ).flow.cachedIn(viewModelScope)

    override fun handleIntent(intent: SampleCatsIntent) = Unit

    override fun reduce(
        state: SampleCatsUiState,
        mutation: SampleCatsMutation,
    ): SampleCatsUiState = state
}
