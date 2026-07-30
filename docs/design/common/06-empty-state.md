# 06. EmptyState 구현 프롬프트

## 작업 브랜치

`feature/designsystem/empty-state` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/empty-state
```

## Claude Code에게 요청

`core:designsystem`에 서버 시작 전, 스캔 시작 전, 추가 클라이언트 대기, 검색 결과 없음 등에 공통으로 사용할 범용 bordered empty state를 구현해줘. BLE 상태 모델은 직접 참조하지 않고 선택적인 슬롯으로 시각 요소를 받는다.

01A 계약: Variant `Dashed/Plain/LeadingSlot/ActionSlot`, State `Informational/Actionable/Disabled`.

## API 샘플

```kotlin
enum class EmptyStateStyle {
    Dashed,
    Plain,
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    style: EmptyStateStyle = EmptyStateStyle.Dashed,
    enabled: Boolean = true,
    leadingContent: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object EmptyStateDefaults {
    const val EMPTY_STATE_TAG = "blebridge_empty_state"
    val BorderWidth = 1.5.dp
    val ContentPadding = 16.dp
    val IndicatorSize = 14.dp
    val ContentSpacing = 8.dp
}
```

## 토큰

- border: `AppTheme.colors.disabledBorder`
- content: `AppTheme.colors.textTertiary`
- background: transparent
- radius: `AppTheme.radius.card`
- 토큰 소유권 메모: 구현 시 `radius.card`가 Empty State와 독립적으로 변경되는 값인지 확인한다. 전용 값이면 `EmptyStateDefaults`로 이동하고, 다른 카드 계열과 함께 변경되는 규칙이면 Semantic alias를 유지한다.
- text: `AppTheme.typography.monoMedium`
- `leadingContent`가 null이면 indicator 영역을 만들지 않음
- BLE activity indicator가 필요하면 호출자가 `leadingContent` 슬롯에서 조합
- dashed border 구현이 과도하게 복잡하면 Compose `PathEffect.dashPathEffect`를 내부 utility로 캡슐화
- disabled에서는 action click을 차단하고 disabled semantic content/border를 사용한다.

## 테스트

- message 표시
- 슬롯이 null일 때 불필요한 간격 미노출
- leading/action 슬롯 노출
- multiline 가운데 정렬
- informational/actionable/disabled 상태 semantics

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Empty State 디자인 PNG 열기](../screenshots/common/06-empty-state.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/06-empty-state.png" alt="01A 컴포넌트 카탈로그 · Empty State" width="680" />

- HTML 화면명: 서버/클라이언트 첫 진입 화면의 빈 목록, `다른 클라이언트 대기 중`
