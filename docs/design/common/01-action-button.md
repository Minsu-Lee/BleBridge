# 01. ActionButton 구현 프롬프트

## 작업 브랜치

`feature/designsystem/action-button` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/action-button
```

## Claude Code에게 요청

`core:designsystem`에 BLE Bridge 전 화면에서 재사용할 action button을 구현해줘. 디자인 기준은 [`BLETransferApp.dc.html`](../BLETransferApp.dc.html)의 서버 시작/중단, 스캔 시작/중단, 채팅 열기, 연결, 재시도 버튼이다.

01A 계약: Variant `RolePrimary/Neutral/Destructive/Text`, Size `Compact/Default/FullWidth`, State `Enabled/Pressed/Disabled/Loading`.

## 구현 위치

```text
core/designsystem/src/main/kotlin/com/jackson/blebridge/core/designsystem/component/button/
├── ActionButton.kt
└── ActionButtonDefaults.kt
core/designsystem/src/androidTest/.../component/button/ActionButtonTest.kt
```

## 공개 API 샘플

```kotlin
enum class ActionButtonStyle {
    RolePrimary,
    Neutral,
    Destructive,
    Text,
}

enum class ActionButtonSize {
    Compact,
    Default,
    FullWidth,
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ActionButtonStyle = ActionButtonStyle.RolePrimary,
    size: ActionButtonSize = ActionButtonSize.Default,
    leadingIcon: ImageVector? = null,
    loading: Boolean = false,
)
```

## Defaults 샘플

해당 컴포넌트 패키지의 `internal object <Component>Defaults`에 구현 세부값과 test tag를 모아줘.

```kotlin
internal object ActionButtonDefaults {
    const val BUTTON_TAG = "blebridge_action_button"
    const val ICON_TAG = "blebridge_action_button_icon"
    val DefaultHeight = 48.dp
    val CompactHeight = 40.dp
    val HorizontalPadding = 20.dp
    val IconSpacing = 8.dp
}
```

## 디자인 및 토큰

- RolePrimary: container `AppTheme.roleColors.active`, content `AppTheme.roleColors.onActive`
- Destructive: container `AppTheme.colors.errorContainer`, content `AppTheme.colors.onErrorContainer`
- Neutral: container `AppTheme.colors.surfaceVariant`, content `AppTheme.colors.textSecondary`
- Disabled: container `AppTheme.colors.disabledContainer`, content `AppTheme.colors.onDisabledContainer`, border `AppTheme.colors.disabledBorder`
- shape: `AppTheme.radius.xLarge`
- text: `AppTheme.typography.titleSmall`, Compact는 `AppTheme.typography.labelLarge`
- 최소 터치 영역: `AppTheme.controlSize.minimumTouchTarget`
- 아이콘: `AppTheme.icons.Play`, `AppTheme.icons.Stop`, `AppTheme.icons.Chat`, `AppTheme.icons.Retry`
- Client RolePrimary는 반드시 `onClient = #0B0D12`를 사용하고 흰색을 하드코딩하지 않는다.

## 상태

- enabled/disabled
- leading icon 유무
- FullWidth 및 Compact/Default wrap content
- destructive stop/retry
- Server/Client provider에 따른 role color
- loading 중에는 progress content를 표시하고 click을 차단하며 loading state description을 제공한다.
- `Text`는 container 없이 semantic content color만 사용한다.

## 테스트

- enabled일 때 click 1회 전달
- disabled일 때 click 미전달 및 disabled semantics
- Server/Client role별 content color
- destructive/neutral color
- icon content description 처리
- 최소 터치 크기 48dp
- loading 표시, click 차단, state description

## 사용 예시

```kotlin
ActionButton(
    text = "스캔 시작",
    leadingIcon = AppTheme.icons.Play,
    onClick = onStartScan,
)

ActionButton(
    text = "스캔 중단",
    leadingIcon = AppTheme.icons.Stop,
    style = ActionButtonStyle.Destructive,
    onClick = onStopScan,
)
```

## 완료 조건

- public API에 raw `Color`, `TextStyle`, magic dp가 불필요하게 노출되지 않는다.
- Preview는 Light/Dark × Server/Client × enabled/disabled를 포함한다.
- `compileDebugKotlin`, `lintDebug`, `assembleDebugAndroidTest`가 성공한다.

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Action Button 디자인 PNG 열기](../screenshots/common/01-action-button.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/01-action-button.png" alt="01A 컴포넌트 카탈로그 · Action Button" width="680" />

- HTML 화면명: `기기 연결 · 서버 (첫 진입·중지)`, `기기 연결 · 클라이언트 (스캔)`
- 채팅 입력 action 참고: HTML 화면명 `클래식 메신저 모드`, `개발 하이브리드 모드`, `터미널 로그형 모드`
