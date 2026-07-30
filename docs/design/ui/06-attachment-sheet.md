# 06. AttachmentSheet 구현 프롬프트

## 작업 브랜치

`feature/ui/attachment-sheet` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/ui/attachment-sheet
```

## Claude Code에게 요청

`core:ui`에 카메라, 갤러리, 파일, 문서, 기타 전송 소스를 표시하는 attachment modal bottom sheet와 tile을 구현해줘.

01A 계약: Variant `BottomSheet/Dialog/TwoColumn/ThreeColumn`, State `Available/PermissionRequired/Disabled`.

## API 샘플

```kotlin
enum class AttachmentPresentation {
    BottomSheet,
    Dialog,
}

enum class AttachmentAvailability {
    Available,
    PermissionRequired,
    Disabled,
}

enum class AttachmentTone {
    Role,
    Neutral,
    Warning,
}

@Immutable
data class AttachmentAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val tone: AttachmentTone,
    val availability: AttachmentAvailability =
        AttachmentAvailability.Available,
)

@Composable
fun AttachmentSheet(
    actions: List<AttachmentAction>,
    onAction: (AttachmentAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "전송할 데이터",
    presentation: AttachmentPresentation =
        AttachmentPresentation.BottomSheet,
)
```

## Defaults 샘플

```kotlin
internal object AttachmentSheetDefaults {
    const val SHEET_TAG = "blebridge_attachment_sheet"
    const val ACTION_TAG_PREFIX = "blebridge_attachment_"
    val SheetHorizontalPadding = 20.dp
    val TileSize = 100.dp
    val TileSpacing = 12.dp
    val DragHandleWidth = 40.dp
}
```

## 디자인

- Compact는 Material3 `ModalBottomSheet`, expanded/tablet은 Dialog presentation을 지원
- surface `AppTheme.colors.surfacePrimary`, scrim `AppTheme.colors.scrim`
- 상단 radius `AppTheme.radius.logo`
- 토큰 소유권 메모: `radius.logo`라는 이름과 Sheet top shape 용도가 일치하지 않는다. 구현 시 Attachment Sheet 전용이면 `AttachmentSheetDefaults`로 이동하며 Splash/Logo와 값이 같다는 이유만으로 공유하지 않는다.
- tile은 role/container 또는 attachment tone별 semantic container 사용
- Role tile: container `AppTheme.roleColors.activeContainer`, content `AppTheme.roleColors.onActiveContainer`
- Neutral tile: container `AppTheme.colors.surfaceVariant`, content `AppTheme.colors.iconSecondary`
- Warning tile: container `AppTheme.colors.warningContainer`, content `AppTheme.colors.onWarningContainer`
- Disabled tile: container `AppTheme.colors.disabledContainer`, content `AppTheme.colors.onDisabledContainer`
- icon `AppTheme.iconSize.default`, label `AppTheme.typography.labelSmall`
- 고정 3열이 아니라 화면 폭에 따라 2~4열 adaptive layout
- 시스템 navigation bar inset 처리
- action list가 비어 있으면 sheet를 표시하지 않거나 empty 정책 명시
- PermissionRequired는 callback을 전달하되 권한 필요 state description을 제공하고 Disabled는 click을 차단한다.

## 테스트

- title/action label
- tile click 및 action 전달
- dismiss
- 5개 action의 adaptive 배치가 overflow 없는지
- content description
- BottomSheet/Dialog × Available/PermissionRequired/Disabled

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:ui` 내부 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive 접근과 임의 dp는 금지하고, Feature는 완성된 Component API와 전달 가능한 Component token만 사용한다.

## 디자인 참조

- [Attachment Sheet 디자인 PNG 열기](../screenshots/ui/06-attachment-sheet.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/ui/06-attachment-sheet.png" alt="01A 컴포넌트 카탈로그 · Attachment Sheet" width="680" />

- HTML 화면명: `첨부 시트 ( + 버튼 )`
