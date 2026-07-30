# 03. SegmentedControl 구현 프롬프트

## 작업 브랜치

`feature/designsystem/segmented-control` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/segmented-control
```

## Claude Code에게 요청

기기 연결 화면의 서버/클라이언트 탭과 설정의 단일 선택 옵션에 사용할 generic single-selection segmented control을 `core:designsystem`에 구현해줘. 채팅 상세 AppBar와 태블릿 채팅 2단 화면의 primary pane에서는 서버/클라이언트 전환을 제공하지 않으므로 이 컴포넌트를 배치하지 않는다.

01A 계약: Variant `Label/IconLabel`, State `Selected/Unselected/Disabled`.

## API 샘플

```kotlin
@Immutable
data class SegmentItem<T>(
    val value: T,
    val label: String,
    val shortLabel: String? = null,
    val icon: ImageVector? = null,
)

@Composable
fun <T> SegmentedControl(
    items: List<SegmentItem<T>>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
)
```

## Defaults 샘플

```kotlin
internal object SegmentedControlDefaults {
    const val CONTROL_TAG = "blebridge_segmented_control"
    const val ITEM_TAG_PREFIX = "blebridge_segment_"
    val ContainerPadding = 4.dp
    val ItemVerticalPadding = 9.dp
    val CompactVerticalPadding = 5.dp
    val ItemSpacing = 4.dp
}
```

## 토큰과 동작

- container: `AppTheme.colors.surfaceVariant`
- selected container: `AppTheme.colors.surfacePrimary`
- selected content: `AppTheme.roleColors.active`
- unselected content: `AppTheme.colors.textTertiary`
- disabled content: `AppTheme.colors.onDisabledContainer`
- shape: outer `AppTheme.radius.xLarge`, selected `AppTheme.radius.large`
- label: Default `AppTheme.typography.labelMedium`, Compact `AppTheme.typography.monoSmall`
- Label과 Icon+Label variant를 지원하고 icon이 null이면 icon 공간을 남기지 않는다.
- 선택된 item은 selected semantics 제공
- 같은 item 재선택 시 callback 중복 호출 금지 여부를 명시하고 테스트
- generic 타입은 domain 모델에 의존하지 않는다.

## 테스트

- 초기 선택 상태
- item click 후 callback
- disabled click 차단
- selected semantics
- Server/Client provider별 selected color
- compact label 표시

## 사용 예시

```kotlin
SegmentedControl(
    items = ConnectionRole.entries.map {
        SegmentItem(it, it.name, it.name.take(1))
    },
    selected = currentRole,
    onSelected = onRoleSelected,
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

- [Segmented Control 디자인 PNG 열기](../screenshots/common/03-segmented-control.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/03-segmented-control.png" alt="01A 컴포넌트 카탈로그 · Segmented Control" width="680" />

- HTML 화면명: 모든 `기기 연결` 폰/태블릿 화면
- 채팅 상세 AppBar의 역할 표시는 전환 control이 아니라 읽기 전용 badge이며 이 컴포넌트의 범위가 아니다.
