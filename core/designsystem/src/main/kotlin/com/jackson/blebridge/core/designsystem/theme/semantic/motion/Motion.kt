package com.jackson.blebridge.core.designsystem.theme.semantic.motion

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Immutable

@Immutable
object Motion {
    const val progressRotationMillis = 1_000
    const val waitingRotationMillis = 1_400
    const val clientPulseMillis = 2_000
    const val brandPulseMillis = 2_400
    const val activityPulseStartScale = 0.6f
    const val activityPulseEndScale = 2.6f
    const val activityPulseAlpha = 0.15f

    val linear: Easing = LinearEasing
    val pulse: Easing = EaseOut
}
