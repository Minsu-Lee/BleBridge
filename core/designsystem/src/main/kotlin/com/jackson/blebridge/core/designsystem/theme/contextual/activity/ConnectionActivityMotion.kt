package com.jackson.blebridge.core.designsystem.theme.contextual.activity

import androidx.compose.runtime.Immutable

/**
 * BLE 동작 상태에 따라 시맨틱 모션 토큰을 적용할지 결정하는 컨텍스트 정책입니다.
 *
 * 시간과 easing 값은 [com.jackson.blebridge.core.designsystem.theme.semantic.motion.Motion]이
 * 소유하고, 상태별 적용 여부만 이 계층에서 결정합니다.
 */
@Immutable
object ConnectionActivityMotion {
    fun usesPulse(status: ConnectionActivityStatus): Boolean = when (status) {
        ConnectionActivityStatus.Advertising,
        ConnectionActivityStatus.Scanning,
        -> true

        ConnectionActivityStatus.Idle,
        ConnectionActivityStatus.Connecting,
        -> false
    }
}
