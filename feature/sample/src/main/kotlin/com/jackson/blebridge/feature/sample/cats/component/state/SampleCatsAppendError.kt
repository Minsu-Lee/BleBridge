package com.jackson.blebridge.feature.sample.cats.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.feature.sample.R
import com.jackson.blebridge.feature.sample.cats.SampleCatsDefaults

@Composable
internal fun SampleCatsAppendError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.xxl),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag(SampleCatsDefaults.APPEND_RETRY_TAG),
        ) {
            Text(text = stringResource(R.string.sample_retry))
        }
    }
}

@Preview
@Composable
private fun SampleCatsAppendErrorPreview() {
    AppTheme {
        SampleCatsAppendError(onRetry = {})
    }
}
