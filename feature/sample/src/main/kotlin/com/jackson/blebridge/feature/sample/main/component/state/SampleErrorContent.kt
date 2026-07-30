package com.jackson.blebridge.feature.sample.main.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.jackson.blebridge.feature.sample.main.SampleDefaults
import com.jackson.blebridge.feature.sample.main.model.SampleError

@Composable
internal fun SampleErrorContent(
    error: SampleError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
    ) {
        Text(
            text = error.message(),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textSecondary,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag(SampleDefaults.RETRY_TAG),
        ) {
            Text(text = stringResource(R.string.sample_retry))
        }
    }
}

@Composable
private fun SampleError.message(): String =
    when (this) {
        SampleError.Network -> stringResource(R.string.sample_error_network)
        SampleError.Timeout -> stringResource(R.string.sample_error_timeout)
        is SampleError.Client -> stringResource(R.string.sample_error_client, statusCode)
        is SampleError.RateLimited -> stringResource(R.string.sample_error_rate_limited)
        is SampleError.Server -> stringResource(R.string.sample_error_server, statusCode)
        SampleError.EmptyBody -> stringResource(R.string.sample_error_empty_body)
        SampleError.InvalidResponse -> stringResource(R.string.sample_error_invalid_response)
        is SampleError.UnexpectedStatus -> {
            stringResource(R.string.sample_error_unexpected_status, statusCode)
        }
        SampleError.Unknown -> stringResource(R.string.sample_error_unknown)
    }

@Preview
@Composable
private fun SampleErrorContentPreview() {
    AppTheme {
        SampleErrorContent(
            error = SampleError.Server(statusCode = 500),
            onRetry = {},
        )
    }
}
