package com.jackson.blebridge.feature.splash.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.core.view.WindowCompat
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.ui.component.splash.SplashContent
import com.jackson.blebridge.core.ui.util.findActivity
import com.jackson.blebridge.feature.splash.R
import com.jackson.blebridge.feature.splash.main.model.SplashIntent
import com.jackson.blebridge.feature.splash.main.model.SplashSideEffect
import com.jackson.blebridge.feature.splash.main.model.SplashUiState

@Composable
fun SplashRoute(
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    SplashSystemBarsEffect()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(SplashIntent.Initialize)
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SplashSideEffect.NavigateToMain -> onNavigateToMain()
        }
    }

    SplashScreen(
        state = state,
    )
}

@Composable
internal fun SplashScreen(
    state: SplashUiState,
    modifier: Modifier = Modifier,
) {
    SplashContent(
        isLoading = state.isLoading,
        versionLabel = state.versionLabel,
        title = stringResource(R.string.splash_title),
        subtitle = stringResource(R.string.splash_subtitle),
        logoContentDescription = stringResource(R.string.splash_logo_description),
        loadingContentDescription = stringResource(R.string.splash_loading_description),
        modifier = modifier.testTag(SplashDefaults.SPLASH_CONTENT_TAG),
    )
}

/**
 * 스플래시가 떠 있는 동안 상태바/내비게이션바 아이콘을 밝은색(라이트 아이콘)으로 전환하고,
 * 화면을 벗어날 때 원래 값으로 복원한다. 다른 화면의 시스템 바 설정에 영향을 주지 않기 위해
 * 변경 전 값을 저장해뒀다가 onDispose에서 되돌리는 방식. 프리뷰(edit mode)에는 실제 Window가
 * 없어 findActivity()가 null을 반환하므로 아무 것도 하지 않고 빠져나온다.
 */
@Composable
private fun SplashSystemBarsEffect() {
    val view = LocalView.current

    DisposableEffect(view) {
        if (view.isInEditMode) {
            return@DisposableEffect onDispose {}
        }

        val window = view.context.findActivity()?.window
            ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(window, view)
        val wasLightStatusBars = controller.isAppearanceLightStatusBars
        val wasLightNavigationBars = controller.isAppearanceLightNavigationBars

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        onDispose {
            controller.isAppearanceLightStatusBars = wasLightStatusBars
            controller.isAppearanceLightNavigationBars = wasLightNavigationBars
        }
    }
}

@Preview(
    name = "Splash · 고정 다크",
    group = "Splash",
    device = Devices.PIXEL_7,
)
@Composable
private fun SplashLoadingPreview() {
    AppTheme {
        SplashScreen(
            state = SplashUiState(
                isLoading = true,
                versionLabel = "v1.0.0 · com.jackson.blebridge",
            ),
        )
    }
}
