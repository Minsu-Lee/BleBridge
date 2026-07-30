package com.jackson.blebridge.feature.sample.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.ui.LoadingContent
import com.jackson.blebridge.core.ui.component.CatFactItem
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.feature.sample.R
import com.jackson.blebridge.feature.sample.main.component.state.SampleErrorContent
import com.jackson.blebridge.feature.sample.main.model.SampleIntent
import com.jackson.blebridge.feature.sample.main.model.SampleSideEffect
import com.jackson.blebridge.feature.sample.main.model.SampleUiState

@Composable
fun SampleRoute(
    onNavigateToCats: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SampleViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(SampleIntent.Initialize)
    }
    viewModel.collectSideEffect { effect ->
        when (effect) {
            SampleSideEffect.NavigateToCats -> onNavigateToCats()
        }
    }

    SampleScreen(
        state = state,
        onRetry = { viewModel.handleIntent(SampleIntent.RetryClicked) },
        onNavigateToCats = { viewModel.handleIntent(SampleIntent.CatsClicked) },
        modifier = modifier,
    )
}

@Composable
internal fun SampleScreen(
    state: SampleUiState,
    onRetry: () -> Unit,
    onNavigateToCats: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(AppTheme.spacing.screen)
            .testTag(SampleDefaults.CONTENT_TAG),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
    ) {
        Text(
            text = stringResource(R.string.sample_title),
            style = AppTheme.typography.screenTitle,
            color = AppTheme.colors.textPrimary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.fact != null -> CatFactItem(
                    fact = state.fact.fact,
                    length = state.fact.length,
                )
                state.error != null -> SampleErrorContent(
                    error = state.error,
                    onRetry = onRetry,
                )
            }
        }
        Button(
            onClick = onNavigateToCats,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SampleDefaults.OPEN_CATS_TAG),
        ) {
            Text(text = stringResource(R.string.sample_open_cats))
        }
    }
}

@Preview
@Composable
private fun SampleScreenPreview() {
    AppTheme {
        SampleScreen(
            state = SampleUiState(
                isLoading = false,
                fact = CatFact(
                    fact = "Cats have five toes on their front paws.",
                    length = 42,
                ),
            ),
            onRetry = {},
            onNavigateToCats = {},
        )
    }
}
