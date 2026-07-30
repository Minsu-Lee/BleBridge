package com.jackson.blebridge.core.designsystem.theme.semantic.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jackson.blebridge.core.designsystem.theme.primitive.typography.AppFontFamilies

/**
 * 화면의 의미와 정보 위계에 따라 이름 붙인 공개 시맨틱 타이포그래피 토큰입니다.
 *
 * 컴포넌트 토큰과 Defaults는 이 스타일을 조합하고, Feature 화면은 완성된 컴포넌트 API를
 * 우선 사용합니다. 원시 글꼴·크기·행간은 디자인시스템 내부에서만 접근할 수 있습니다.
 */
@Immutable
data class AppTypography(
    val headline: TextStyle,
    val screenTitle: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val monoLarge: TextStyle,
    val monoTitle: TextStyle,
    val monoMedium: TextStyle,
    val monoSmall: TextStyle,
) {
    internal val material: Typography
        get() = Typography(
            headlineMedium = headline,
            titleLarge = titleLarge,
            titleMedium = titleMedium,
            titleSmall = titleSmall,
            bodyLarge = bodyLarge,
            bodyMedium = bodyMedium,
            labelLarge = labelLarge,
            labelMedium = labelMedium,
            labelSmall = labelSmall,
        )
}

internal val DefaultAppTypography = AppTypography(
    headline = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp,
    ),
    screenTitle = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamilies.pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
    monoLarge = TextStyle(
        fontFamily = AppFontFamilies.jetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    ),
    monoTitle = TextStyle(
        fontFamily = AppFontFamilies.jetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    monoMedium = TextStyle(
        fontFamily = AppFontFamilies.jetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    monoSmall = TextStyle(
        fontFamily = AppFontFamilies.jetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
    ),
)
