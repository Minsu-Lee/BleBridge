package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * [SplashContent]와 내부 하위 컴포넌트만 사용하는 Splash 전용 Component token입니다.
 *
 * 앱의 Light/Dark 테마와 무관한 고정 브랜드 표현이므로 외부에 공개하지 않습니다.
 */
@Immutable
internal object SplashTokens {
    val background = Color(0xFF0D1017)
    val glow = Color(0xFF2563EB)
    val logoPulse = Color(0xFF2563EB)
    val logoIcon = Color.White
    val title = Color(0xFFE6EAF2)
    val subtitle = Color(0xFF667085)
    val version = Color(0xFF4A5361)

    val logoContainerSize = 104.dp
    val logoIconSize = 58.dp
    val logoCornerRadius = 30.dp
    val brandContentSpacing = 26.dp
    val loadingDotSize = 7.dp
    val loadingDotSpacing = 7.dp
    val loadingBottomOffset = 64.dp
    val versionBottomOffset = 34.dp

    const val glowAlpha = 0.22f
    const val pulseStartAlpha = 0.35f
    const val pulseEndScale = 3f
    const val glowRadius = 900f
}
