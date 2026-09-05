package com.jackson.blebridge.core.designsystem.component.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme

/** `SegmentedControl`의 test tag·치수·토큰 매핑을 소유하는 내부 계약입니다. */
internal object SegmentedControlDefaults {
    const val CONTROL_TAG = "blebridge_segmented_control"
    const val ITEM_TAG_PREFIX = "blebridge_segment_"
    val ContainerPadding = 4.dp
    val ItemVerticalPadding = 9.dp
    val CompactVerticalPadding = 5.dp
    val ItemSpacing = 4.dp
    val IconSpacing = 4.dp

    fun <T> itemTag(value: T): String = ITEM_TAG_PREFIX + value.toString()

    fun iconSize(compact: Boolean): Dp =
        if (compact) AppTheme.iconSize.xSmall else AppTheme.iconSize.small

    fun verticalPadding(compact: Boolean): Dp =
        if (compact) CompactVerticalPadding else ItemVerticalPadding

    @Composable
    fun textStyle(compact: Boolean): TextStyle =
        if (compact) AppTheme.typography.monoSmall else AppTheme.typography.labelMedium

    @Composable
    fun colors(selected: Boolean, enabled: Boolean): SegmentItemColors {
        if (!enabled) {
            return SegmentItemColors(
                container = if (selected) AppTheme.colors.surfacePrimary else Color.Transparent,
                content = AppTheme.colors.onDisabledContainer,
            )
        }

        return if (selected) {
            SegmentItemColors(
                container = AppTheme.colors.surfacePrimary,
                content = AppTheme.roleColors.active,
            )
        } else {
            SegmentItemColors(
                container = Color.Transparent,
                content = AppTheme.colors.textTertiary,
            )
        }
    }
}

@Immutable
internal data class SegmentItemColors(
    val container: Color,
    val content: Color,
)
