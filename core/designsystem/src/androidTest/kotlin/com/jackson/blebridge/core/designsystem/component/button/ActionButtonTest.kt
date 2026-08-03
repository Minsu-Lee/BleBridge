package com.jackson.blebridge.core.designsystem.component.button

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleProvider
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * ActionButton 계측 테스트 (`.orca/plan/action-button/testcases.md` TC-01~TC-07).
 *
 * `connectedDebugAndroidTest`는 기기가 필요해 이 워크플로에서는 실행하지 않는다. 색상 계열
 * 단정(TC-03/04)은 `AppThemeTest.kt`와 동일하게 `ActionButtonDefaults.colors(...)`를 조합
 * composition 내부에서 직접 호출해 픽셀 캡처 없이 결정적으로 검증한다. 단 TC-07은 순수 함수
 * 재호출만으로는 실제 렌더링 회귀(`enabled && !loading`)를 잡지 못하므로
 * `captureToImage()`로 실제로 그려진 픽셀을 관찰한다(기기에서만 실행 가능, 이 워크플로는
 * 컴파일까지만 검증).
 */
class ActionButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `활성 상태에서 클릭하면 onClick이 한 번 호출된다`() {
        var clickCount = 0

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    ActionButton(text = "시작", onClick = { clickCount++ })
                }
            }
        }

        composeRule.onNodeWithTag(ActionButtonDefaults.BUTTON_TAG).performClick()

        composeRule.runOnIdle {
            assertEquals(1, clickCount)
        }
    }

    @Test
    fun `비활성 상태에서는 클릭이 전달되지 않고 비활성 semantics를 노출한다`() {
        var clickCount = 0

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    ActionButton(text = "시작", enabled = false, onClick = { clickCount++ })
                }
            }
        }

        composeRule.onNodeWithTag(ActionButtonDefaults.BUTTON_TAG)
            .assertIsNotEnabled()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(0, clickCount)
        }
    }

    @Test
    fun `RolePrimary style은 Server·Client 역할별 content color를 roleColors onActive와 일치시킨다`() {
        ConnectionRole.entries.forEach { role ->
            var expected: Color = Color.Unspecified
            var actual: Color = Color.Unspecified

            composeRule.setContent {
                AppTheme {
                    ConnectionRoleProvider(role = role) {
                        expected = AppTheme.roleColors.onActive
                        actual = ActionButtonDefaults.colors(
                            style = ActionButtonStyle.RolePrimary,
                            enabled = true,
                        ).content
                    }
                }
            }

            composeRule.runOnIdle {
                assertEquals("role=$role", expected, actual)
            }
        }
    }

    @Test
    fun `Destructive와 Neutral은 배경색이 같아도 content color로 구분된다`() {
        lateinit var destructive: ActionButtonColors
        lateinit var neutral: ActionButtonColors
        var expectedDestructiveContent: Color = Color.Unspecified
        var expectedNeutralContent: Color = Color.Unspecified

        composeRule.setContent {
            AppTheme(darkTheme = false) {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    destructive = ActionButtonDefaults.colors(
                        style = ActionButtonStyle.Destructive,
                        enabled = true,
                    )
                    neutral = ActionButtonDefaults.colors(
                        style = ActionButtonStyle.Neutral,
                        enabled = true,
                    )
                    expectedDestructiveContent = AppTheme.colors.onErrorContainer
                    expectedNeutralContent = AppTheme.colors.textSecondary
                }
            }
        }

        composeRule.runOnIdle {
            assertEquals(expectedDestructiveContent, destructive.content)
            assertEquals(expectedNeutralContent, neutral.content)
            assertNotEquals(destructive.content, neutral.content)
        }
    }

    @Test
    fun `leadingIcon은 노드로 존재하되 contentDescription을 노출하지 않는다`() {
        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    ActionButton(
                        text = "스캔 시작",
                        leadingIcon = AppTheme.icons.Play,
                        onClick = {},
                    )
                }
            }
        }

        composeRule.onNodeWithTag(ActionButtonDefaults.ICON_TAG, useUnmergedTree = true)
            .assertExists()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
    }

    @Test
    fun `Compact 크기를 포함한 모든 size에서 최소 48dp 터치 영역을 보장한다`() {
        ActionButtonSize.entries.forEach { size ->
            composeRule.setContent {
                AppTheme {
                    ConnectionRoleProvider(role = ConnectionRole.Server) {
                        ActionButton(text = "버튼", size = size, onClick = {})
                    }
                }
            }

            composeRule.onNodeWithTag(ActionButtonDefaults.BUTTON_TAG)
                .assertTouchHeightIsEqualTo(48.dp)
        }
    }

    @Test
    fun `loading 상태는 진행 표시를 노출하고 클릭을 차단하며 state description을 제공한다`() {
        var clickCount = 0
        lateinit var density: Density
        lateinit var expectedEnabledColors: ActionButtonColors
        lateinit var expectedDisabledColors: ActionButtonColors

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    density = LocalDensity.current
                    ActionButton(
                        text = "연결 중",
                        style = ActionButtonStyle.RolePrimary,
                        loading = true,
                        onClick = { clickCount++ },
                    )
                    expectedEnabledColors = ActionButtonDefaults.colors(
                        style = ActionButtonStyle.RolePrimary,
                        enabled = true,
                    )
                    expectedDisabledColors = ActionButtonDefaults.colors(
                        style = ActionButtonStyle.RolePrimary,
                        enabled = false,
                    )
                }
            }
        }

        composeRule.onNodeWithTag(ActionButtonDefaults.PROGRESS_TAG, useUnmergedTree = true)
            .assertExists()

        composeRule.onNodeWithTag(ActionButtonDefaults.BUTTON_TAG)
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    ActionButtonDefaults.LOADING_STATE_DESCRIPTION,
                ),
            )

        // 순수 함수 재호출이 아니라 실제로 그려진 픽셀을 읽어 회귀(예: 구현이
        // `enabled && !loading`으로 disabled 색을 쓰게 되는 경우)를 잡는다. 좌측 여백
        // (HorizontalPadding, container 배경만 있고 progress/텍스트가 없는 영역)의
        // 세로 중앙 픽셀은 shape의 모서리 반경 영향도 받지 않는다.
        val bitmap = composeRule.onNodeWithTag(ActionButtonDefaults.BUTTON_TAG).captureToImage()
        val insetPx = with(density) { 4.dp.roundToPx() }
            .coerceIn(0, (bitmap.width / 2) - 1)
        val renderedContainerColor = bitmap.toPixelMap()[insetPx, bitmap.height / 2]

        assertColorsApproximatelyEqual(
            "loading 중 container 색은 enabled 색과 같아야 한다",
            expected = expectedEnabledColors.container,
            actual = renderedContainerColor,
        )
        assertColorsNotApproximatelyEqual(
            "loading 중 container 색이 disabled 색으로 바뀌면 안 된다",
            unexpected = expectedDisabledColors.container,
            actual = renderedContainerColor,
        )

        composeRule.onNodeWithTag(ActionButtonDefaults.BUTTON_TAG).performClick()

        composeRule.runOnIdle {
            assertEquals(0, clickCount)
        }
    }

    private fun assertColorsApproximatelyEqual(
        message: String,
        expected: Color,
        actual: Color,
        tolerance: Float = 0.02f,
    ) {
        assertTrue(
            "$message (expected=$expected, actual=$actual)",
            colorsMatch(expected, actual, tolerance),
        )
    }

    private fun assertColorsNotApproximatelyEqual(
        message: String,
        unexpected: Color,
        actual: Color,
        tolerance: Float = 0.02f,
    ) {
        assertFalse(
            "$message (unexpected=$unexpected, actual=$actual)",
            colorsMatch(unexpected, actual, tolerance),
        )
    }

    private fun colorsMatch(a: Color, b: Color, tolerance: Float): Boolean =
        abs(a.red - b.red) <= tolerance &&
            abs(a.green - b.green) <= tolerance &&
            abs(a.blue - b.blue) <= tolerance
}
