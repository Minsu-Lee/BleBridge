package com.jackson.blebridge.core.designsystem.theme.semantic.size

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

/** Visual container sizes. Interactive components must still expose a 48dp touch target. */
@Immutable
object ControlSize {
    val compact = 38.dp
    val default = 40.dp
    val comfortable = 42.dp
    val minimumTouchTarget = 48.dp
}
