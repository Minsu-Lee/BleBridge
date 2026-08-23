package com.jackson.blebridge.core.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme

/** `IconButton`의 test tag·치수·토큰 매핑을 소유하는 내부 계약입니다. */
internal object IconButtonDefaults {
    const val BUTTON_TAG = "blebridge_icon_button"
    const val ICON_TAG = "blebridge_icon_button_icon"
    val IconSize = 20.dp
    val BorderWidth = 1.dp
    val MinimumTouchTarget = 48.dp

    fun shapeFor(shape: IconButtonShape): Dp = when (shape) {
        IconButtonShape.Circle -> AppTheme.radius.full
        IconButtonShape.Rounded -> AppTheme.radius.large
    }

    @Composable
    fun colors(
        style: IconButtonStyle,
        tone: IconButtonTone,
        enabled: Boolean,
        selected: Boolean,
    ): IconButtonColors {
        if (!enabled) {
            return IconButtonColors(
                container = AppTheme.colors.disabledContainer,
                content = AppTheme.colors.onDisabledContainer,
                border = AppTheme.colors.disabledBorder,
            )
        }

        if (tone == IconButtonTone.Inverse) {
            return IconButtonColors(
                container = AppTheme.media.chromeBackground,
                content = AppTheme.media.content,
                border = null,
            )
        }

        val isRole = tone == IconButtonTone.Role

        return when (style) {
            IconButtonStyle.Filled -> if (isRole) {
                IconButtonColors(
                    container = AppTheme.roleColors.active,
                    content = AppTheme.roleColors.onActive,
                    border = null,
                )
            } else {
                IconButtonColors(
                    container = AppTheme.colors.iconPrimary,
                    content = AppTheme.colors.backgroundPrimary,
                    border = null,
                )
            }

            IconButtonStyle.Tonal -> if (isRole) {
                IconButtonColors(
                    container = AppTheme.roleColors.activeContainer,
                    content = AppTheme.roleColors.onActiveContainer,
                    border = null,
                )
            } else {
                IconButtonColors(
                    container = AppTheme.colors.surfaceVariant,
                    content = AppTheme.colors.iconSecondary,
                    border = null,
                )
            }

            IconButtonStyle.Outlined -> IconButtonColors(
                container = Color.Transparent,
                content = if (isRole) AppTheme.roleColors.active else AppTheme.colors.iconSecondary,
                border = AppTheme.colors.borderNormal,
            )

            IconButtonStyle.Ghost -> IconButtonColors(
                container = Color.Transparent,
                content = when {
                    isRole && selected -> AppTheme.roleColors.active
                    !isRole && selected -> AppTheme.colors.iconPrimary
                    else -> AppTheme.colors.iconSecondary
                },
                border = null,
            )
        }
    }
}

@Immutable
internal data class IconButtonColors(
    val container: Color,
    val content: Color,
    val border: Color?,
)
