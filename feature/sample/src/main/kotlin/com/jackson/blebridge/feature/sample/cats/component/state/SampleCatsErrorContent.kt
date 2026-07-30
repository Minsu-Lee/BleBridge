package com.jackson.blebridge.feature.sample.cats.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.feature.sample.R
import com.jackson.blebridge.feature.sample.cats.SampleCatsDefaults

@Composable
internal fun SampleCatsErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(AppTheme.spacing.screen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
    ) {
        Text(
            text = stringResource(R.string.sample_error),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textSecondary,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag(SampleCatsDefaults.RETRY_TAG),
        ) {
            Text(text = stringResource(R.string.sample_retry))
        }
    }
}

@Preview
@Composable
private fun SampleCatsErrorContentPreview() {
    AppTheme {
        SampleCatsErrorContent(onRetry = {})
    }
}
