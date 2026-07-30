package com.jackson.blebridge.feature.sample.cats.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme

@Composable
internal fun SampleCatsAppendLoading(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.xxl),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SampleCatsAppendLoadingPreview() {
    AppTheme {
        SampleCatsAppendLoading()
    }
}
