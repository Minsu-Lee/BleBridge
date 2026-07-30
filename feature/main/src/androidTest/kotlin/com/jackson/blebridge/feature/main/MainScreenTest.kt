package com.jackson.blebridge.feature.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.feature.main.model.MainUiState
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mainScreenIsDisplayed() {
        composeRule.setContent {
            AppTheme {
                MainScreen(state = MainUiState)
            }
        }

        composeRule.onNodeWithTag(MainDefaults.MAIN_CONTENT_TAG).assertIsDisplayed()
    }
}
