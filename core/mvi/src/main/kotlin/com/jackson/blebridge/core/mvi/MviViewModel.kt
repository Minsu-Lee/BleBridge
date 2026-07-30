package com.jackson.blebridge.core.mvi

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class MviViewModel<
    STATE : MviState,
    SIDE_EFFECT : MviSideEffect,
    INTENT : MviIntent,
    MUTATION : MviMutation,
>(initialState: STATE) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _sideEffect = Channel<SIDE_EFFECT>(Channel.BUFFERED)
    val sideEffect: Flow<SIDE_EFFECT> = _sideEffect.receiveAsFlow()

    abstract fun handleIntent(intent: INTENT)

    protected abstract fun reduce(state: STATE, mutation: MUTATION): STATE

    protected fun intent(block: suspend MviActionScope.() -> Unit) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable -> Timber.e(throwable) },
        ) {
            MviActionScope().block()
        }
    }

    @Composable
    fun collectAsState(): State<STATE> = state.collectAsStateWithLifecycle()

    @SuppressLint("ComposableNaming")
    @Composable
    fun collectSideEffect(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        onEffect: suspend (SIDE_EFFECT) -> Unit,
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val callback by rememberUpdatedState(onEffect)

        LaunchedEffect(sideEffect, lifecycleOwner) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
                sideEffect.collect { callback(it) }
            }
        }
    }

    protected inner class MviActionScope {
        val state: STATE get() = _state.value

        suspend fun applyMutation(mutation: MUTATION) {
            _state.update { reduce(it, mutation) }
        }

        suspend fun postSideEffect(sideEffect: SIDE_EFFECT) {
            _sideEffect.send(sideEffect)
        }
    }
}
