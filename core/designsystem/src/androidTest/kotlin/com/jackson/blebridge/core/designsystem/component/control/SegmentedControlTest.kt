package com.jackson.blebridge.core.designsystem.component.control

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleProvider
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * SegmentedControl 계측 테스트 (`.orca/plan/segmented-control/testcases.md` TC-01~TC-07).
 *
 * `connectedDebugAndroidTest`는 기기가 필요해 이 워크플로에서는 실행하지 않는다.
 * TC-05는 순수 함수 재호출이 아니라 실제 렌더의 `captureToImage()` 픽셀을 관찰한다
 * (`IconButtonTest.kt`와 동일한 근거).
 */
class SegmentedControlTest {
    @get:Rule
    val composeRule = createComposeRule()

    private enum class TestSegment { Alpha, Beta }

    @Test
    fun `초기 선택 상태가 올바른 item의 selected semantics에 반영된다`() {
        val items = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha"),
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    SegmentedControl(items = items, selected = TestSegment.Alpha, onSelected = {})
                }
            }
        }

        composeRule.onNodeWithTag(SegmentedControlDefaults.itemTag(TestSegment.Alpha)).assertIsSelected()
    }

    @Test
    fun `item 클릭 시 onSelected에 해당 value를 한 번 전달한다`() {
        val items = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha"),
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )
        val selectedCalls = mutableListOf<TestSegment>()

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    SegmentedControl(
                        items = items,
                        selected = TestSegment.Alpha,
                        onSelected = { selectedCalls.add(it) },
                    )
                }
            }
        }

        composeRule.onNodeWithTag(SegmentedControlDefaults.itemTag(TestSegment.Beta)).performClick()

        composeRule.runOnIdle {
            assertEquals(listOf(TestSegment.Beta), selectedCalls)
        }
    }

    @Test
    fun `비활성 상태에서는 item 클릭이 onSelected로 전달되지 않는다`() {
        val items = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha"),
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )
        var clickCount = 0

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    SegmentedControl(
                        items = items,
                        selected = TestSegment.Alpha,
                        onSelected = { clickCount++ },
                        enabled = false,
                    )
                }
            }
        }

        composeRule.onNodeWithTag(SegmentedControlDefaults.itemTag(TestSegment.Beta)).performClick()

        composeRule.runOnIdle {
            assertEquals(0, clickCount)
        }
    }

    @Test
    fun `선택 item은 selected true이고 나머지는 false이다`() {
        val items = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha"),
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    SegmentedControl(items = items, selected = TestSegment.Beta, onSelected = {})
                }
            }
        }

        composeRule.onNodeWithTag(SegmentedControlDefaults.itemTag(TestSegment.Beta)).assertIsSelected()
        composeRule.onNodeWithTag(SegmentedControlDefaults.itemTag(TestSegment.Alpha)).assertIsNotSelected()
    }

    @Test
    fun `선택 item content는 Server와 Client 역할별 active 색으로 렌더된다`() {
        var expectedServerContent: Color = Color.Unspecified
        var expectedClientContent: Color = Color.Unspecified
        val items = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha"),
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )

        composeRule.setContent {
            AppTheme {
                Column {
                    ConnectionRoleProvider(role = ConnectionRole.Server) {
                        expectedServerContent = AppTheme.roleColors.active
                        SegmentedControl(
                            items = items,
                            selected = TestSegment.Alpha,
                            onSelected = {},
                            modifier = Modifier.testTag(SERVER_CONTROL_TAG),
                        )
                    }
                    ConnectionRoleProvider(role = ConnectionRole.Client) {
                        expectedClientContent = AppTheme.roleColors.active
                        SegmentedControl(
                            items = items,
                            selected = TestSegment.Alpha,
                            onSelected = {},
                            modifier = Modifier.testTag(CLIENT_CONTROL_TAG),
                        )
                    }
                }
            }
        }

        val serverPixels = composeRule.onNodeWithTag(SERVER_CONTROL_TAG).captureToImage().toPixelMap()
        val clientPixels = composeRule.onNodeWithTag(CLIENT_CONTROL_TAG).captureToImage().toPixelMap()

        assertTrue(
            "Server 선택 item에는 실제 렌더에서 roleColors.active와 일치하는 픽셀이 있어야 한다",
            hasPixelMatching(serverPixels, expectedServerContent),
        )
        assertTrue(
            "Client 선택 item에는 실제 렌더에서 roleColors.active와 일치하는 픽셀이 있어야 한다",
            hasPixelMatching(clientPixels, expectedClientContent),
        )
        assertFalse(
            "Server와 Client의 roleColors.active는 서로 달라야 한다",
            colorsMatch(expectedServerContent, expectedClientContent, tolerance = 0.02f),
        )
    }

    @Test
    fun `Compact는 shortLabel을 표시하고 Default는 label을 표시한다`() {
        val itemsWithShortLabel = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha", shortLabel = "A"),
        )
        val itemsWithoutShortLabel = listOf(
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    Column {
                        SegmentedControl(
                            items = itemsWithShortLabel,
                            selected = TestSegment.Alpha,
                            onSelected = {},
                            compact = true,
                            modifier = Modifier.testTag(COMPACT_WITH_SHORT_LABEL_TAG),
                        )
                        SegmentedControl(
                            items = itemsWithShortLabel,
                            selected = TestSegment.Alpha,
                            onSelected = {},
                            compact = false,
                            modifier = Modifier.testTag(DEFAULT_WITH_SHORT_LABEL_TAG),
                        )
                        SegmentedControl(
                            items = itemsWithoutShortLabel,
                            selected = TestSegment.Beta,
                            onSelected = {},
                            compact = true,
                            modifier = Modifier.testTag(COMPACT_WITHOUT_SHORT_LABEL_TAG),
                        )
                    }
                }
            }
        }

        // 각 control의 subtree 범위 안에서만 기대 라벨 존재/반대 라벨 부재를 단정해,
        // Compact/Default의 shortLabel·label 매핑이 뒤바뀌어도 다른 control의 문자열로
        // 우연히 통과하지 않도록 한다.
        composeRule.onNode(withDescendantText(COMPACT_WITH_SHORT_LABEL_TAG, "A")).assertExists()
        composeRule.onNode(withDescendantText(COMPACT_WITH_SHORT_LABEL_TAG, "Alpha")).assertDoesNotExist()

        composeRule.onNode(withDescendantText(DEFAULT_WITH_SHORT_LABEL_TAG, "Alpha")).assertExists()
        composeRule.onNode(withDescendantText(DEFAULT_WITH_SHORT_LABEL_TAG, "A")).assertDoesNotExist()

        composeRule.onNode(withDescendantText(COMPACT_WITHOUT_SHORT_LABEL_TAG, "Beta")).assertExists()
    }

    @Test
    fun `같은 item을 다시 클릭하면 onSelected를 호출하지 않는다`() {
        val items = listOf(
            SegmentItem(value = TestSegment.Alpha, label = "Alpha"),
            SegmentItem(value = TestSegment.Beta, label = "Beta"),
        )
        var clickCount = 0

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    SegmentedControl(
                        items = items,
                        selected = TestSegment.Alpha,
                        onSelected = { clickCount++ },
                    )
                }
            }
        }

        composeRule.onNodeWithTag(SegmentedControlDefaults.itemTag(TestSegment.Alpha)).performClick()

        composeRule.runOnIdle {
            assertEquals(0, clickCount)
        }
    }

    private fun withDescendantText(controlTag: String, text: String): SemanticsMatcher =
        hasTestTag(controlTag).and(hasAnyDescendant(hasText(text)))

    private fun colorsMatch(a: Color, b: Color, tolerance: Float): Boolean =
        abs(a.red - b.red) <= tolerance &&
            abs(a.green - b.green) <= tolerance &&
            abs(a.blue - b.blue) <= tolerance

    private fun hasPixelMatching(
        pixelMap: PixelMap,
        target: Color,
        tolerance: Float = 0.05f,
    ): Boolean {
        for (x in 0 until pixelMap.width) {
            for (y in 0 until pixelMap.height) {
                if (colorsMatch(pixelMap[x, y], target, tolerance)) return true
            }
        }
        return false
    }

    private companion object {
        const val SERVER_CONTROL_TAG = "${SegmentedControlDefaults.CONTROL_TAG}_server"
        const val CLIENT_CONTROL_TAG = "${SegmentedControlDefaults.CONTROL_TAG}_client"
        const val COMPACT_WITH_SHORT_LABEL_TAG = "${SegmentedControlDefaults.CONTROL_TAG}_compact_short"
        const val DEFAULT_WITH_SHORT_LABEL_TAG = "${SegmentedControlDefaults.CONTROL_TAG}_default_short"
        const val COMPACT_WITHOUT_SHORT_LABEL_TAG = "${SegmentedControlDefaults.CONTROL_TAG}_compact_fallback"
    }
}
