package com.jackson.blebridge.feature.splash.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.feature.splash.main.model.SplashUiState
import org.junit.Rule
import org.junit.Test

class SplashScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `로딩 상태는 브랜드 콘텐츠와 진행 표시를 노출한다`() {
        composeRule.setContent {
            AppTheme(darkTheme = true) {
                SplashScreen(
                    state = SplashUiState(
                        versionLabel = "v1.0.0 · com.jackson.blebridge",
                    ),
                )
            }
        }

        composeRule.onNodeWithText("BLE Bridge").assertIsDisplayed()
        composeRule.onNodeWithText("데이터 전송 샘플앱").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("BLE Bridge 로고").assertIsDisplayed()
        composeRule.onNodeWithText("v1.0.0 · com.jackson.blebridge").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("앱을 준비하는 중").assertIsDisplayed()
    }
}
