# 04. TransferCard 구현 프롬프트

## 작업 브랜치

`feature/ui/transfer-card` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/ui/transfer-card
```

## Claude Code에게 요청

파일·이미지·영상 전송의 대기/진행/완료/실패 상태를 표시하는 transfer card를 구현해줘. 도메인 transfer 모델에 직접 의존하지 않는 UI 전용 모델을 `core:ui`에 정의한다.

01A 계약: Variant `File/Image/Video/Compact/Detailed`, State `Pending/Progress/Paused/Done/Failed`.

## API 샘플

```kotlin
enum class TransferKind { File, Image, Video }
enum class TransferState { Pending, Progress, Paused, Done, Failed }

@Immutable
data class TransferPresentation(
    val name: String,
    val summary: String,
    val telemetry: String? = null,
    val progress: Float? = null,
    val kind: TransferKind,
    val state: TransferState,
)

@Composable
fun TransferCard(
    transfer: TransferPresentation,
    modifier: Modifier = Modifier,
    isOwnMessage: Boolean = false,
    onOpen: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    onPause: (() -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object TransferCardDefaults {
    const val CARD_TAG = "blebridge_transfer_card"
    const val PROGRESS_TAG = "blebridge_transfer_progress"
    const val RETRY_TAG = "blebridge_transfer_retry"
    val ContentPadding = 12.dp
    val IconContainer = 38.dp
    val ProgressHeight = 5.dp
    val ContentSpacing = 11.dp
}
```

## 토큰

- card container: `AppTheme.chatTokens.colors.chromeSurface`
- primary content: `AppTheme.chatTokens.colors.onChrome`, secondary content: `AppTheme.chatTokens.colors.onChromeMuted`
- border: `AppTheme.colors.borderNormal`
- radius: `AppTheme.chatTokens.cardRadius`
- kind icon: `AppTheme.icons.File`, `Image`, `Video`
- Pending: indicator/content `AppTheme.chatTokens.colors.onChromeMuted`
- Progress: progress `AppTheme.roleColors.active`, track `AppTheme.colors.surfaceVariant`
- Paused: progress `AppTheme.colors.warning`, resume/cancel action
- Done: content `AppTheme.colors.success`, optional open action
- Failed: container `AppTheme.colors.errorContainer`, content `AppTheme.colors.onErrorContainer`, Retry
- telemetry: `AppTheme.chatTokens.metadataTextStyle`
- `showTelemetry=false`면 chunk/MTU/speed 문자열 숨김
- progress는 `coerceIn(0f, 1f)`, null 처리

## 테스트

- kind별 아이콘
- 다섯 상태별 label/color/action
- progress 0/0.62/1 및 범위 clamp
- telemetry mode별 노출
- 긴 파일명 ellipsis
- retry/open callback

## 사용 예시

```kotlin
TransferCard(
    transfer = TransferPresentation(
        name = "firmware_v3.zip",
        summary = "8.7 MB · 전송 중",
        telemetry = "318/512 · 4.3 KB/s · ~7s",
        progress = 0.62f,
        kind = TransferKind.File,
        state = TransferState.Progress,
    ),
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

- [Transfer Card 디자인 PNG 열기](../screenshots/ui/04-transfer-card.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/ui/04-transfer-card.png" alt="01A 컴포넌트 카탈로그 · Transfer Card" width="680" />

## 모드 및 테마 가이드

- Classic: 파일명, 용량, 핵심 상태만 표시
- Developer Hybrid: chunk, MTU, 속도, ETA telemetry 표시
- Terminal Log: Mono compact card/inline block 사용
- Light/Dark에서 container, border, text token을 전환하되 progress/error semantic color는 유지한다.
- 세 모드 × Light/Dark × Progress/Done/Failed Preview를 준비한다.

- HTML 화면명: `클래식 메신저 모드`, `개발 하이브리드 모드`, `터미널 로그형 모드`
- HTML 화면명: 각 모드의 `전송 실패 · 재시도` 화면
