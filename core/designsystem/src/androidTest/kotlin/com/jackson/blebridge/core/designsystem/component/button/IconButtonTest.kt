package com.jackson.blebridge.core.designsystem.component.button

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
 * IconButton 계측 테스트 (`.orca/plan/icon-button/testcases.md` TC-01~TC-07).
 *
 * `connectedDebugAndroidTest`는 기기가 필요해 이 워크플로에서는 실행하지 않는다.
 * TC-05~TC-07은 순수 함수 재호출이 아니라 실제 렌더의 `captureToImage()` 픽셀을 관찰한다
 * (`ActionButtonTest.kt`와 동일한 근거: 실제 렌더 회귀는 색상 함수 재호출만으로는 잡히지 않는다).
 */
class IconButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `활성 상태에서 클릭하면 onClick이 한 번 호출된다`() {
        var clickCount = 0

        composeRule.setContent {
            AppTheme {
                IconButton(icon = AppTheme.icons.Add, contentDescription = "추가", onClick = { clickCount++ })
            }
        }

        composeRule.onNodeWithTag(IconButtonDefaults.BUTTON_TAG).performClick()

        composeRule.runOnIdle {
            assertEquals(1, clickCount)
        }
    }

    @Test
    fun `비활성 상태에서는 클릭이 전달되지 않고 비활성 semantics를 노출한다`() {
        var clickCount = 0

        composeRule.setContent {
            AppTheme {
                IconButton(
                    icon = AppTheme.icons.Add,
                    contentDescription = "추가",
                    enabled = false,
                    onClick = { clickCount++ },
                )
            }
        }

        composeRule.onNodeWithTag(IconButtonDefaults.BUTTON_TAG)
            .assertIsNotEnabled()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(0, clickCount)
        }
    }

    @Test
    fun `각 아이콘의 content description을 탐색할 수 있다`() {
        val icons = listOf(
            "추가" to AppTheme.icons.Add,
            "전송" to AppTheme.icons.Send,
            "설정" to AppTheme.icons.Settings,
            "닫기" to AppTheme.icons.Close,
            "다운로드" to AppTheme.icons.Download,
            "일시정지" to AppTheme.icons.Pause,
        )

        composeRule.setContent {
            AppTheme {
                Column {
                    icons.forEach { (description, icon) ->
                        IconButton(icon = icon, contentDescription = description, onClick = {})
                    }
                }
            }
        }

        icons.forEach { (description, _) ->
            composeRule.onNodeWithContentDescription(description).assertExists()
        }
    }

    @Test
    fun `42dp 시각 크기에서도 최소 48dp 터치 영역을 보장한다`() {
        composeRule.setContent {
            AppTheme {
                IconButton(
                    icon = AppTheme.icons.Add,
                    contentDescription = "추가",
                    visualSize = 42.dp,
                    onClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(IconButtonDefaults.BUTTON_TAG)
            .assertTouchWidthIsEqualTo(48.dp)
            .assertTouchHeightIsEqualTo(48.dp)
    }

    @Test
    fun `Filled Role은 Server와 Client 역할별 실제 렌더 container·content 색을 사용한다`() {
        lateinit var density: Density
        var expectedServerContainer: Color = Color.Unspecified
        var expectedClientContainer: Color = Color.Unspecified
        var expectedServerContent: Color = Color.Unspecified
        var expectedClientContent: Color = Color.Unspecified

        composeRule.setContent {
            AppTheme {
                density = LocalDensity.current
                Column {
                    ConnectionRoleProvider(role = ConnectionRole.Server) {
                        expectedServerContainer = AppTheme.roleColors.active
                        expectedServerContent = AppTheme.roleColors.onActive
                        IconButton(
                            icon = AppTheme.icons.Add,
                            contentDescription = "서버",
                            style = IconButtonStyle.Filled,
                            tone = IconButtonTone.Role,
                            modifier = Modifier.testTag(SERVER_BUTTON_TAG),
                            onClick = {},
                        )
                    }
                    ConnectionRoleProvider(role = ConnectionRole.Client) {
                        expectedClientContainer = AppTheme.roleColors.active
                        expectedClientContent = AppTheme.roleColors.onActive
                        IconButton(
                            icon = AppTheme.icons.Add,
                            contentDescription = "클라이언트",
                            style = IconButtonStyle.Filled,
                            tone = IconButtonTone.Role,
                            modifier = Modifier.testTag(CLIENT_BUTTON_TAG),
                            onClick = {},
                        )
                    }
                }
            }
        }

        val serverPixels = composeRule.onNodeWithTag(SERVER_BUTTON_TAG).captureToImage().toPixelMap()
        val clientPixels = composeRule.onNodeWithTag(CLIENT_BUTTON_TAG).captureToImage().toPixelMap()

        // 캡처 노드는 minimumInteractiveComponentSize()로 48dp지만 시각 Surface는
        // AppTheme.controlSize.comfortable(42dp)만큼만 중앙에 그려진다. 42dp 정사각형에
        // 내접하는 원의 세로 중심선(centerX)은 항상 원 내부이므로, container만 있고 아이콘
        // (20dp) 바깥인 y 지점을 밀도 기준으로 계산해 순수 container 색을 관찰한다.
        val offsetPx = visualSurfaceOffsetPx(density)
        val insetPx = density.dpToPx(2.dp)
        val containerSampleX = serverPixels.width / 2
        val containerSampleY = offsetPx + insetPx

        val serverContainerPixel = serverPixels[containerSampleX, containerSampleY]
        val clientContainerPixel = clientPixels[clientPixels.width / 2, containerSampleY]

        assertColorsApproximatelyEqual(
            "Server container는 roleColors.active와 일치해야 한다",
            expected = expectedServerContainer,
            actual = serverContainerPixel,
        )
        assertColorsApproximatelyEqual(
            "Client container는 roleColors.active와 일치해야 한다",
            expected = expectedClientContainer,
            actual = clientContainerPixel,
        )
        assertFalse(
            "Server와 Client의 실제 렌더 container 색은 서로 달라야 한다",
            colorsMatch(serverContainerPixel, clientContainerPixel, tolerance = 0.02f),
        )

        // 아이콘(20dp)은 캡처 중심에 정렬되므로, 중심을 기준으로 한 20dp 정사각 영역에서
        // roleColors.onActive와 일치하는 불투명 픽셀을 찾아 content 색을 검증한다.
        val iconHalfPx = density.dpToPx(IconButtonDefaults.IconSize / 2)
        val serverIconRange = (containerSampleX - iconHalfPx)..(containerSampleX + iconHalfPx)
        val clientIconRange = (clientPixels.width / 2 - iconHalfPx)..(clientPixels.width / 2 + iconHalfPx)

        assertTrue(
            "Server content(icon)는 실제 렌더에서 roleColors.onActive와 일치하는 픽셀이 있어야 한다",
            hasPixelMatching(serverPixels, serverIconRange, serverIconRange, expectedServerContent),
        )
        assertTrue(
            "Client content(icon)는 실제 렌더에서 roleColors.onActive와 일치하는 픽셀이 있어야 한다",
            hasPixelMatching(clientPixels, clientIconRange, clientIconRange, expectedClientContent),
        )
    }

    @Test
    fun `Circle과 Rounded shape는 서로 다른 clip을 실제 렌더에 반영한다`() {
        lateinit var density: Density
        var expectedContainer: Color = Color.Unspecified

        composeRule.setContent {
            AppTheme {
                density = LocalDensity.current
                expectedContainer = AppTheme.colors.surfaceVariant
                Column {
                    IconButton(
                        icon = AppTheme.icons.Add,
                        contentDescription = "circle",
                        style = IconButtonStyle.Tonal,
                        shape = IconButtonShape.Circle,
                        modifier = Modifier.testTag(CIRCLE_BUTTON_TAG),
                        onClick = {},
                    )
                    IconButton(
                        icon = AppTheme.icons.Add,
                        contentDescription = "rounded",
                        style = IconButtonStyle.Tonal,
                        shape = IconButtonShape.Rounded,
                        modifier = Modifier.testTag(ROUNDED_BUTTON_TAG),
                        onClick = {},
                    )
                }
            }
        }

        val circlePixels = composeRule.onNodeWithTag(CIRCLE_BUTTON_TAG).captureToImage().toPixelMap()
        val roundedPixels = composeRule.onNodeWithTag(ROUNDED_BUTTON_TAG).captureToImage().toPixelMap()

        // 캡처 노드(48dp)에는 42dp 시각 Surface 주변 터치 여백이 섞여 있으므로, 먼저 밀도
        // 기준으로 Surface의 실제 시작 오프셋을 구한다. 세로 중심선(centerX)은 항상 Circle
        // 내부이므로, 그 지점의 container 색을 관찰해 두 캡처 모두 유효한 위치를 읽고
        // 있는지부터 검증한다(캡처 자체의 유효성 보강).
        val offsetPx = visualSurfaceOffsetPx(density)
        val centerX = circlePixels.width / 2
        val centerReferenceY = offsetPx + density.dpToPx(2.dp)

        assertColorsApproximatelyEqual(
            "Circle 캡처의 기준(중심) 픽셀은 container 색이어야 한다",
            expected = expectedContainer,
            actual = circlePixels[centerX, centerReferenceY],
        )
        assertColorsApproximatelyEqual(
            "Rounded 캡처의 기준(중심) 픽셀은 container 색이어야 한다",
            expected = expectedContainer,
            actual = roundedPixels[centerX, centerReferenceY],
        )

        // Surface 좌상단에서 (4dp, 4dp) 오프셋인 지점: Rounded(radius.large=10dp)의 모서리
        // arc는 corner-circle(반경 10dp, 중심 (10,10)) 안쪽만 유지하므로 이 지점(원점 거리
        // ≈5.7dp)은 여전히 container다. 반면 Circle(반경 21dp, 중심 (21,21))의 같은 지점은
        // 중심에서 거리 ≈24dp로 반경 밖이라 clip돼 container가 아니다.
        val cornerX = offsetPx + density.dpToPx(4.dp)
        val cornerY = offsetPx + density.dpToPx(4.dp)
        val circleCorner = circlePixels[cornerX, cornerY]
        val roundedCorner = roundedPixels[cornerX, cornerY]

        assertFalse(
            "Circle 모서리는 clip으로 container 색이 아니어야 한다",
            colorsMatch(circleCorner, expectedContainer, tolerance = 0.02f),
        )
        assertColorsApproximatelyEqual(
            "Rounded 모서리는 radius.large 코너 arc 안쪽이라 container 색이 남아 있어야 한다",
            expected = expectedContainer,
            actual = roundedCorner,
        )
        assertFalse(
            "Circle과 Rounded의 모서리 렌더 결과는 서로 달라야 한다",
            colorsMatch(circleCorner, roundedCorner, tolerance = 0.02f),
        )
    }

    @Test
    fun `Ghost Role만 selected 색이 변하고 나머지 style은 시각 차이가 없다`() {
        fun unselectedTagOf(style: IconButtonStyle) = "${style}_unselected"
        fun selectedTagOf(style: IconButtonStyle) = "${style}_selected"

        composeRule.setContent {
            AppTheme {
                ConnectionRoleProvider(role = ConnectionRole.Server) {
                    Column {
                        IconButtonStyle.entries.forEach { style ->
                            IconButton(
                                icon = AppTheme.icons.Add,
                                contentDescription = "$style unselected",
                                style = style,
                                tone = IconButtonTone.Role,
                                selected = false,
                                modifier = Modifier.testTag(unselectedTagOf(style)),
                                onClick = {},
                            )
                            IconButton(
                                icon = AppTheme.icons.Add,
                                contentDescription = "$style selected",
                                style = style,
                                tone = IconButtonTone.Role,
                                selected = true,
                                modifier = Modifier.testTag(selectedTagOf(style)),
                                onClick = {},
                            )
                        }
                    }
                }
            }
        }

        IconButtonStyle.entries.forEach { style ->
            val unselectedPixels = composeRule.onNodeWithTag(unselectedTagOf(style)).captureToImage().toPixelMap()
            val selectedPixels = composeRule.onNodeWithTag(selectedTagOf(style)).captureToImage().toPixelMap()

            val unselectedCenter = unselectedPixels[unselectedPixels.width / 2, unselectedPixels.height / 2]
            val selectedCenter = selectedPixels[selectedPixels.width / 2, selectedPixels.height / 2]

            if (style == IconButtonStyle.Ghost) {
                assertFalse(
                    "Ghost+Role은 selected일 때 색이 달라야 한다",
                    colorsMatch(unselectedCenter, selectedCenter, tolerance = 0.02f),
                )
            } else {
                assertTrue(
                    "$style 은 selected 여부와 무관하게 시각 차이가 없어야 한다",
                    colorsMatch(unselectedCenter, selectedCenter, tolerance = 0.02f),
                )
            }
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

    private fun colorsMatch(a: Color, b: Color, tolerance: Float): Boolean =
        abs(a.red - b.red) <= tolerance &&
            abs(a.green - b.green) <= tolerance &&
            abs(a.blue - b.blue) <= tolerance

    /** `IconButtonDefaults.MinimumTouchTarget`(48dp)과 `AppTheme.controlSize.comfortable`(42dp,
     * 기본 `visualSize`)의 차이만큼 캡처 노드 안에서 시각 Surface가 중앙 정렬되는 편도 오프셋. */
    private fun visualSurfaceOffsetPx(density: Density): Int =
        with(density) {
            ((IconButtonDefaults.MinimumTouchTarget - AppTheme.controlSize.comfortable) / 2).roundToPx()
        }

    private fun Density.dpToPx(value: Dp): Int = with(this) { value.roundToPx() }

    private fun hasPixelMatching(
        pixelMap: PixelMap,
        xRange: IntRange,
        yRange: IntRange,
        target: Color,
        tolerance: Float = 0.02f,
    ): Boolean {
        for (x in xRange) {
            for (y in yRange) {
                if (colorsMatch(pixelMap[x, y], target, tolerance)) return true
            }
        }
        return false
    }

    private companion object {
        const val SERVER_BUTTON_TAG = "${IconButtonDefaults.BUTTON_TAG}_server"
        const val CLIENT_BUTTON_TAG = "${IconButtonDefaults.BUTTON_TAG}_client"
        const val CIRCLE_BUTTON_TAG = "${IconButtonDefaults.BUTTON_TAG}_circle"
        const val ROUNDED_BUTTON_TAG = "${IconButtonDefaults.BUTTON_TAG}_rounded"
    }
}
