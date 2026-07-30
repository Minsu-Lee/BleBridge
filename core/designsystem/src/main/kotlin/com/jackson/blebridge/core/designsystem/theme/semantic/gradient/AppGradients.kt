package com.jackson.blebridge.core.designsystem.theme.semantic.gradient

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import com.jackson.blebridge.core.designsystem.theme.primitive.color.ColorPalette

@Immutable
object AppGradients {
    val brand = Brush.linearGradient(
        colors = listOf(
            ColorPalette.server,
            ColorPalette.serverStrong,
        ),
    )
}
