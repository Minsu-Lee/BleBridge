package com.jackson.blebridge.core.designsystem.theme.semantic.radius

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
object Radius {
    val none = 0.dp
    val xSmall = 4.dp
    val small = 6.dp
    val medium = 8.dp
    val large = 10.dp
    val xLarge = 12.dp
    /*
     * 아래 값은 크기 단계가 아니라 현재 디자인에서 반복되는 UI 용도를 표현하는
     * 시맨틱 별칭이다. 대응 컴포넌트가 아직 구현되지 않았으므로 프롬프트가 참조할
     * 기준값으로 우선 유지한다.
     *
     * 공통 컴포넌트 구현 시 소유권을 다음과 같이 다시 검토한다.
     * - card: EmptyState, SettingsField, DeviceListItem 및 ChatTokens.cardRadius
     * - bubble: MessageBubble 및 ChatTokens.bubbleRadius
     * - input: ChoiceDialog와 입력 계열 컴포넌트. 현재 Chat Input은 full을 사용하므로
     *   이름과 실제 용도가 일치하는지 반드시 확인한다.
     * - logo: Splash/Logo와 AttachmentSheet 상단 shape. 서로 같은 변경 축인지 확인한다.
     *
     * 각 값이 하나의 컴포넌트군에서만 변경되는 것으로 확인되고 해당 Defaults/Tokens가
     * 실제로 생성된 뒤에만 Component 계층으로 이동한다. 여러 컴포넌트가 같은 디자인
     * 규칙으로 함께 변경되는 값이면 Semantic alias로 유지하거나 더 정확한 이름으로
     * 변경한다.
     *
     * 이동·이름 변경 시 Kotlin 참조, Material Shapes 매핑, Android 테스트, common/ui
     * 구현 프롬프트, README, BLETransferApp.dc.html의 표시 JSON과 다운로드 JSON을
     * 같은 작업에서 원자적으로 갱신한다.
     */
    val card = 14.dp
    val bubble = 16.dp
    val input = 20.dp
    val logo = 24.dp
    val full = 999.dp
}
