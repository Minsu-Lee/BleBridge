package com.jackson.blebridge.core.designsystem.theme.component.media

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.jackson.blebridge.core.designsystem.theme.primitive.color.ColorPalette

/** 앱 Light/Dark 테마와 무관한 이미지·영상 뷰어 컴포넌트 토큰입니다. */
@Immutable
object MediaTokens {
    val chromeBackground: Color = ColorPalette.mediaChrome
    val content: Color = ColorPalette.white
    val mutedContent: Color = ColorPalette.mediaMuted
    val controlTrack: Color = ColorPalette.mediaTrack
    val controlProgress: Color = ColorPalette.serverDark
    val scrim: Color = ColorPalette.scrim
}
