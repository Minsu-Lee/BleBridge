package com.jackson.blebridge.core.designsystem.theme.contextual.activity

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.jackson.blebridge.core.designsystem.theme.semantic.color.AppColors

enum class ConnectionActivityStatus {
    Idle,
    Connecting,
    Advertising,
    Scanning,
}

@Immutable
data class ConnectionActivityStatusColors(
    val accent: Color,
    val container: Color,
    val onContainer: Color,
    val border: Color,
)

@Immutable
data class ConnectionActivityColors(
    val idle: ConnectionActivityStatusColors,
    val connecting: ConnectionActivityStatusColors,
    val advertising: ConnectionActivityStatusColors,
    val scanning: ConnectionActivityStatusColors,
) {
    fun forStatus(status: ConnectionActivityStatus): ConnectionActivityStatusColors = when (status) {
        ConnectionActivityStatus.Idle -> idle
        ConnectionActivityStatus.Connecting -> connecting
        ConnectionActivityStatus.Advertising -> advertising
        ConnectionActivityStatus.Scanning -> scanning
    }
}

internal fun AppColors.toActivityColors() = ConnectionActivityColors(
    idle = ConnectionActivityStatusColors(
        accent = iconDisabled,
        container = disabledContainer,
        onContainer = onDisabledContainer,
        border = disabledBorder,
    ),
    connecting = ConnectionActivityStatusColors(
        accent = server,
        container = serverContainer,
        onContainer = onServerContainer,
        border = serverContainer,
    ),
    advertising = ConnectionActivityStatusColors(
        accent = advertising,
        container = advertisingContainer,
        onContainer = onAdvertisingContainer,
        border = advertising,
    ),
    scanning = ConnectionActivityStatusColors(
        accent = client,
        container = clientContainer,
        onContainer = onClientContainer,
        border = clientContainer,
    ),
)
