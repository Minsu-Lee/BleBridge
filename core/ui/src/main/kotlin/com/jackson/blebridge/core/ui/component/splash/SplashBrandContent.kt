package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme

@Composable
internal fun SplashBrandContent(
    isLoading: Boolean,
    title: String,
    subtitle: String,
    logoContentDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SplashLogo(
            isPulsing = isLoading,
            contentDescription = logoContentDescription,
        )

        Spacer(modifier = Modifier.size(SplashTokens.brandContentSpacing))

        Text(
            text = title,
            style = AppTheme.typography.headline,
            color = SplashTokens.title,
        )

        Spacer(modifier = Modifier.size(AppTheme.spacing.sm))

        Text(
            text = subtitle,
            style = AppTheme.typography.monoSmall,
            color = SplashTokens.subtitle,
        )
    }
}

@Preview(
    name = "SplashBrandContent · 로딩 중",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashBrandContentLoadingPreview() {
    AppTheme {
        SplashBrandContent(
            isLoading = true,
            title = "BLE Bridge",
            subtitle = "데이터 전송 샘플앱",
            logoContentDescription = "BLE Bridge 로고",
        )
    }
}

@Preview(
    name = "SplashBrandContent · 정지",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashBrandContentIdlePreview() {
    AppTheme {
        SplashBrandContent(
            isLoading = false,
            title = "BLE Bridge",
            subtitle = "데이터 전송 샘플앱",
            logoContentDescription = "BLE Bridge 로고",
        )
    }
}
