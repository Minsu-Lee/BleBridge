package com.jackson.blebridge.core.designsystem.theme.contextual.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRole
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleColors
import com.jackson.blebridge.core.designsystem.theme.contextual.role.ConnectionRoleProvider
import com.jackson.blebridge.core.designsystem.theme.contextual.role.LocalConnectionRoleColors
import com.jackson.blebridge.core.designsystem.theme.primitive.color.ColorPalette

enum class ChatMode {
    Classic,
    DeveloperHybrid,
    Terminal,
}

@Immutable
data class ChatColors(
    val screenBackground: Color,
    val chromeBackground: Color,
    val chromeSurface: Color,
    val onChrome: Color,
    val onChromeMuted: Color,
    val messageAreaBackground: Color,
    val ownBubble: Color,
    val onOwnBubble: Color,
    val peerBubble: Color,
    val onPeerBubble: Color,
    val inputBackground: Color,
    val inputContent: Color,
    val timestamp: Color,
    val divider: Color,
)

@Immutable
data class ChatTokens(
    val mode: ChatMode,
    val colors: ChatColors,
    val headerTextStyle: TextStyle,
    val messageTextStyle: TextStyle,
    val metadataTextStyle: TextStyle,
    val inputTextStyle: TextStyle,
    val contentHorizontalPadding: Dp,
    val contentVerticalPadding: Dp,
    val messageSpacing: Dp,
    val bubbleRadius: Dp,
    val cardRadius: Dp,
    val inputRadius: Dp,
    val actionSize: Dp,
    val showTelemetry: Boolean,
    val showRoleTags: Boolean,
    val terminalLayout: Boolean,
)

internal val LocalChatTokens = staticCompositionLocalOf<ChatTokens> {
    error("Chat tokens are only available inside ChatModeProvider or ChatContext")
}

@Composable
fun ChatModeProvider(
    mode: ChatMode = ChatMode.DeveloperHybrid,
    content: @Composable () -> Unit,
) {
    val roleColors = LocalConnectionRoleColors.current
    val tokens = createChatTokens(mode = mode, roleColors = roleColors)
    CompositionLocalProvider(LocalChatTokens provides tokens, content = content)
}

/**
 * Provides both role and chat presentation tokens for an isolated chat composition.
 *
 * Prefer [ConnectionRoleProvider] at the role-aware app or session scope and
 * [ChatModeProvider] at the chat route in the production navigation flow. This
 * convenience API is intended for previews, UI tests, or standalone chat content that owns both
 * values.
 */
@Composable
fun ChatContext(
    role: ConnectionRole,
    mode: ChatMode = ChatMode.DeveloperHybrid,
    content: @Composable () -> Unit,
) {
    ConnectionRoleProvider(role = role) {
        ChatModeProvider(mode = mode, content = content)
    }
}

@Composable
private fun createChatTokens(
    mode: ChatMode,
    roleColors: ConnectionRoleColors,
): ChatTokens {
    val base = AppTheme.colors
    val type = AppTheme.typography
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val controlSize = AppTheme.controlSize

    return when (mode) {
        ChatMode.Classic -> ChatTokens(
            mode = mode,
            colors = ChatColors(
                screenBackground = base.backgroundPrimary,
                chromeBackground = base.surfacePrimary,
                chromeSurface = base.surfaceVariant,
                onChrome = base.textPrimary,
                onChromeMuted = base.textTertiary,
                messageAreaBackground = base.backgroundSecondary,
                ownBubble = roleColors.active,
                onOwnBubble = roleColors.onActive,
                peerBubble = base.surfacePrimary,
                onPeerBubble = base.textPrimary,
                inputBackground = base.inputBackground,
                inputContent = base.textPlaceholder,
                timestamp = base.textTertiary,
                divider = base.borderSubtle,
            ),
            headerTextStyle = type.titleMedium,
            messageTextStyle = type.bodyLarge,
            metadataTextStyle = type.labelSmall,
            inputTextStyle = type.bodyMedium,
            contentHorizontalPadding = 14.dp,
            contentVerticalPadding = 18.dp,
            messageSpacing = 11.dp,
            bubbleRadius = radius.bubble,
            cardRadius = radius.card,
            inputRadius = radius.full,
            actionSize = controlSize.comfortable,
            showTelemetry = false,
            showRoleTags = false,
            terminalLayout = false,
        )

        ChatMode.DeveloperHybrid -> ChatTokens(
            mode = mode,
            colors = ChatColors(
                screenBackground = base.backgroundPrimary,
                chromeBackground = base.surfacePrimary,
                chromeSurface = base.surfaceVariant,
                onChrome = base.textPrimary,
                onChromeMuted = base.textSecondary,
                messageAreaBackground = base.surfaceSecondary,
                ownBubble = roleColors.active,
                onOwnBubble = roleColors.onActive,
                peerBubble = roleColors.peerContainer,
                onPeerBubble = roleColors.onPeerContainer,
                inputBackground = base.inputBackground,
                inputContent = base.textPlaceholder,
                timestamp = base.textTertiary,
                divider = base.borderSubtle,
            ),
            headerTextStyle = type.titleSmall,
            messageTextStyle = type.bodyLarge,
            metadataTextStyle = type.monoSmall,
            inputTextStyle = type.bodyMedium,
            contentHorizontalPadding = 14.dp,
            contentVerticalPadding = spacing.xxl,
            messageSpacing = spacing.xl,
            bubbleRadius = radius.bubble,
            cardRadius = radius.card,
            inputRadius = radius.full,
            actionSize = controlSize.comfortable,
            showTelemetry = true,
            showRoleTags = true,
            terminalLayout = false,
        )

        ChatMode.Terminal -> ChatTokens(
            mode = mode,
            colors = ChatColors(
                screenBackground = base.backgroundPrimary,
                chromeBackground = ColorPalette.darkChrome,
                chromeSurface = ColorPalette.darkOutline,
                onChrome = ColorPalette.white,
                onChromeMuted = ColorPalette.gray550,
                messageAreaBackground = if (base.backgroundPrimary == ColorPalette.white) {
                    ColorPalette.gray25
                } else {
                    ColorPalette.black
                },
                ownBubble = roleColors.active,
                onOwnBubble = roleColors.onActive,
                peerBubble = base.surfacePrimary,
                onPeerBubble = base.textPrimary,
                inputBackground = ColorPalette.darkOutline,
                inputContent = ColorPalette.gray550,
                timestamp = if (base.backgroundPrimary == ColorPalette.white) {
                    ColorPalette.gray400
                } else {
                    ColorPalette.darkTextMuted
                },
                divider = base.borderNormal,
            ),
            headerTextStyle = type.monoTitle,
            messageTextStyle = type.monoLarge,
            metadataTextStyle = type.monoSmall,
            inputTextStyle = type.monoLarge,
            contentHorizontalPadding = spacing.xl,
            contentVerticalPadding = spacing.xl,
            messageSpacing = 3.dp,
            bubbleRadius = radius.large,
            cardRadius = radius.large,
            inputRadius = radius.large,
            actionSize = controlSize.default,
            showTelemetry = true,
            showRoleTags = true,
            terminalLayout = true,
        )
    }
}
