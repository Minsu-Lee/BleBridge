package com.jackson.blebridge.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleProvider

enum class ActionButtonStyle {
    RolePrimary,
    Neutral,
    Destructive,
    Text,
}

enum class ActionButtonSize {
    Compact,
    Default,
    FullWidth,
}

/**
 * BLE Bridge 전 화면에서 재사용하는 action button입니다.
 *
 * 상태(`enabled`/`loading`)를 소유하지 않으며 호출자가 hoist합니다. `RolePrimary`·`Text`
 * style은 `ConnectionRoleProvider` 범위 안에서만 사용합니다.
 */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ActionButtonStyle = ActionButtonStyle.RolePrimary,
    size: ActionButtonSize = ActionButtonSize.Default,
    leadingIcon: ImageVector? = null,
    loading: Boolean = false,
) {
    val colors = ActionButtonDefaults.colors(style = style, enabled = enabled)
    val sizeModifier = if (size == ActionButtonSize.FullWidth) {
        Modifier.fillMaxWidth()
    } else {
        Modifier
    }
    val loadingSemantics = if (loading) {
        Modifier.semantics { stateDescription = ActionButtonDefaults.LOADING_STATE_DESCRIPTION }
    } else {
        Modifier
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .testTag(ActionButtonDefaults.BUTTON_TAG)
            .then(sizeModifier)
            // 시각 높이를 고정하기 전에 터치 영역 확장 여지를 먼저 예약해야
            // Compact(40dp)에서도 48dp 터치 영역이 보장된다(모디파이어 순서 의존).
            .minimumInteractiveComponentSize()
            .height(ActionButtonDefaults.height(size))
            .then(loadingSemantics),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(AppTheme.radius.xLarge),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.container,
            contentColor = colors.content,
            disabledContainerColor = colors.container,
            disabledContentColor = colors.content,
        ),
        border = colors.border?.let { BorderStroke(ActionButtonDefaults.BorderWidth, it) },
        contentPadding = PaddingValues(horizontal = ActionButtonDefaults.HorizontalPadding),
    ) {
        val iconSize = ActionButtonDefaults.iconSize(size)
        when {
            loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(iconSize)
                        .testTag(ActionButtonDefaults.PROGRESS_TAG),
                    color = colors.content,
                    strokeWidth = ActionButtonDefaults.ProgressStrokeWidth,
                )
                Spacer(Modifier.width(ActionButtonDefaults.IconSpacing))
            }

            leadingIcon != null -> {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .testTag(ActionButtonDefaults.ICON_TAG),
                    tint = colors.content,
                )
                Spacer(Modifier.width(ActionButtonDefaults.IconSpacing))
            }
        }
        Text(text = text, style = ActionButtonDefaults.textStyle(size))
    }
}

@Preview(name = "Light · Server", showBackground = true)
@Composable
private fun ActionButtonLightServerPreview() {
    AppTheme(darkTheme = false) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            ActionButtonPreviewContent()
        }
    }
}

@Preview(name = "Light · Client", showBackground = true)
@Composable
private fun ActionButtonLightClientPreview() {
    AppTheme(darkTheme = false) {
        ConnectionRoleProvider(role = ConnectionRole.Client) {
            ActionButtonPreviewContent()
        }
    }
}

@Preview(name = "Dark · Server", showBackground = true)
@Composable
private fun ActionButtonDarkServerPreview() {
    AppTheme(darkTheme = true) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            ActionButtonPreviewContent()
        }
    }
}

@Preview(name = "Dark · Client", showBackground = true)
@Composable
private fun ActionButtonDarkClientPreview() {
    AppTheme(darkTheme = true) {
        ConnectionRoleProvider(role = ConnectionRole.Client) {
            ActionButtonPreviewContent()
        }
    }
}

@Composable
private fun ActionButtonPreviewContent() {
    Column(
        modifier = Modifier.padding(AppTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        ActionButton(text = "스캔 시작", leadingIcon = AppTheme.icons.Play, onClick = {})
        ActionButton(
            text = "스캔 중단",
            leadingIcon = AppTheme.icons.Stop,
            style = ActionButtonStyle.Destructive,
            onClick = {},
        )
        ActionButton(
            text = "채팅 열기",
            leadingIcon = AppTheme.icons.Chat,
            style = ActionButtonStyle.Neutral,
            size = ActionButtonSize.Compact,
            onClick = {},
        )
        ActionButton(
            text = "재시도",
            leadingIcon = AppTheme.icons.Retry,
            style = ActionButtonStyle.Text,
            onClick = {},
        )
        ActionButton(text = "비활성", onClick = {}, enabled = false)
        ActionButton(text = "연결 중", onClick = {}, loading = true, size = ActionButtonSize.FullWidth)
    }
}
