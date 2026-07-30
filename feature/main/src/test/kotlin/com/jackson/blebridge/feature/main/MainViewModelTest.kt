package com.jackson.blebridge.feature.main

import com.jackson.blebridge.feature.main.model.MainUiState
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class MainViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `초기 상태는 MainUiState이다`() = runTest(mainDispatcherExtension.testDispatcher) {
        val viewModel = MainViewModel()

        assertEquals(MainUiState, viewModel.state.value)
    }
}
