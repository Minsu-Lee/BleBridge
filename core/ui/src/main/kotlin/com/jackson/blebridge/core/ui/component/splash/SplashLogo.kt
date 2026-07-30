package com.jackson.blebridge.core.ui.component.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun SplashLogo(
    isPulsing: Boolean,
    contentDescription: String,
) {
    val pulseScale = remember { Animatable(1f) }
    val pulseAlpha = remember { Animatable(0f) }

    LaunchedEffect(isPulsing) {
        if (!isPulsing) {
            pulseScale.snapTo(1f)
            pulseAlpha.snapTo(0f)
            return@LaunchedEffect
        }

        repeat(SplashDefaults.PULSE_ITERATIONS) {
            pulseScale.snapTo(1f)
            pulseAlpha.snapTo(SplashTokens.pulseStartAlpha)
            coroutineScope {
                launch {
                    pulseScale.animateTo(
                        targetValue = SplashTokens.pulseEndScale,
                        animationSpec = tween(
                            durationMillis = SplashDefaults.PULSE_DURATION_MILLIS,
                            easing = AppTheme.motion.pulse,
                        ),
                    )
                }
                launch {
                    pulseAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = SplashDefaults.PULSE_DURATION_MILLIS,
                            easing = AppTheme.motion.pulse,
                        ),
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.size(SplashTokens.logoContainerSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale.value)
                .alpha(pulseAlpha.value)
                .background(
                    color = SplashTokens.logoPulse,
                    shape = RoundedCornerShape(SplashTokens.logoCornerRadius),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = AppTheme.gradients.brand,
                    shape = RoundedCornerShape(SplashTokens.logoCornerRadius),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AppTheme.icons.Logo,
                contentDescription = contentDescription,
                modifier = Modifier.size(SplashTokens.logoIconSize),
                tint = SplashTokens.logoIcon,
            )
        }
    }
}

@Preview(
    name = "SplashLogo · 로딩 중",
    group = "Splash",
)
@Composable
private fun SplashLogoLoadingPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(SplashTokens.background)
                .padding(16.dp),
        ) {
            SplashLogo(
                isPulsing = true,
                contentDescription = "BLE Bridge 로고",
            )
        }
    }
}

@Preview(
    name = "SplashLogo · 정지",
    group = "Splash",
)
@Composable
private fun SplashLogoIdlePreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(SplashTokens.background)
                .padding(16.dp),
        ) {
            SplashLogo(
                isPulsing = false,
                contentDescription = "BLE Bridge 로고",
            )
        }
    }
}
