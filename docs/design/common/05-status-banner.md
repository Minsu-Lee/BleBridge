# 05. StatusBanner 구현 프롬프트

## 작업 브랜치

`feature/designsystem/status-banner` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/status-banner
```

## Claude Code에게 요청

`core:designsystem`에 앱바 하단과 채팅 상단에서 사용할 범용 status banner를 구현해줘. 상태 문구와 semantic tone은 호출자가 전달하고, BLE 실행 상태를 직접 참조하지 않는다.

01A 계약: Variant `Regular/Compact/Dot/CustomLeading`, State `Neutral/Info/Success/Warning/Error`.

## API 샘플

```kotlin
enum class StatusTone {
    Neutral,
    Info,
    Success,
    Warning,
    Error,
}

@Composable
fun StatusBanner(
    text: String,
    tone: StatusTone,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showIndicator: Boolean = true,
    leadingContent: (@Composable () -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object StatusBannerDefaults {
    const val BANNER_TAG = "blebridge_status_banner"
    const val INDICATOR_TAG = "blebridge_status_indicator"
    val HorizontalPadding = 14.dp
    val VerticalPadding = 7.dp
    val IndicatorSize = 7.dp
    val ContentSpacing = 8.dp
}
```

## 토큰 매핑

- Neutral: container `AppTheme.colors.surfaceVariant`, content `AppTheme.colors.textSecondary`, indicator `AppTheme.colors.iconSecondary`
- Info: container `AppTheme.colors.serverContainer`, content `AppTheme.colors.onServerContainer`, indicator `AppTheme.colors.server`
- Success: container `AppTheme.colors.successContainer`, content `AppTheme.colors.onSuccessContainer`, indicator `AppTheme.colors.success`
- Warning: container `AppTheme.colors.warningContainer`, content `AppTheme.colors.onWarningContainer`, indicator `AppTheme.colors.warning`
- Error: container `AppTheme.colors.errorContainer`, content `AppTheme.colors.onErrorContainer`, indicator `AppTheme.colors.error`
- shape: `AppTheme.radius.large`
- text: `AppTheme.typography.monoMedium`, Compact는 `AppTheme.typography.monoSmall`
- indicator 표현은 tone에 종속시키지 않고 호출자가 전달하거나 범용 dot으로 표현
- `leadingContent`가 있으면 기본 dot을 대체하고, 둘 다 없으면 시작 여백을 남기지 않는다.

## 테스트

- 각 tone의 semantic text와 indicator
- indicator 노출 정책
- 긴 텔레메트리 텍스트 줄바꿈/말줄임 정책
- Light/Dark 대비

## 사용 예시

```kotlin
StatusBanner(
    text = "연결됨 · Galaxy-A1 · MTU 247 · 2M PHY",
    tone = StatusTone.Success,
)
```

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Status Banner 디자인 PNG 열기](../screenshots/common/05-status-banner.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/05-status-banner.png" alt="01A 컴포넌트 카탈로그 · Status Banner" width="680" />

- HTML 화면명: `앱바 하단 · 연결 상태 배너`
- 개발 하이브리드 상단: HTML 화면명 `개발 하이브리드 모드`
