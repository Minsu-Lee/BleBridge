package com.jackson.blebridge.feature.main

import com.jackson.blebridge.core.mvi.MviViewModel
import com.jackson.blebridge.feature.main.model.MainIntent
import com.jackson.blebridge.feature.main.model.MainMutation
import com.jackson.blebridge.feature.main.model.MainSideEffect
import com.jackson.blebridge.feature.main.model.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    MviViewModel<MainUiState, MainSideEffect, MainIntent, MainMutation>(MainUiState) {

    override fun handleIntent(intent: MainIntent) {
        // MainIntent 케이스가 추가되면 when (intent) { ... }으로 분기합니다.
    }

    override fun reduce(state: MainUiState, mutation: MainMutation): MainUiState {
        // MainMutation 케이스가 추가되면 when (mutation) { ... }으로 분기합니다.
        return state
    }
}
