# 08. SettingsField 구현 프롬프트

## 작업 브랜치

`feature/designsystem/settings-field` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/settings-field
```

## Claude Code에게 요청

`core:designsystem`에 설정 화면과 태블릿 설정 dialog에서 반복되는 section label, value row, switch row, slider field를 일관된 API로 구현해줘. 모든 상태는 호출자가 소유한다.

01A 계약: Variant `Section/Value/Switch/Slider`, State `Default/Focused/Changed/Disabled/Error`.

## API 샘플

```kotlin
@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
)

@Composable
fun SettingsValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    changed: Boolean = false,
    supportingText: String? = null,
    isError: Boolean = false,
)

@Composable
fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    isError: Boolean = false,
)

@Composable
fun SettingsSliderField(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    changed: Boolean = false,
    supportingText: String? = null,
    isError: Boolean = false,
)
```

## Defaults 샘플

```kotlin
internal object SettingsDefaults {
    const val SECTION_TAG_PREFIX = "blebridge_settings_section_"
    const val ROW_TAG_PREFIX = "blebridge_settings_row_"
    val RowMinHeight = 48.dp
    val HorizontalPadding = 16.dp
    val VerticalPadding = 12.dp
    val SectionSpacing = 20.dp
}
```

## 토큰

- section label: `AppTheme.typography.monoSmall`, color `AppTheme.colors.textTertiary`
- card: `AppTheme.colors.surfacePrimary`, border `AppTheme.colors.borderNormal`, shape `AppTheme.radius.card`
- 토큰 소유권 메모: 구현 시 `radius.card`가 Settings Field 전용 변경 축인지 확인한다. 전용이면 `SettingsDefaults`로 이동하고, 공통 카드 규칙이면 Semantic alias를 유지한다.
- row label: `AppTheme.typography.bodyMedium`, value는 `AppTheme.typography.monoMedium`
- divider: `AppTheme.colors.borderSubtle`
- switch/slider active: `AppTheme.roleColors.active`
- disabled: container `AppTheme.colors.disabledContainer`, content `AppTheme.colors.onDisabledContainer`
- error: content `AppTheme.colors.error`, supporting container가 필요하면 `AppTheme.colors.errorContainer`
- slider의 min/max/value는 호출자가 포맷팅하여 전달
- Focused/Changed/Error는 색상만이 아니라 state/supporting text semantics로도 전달한다.

## 테스트

- value row click optional
- switch state hoisting/enabled
- slider range와 callback
- content description 및 state description
- 긴 value ellipsis
- Light/Dark

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Settings Field 디자인 PNG 열기](../screenshots/common/08-settings-field.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/08-settings-field.png" alt="01A 컴포넌트 카탈로그 · Settings Field" width="680" />

- HTML 화면명: `설정 (MTU · 청크 · 재전송)`, `태블릿 · 설정 다이얼로그 (2열)`
