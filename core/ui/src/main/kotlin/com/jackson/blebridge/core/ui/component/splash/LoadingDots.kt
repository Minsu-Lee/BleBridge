package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
internal fun LoadingDots(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    var activeDotIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            repeat(SplashDefaults.LOADING_DOT_COUNT) { index ->
                activeDotIndex = index
                delay(SplashDefaults.LOADING_DOT_STEP_MILLIS)
            }
        }
    }

    Row(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        horizontalArrangement = Arrangement.spacedBy(SplashTokens.loadingDotSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(SplashDefaults.LOADING_DOT_COUNT) { index ->
            LoadingDot(isActive = index == activeDotIndex)
        }
    }
}

@Preview(
    name = "LoadingDots",
    group = "Splash",
)
@Composable
private fun LoadingDotsPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(SplashTokens.background)
                .padding(16.dp),
        ) {
            LoadingDots(contentDescription = "앱을 준비하는 중")
        }
    }
}
