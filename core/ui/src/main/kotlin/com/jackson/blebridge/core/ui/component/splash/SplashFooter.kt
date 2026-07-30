package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme

@Composable
internal fun BoxScope.SplashFooter(
    isLoading: Boolean,
    versionLabel: String,
    loadingContentDescription: String,
) {
    if (isLoading) {
        LoadingDots(
            contentDescription = loadingContentDescription,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationY = -SplashTokens.loadingBottomOffset.toPx()
                },
        )
    }

    Text(
        text = versionLabel,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .graphicsLayer {
                translationY = -SplashTokens.versionBottomOffset.toPx()
            },
        style = AppTheme.typography.monoSmall,
        color = SplashTokens.version,
    )
}

@Preview(
    name = "SplashFooter · 로딩 중",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashFooterLoadingPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SplashFooter(
                isLoading = true,
                versionLabel = "v1.0.0 · com.jackson.blebridge",
                loadingContentDescription = "앱을 준비하는 중",
            )
        }
    }
}

@Preview(
    name = "SplashFooter · 정지",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashFooterIdlePreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SplashFooter(
                isLoading = false,
                versionLabel = "v1.0.0 · com.jackson.blebridge",
                loadingContentDescription = "앱을 준비하는 중",
            )
        }
    }
}
