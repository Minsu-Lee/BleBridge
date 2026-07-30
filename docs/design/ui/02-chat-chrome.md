# 02. ChatChrome 구현 프롬프트

## 작업 브랜치

`feature/ui/chat-chrome` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/ui/chat-chrome
```

## Claude Code에게 요청

채팅 화면에서 반복되는 role badge와 date divider를 `core:ui`의 chat chrome 컴포넌트로 구현해줘. AppBar와 header layout은 `core:designsystem`의 `AppBar` custom title variant가 담당한다. Classic/DeveloperHybrid/Terminal 표현 차이는 `AppTheme.chatTokens`로 해결한다.

01A 계약: Variant `Header/RoleBadge/DateDivider`, Mode `Classic/DeveloperHybrid/Terminal`.

## API 샘플

```kotlin
@Composable
fun ChatHeaderTitle(
    title: String,
    subtitle: String?,
    role: ConnectionRole,
    connectionStateLabel: String?,
    modifier: Modifier = Modifier,
)

@Composable
fun ConnectionRoleBadge(
    role: ConnectionRole,
    compact: Boolean = false,
)

@Composable
fun DateDivider(
    label: String,
    modifier: Modifier = Modifier,
)
```

## Defaults 샘플

```kotlin
internal object ChatChromeDefaults {
    const val ROLE_BADGE_TAG = "blebridge_role_badge"
    const val DATE_DIVIDER_TAG = "blebridge_date_divider"
    val HeaderHorizontalPadding = 16.dp
    val HeaderVerticalPadding = 11.dp
    val BadgeHorizontalPadding = 7.dp
    val BadgeVerticalPadding = 2.dp
}
```

## 토큰

- Header는 `AppBar`의 custom title 슬롯에 넣는 content이며 자체 AppBar를 만들지 않는다.
- Classic은 status dot + 2줄 title, Hybrid는 role badge와 title 동일 baseline, Terminal은 `AppTheme.chatTokens.headerTextStyle`과 `metadataTextStyle`을 사용한다.
- metadata: `AppTheme.chatTokens.metadataTextStyle`
- role badge: container `AppTheme.roleColors.active`, content `AppTheme.roleColors.onActive`
- divider: `AppTheme.chatTokens.colors.divider`
- date chip: container `AppTheme.chatTokens.colors.chromeSurface`, content `AppTheme.chatTokens.colors.onChromeMuted`
- Terminal도 `ColorPalette`를 직접 참조하지 않고 `AppTheme.chatTokens.colors.chromeBackground`, `chromeSurface`, `onChrome`, `onChromeMuted`를 사용한다.

## 테스트

- 세 모드별 role badge/date divider 표현
- 세 모드별 Header title hierarchy와 긴 제목 ellipsis
- Header에 설정 및 역할 전환 action이 포함되지 않는지 확인
- Server/Client role badge
- date divider label
- Light/Dark 대비

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:ui` 내부 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive 접근과 임의 dp는 금지하고, Feature는 완성된 Component API와 전달 가능한 Component token만 사용한다.

## 디자인 참조

- [Chat Chrome 디자인 PNG 열기](../screenshots/ui/02-chat-chrome.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/ui/02-chat-chrome.png" alt="01A 컴포넌트 카탈로그 · Chat Chrome" width="680" />

- HTML 화면명: `클래식 메신저 모드`
- HTML 화면명: `개발 하이브리드 모드`
- HTML 화면명: `터미널 로그형 모드`
