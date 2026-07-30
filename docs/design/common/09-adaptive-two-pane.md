# 09. AdaptiveTwoPaneScaffold 구현 프롬프트

## 작업 브랜치

`feature/designsystem/adaptive-two-pane` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/adaptive-two-pane
```

## Claude Code에게 요청

`core:designsystem`에 태블릿의 기기 목록+상세, 채팅 목록+대화, 설정 2열 표현에 재사용할 adaptive two-pane scaffold를 구현해줘. 하위 content의 비즈니스 모델을 알지 않는 layout primitive여야 한다.

01A 계약: Variant `Single/Dual/FixedPrimary/WeightedSecondary`, State/size class `Compact/Medium/Expanded`.

기기 연결을 primary pane으로 사용하는 Classic/Developer Hybrid/Terminal 채팅 레이아웃에서는 primary pane AppBar에 `기기 연결` 제목과 Settings action을 조합한다. Settings action은 pane scaffold가 소유하지 않고 호출자가 `AppBar` 슬롯과 callback으로 제공한다.

채팅 2단 레이아웃의 primary pane에는 서버/클라이언트 전환 segmented control이나 기기 추가 `+` action을 배치하지 않는다. 연결 역할 선택과 탐색 시작은 기기 연결 Feature 화면에서 수행하고, 채팅 화면의 primary pane은 현재 연결 목록과 설정 진입만 제공한다.

## API 샘플

```kotlin
enum class PaneMode {
    SinglePrimary,
    SingleSecondary,
    Dual,
}

sealed interface PrimaryPaneSizing {
    data class Fixed(val width: Dp) : PrimaryPaneSizing
    data class Weighted(val weight: Float) : PrimaryPaneSizing
}

@Composable
fun AdaptiveTwoPaneScaffold(
    primaryPane: @Composable () -> Unit,
    secondaryPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    paneMode: PaneMode,
    primaryPaneSizing: PrimaryPaneSizing =
        PrimaryPaneSizing.Fixed(
            AdaptiveTwoPaneDefaults.DefaultPrimaryPaneWidth,
        ),
    divider: Boolean = true,
)
```

`WindowSizeClass`를 직접 받기보다 Feature 또는 상위 layout이 계산한 `paneMode`를 받는다.

## Defaults 샘플

```kotlin
internal object AdaptiveTwoPaneDefaults {
    const val SCAFFOLD_TAG = "blebridge_adaptive_two_pane"
    const val PRIMARY_PANE_TAG = "blebridge_primary_pane"
    const val SECONDARY_PANE_TAG = "blebridge_secondary_pane"
    val DefaultPrimaryPaneWidth = 330.dp
    val CompactPrimaryPaneWidth = 300.dp
    val DividerWidth = 1.dp
}
```

## 레이아웃 규칙

- two-pane: 고정 또는 제한된 primary pane + weight 1 secondary pane
- single-pane: 호출자가 선택한 pane 하나만 표시할 수 있도록 별도 overload나 enum 고려
- divider: `AppTheme.colors.borderSubtle`
- primary background: `AppTheme.colors.surfaceSecondary`
- secondary background: `AppTheme.colors.backgroundPrimary`
- 각 pane은 자체 inset/scroll을 소유하도록 scaffold가 강제 padding하지 않는다.
- tablet 900×600 디자인에서 primary 300~330dp
- RTL에서 pane 순서/경계가 자연스럽게 동작해야 한다.

## 테스트

- two-pane에서 두 content 노출
- single-pane 정책
- primary width
- divider on/off
- RTL
- 작은 폭에서 overflow 없음

## 사용 예시

```kotlin
AdaptiveTwoPaneScaffold(
    paneMode = if (isExpanded) PaneMode.Dual else PaneMode.SinglePrimary,
    primaryPane = { DeviceListPane() },
    secondaryPane = { ConversationPane() },
)
```

## 완료 조건

- Device, Chat, Settings 모델에 의존하지 않는다.
- 다른 `core:designsystem` 및 `core:ui` 컴포넌트를 pane content에서 조합할 수 있지만 scaffold에서 이를 직접 참조하지 않는다.
- phone/tablet Preview를 제공한다.

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Adaptive Two Pane 디자인 PNG 열기](../screenshots/common/09-adaptive-two-pane.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/09-adaptive-two-pane.png" alt="01A 컴포넌트 카탈로그 · Adaptive Two Pane" width="680" />

- HTML 화면명: `기기 연결 · 태블릿`, `태블릿 · 2단 · 클래식/하이브리드/터미널`, `태블릿 · 설정 다이얼로그`
