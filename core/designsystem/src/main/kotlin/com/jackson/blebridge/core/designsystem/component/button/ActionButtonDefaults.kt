package com.jackson.blebridge.core.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme

/** `ActionButton`의 test tag·치수·토큰 매핑을 소유하는 내부 계약입니다. */
internal object ActionButtonDefaults {
    const val BUTTON_TAG = "blebridge_action_button"
    const val ICON_TAG = "blebridge_action_button_icon"
    const val PROGRESS_TAG = "blebridge_action_button_progress"
    const val LOADING_STATE_DESCRIPTION = "로딩 중"

    val DefaultHeight = 48.dp
    val CompactHeight = 40.dp
    val HorizontalPadding = 20.dp
    val IconSpacing = 8.dp
    val BorderWidth = 1.dp
    val ProgressStrokeWidth = 2.dp

    fun height(size: ActionButtonSize): Dp = when (size) {
        ActionButtonSize.Compact -> CompactHeight
        ActionButtonSize.Default, ActionButtonSize.FullWidth -> DefaultHeight
    }

    fun iconSize(size: ActionButtonSize): Dp = when (size) {
        ActionButtonSize.Compact -> AppTheme.iconSize.xSmall
        ActionButtonSize.Default, ActionButtonSize.FullWidth -> AppTheme.iconSize.small
    }

    @Composable
    fun textStyle(size: ActionButtonSize): TextStyle = when (size) {
        ActionButtonSize.Compact -> AppTheme.typography.labelLarge
        ActionButtonSize.Default, ActionButtonSize.FullWidth -> AppTheme.typography.titleSmall
    }

    @Composable
    fun colors(style: ActionButtonStyle, enabled: Boolean): ActionButtonColors {
        if (!enabled) {
            return if (style == ActionButtonStyle.Text) {
                ActionButtonColors(
                    container = Color.Transparent,
                    content = AppTheme.colors.onDisabledContainer,
                    border = null,
                )
            } else {
                ActionButtonColors(
                    container = AppTheme.colors.disabledContainer,
                    content = AppTheme.colors.onDisabledContainer,
                    border = AppTheme.colors.disabledBorder,
                )
            }
        }

        return when (style) {
            ActionButtonStyle.RolePrimary -> ActionButtonColors(
                container = AppTheme.roleColors.active,
                content = AppTheme.roleColors.onActive,
                border = null,
            )

            ActionButtonStyle.Neutral -> ActionButtonColors(
                container = AppTheme.colors.surfaceVariant,
                content = AppTheme.colors.textSecondary,
                border = null,
            )

            ActionButtonStyle.Destructive -> ActionButtonColors(
                container = AppTheme.colors.errorContainer,
                content = AppTheme.colors.onErrorContainer,
                border = null,
            )

            ActionButtonStyle.Text -> ActionButtonColors(
                container = Color.Transparent,
                content = AppTheme.roleColors.active,
                border = null,
            )
        }
    }
}

@Immutable
internal data class ActionButtonColors(
    val container: Color,
    val content: Color,
    val border: Color?,
)
