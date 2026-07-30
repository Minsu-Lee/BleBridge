# 05. ChatInput 구현 프롬프트

## 작업 브랜치

`feature/ui/chat-input` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/ui/chat-input
```

## Claude Code에게 요청

`core:ui`에 첨부 버튼, 입력 필드, 전송 버튼으로 구성된 controlled chat input을 구현해줘. TextField 상태와 전송 가능 여부는 호출자가 소유한다.

01A 계약: Variant `TextOnly/Attachment/Classic/DeveloperHybrid/Terminal`, State `Empty/Typing/Ready/Sending/Disabled`.

## API 샘플

```kotlin
@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSending: Boolean = false,
    placeholder: String = "메시지 입력",
    onAttachment: (() -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object ChatInputDefaults {
    const val INPUT_TAG = "blebridge_chat_input"
    const val TEXT_FIELD_TAG = "blebridge_chat_text_field"
    const val ATTACHMENT_TAG = "blebridge_chat_attachment"
    const val SEND_TAG = "blebridge_chat_send"
    const val MAX_LINES = 4
    val ContentSpacing = 10.dp
    val HorizontalPadding = 16.dp
    val VerticalPadding = 12.dp
}
```

## 디자인

- background: `AppTheme.chatTokens.colors.chromeBackground`
- divider: `AppTheme.chatTokens.colors.divider`
- text field container: `AppTheme.chatTokens.colors.inputBackground`
- placeholder/content: `AppTheme.chatTokens.colors.inputContent`
- input text: `AppTheme.chatTokens.inputTextStyle`, text color `AppTheme.chatTokens.colors.onChrome`
- radius: `AppTheme.chatTokens.inputRadius`
- 토큰 소유권 메모: Classic/DeveloperHybrid는 `radius.full`, Terminal은 `radius.large`를 사용한다. 전역 `radius.input`을 자동 적용하지 말고 `ChatTokens.inputRadius`를 단일 진입점으로 유지한다.
- action size: `AppTheme.chatTokens.actionSize`
- Add와 Send는 `IconButton`
- send enabled = `enabled && !isSending && value.isNotBlank()`
- Sending 중 입력 정책은 호출자가 결정하되 중복 send는 반드시 차단하고 state description을 제공한다.
- IME Send와 버튼 click은 같은 callback
- Terminal에서는 fixed dark chrome과 mono input을 자동 적용

## 테스트

- typing state 전달
- blank send disabled
- IME Send
- attachment optional
- enabled false 전체 차단
- Sending 중 중복 send 차단과 progress/state semantics
- Classic/Hybrid/Terminal colors와 typography
- 4줄 이상 scroll/height 정책

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:ui` 내부 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive 접근과 임의 dp는 금지하고, Feature는 완성된 Component API와 전달 가능한 Component token만 사용한다.

## 디자인 참조

- [Chat Input 디자인 PNG 열기](../screenshots/ui/05-chat-input.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/ui/05-chat-input.png" alt="01A 컴포넌트 카탈로그 · Chat Input" width="680" />

## 모드 및 테마 가이드

- Classic: pill TextField와 circular attachment/send action
- Developer Hybrid: Classic 구조를 유지하되 density와 technical feedback token 적용 가능
- Terminal Log: compact rounded-rectangle field, Mono placeholder, square action
- Light/Dark는 mode와 독립적으로 surface/content/border token을 전환한다.
- 세 모드 × Light/Dark × Empty/Ready/Disabled Preview를 준비한다.

- HTML 화면명: `클래식 메신저 모드`, `개발 하이브리드 모드`, `터미널 로그형 모드`
