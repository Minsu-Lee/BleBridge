# 01. DeviceListItem 구현 프롬프트

## 작업 브랜치

`feature/ui/device-list-item` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/ui/device-list-item
```

## Claude Code에게 요청

`core:ui`에 서버의 연결된 클라이언트와 클라이언트의 발견된 서버 목록에서 공유할 device list item을 구현해줘. BLE scan/domain 모델을 직접 받지 말고 표시용 UI 모델만 받는다.

01A 계약: Variant `Server/Client/Phone/TabletCompact`, State `Available/Connecting/Connected/Weak/Disabled`.

## API 샘플

```kotlin
enum class DeviceState {
    Available,
    Connecting,
    Connected,
    Weak,
    Disabled,
}

@Composable
fun DeviceListItem(
    name: String,
    metadata: String,
    role: ConnectionRole,
    state: DeviceState,
    modifier: Modifier = Modifier,
    signalLevel: Int? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object DeviceListItemDefaults {
    const val ITEM_TAG = "blebridge_device_item"
    const val ACTION_TAG = "blebridge_device_action"
    const val SIGNAL_TAG = "blebridge_signal_strength"
    val ContentPadding = 14.dp
    val ItemSpacing = 12.dp
    val DeviceIconContainer = 40.dp
}
```

## 디자인

- surface: Phone `AppTheme.colors.surfacePrimary`, TabletCompact `AppTheme.colors.surfaceSecondary`
- border: Default `AppTheme.colors.borderNormal`, Disabled `AppTheme.colors.disabledBorder`
- radius: `AppTheme.radius.card`
- 토큰 소유권 메모: 구현 시 `radius.card`를 그대로 복사하지 말고 Phone/TabletCompact variant가 같은 radius 변경 축을 공유하는지 확인한다. Device List Item 전용이면 `DeviceListItemDefaults`가 소유한다.
- device icon: `AppTheme.icons.DevicePhone`
- Available: icon container `AppTheme.roleColors.activeContainer`, icon content `AppTheme.roleColors.onActiveContainer`
- Connecting: indicator/content `AppTheme.roleColors.active`, action 차단
- Connected: icon container `AppTheme.colors.successContainer`, icon content `AppTheme.colors.onSuccessContainer`
- Weak: signal `AppTheme.colors.warning`, metadata `AppTheme.colors.textTertiary`
- Disabled: container `AppTheme.colors.disabledContainer`, content `AppTheme.colors.onDisabledContainer`
- `role`은 항목이 나타내는 Server/Client 의미이며 현재 세션의 active role과 혼동하지 않는다.
- title: `AppTheme.typography.labelLarge`
- metadata: `AppTheme.typography.monoSmall`
- signal: `SignalStrength` 또는 4개의 bar를 내부 drawing으로 구현
- action은 `ActionButton` compact 사용
- Connecting은 중복 연결 action을 차단하고 progress/state description을 표시한다.
- Weak/Disabled는 opacity만으로 의미를 전달하지 말고 content color와 semantics를 함께 설정

## 테스트

- name/metadata 표시
- signal level 0~4 clamp
- action 유무 및 click
- available/connecting/connected/weak/disabled semantics
- 긴 이름 ellipsis

## 사용 예시

```kotlin
DeviceListItem(
    name = "BLE-Bridge-Srv",
    metadata = "DC:A6:32:1B:90:E4 · -48 dBm",
    role = ConnectionRole.Server,
    state = DeviceState.Available,
    signalLevel = 4,
    actionLabel = "연결",
    onAction = onConnect,
)
```

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:ui` 내부 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive 접근과 임의 dp는 금지하고, Feature는 완성된 Component API와 전달 가능한 Component token만 사용한다.

## 디자인 참조

- [Device List Item 디자인 PNG 열기](../screenshots/ui/01-device-list-item.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/ui/01-device-list-item.png" alt="01A 컴포넌트 카탈로그 · Device List Item" width="680" />

## 모드 및 테마 가이드

- 채팅 표시 모드와 무관한 컴포넌트다.
- Light/Dark에 따라 surface, border, primary/secondary text token만 전환한다.
- Server/Client role color와 Available/Connected/Weak/Error 상태 색은 theme 축과 독립적으로 적용한다.
- Light/Dark × Server/Client × 주요 상태 Preview를 준비한다.

- HTML 화면명: `기기 연결 · 서버 (광고·대기)`, `기기 연결 · 클라이언트 (스캔)`, 태블릿 양쪽 탭
