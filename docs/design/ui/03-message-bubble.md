# 03. MessageBubble 구현 프롬프트

## 작업 브랜치

`feature/ui/message-bubble` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/ui/message-bubble
```

## Claude Code에게 요청

`core:ui`에 Classic, DeveloperHybrid, Terminal 채팅에서 공유할 message bubble을 구현해줘. 송수신 방향과 BLE 역할을 혼동하지 말고 `kind`는 Own/Peer/System 정렬과 색, `senderRole`은 role tag 의미로만 사용한다.

01A 계약: Variant `Text/Own/Peer/System`, State `Sending/Sent/Read/Failed`.

## API 샘플

```kotlin
enum class MessageKind {
    Own,
    Peer,
    System,
}

enum class MessageDeliveryState {
    Sending,
    Sent,
    Read,
    Failed,
}

@Composable
fun MessageBubble(
    text: String,
    kind: MessageKind,
    modifier: Modifier = Modifier,
    senderRole: ConnectionRole? = null,
    timestamp: String? = null,
    deliveryState: MessageDeliveryState? = null,
    deliveryLabel: String? = null,
    onRetry: (() -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object MessageBubbleDefaults {
    const val BUBBLE_TAG = "blebridge_message_bubble"
    const val ROLE_TAG = "blebridge_message_role"
    const val METADATA_TAG = "blebridge_message_metadata"
    val HorizontalPadding = 13.dp
    val VerticalPadding = 9.dp
    val MaxWidthFraction = 0.78f
    val TailRadius = 4.dp
}
```

## 토큰과 표현

- own: container `AppTheme.chatTokens.colors.ownBubble`, content `AppTheme.chatTokens.colors.onOwnBubble`
- peer: container `AppTheme.chatTokens.colors.peerBubble`, content `AppTheme.chatTokens.colors.onPeerBubble`
- text: `AppTheme.chatTokens.messageTextStyle`
- metadata: `AppTheme.chatTokens.metadataTextStyle`, timestamp color `AppTheme.chatTokens.colors.timestamp`
- radius: `AppTheme.chatTokens.bubbleRadius`; own/peer 방향에 따라 한쪽 하단만 `TailRadius`
- 토큰 소유권 메모: `radius.bubble`의 최종 소유권은 이 컴포넌트와 `ChatTokens.bubbleRadius` 구현 시 결정한다. 채팅 모드에 따라 바뀌므로 전역 Radius보다 Contextual/Component token을 우선 검토한다.
- System: container `AppTheme.chatTokens.colors.chromeSurface`, content `AppTheme.chatTokens.colors.onChromeMuted`
- System은 중앙 정렬 neutral presentation이며 own/peer role color를 사용하지 않는다.
- Failed에서만 retry action을 노출하고 Sending/Sent/Read는 label 또는 semantics로 전달한다.
- `showRoleTags`가 true이고 `senderRole`이 있으면 `[S]/[C]` 또는 role label 표시
- `terminalLayout`에서는 일반 bubble 대신 dense row 표현을 내부 분기하되 API는 유지
- 긴 URL/파일명도 layout overflow가 없어야 한다.

## 접근성

- bubble 전체를 하나의 읽기 단위로 merge할지 metadata를 분리할지 명시
- delivery icon만으로 상태를 전달하지 않고 label 제공
- Client own bubble은 `onActive`를 사용

## 테스트

- own/peer 정렬과 색
- role tag on/off
- telemetry 숨김 모드
- Terminal dense layout
- timestamp/delivery optional
- Sending/Sent/Read/Failed 및 Failed retry callback
- System presentation

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:ui` 내부 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive 접근과 임의 dp는 금지하고, Feature는 완성된 Component API와 전달 가능한 Component token만 사용한다.

## 디자인 참조

- [Message Bubble 디자인 PNG 열기](../screenshots/ui/03-message-bubble.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/ui/03-message-bubble.png" alt="01A 컴포넌트 카탈로그 · Message Bubble" width="680" />

## 모드 및 테마 가이드

- Classic: Pretendard, rounded bubble, 시간 중심의 최소 metadata
- Developer Hybrid: bubble 구조를 유지하고 Mono telemetry를 선택적으로 추가
- Terminal Log: bubble 대신 compact log row/card 표현
- 각 모드는 Light/Dark surface/content token을 모두 지원하고 own/peer 및 role 의미는 바뀌지 않는다.
- Classic/Hybrid/Terminal × Light/Dark × Own/Peer Preview를 준비한다.

- HTML 화면명: `클래식 메신저 모드`, `개발 하이브리드 모드`, `터미널 로그형 모드`
