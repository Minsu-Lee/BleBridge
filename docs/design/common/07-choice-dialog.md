# 07. ChoiceDialog 구현 프롬프트

## 작업 브랜치

`feature/designsystem/choice-dialog` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/choice-dialog
```

## Claude Code에게 요청

`core:designsystem`에 최초 진입 채팅 표시 모드 선택과 향후 단일 선택 설정에 재사용할 choice dialog와 choice row를 구현해줘.

01A 계약: Variant `SingleChoice/Badge/Dismissible/Required`, State `Selected/Unselected/Disabled`.

## API 샘플

```kotlin
enum class ChoiceDialogDismissPolicy {
    Dismissible,
    Required,
}

@Immutable
data class Choice<T>(
    val value: T,
    val title: String,
    val description: String? = null,
    val badge: String? = null,
)

@Composable
fun <T> ChoiceDialog(
    title: String,
    description: String?,
    choices: List<Choice<T>>,
    selected: T,
    onSelected: (T) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String,
    dismissPolicy: ChoiceDialogDismissPolicy =
        ChoiceDialogDismissPolicy.Dismissible,
    onDismiss: (() -> Unit)? = null,
)
```

## Defaults 샘플

```kotlin
internal object ChoiceDialogDefaults {
    const val DIALOG_TAG = "blebridge_choice_dialog"
    const val CHOICE_TAG_PREFIX = "blebridge_choice_"
    const val CONFIRM_TAG = "blebridge_choice_confirm"
    val DialogPadding = 20.dp
    val ChoiceSpacing = 10.dp
    val ChoicePadding = 14.dp
}
```

## 토큰

- dialog surface: `AppTheme.colors.surfacePrimary`, shape `AppTheme.radius.input`
- 토큰 소유권 메모: `radius.input`이라는 현재 이름과 Dialog container 용도가 일치하지 않는다. 구현 시 실제 디자인을 재확인하고, Dialog 전용이면 `ChoiceDialogDefaults`로 이동한다. 이름만 보고 입력 컴포넌트와 공유하지 않는다.
- scrim: `AppTheme.colors.scrim`
- selected choice: border/content `AppTheme.roleColors.active`, container `AppTheme.roleColors.activeContainer`
- unselected choice: border `AppTheme.colors.borderNormal`, content `AppTheme.colors.textPrimary`
- disabled choice: container `AppTheme.colors.disabledContainer`, content `AppTheme.colors.onDisabledContainer`
- title `AppTheme.typography.titleMedium`, description `AppTheme.typography.bodyMedium`
- radio는 Material component를 쓰되 semantic colors 적용
- confirm은 `ActionButton`

## 테스트

- 선택 상태와 callback
- confirm callback
- dismiss 정책
- Required에서는 back/scrim dismiss를 차단하고 명시적 선택·확인 경로를 제공
- badge 노출
- 접근성 selected semantics
- 리스트가 긴 경우 scroll

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Choice Dialog 디자인 PNG 열기](../screenshots/common/07-choice-dialog.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/07-choice-dialog.png" alt="01A 컴포넌트 카탈로그 · Choice Dialog" width="680" />

- HTML 화면명: `최초 진입 · 표시 모드 선택 팝업`
