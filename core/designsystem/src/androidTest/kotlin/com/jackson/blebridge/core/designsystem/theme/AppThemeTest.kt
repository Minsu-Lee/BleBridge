package com.jackson.blebridge.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import com.jackson.blebridge.core.designsystem.theme.contextual.activity.ConnectionActivityColors
import com.jackson.blebridge.core.designsystem.theme.contextual.activity.ConnectionActivityStatus
import com.jackson.blebridge.core.designsystem.theme.contextual.chat.ChatContext
import com.jackson.blebridge.core.designsystem.theme.contextual.chat.ChatMode
import com.jackson.blebridge.core.designsystem.theme.contextual.chat.ChatTokens
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleColors
import com.jackson.blebridge.core.designsystem.theme.semantic.color.AppColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppThemeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `라이트 테마는 디자인 명세의 배경과 역할 색상을 제공한다`() {
        lateinit var colors: AppColors

        composeRule.setContent {
            AppTheme(darkTheme = false) {
                colors = AppTheme.colors
            }
        }

        composeRule.runOnIdle {
            assertEquals(Color(0xFFFFFFFF), colors.backgroundPrimary)
            assertEquals(Color(0xFF2563EB), colors.server)
            assertEquals(Color(0xFF0E9488), colors.client)
            assertEquals(Color(0xFFEDEFF3), colors.disabledContainer)
            assertEquals(Color(0xFFA3A9B3), colors.onDisabledContainer)
            assertEquals(Color(0xFF7C3AED), colors.advertising)
            assertNotEquals(colors.server, colors.client)
        }
    }

    @Test
    fun `다크 테마는 디자인 명세의 배경과 역할 색상을 제공한다`() {
        lateinit var colors: AppColors

        composeRule.setContent {
            AppTheme(darkTheme = true) {
                colors = AppTheme.colors
            }
        }

        composeRule.runOnIdle {
            assertEquals(Color(0xFF0D1017), colors.backgroundPrimary)
            assertEquals(Color(0xFF4F8DFF), colors.server)
            assertEquals(Color(0xFF2DD4BF), colors.client)
            assertEquals(Color(0xFF5EEAD4), colors.onClientContainer)
            assertEquals(Color(0xFFA78BFA), colors.advertising)
        }
    }

    @Test
    fun `명암 역할 표시 모드의 모든 조합은 일관된 토큰을 제공한다`() {
        val darkTheme = mutableStateOf(false)
        val role = mutableStateOf(ConnectionRole.Server)
        val mode = mutableStateOf(ChatMode.Classic)
        lateinit var roleColors: ConnectionRoleColors
        lateinit var chatTokens: ChatTokens
        var materialPrimary = Color.Unspecified
        var materialSecondary = Color.Unspecified
        var materialPrimaryContainer = Color.Unspecified
        var materialSecondaryContainer = Color.Unspecified

        composeRule.setContent {
            AppTheme(darkTheme = darkTheme.value) {
                ChatContext(role = role.value, mode = mode.value) {
                    roleColors = AppTheme.roleColors
                    chatTokens = AppTheme.chatTokens
                    materialPrimary = MaterialTheme.colorScheme.primary
                    materialSecondary = MaterialTheme.colorScheme.secondary
                    materialPrimaryContainer = MaterialTheme.colorScheme.primaryContainer
                    materialSecondaryContainer = MaterialTheme.colorScheme.secondaryContainer
                }
            }
        }

        listOf(false, true).forEach { isDark ->
            ConnectionRole.entries.forEach { currentRole ->
                ChatMode.entries.forEach { currentMode ->
                    composeRule.runOnIdle {
                        darkTheme.value = isDark
                        role.value = currentRole
                        mode.value = currentMode
                    }
                    composeRule.waitForIdle()

                    assertEquals(currentRole, roleColors.activeRole)
                    assertEquals(currentMode, chatTokens.mode)
                    assertEquals(roleColors.active, materialPrimary)
                    assertEquals(roleColors.peer, materialSecondary)
                    assertEquals(
                        roleColors.activeContainer,
                        materialPrimaryContainer,
                    )
                    assertEquals(
                        roleColors.peerContainer,
                        materialSecondaryContainer,
                    )
                    assertTrue(
                        roleColors.active.contrastRatio(roleColors.onActive) >= 4.5f,
                    )
                    assertTrue(
                        roleColors.activeContainer.contrastRatio(roleColors.onActiveContainer) >= 4.5f,
                    )
                }
            }
        }
    }

    @Test
    fun `채팅 표시 모드는 각 모드의 표현 규칙을 제공한다`() {
        val mode = mutableStateOf(ChatMode.Classic)
        lateinit var tokens: ChatTokens

        composeRule.setContent {
            AppTheme(darkTheme = false) {
                ChatContext(role = ConnectionRole.Server, mode = mode.value) {
                    tokens = AppTheme.chatTokens
                }
            }
        }

        composeRule.runOnIdle {
            assertEquals(false, tokens.showTelemetry)
            assertEquals(false, tokens.showRoleTags)
            assertEquals(false, tokens.terminalLayout)
            assertEquals(11.dp, tokens.messageSpacing)
            assertEquals(16.dp, tokens.bubbleRadius)
            assertEquals(999.dp, tokens.inputRadius)
            mode.value = ChatMode.DeveloperHybrid
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals(true, tokens.showTelemetry)
            assertEquals(true, tokens.showRoleTags)
            assertEquals(false, tokens.terminalLayout)
            assertEquals(12.dp, tokens.messageSpacing)
            assertEquals(999.dp, tokens.inputRadius)
            mode.value = ChatMode.Terminal
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals(true, tokens.showTelemetry)
            assertEquals(true, tokens.showRoleTags)
            assertEquals(true, tokens.terminalLayout)
            assertEquals(3.dp, tokens.messageSpacing)
            assertEquals(10.dp, tokens.bubbleRadius)
            assertEquals(10.dp, tokens.inputRadius)
            assertEquals(15.sp, tokens.headerTextStyle.fontSize)
            assertEquals(FontWeight.SemiBold, tokens.headerTextStyle.fontWeight)
            assertEquals(Color(0xFF12141A), tokens.colors.chromeBackground)
            assertEquals(Color(0xFFFBFBFC), tokens.colors.messageAreaBackground)
        }
    }

    @Test
    fun `클라이언트 전경색은 일반 텍스트 최소 대비율을 충족한다`() {
        lateinit var roleColors: ConnectionRoleColors

        composeRule.setContent {
            AppTheme(darkTheme = false) {
                ChatContext(role = ConnectionRole.Client) {
                    roleColors = AppTheme.roleColors
                }
            }
        }

        composeRule.runOnIdle {
            assertEquals(Color(0xFF0E9488), roleColors.active)
            assertEquals(Color(0xFF0B0D12), roleColors.onActive)
            assertTrue(roleColors.active.contrastRatio(roleColors.onActive) >= 4.5f)
        }
    }

    @Test
    fun `BLE 동작 상태는 역할과 독립된 semantic color를 제공한다`() {
        val darkTheme = mutableStateOf(false)
        lateinit var activityColors: ConnectionActivityColors
        lateinit var foundationColors: AppColors

        composeRule.setContent {
            AppTheme(darkTheme = darkTheme.value) {
                activityColors = AppTheme.activityColors
                foundationColors = AppTheme.colors
            }
        }

        listOf(false, true).forEach { isDark ->
            composeRule.runOnIdle {
                darkTheme.value = isDark
            }
            composeRule.waitForIdle()
            composeRule.runOnIdle {
                ConnectionActivityStatus.entries
                    .filterNot { it == ConnectionActivityStatus.Idle }
                    .forEach { status ->
                    val colors = activityColors.forStatus(status)
                    assertTrue(colors.container.contrastRatio(colors.onContainer) >= 4.5f)
                    }

                assertEquals(
                    foundationColors.server,
                    activityColors.connecting.accent,
                )
                assertEquals(
                    foundationColors.client,
                    activityColors.scanning.accent,
                )
                assertEquals(
                    foundationColors.advertising,
                    activityColors.advertising.accent,
                )
                assertEquals(
                    foundationColors.disabledContainer,
                    activityColors.idle.container,
                )
            }
        }
    }

    @Test
    fun `foundation scale과 motion은 전체 화면 명세 값을 제공한다`() {
        composeRule.setContent {
            AppTheme {
                assertEquals(22.sp, AppTheme.typography.screenTitle.fontSize)
                assertEquals(15.sp, AppTheme.typography.monoTitle.fontSize)
                assertEquals(48.dp, AppTheme.controlSize.minimumTouchTarget)
                assertEquals(14.dp, AppTheme.radius.card)
                assertEquals(24.dp, AppTheme.spacing.screen)
                assertEquals(2_000, AppTheme.motion.clientPulseMillis)
                assertEquals(2_400, AppTheme.motion.brandPulseMillis)
                assertEquals(0.6f, AppTheme.motion.activityPulseStartScale)
                assertEquals(2.6f, AppTheme.motion.activityPulseEndScale)
                assertEquals(0.15f, AppTheme.motion.activityPulseAlpha)
                assertTrue(AppTheme.activityMotion.usesPulse(ConnectionActivityStatus.Scanning))
                assertTrue(AppTheme.activityMotion.usesPulse(ConnectionActivityStatus.Advertising))
                assertEquals(false, AppTheme.activityMotion.usesPulse(ConnectionActivityStatus.Idle))
                assertEquals(false, AppTheme.activityMotion.usesPulse(ConnectionActivityStatus.Connecting))
                assertEquals(Color(0xFF050607), AppTheme.media.chromeBackground)
                assertEquals(Color.White, AppTheme.media.content)
                assertEquals(Color(0xFF9AA2B0), AppTheme.media.mutedContent)
                assertEquals(Color(0xFF343943), AppTheme.media.controlTrack)
                assertEquals(104.dp, AppTheme.iconSize.splash)
            }
        }
    }

    @Test
    fun `디자인시스템에 포함된 글꼴과 아이콘을 사용할 수 있다`() {
        composeRule.setContent {
            AppTheme {
                assertNotEquals(
                    AppTheme.typography.bodyLarge.fontFamily,
                    AppTheme.typography.monoLarge.fontFamily,
                )
                assertEquals("AppLogo", AppTheme.icons.Logo.name)
                assertEquals("Send", AppTheme.icons.Send.name)
                assertEquals(24.dp, AppTheme.icons.Send.defaultWidth)
                assertEquals("Chat", AppTheme.icons.Chat.name)
                assertEquals("Stop", AppTheme.icons.Stop.name)
                assertEquals("DevicePhone", AppTheme.icons.DevicePhone.name)
                assertEquals("SignalStrength", AppTheme.icons.SignalStrength.name)
                assertEquals("Pause", AppTheme.icons.Pause.name)
                assertEquals("SkipBackward", AppTheme.icons.SkipBackward.name)
                assertEquals("SkipForward", AppTheme.icons.SkipForward.name)
                assertEquals("Settings", AppTheme.icons.Settings.name)
                listOf(
                    AppTheme.icons.Add,
                    AppTheme.icons.Send,
                    AppTheme.icons.Back,
                    AppTheme.icons.Close,
                    AppTheme.icons.Check,
                    AppTheme.icons.Chat,
                    AppTheme.icons.Download,
                    AppTheme.icons.SwitchRole,
                    AppTheme.icons.Retry,
                    AppTheme.icons.Settings,
                    AppTheme.icons.File,
                    AppTheme.icons.Image,
                    AppTheme.icons.Video,
                    AppTheme.icons.Camera,
                    AppTheme.icons.Play,
                    AppTheme.icons.Stop,
                    AppTheme.icons.Pause,
                    AppTheme.icons.SkipBackward,
                    AppTheme.icons.SkipForward,
                    AppTheme.icons.DevicePhone,
                    AppTheme.icons.SignalStrength,
                    AppTheme.icons.More,
                    AppTheme.icons.Error,
                ).forEach { icon ->
                    assertEquals(24.dp, icon.defaultWidth)
                    assertEquals(24.dp, icon.defaultHeight)
                }
            }
        }
    }
}

private fun Color.contrastRatio(other: Color): Float {
    val lighter = maxOf(luminance(), other.luminance())
    val darker = minOf(luminance(), other.luminance())
    return (lighter + 0.05f) / (darker + 0.05f)
}
