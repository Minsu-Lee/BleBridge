package com.jackson.blebridge.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleProvider

enum class IconButtonStyle {
    Filled,
    Tonal,
    Outlined,
    Ghost,
}

enum class IconButtonTone {
    Role,
    Neutral,
    Inverse,
}

enum class IconButtonShape {
    Circle,
    Rounded,
}

/**
 * BLE Bridge 전 화면에서 재사용하는 icon-only action button입니다.
 *
 * 상태(`enabled`/`selected`)를 소유하지 않으며 호출자가 hoist합니다. `Role` tone은
 * `ConnectionRoleProvider` 범위 안에서만 사용합니다.
 */
@Composable
fun IconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: IconButtonStyle = IconButtonStyle.Tonal,
    tone: IconButtonTone = IconButtonTone.Neutral,
    shape: IconButtonShape = IconButtonShape.Circle,
    visualSize: Dp = AppTheme.controlSize.comfortable,
    selected: Boolean = false,
) {
    val colors = IconButtonDefaults.colors(
        style = style,
        tone = tone,
        enabled = enabled,
        selected = selected,
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .testTag(IconButtonDefaults.BUTTON_TAG)
            .minimumInteractiveComponentSize()
            .size(visualSize),
        enabled = enabled,
        shape = RoundedCornerShape(IconButtonDefaults.shapeFor(shape)),
        color = colors.container,
        contentColor = colors.content,
        border = colors.border?.let { BorderStroke(IconButtonDefaults.BorderWidth, it) },
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(IconButtonDefaults.IconSize)
                    .testTag(IconButtonDefaults.ICON_TAG),
                tint = colors.content,
            )
        }
    }
}

@Preview(name = "Light · Matrix", showBackground = true)
@Composable
private fun IconButtonLightMatrixPreview() {
    AppTheme(darkTheme = false) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            IconButtonMatrixPreviewContent()
        }
    }
}

@Preview(name = "Dark · Matrix", showBackground = true)
@Composable
private fun IconButtonDarkMatrixPreview() {
    AppTheme(darkTheme = true) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            IconButtonMatrixPreviewContent()
        }
    }
}

@Composable
private fun IconButtonMatrixPreviewContent() {
    Column(
        modifier = Modifier.padding(AppTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            IconButton(icon = AppTheme.icons.Add, contentDescription = "첨부", style = IconButtonStyle.Outlined, onClick = {})
            IconButton(
                icon = AppTheme.icons.Send,
                contentDescription = "전송",
                style = IconButtonStyle.Filled,
                tone = IconButtonTone.Role,
                onClick = {},
            )
            IconButton(icon = AppTheme.icons.Settings, contentDescription = "설정", onClick = {})
            IconButton(
                icon = AppTheme.icons.Pause,
                contentDescription = "일시정지",
                style = IconButtonStyle.Ghost,
                shape = IconButtonShape.Rounded,
                onClick = {},
            )
            IconButton(icon = AppTheme.icons.Add, contentDescription = "비활성", enabled = false, onClick = {})
        }
    }
}

@Preview(name = "Media · Image AppBar", showBackground = true)
@Composable
private fun IconButtonImageAppBarPreview() {
    AppTheme(darkTheme = true) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.media.chromeBackground)
                .padding(AppTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                icon = AppTheme.icons.Close,
                contentDescription = "닫기",
                style = IconButtonStyle.Ghost,
                tone = IconButtonTone.Inverse,
                onClick = {},
            )
            IconButton(
                icon = AppTheme.icons.Download,
                contentDescription = "다운로드",
                style = IconButtonStyle.Ghost,
                tone = IconButtonTone.Inverse,
                onClick = {},
            )
        }
    }
}

@Preview(name = "Media · Video AppBar", showBackground = true)
@Composable
private fun IconButtonVideoAppBarPreview() {
    AppTheme(darkTheme = true) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.media.chromeBackground)
                .padding(AppTheme.spacing.sm),
            horizontalArrangement = Arrangement.Start,
        ) {
            IconButton(
                icon = AppTheme.icons.Close,
                contentDescription = "닫기",
                style = IconButtonStyle.Ghost,
                tone = IconButtonTone.Inverse,
                onClick = {},
            )
        }
    }
}

@Preview(name = "Light · Selected 매트릭스", showBackground = true)
@Composable
private fun IconButtonSelectedMatrixLightPreview() {
    AppTheme(darkTheme = false) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            IconButtonSelectedMatrixPreviewContent()
        }
    }
}

@Preview(name = "Dark · Selected 매트릭스", showBackground = true)
@Composable
private fun IconButtonSelectedMatrixDarkPreview() {
    AppTheme(darkTheme = true) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            IconButtonSelectedMatrixPreviewContent()
        }
    }
}

@Composable
private fun IconButtonSelectedMatrixPreviewContent() {
    Column(
        modifier = Modifier.padding(AppTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        // Ghost+Role만 selected 시 색이 바뀐다. 나머지 style은 새 토큰이 없어
        // selected/unselected 시각 차이가 의도적으로 없다(analysis "Shape × State" 참고).
        IconButtonStyle.entries.forEach { style ->
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                IconButton(
                    icon = AppTheme.icons.Settings,
                    contentDescription = "$style unselected",
                    style = style,
                    tone = IconButtonTone.Role,
                    selected = false,
                    onClick = {},
                )
                IconButton(
                    icon = AppTheme.icons.Settings,
                    contentDescription = "$style selected",
                    style = style,
                    tone = IconButtonTone.Role,
                    selected = true,
                    onClick = {},
                )
            }
        }
    }
}
