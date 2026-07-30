package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme

/**
 * BLE Bridge의 고정 브랜드 Splash 표현을 완성된 형태로 제공합니다.
 *
 * Feature는 상태와 지역화 문자열만 전달하며 배경, 로고, pulse, 로딩 표시와 버전 배치는
 * 이 컴포넌트가 소유합니다.
 */
@Composable
fun SplashContent(
    isLoading: Boolean,
    versionLabel: String,
    title: String,
    subtitle: String,
    logoContentDescription: String,
    loadingContentDescription: String,
    modifier: Modifier = Modifier,
) {
    val background = SplashTokens.background
    val opaqueGlow = SplashTokens.glow
        .copy(alpha = SplashTokens.glowAlpha)
        .compositeOver(background)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(opaqueGlow, background),
                    radius = SplashTokens.glowRadius,
                ),
            ),
    ) {
        SplashBrandContent(
            isLoading = isLoading,
            title = title,
            subtitle = subtitle,
            logoContentDescription = logoContentDescription,
            modifier = Modifier.align(Alignment.Center),
        )

        SplashFooter(
            isLoading = isLoading,
            versionLabel = versionLabel,
            loadingContentDescription = loadingContentDescription,
        )
    }
}

@Preview(
    name = "SplashContent · 로딩 중",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashContentLoadingPreview() {
    AppTheme {
        SplashContent(
            isLoading = true,
            versionLabel = "v1.0.0 · com.jackson.blebridge",
            title = "BLE Bridge",
            subtitle = "데이터 전송 샘플앱",
            logoContentDescription = "BLE Bridge 로고",
            loadingContentDescription = "앱을 준비하는 중",
        )
    }
}

@Preview(
    name = "SplashContent · 정지",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashContentIdlePreview() {
    AppTheme {
        SplashContent(
            isLoading = false,
            versionLabel = "v1.0.0 · com.jackson.blebridge",
            title = "BLE Bridge",
            subtitle = "데이터 전송 샘플앱",
            logoContentDescription = "BLE Bridge 로고",
            loadingContentDescription = "앱을 준비하는 중",
        )
    }
}
