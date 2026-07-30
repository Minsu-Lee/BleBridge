package com.jackson.blebridge.core.ui.component.splash

/** Splash 컴포넌트 내부 애니메이션과 반복 규칙입니다. */
internal object SplashDefaults {
    const val BRAND_ANIMATION_DURATION_MILLIS = 2_400L
    const val PULSE_ITERATIONS = 2
    const val PULSE_DURATION_MILLIS =
        (BRAND_ANIMATION_DURATION_MILLIS / PULSE_ITERATIONS).toInt()

    const val LOADING_DOT_COUNT = 3
    const val LOADING_DOT_STEP_MILLIS = 300L
    const val LOADING_DOT_FADE_MILLIS = 180
    const val LOADING_DOT_INACTIVE_ALPHA = 0.22f
}
