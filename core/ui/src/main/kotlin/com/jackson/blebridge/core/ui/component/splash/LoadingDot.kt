package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme

@Composable
internal fun LoadingDot(isActive: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else SplashDefaults.LOADING_DOT_INACTIVE_ALPHA,
        animationSpec = tween(durationMillis = SplashDefaults.LOADING_DOT_FADE_MILLIS),
        label = "loading dot alpha",
    )

    Box(
        modifier = Modifier
            .size(SplashTokens.loadingDotSize)
            .alpha(alpha)
            .background(SplashTokens.glow, CircleShape),
    )
}

@Preview(
    name = "LoadingDot · Active",
    group = "Splash",
)
@Composable
private fun LoadingDotActivePreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(SplashTokens.background)
                .padding(16.dp),
        ) {
            LoadingDot(isActive = true)
        }
    }
}

@Preview(
    name = "LoadingDot · Inactive",
    group = "Splash",
)
@Composable
private fun LoadingDotInactivePreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(SplashTokens.background)
                .padding(16.dp),
        ) {
            LoadingDot(isActive = false)
        }
    }
}
