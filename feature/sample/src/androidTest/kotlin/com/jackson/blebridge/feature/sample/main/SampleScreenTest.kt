package com.jackson.blebridge.feature.sample.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.feature.sample.main.model.SampleError
import com.jackson.blebridge.feature.sample.main.model.SampleUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SampleScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun random_fact와_목록_이동_버튼을_표시한다() {
        var navigateClicked = false
        composeRule.setContent {
            AppTheme {
                SampleScreen(
                    state = SampleUiState(
                        isLoading = false,
                        fact = CatFact("Cats can jump high.", 19),
                    ),
                    onRetry = {},
                    onNavigateToCats = { navigateClicked = true },
                )
            }
        }

        composeRule.onNodeWithText("19").assertIsDisplayed()
        composeRule.onNodeWithText("Cats can jump high.").assertIsDisplayed()
        composeRule.onNodeWithTag(SampleDefaults.OPEN_CATS_TAG).performClick()
        assertTrue(navigateClicked)
    }

    @Test
    fun 오류에서_retry를_호출한다() {
        var retryClicked = false
        composeRule.setContent {
            AppTheme {
                SampleScreen(
                    state = SampleUiState(
                        isLoading = false,
                        error = SampleError.Server(503),
                    ),
                    onRetry = { retryClicked = true },
                    onNavigateToCats = {},
                )
            }
        }

        composeRule.onNodeWithTag(SampleDefaults.RETRY_TAG).performClick()
        assertTrue(retryClicked)
    }
}
