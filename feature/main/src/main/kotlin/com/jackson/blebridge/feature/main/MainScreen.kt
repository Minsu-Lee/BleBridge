package com.jackson.blebridge.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.feature.main.model.MainUiState

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    MainScreen(
        state = state,
        modifier = modifier,
    )
}

@Composable
internal fun MainScreen(
    state: MainUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(MainDefaults.MAIN_CONTENT_TAG),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.main_title),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.textPrimary,
        )
    }
}
