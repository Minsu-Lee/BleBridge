# 02. IconButton 구현 프롬프트

## 작업 브랜치

`feature/designsystem/icon-button` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/icon-button
```

## Claude Code에게 요청

`core:designsystem`에 원형·둥근 사각형 icon-only action을 통합하는 `IconButton`을 구현해줘. 디자인 기준은 채팅 입력의 첨부/전송, 기기 연결의 설정, 이미지·영상 닫기, 이미지 다운로드, 미디어 재생 제어다. 채팅 상세의 설정 및 서버/클라이언트 전환 action은 현재 디자인 범위에 포함하지 않는다.

01A 계약: Variant `Filled/Tonal/Outlined/Ghost`, Shape `Circle/Rounded`, State `Enabled/Pressed/Selected/Disabled`.

## API 샘플

```kotlin
enum class IconButtonStyle {
    Filled,
    Tonal,
    Outlined,
    Ghost,
}

enum class IconButtonTone {
    Role,
    Neutral,
    Inverse,
}

enum class IconButtonShape {
    Circle,
    Rounded,
}

@Composable
fun IconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: IconButtonStyle = IconButtonStyle.Tonal,
    tone: IconButtonTone = IconButtonTone.Neutral,
    shape: IconButtonShape = IconButtonShape.Circle,
    visualSize: Dp = AppTheme.controlSize.comfortable,
    selected: Boolean = false,
)
```

## Defaults 샘플

```kotlin
internal object IconButtonDefaults {
    const val BUTTON_TAG = "blebridge_icon_button"
    const val ICON_TAG = "blebridge_icon_button_icon"
    val IconSize = 20.dp
    val BorderWidth = 1.dp
    val MinimumTouchTarget = 48.dp
}
```

## 토큰

- Filled + Role: container `AppTheme.roleColors.active`, content `AppTheme.roleColors.onActive`
- Tonal + Role: container `AppTheme.roleColors.activeContainer`, content `AppTheme.roleColors.onActiveContainer`
- Tonal + Neutral: container `AppTheme.colors.surfaceVariant`, content `AppTheme.colors.iconSecondary`
- Outlined: transparent container + border `AppTheme.colors.borderNormal` + content `AppTheme.colors.iconSecondary`
- Ghost: transparent container + content `AppTheme.colors.iconSecondary`; Role selected 상태는 `AppTheme.roleColors.active`
- Inverse tone: container `AppTheme.media.chromeBackground`, content `AppTheme.media.content`
- Disabled: container `AppTheme.colors.disabledContainer`, content `AppTheme.colors.onDisabledContainer`, border `AppTheme.colors.disabledBorder`
- Circle: `AppTheme.radius.full`, Rounded: `AppTheme.radius.large`
- icon은 반드시 `AppTheme.icons` 사용

## 접근성

- `contentDescription`은 필수 non-null
- visual size가 38~42dp여도 touch target은 48dp
- disabled semantics와 click 차단
- media viewer의 inverse 버튼은 충분한 scrim 대비 확보

## 테스트

- click/enabled/disabled
- content description 탐색 가능
- 48dp touch target
- Filled + Role tone의 Server/Client 색
- Circle/Rounded shape
- Filled/Tonal/Outlined/Ghost × selected/unselected

## 사용 예시

```kotlin
IconButton(
    icon = AppTheme.icons.Add,
    contentDescription = "첨부",
    style = IconButtonStyle.Outlined,
    onClick = onAttachment,
)
```

## 완료 조건

- Material `IconButton` 기본 색을 그대로 사용하지 말고 semantic token을 적용한다.
- Preview에 Add, Send, Settings, Close, Download, Pause를 포함한다.
- Close는 이미지와 영상 AppBar, Download는 이미지 AppBar 전용 조합도 Preview로 검증한다.

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Icon Button 디자인 PNG 열기](../screenshots/common/02-icon-button.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/02-icon-button.png" alt="01A 컴포넌트 카탈로그 · Icon Button" width="680" />

- HTML 화면명: `클래식 메신저 모드`, `개발 하이브리드 모드`, `터미널 로그형 모드`
- HTML 화면명: `이미지 상세 · 핀치 줌`, `영상 플레이어`
