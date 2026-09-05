package com.jackson.blebridge.core.designsystem.component.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.jackson.blebridge.core.designsystem.icon.AppIcons
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleProvider

/**
 * `SegmentedControl`의 선택 항목입니다.
 *
 * [value]는 항목 식별(테스트 태그, 동등 비교)에 `toString()`을 사용하므로 호출자는 `T`가
 * 고유하고 안정적인 `toString()`(예: enum)을 갖도록 책임집니다. [icon]이 `null`이면 Label
 * variant, 아니면 IconLabel variant로 렌더링됩니다.
 */
@Immutable
data class SegmentItem<T>(
    val value: T,
    val label: String,
    val shortLabel: String? = null,
    val icon: ImageVector? = null,
)

/**
 * BLE Bridge 기기 연결·설정 화면에서 재사용하는 단일 선택 segmented control입니다.
 *
 * 상태(`selected`)를 소유하지 않으며 호출자가 hoist합니다. 선택 item의 content는
 * `AppTheme.roleColors.active`를 사용하므로 `ConnectionRoleProvider` 범위 안에서만
 * 사용합니다. 이미 선택된 item을 다시 클릭해도 [onSelected]는 다시 호출되지 않습니다.
 */
@Composable
fun <T> SegmentedControl(
    items: List<SegmentItem<T>>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier
            .testTag(SegmentedControlDefaults.CONTROL_TAG)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.xLarge))
            .background(AppTheme.colors.surfaceVariant)
            .selectableGroup()
            .padding(SegmentedControlDefaults.ContainerPadding),
        horizontalArrangement = Arrangement.spacedBy(SegmentedControlDefaults.ItemSpacing),
    ) {
        items.forEach { item ->
            val isSelected = item.value == selected
            val colors = SegmentedControlDefaults.colors(selected = isSelected, enabled = enabled)

            Row(
                // clip·background·selectable을 minimumInteractiveComponentSize() 바깥(왼쪽)에
                // 둔다. 안쪽에 두면(터치 영역 확장 이전) pill의 배경이 확장 전 작은 내용
                // 크기로만 그려져, 48dp로 확장된 item 슬롯 안에 작은 pill이 떠 있는 것처럼
                // 보이는 시각 불일치가 생긴다(바깥 container가 이미 눈에 보이는 배경을 가진
                // SegmentedControl 특유의 문제 — ActionButton은 `.height(size)`로 시각 높이를
                // 직접 고정해 이 확장분이 애초에 안 보인다). 이 순서면 pill 배경·클릭 영역이
                // 실제로 확장된 최종 크기(48dp 미만이면 48dp)를 그대로 채운다.
                modifier = Modifier
                    .weight(1f)
                    .testTag(SegmentedControlDefaults.itemTag(item.value))
                    .clip(RoundedCornerShape(AppTheme.radius.large))
                    .background(colors.container)
                    .selectable(
                        selected = isSelected,
                        enabled = enabled,
                        role = Role.Tab,
                        onClick = {
                            if (item.value != selected) onSelected(item.value)
                        },
                    )
                    .minimumInteractiveComponentSize()
                    .padding(vertical = SegmentedControlDefaults.verticalPadding(compact)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedControlDefaults.iconSize(compact)),
                        tint = colors.content,
                    )
                    Spacer(Modifier.width(SegmentedControlDefaults.IconSpacing))
                }
                Text(
                    text = if (compact) item.shortLabel ?: item.label else item.label,
                    style = SegmentedControlDefaults.textStyle(compact),
                    color = colors.content,
                )
            }
        }
    }
}

private enum class PreviewSegment { Server, Client, Bridge }

@Preview(name = "Light · Server", showBackground = true)
@Composable
private fun SegmentedControlLightServerPreview() {
    AppTheme(darkTheme = false) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            SegmentedControlPreviewContent()
        }
    }
}

@Preview(name = "Light · Client", showBackground = true)
@Composable
private fun SegmentedControlLightClientPreview() {
    AppTheme(darkTheme = false) {
        ConnectionRoleProvider(role = ConnectionRole.Client) {
            SegmentedControlPreviewContent()
        }
    }
}

@Preview(name = "Dark · Server", showBackground = true)
@Composable
private fun SegmentedControlDarkServerPreview() {
    AppTheme(darkTheme = true) {
        ConnectionRoleProvider(role = ConnectionRole.Server) {
            SegmentedControlPreviewContent()
        }
    }
}

@Preview(name = "Dark · Client", showBackground = true)
@Composable
private fun SegmentedControlDarkClientPreview() {
    AppTheme(darkTheme = true) {
        ConnectionRoleProvider(role = ConnectionRole.Client) {
            SegmentedControlPreviewContent()
        }
    }
}

@Composable
private fun SegmentedControlPreviewContent() {
    val labelItems = listOf(
        SegmentItem(value = PreviewSegment.Server, label = "서버"),
        SegmentItem(value = PreviewSegment.Client, label = "클라이언트"),
    )
    val iconItems = listOf(
        SegmentItem(
            value = PreviewSegment.Server,
            label = "서버",
            shortLabel = "S",
            icon = AppIcons.DevicePhone,
        ),
        SegmentItem(
            value = PreviewSegment.Client,
            label = "클라이언트",
            shortLabel = "C",
            icon = AppIcons.SwitchRole,
        ),
        SegmentItem(
            value = PreviewSegment.Bridge,
            label = "설정",
            shortLabel = "설",
            icon = AppIcons.Settings,
        ),
    )

    Column(
        modifier = Modifier.padding(AppTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        var labelSelected by remember { mutableStateOf(PreviewSegment.Server) }
        SegmentedControl(items = labelItems, selected = labelSelected, onSelected = { labelSelected = it })

        var iconSelected by remember { mutableStateOf(PreviewSegment.Client) }
        SegmentedControl(
            items = iconItems,
            selected = iconSelected,
            onSelected = { iconSelected = it },
            compact = true,
        )

        SegmentedControl(items = labelItems, selected = PreviewSegment.Server, onSelected = {}, enabled = false)
    }
}
