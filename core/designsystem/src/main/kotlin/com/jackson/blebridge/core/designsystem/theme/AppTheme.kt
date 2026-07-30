package com.jackson.blebridge.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.jackson.blebridge.core.designsystem.icon.AppIcons
import com.jackson.blebridge.core.designsystem.theme.component.media.MediaTokens
import com.jackson.blebridge.core.designsystem.theme.contextual.activity.ConnectionActivityColors
import com.jackson.blebridge.core.designsystem.theme.contextual.activity.ConnectionActivityMotion
import com.jackson.blebridge.core.designsystem.theme.contextual.activity.toActivityColors
import com.jackson.blebridge.core.designsystem.theme.contextual.chat.ChatTokens
import com.jackson.blebridge.core.designsystem.theme.contextual.chat.LocalChatTokens
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleColors
import com.jackson.blebridge.core.designsystem.theme.contextual.role.LocalConnectionRoleColors
import com.jackson.blebridge.core.designsystem.theme.semantic.color.AppColors
import com.jackson.blebridge.core.designsystem.theme.semantic.color.DarkAppColors
import com.jackson.blebridge.core.designsystem.theme.semantic.color.LightAppColors
import com.jackson.blebridge.core.designsystem.theme.semantic.color.LocalAppColors
import com.jackson.blebridge.core.designsystem.theme.semantic.gradient.AppGradients
import com.jackson.blebridge.core.designsystem.theme.semantic.iconsize.IconSize
import com.jackson.blebridge.core.designsystem.theme.semantic.motion.Motion
import com.jackson.blebridge.core.designsystem.theme.semantic.radius.Radius
import com.jackson.blebridge.core.designsystem.theme.semantic.size.ControlSize
import com.jackson.blebridge.core.designsystem.theme.semantic.spacing.Spacing
import com.jackson.blebridge.core.designsystem.theme.semantic.typography.AppTypography
import com.jackson.blebridge.core.designsystem.theme.semantic.typography.DefaultAppTypography

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkAppColors else LightAppColors
    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = colors.server,
            onPrimary = colors.onServer,
            primaryContainer = colors.serverContainer,
            onPrimaryContainer = colors.onServerContainer,
            secondary = colors.client,
            onSecondary = colors.onClient,
            secondaryContainer = colors.clientContainer,
            onSecondaryContainer = colors.onClientContainer,
            background = colors.backgroundPrimary,
            onBackground = colors.textPrimary,
            surface = colors.surfacePrimary,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.borderNormal,
            outlineVariant = colors.borderSubtle,
            error = colors.error,
            onError = colors.textInverse,
            errorContainer = colors.errorContainer,
            onErrorContainer = colors.onErrorContainer,
        )
    } else {
        lightColorScheme(
            primary = colors.server,
            onPrimary = colors.onServer,
            primaryContainer = colors.serverContainer,
            onPrimaryContainer = colors.onServerContainer,
            secondary = colors.client,
            onSecondary = colors.onClient,
            secondaryContainer = colors.clientContainer,
            onSecondaryContainer = colors.onClientContainer,
            background = colors.backgroundPrimary,
            onBackground = colors.textPrimary,
            surface = colors.surfacePrimary,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.borderNormal,
            outlineVariant = colors.borderSubtle,
            error = colors.error,
            onError = colors.textInverse,
            errorContainer = colors.errorContainer,
            onErrorContainer = colors.onErrorContainer,
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = DefaultAppTypography.material,
            shapes = AppShapes,
            content = content,
        )
    }
}

/**
 * 디자인시스템의 공개 Semantic, Contextual, Component 토큰 진입점입니다.
 *
 * `core:designsystem`과 `core:ui`의 컴포넌트 토큰·Defaults가 이 값을 조합합니다.
 * Feature 화면은 foundation 토큰을 직접 조립하기보다 완성된 컴포넌트 API와 컴포넌트
 * 토큰을 사용합니다.
 */
object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val spacing: Spacing
        get() = Spacing

    val radius: Radius
        get() = Radius

    val iconSize: IconSize
        get() = IconSize

    val controlSize: ControlSize
        get() = ControlSize

    val gradients: AppGradients
        get() = AppGradients

    val motion: Motion
        get() = Motion

    val activityMotion: ConnectionActivityMotion
        get() = ConnectionActivityMotion

    val media: MediaTokens
        get() = MediaTokens

    val icons: AppIcons
        get() = AppIcons

    val typography: AppTypography
        get() = DefaultAppTypography

    val roleColors: ConnectionRoleColors
        @Composable
        @ReadOnlyComposable
        get() = LocalConnectionRoleColors.current

    val activityColors: ConnectionActivityColors
        @Composable
        @ReadOnlyComposable
        get() = colors.toActivityColors()

    val chatTokens: ChatTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChatTokens.current
}

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.xSmall),
    small = RoundedCornerShape(Radius.medium),
    medium = RoundedCornerShape(Radius.xLarge),
    large = RoundedCornerShape(Radius.bubble),
    extraLarge = RoundedCornerShape(Radius.logo),
)
