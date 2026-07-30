package com.jackson.blebridge.feature.splash.main

import app.cash.turbine.test
import com.jackson.blebridge.domain.provider.AppInfoProvider
import com.jackson.blebridge.feature.splash.main.model.SplashIntent
import com.jackson.blebridge.feature.splash.main.model.SplashSideEffect
import com.jackson.blebridge.feature.splash.main.model.SplashUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SplashViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val testDispatcher get() = mainDispatcherExtension.testDispatcher

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `초기화 시간이 지나면 준비 상태와 화면 이동 이벤트를 발생시킨다`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel(FakeAppInfoProvider)

        viewModel.sideEffect.test {
            viewModel.handleIntent(SplashIntent.Initialize)
            advanceTimeBy(SplashDefaults.DURATION_MILLIS)
            runCurrent()

            assertEquals(SplashSideEffect.NavigateToMain, awaitItem())
        }

        assertEquals(
            SplashUiState(isLoading = false, versionLabel = EXPECTED_VERSION_LABEL),
            viewModel.state.value,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `초기화 요청이 반복되어도 화면 이동은 한 번만 발생한다`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel(FakeAppInfoProvider)

        viewModel.sideEffect.test {
            viewModel.handleIntent(SplashIntent.Initialize)
            viewModel.handleIntent(SplashIntent.Initialize)
            advanceTimeBy(SplashDefaults.DURATION_MILLIS)
            runCurrent()

            assertEquals(SplashSideEffect.NavigateToMain, awaitItem())
        }

        assertEquals(
            SplashUiState(isLoading = false, versionLabel = EXPECTED_VERSION_LABEL),
            viewModel.state.value,
        )
    }

    private data object FakeAppInfoProvider : AppInfoProvider {
        override val versionName = "1.0.0"
        override val packageName = "com.jackson.blebridge"
    }

    private companion object {
        const val EXPECTED_VERSION_LABEL = "v1.0.0 · com.jackson.blebridge"
    }
}
