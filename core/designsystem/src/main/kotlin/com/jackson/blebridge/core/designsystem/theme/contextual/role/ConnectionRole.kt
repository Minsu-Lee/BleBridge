package com.jackson.blebridge.core.designsystem.theme.contextual.role

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.designsystem.theme.semantic.color.AppColors

enum class ConnectionRole {
    Server,
    Client,
}

@Immutable
data class ConnectionRoleColors(
    val activeRole: ConnectionRole,
    val active: Color,
    val onActive: Color,
    val activeContainer: Color,
    val onActiveContainer: Color,
    val peer: Color,
    val onPeer: Color,
    val peerContainer: Color,
    val onPeerContainer: Color,
)

internal val LocalConnectionRoleColors = staticCompositionLocalOf<ConnectionRoleColors> {
    error("BleBridge role colors are only available inside ConnectionRoleProvider or ChatContext")
}

@Composable
fun ConnectionRoleProvider(
    role: ConnectionRole,
    content: @Composable () -> Unit,
) {
    val roleColors = AppTheme.colors.forRole(role)
    val roleAwareMaterialColors = MaterialTheme.colorScheme.copy(
        primary = roleColors.active,
        onPrimary = roleColors.onActive,
        primaryContainer = roleColors.activeContainer,
        onPrimaryContainer = roleColors.onActiveContainer,
        secondary = roleColors.peer,
        onSecondary = roleColors.onPeer,
        secondaryContainer = roleColors.peerContainer,
        onSecondaryContainer = roleColors.onPeerContainer,
    )

    CompositionLocalProvider(LocalConnectionRoleColors provides roleColors) {
        MaterialTheme(
            colorScheme = roleAwareMaterialColors,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            content = content,
        )
    }
}

internal fun AppColors.forRole(role: ConnectionRole): ConnectionRoleColors = when (role) {
    ConnectionRole.Server -> ConnectionRoleColors(
        activeRole = role,
        active = server,
        onActive = onServer,
        activeContainer = serverContainer,
        onActiveContainer = onServerContainer,
        peer = client,
        onPeer = onClient,
        peerContainer = clientContainer,
        onPeerContainer = onClientContainer,
    )

    ConnectionRole.Client -> ConnectionRoleColors(
        activeRole = role,
        active = client,
        onActive = onClient,
        activeContainer = clientContainer,
        onActiveContainer = onClientContainer,
        peer = server,
        onPeer = onServer,
        peerContainer = serverContainer,
        onPeerContainer = onServerContainer,
    )
}
