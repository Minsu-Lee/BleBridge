# 10. AppBar 구현 프롬프트

## 작업 브랜치

`feature/designsystem/app-bar` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/app-bar
```

## Claude Code에게 요청

`core:designsystem`에 BLE Bridge 전 화면에서 재사용할 `AppBar`를 구현해줘. 컴포넌트는 화면 제목과 navigation/action 슬롯의 배치만 책임지고, 설정 이동이나 채팅 같은 앱 동작 및 도메인 모델은 알지 않아야 한다.

01A 계약: Variant `General/ImageViewer/VideoPlayer`, State `Default/Scrolled/Inverse`, action slot `0/1/2/N`.

디자인 기준은 [`BLETransferApp.dc.html`](../BLETransferApp.dc.html)의 모바일·태블릿 `기기 연결`, 설정, 미디어 화면 및 채팅 화면 상단 영역이다.

## 지원 유형

별도 enum으로 화면 종류를 고정하지 말고 하나의 slot API로 다음 구성을 모두 표현한다.

1. 제목만 있는 AppBar
2. 제목 + 우측 N개 action
3. navigation icon + 제목 + 우측 action
4. 제목 + 보조 설명 또는 상태
5. 중앙 정렬 제목이 필요한 modal AppBar
6. 스크롤 시 divider 또는 elevation이 나타나는 상태
7. Classic 채팅 상세: Back + 상태 dot + 2줄 title/subtitle
8. Developer Hybrid 채팅 상세: Back + role badge + title/Mono metadata
9. Terminal Log 채팅 상세: inverse Back + Mono title/status
10. Image Viewer: inverse Close + 시작 정렬 파일명/파일 메타데이터 + Download action
11. Video Player: inverse Close + 시작 정렬 파일명/코덱·재생시간 메타데이터 + action 없음

채팅 상세처럼 상위 화면으로 돌아가야 하는 화면은 `navigationIcon`에 뒤로가기 버튼을 배치한다. 모바일 상세 화면에서는 필수이며, 태블릿은 별도 detail destination으로 진입한 경우 표시하고 동일 화면의 고정 secondary pane이면 호출자가 생략할 수 있다.

아이콘 action과 텍스트 action은 AppBar 내부에서 별도 sealed type으로 모델링하지 않는다. 호출자가 `actions` 람다에서 `IconButton`, `TextButton` 또는 다른 Composable을 배치한다.

## 공개 API 샘플

```kotlin
enum class AppBarTitleAlignment {
    Start,
    Center,
}

@Composable
fun AppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    subtitle: String? = null,
    titleAlignment: AppBarTitleAlignment =
        AppBarTitleAlignment.Start,
    showDivider: Boolean = false,
    containerColor: Color = AppTheme.colors.backgroundPrimary,
    contentColor: Color = AppTheme.colors.textPrimary,
)
```

제목 자체에 badge나 custom typography가 필요해질 경우에만 다음 overload를 추가한다.

```kotlin
@Composable
fun AppBar(
    title: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    titleAlignment: AppBarTitleAlignment =
        AppBarTitleAlignment.Start,
    showDivider: Boolean = false,
)
```

두 overload가 동일한 내부 layout을 사용하도록 구현하고, 문자열 API를 우선 권장한다.

채팅 상세처럼 제목 왼쪽에 상태 dot 또는 badge가 포함되는 경우 custom title overload를 사용한다.

```kotlin
AppBar(
    navigationIcon = {
        IconButton(
            onClick = onBackClick,
            icon = AppTheme.icons.Back,
            contentDescription = "뒤로",
        )
    },
    title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ConnectionStatusDot(status = connectionStatus)
            Column {
                Text(title, style = AppTheme.typography.titleMedium)
                Text(
                    subtitle,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textTertiary,
                )
            }
        }
    },
)
```

기존 채팅 상세의 설정 아이콘과 Server/Client 전환 action은 포함하지 않는다.

모드별 title content의 정보 순서는 모바일과 태블릿에서 동일해야 한다.

- Classic 첫 줄: status dot + `서버 모드` 또는 `클라이언트 모드`
- Classic 둘째 줄: 연결된 기기명 + 연결 상태
- Hybrid 첫 줄: `SERVER/CLIENT` badge와 `GATT Peripheral/Central` title을 반드시 같은 baseline에 배치
- Hybrid 둘째 줄: service/peer/write/notify metadata
- Terminal 첫 줄: `server ● gatt` 또는 `client ● central`
- Terminal 둘째 줄: peer/MTU 또는 error/reconnecting 상태
- Image Viewer 첫 줄: 이미지 파일명, 한 줄 ellipsis
- Image Viewer 둘째 줄: 파일 크기 · 해상도
- Image Viewer action: 왼쪽 Close, 오른쪽 Download
- Video Player 첫 줄: 영상 파일명, 한 줄 ellipsis
- Video Player 둘째 줄: 파일 크기 · codec · duration
- Video Player action: 왼쪽 Close, 오른쪽 action 없음
- 태블릿의 primary pane 화면 제목은 모든 모드에서 `기기 연결`로 통일
- 태블릿 2단 Classic/Hybrid/Terminal의 primary pane도 `기기 연결` 우측에 Settings action을 배치하며, secondary 채팅 상세 AppBar에는 Settings action을 두지 않는다.

## 사용 예시

```kotlin
AppBar(
    title = "기기 연결",
    actions = {
        IconButton(
            onClick = onSettingsClick,
            icon = AppTheme.icons.Settings,
            contentDescription = "설정",
        )
    },
)
```

```kotlin
AppBar(
    title = "설정",
    navigationIcon = {
        IconButton(
            onClick = onBackClick,
            icon = AppTheme.icons.Back,
            contentDescription = "뒤로",
        )
    },
    actions = {
        TextButton(onClick = onSaveClick) {
            Text("저장")
        }
    },
)
```

### 이미지 상세 AppBar

```kotlin
AppBar(
    navigationIcon = {
        IconButton(
            onClick = onCloseClick,
            icon = AppTheme.icons.Close,
            contentDescription = "이미지 닫기",
            tone = IconButtonTone.Inverse,
            style = IconButtonStyle.Ghost,
        )
    },
    title = "IMG_20260722_0941.jpg",
    subtitle = "3.1 MB · 1080×1440",
    actions = {
        IconButton(
            onClick = onDownloadClick,
            icon = AppTheme.icons.Download,
            contentDescription = "이미지 다운로드",
            tone = IconButtonTone.Inverse,
            style = IconButtonStyle.Ghost,
        )
    },
    containerColor = AppTheme.media.chromeBackground,
    contentColor = AppTheme.media.content,
)
```

### 영상 플레이어 AppBar

```kotlin
AppBar(
    navigationIcon = {
        IconButton(
            onClick = onCloseClick,
            icon = AppTheme.icons.Close,
            contentDescription = "영상 닫기",
            tone = IconButtonTone.Inverse,
            style = IconButtonStyle.Ghost,
        )
    },
    title = "clip_field_test.mp4",
    subtitle = "18.2 MB · H.264 · 0:42",
    containerColor = AppTheme.media.chromeBackground,
    contentColor = AppTheme.media.content,
)
```

## Defaults 샘플

```kotlin
internal object AppBarDefaults {
    const val APP_BAR_TAG = "blebridge_app_bar"
    const val TITLE_TAG = "blebridge_app_bar_title"
    const val SUBTITLE_TAG = "blebridge_app_bar_subtitle"
    const val NAVIGATION_TAG = "blebridge_app_bar_navigation"
    const val ACTIONS_TAG = "blebridge_app_bar_actions"

    val PhoneMinHeight = 56.dp
    val TabletMinHeight = 52.dp
    val HorizontalPadding = 16.dp
    val NavigationContentSpacing = 4.dp
    val ActionSpacing = 4.dp
    val TitleSubtitleSpacing = 2.dp
    val DividerWidth = 1.dp
}
```

## 디자인시스템 토큰

- 화면 제목: `AppTheme.typography.screenTitle`
- compact/tablet 제목: `AppTheme.typography.titleMedium`
- subtitle: `AppTheme.typography.labelSmall`
- 배경: `AppTheme.colors.backgroundPrimary`
- 제목: `AppTheme.colors.textPrimary`
- 보조 문구: `AppTheme.colors.textTertiary`
- divider: `AppTheme.colors.borderSubtle`
- 수평 여백: `AppTheme.spacing.xxl` (`16.dp`)
- action visual size: `AppTheme.iconSize.actionContainer`
- action icon: `AppTheme.iconSize.medium`
- 최소 터치 영역: `AppTheme.controlSize.minimumTouchTarget`

새로운 전역 색상·간격 토큰은 추가하지 않는다. `56dp/52dp` AppBar 높이처럼 컴포넌트 구조에만 필요한 값은 `AppBarDefaults`가 소유한다.

## 유형별 Light/Dark 가이드

| 유형 | Light | Dark | typography |
|---|---|---|---|
| 일반 화면 | backgroundPrimary / textPrimary | dark backgroundPrimary / textPrimary | screenTitle |
| Classic 상세 | surface + subtle divider | dark surface + dark divider | titleMedium + body/label subtitle |
| Developer Hybrid | role badge + neutral surface | role badge + dark neutral surface | Pretendard title + Mono metadata |
| Terminal Log | inverse terminal surface | 동일한 terminal surface | monoMedium + monoSmall |
| Image Viewer | 고정 `media` surface | 고정 `media` surface | titleMedium + Mono 파일 크기·해상도 |
| Video Player | 고정 `media` surface | 고정 `media` surface | titleMedium + Mono 파일 크기·codec·duration |

채팅 모드는 AppBar가 enum으로 직접 분기하지 않고 호출 범위의 `AppTheme.chatTokens`를 소비한다. Terminal은 chat token을, Image Viewer/Video Player는 `AppTheme.media`를 사용하며 시스템 theme와 무관하게 고정된 presentation을 유지한다.

## 레이아웃 규칙

- action 개수는 API에서 제한하지 않되 일반 화면은 최대 2개를 권장한다.
- 3개 이상의 action은 overflow menu로 보내는 것을 사용 지침에 명시한다.
- navigation 영역이 없을 때 제목이 불필요하게 들여쓰기되지 않아야 한다.
- 뒤로가기 버튼은 항상 제목 왼쪽 첫 번째 focus target이며 `AppTheme.icons.Back`을 사용한다.
- 모바일 상세 화면은 뒤로가기 navigation을 기본 구성으로 사용한다.
- 태블릿은 navigation 구조에 따라 뒤로가기를 표시하거나 생략할 수 있으며 AppBar가 window size를 직접 판단하지 않는다.
- 중앙 제목을 요청한 modal 유형은 좌우 슬롯 폭이 달라도 화면 중앙을 유지해야 한다.
- custom title의 상태 dot/badge는 title content 내부에 속하며 navigation slot을 대체하지 않는다.
- 2줄 title은 title 1줄 + subtitle 1줄로 제한하고 각각 ellipsis 처리한다.
- phone/tablet은 AppBar 높이와 좌우 여백만 달라질 수 있고 title의 정보 순서, typography 계층, badge 위치는 동일하다.
- 긴 제목은 한 줄 ellipsis, subtitle은 최대 한 줄을 기본값으로 한다.
- edge-to-edge status bar inset 적용 여부는 화면 Scaffold가 소유한다.
- AppBar는 navigation이나 settings route를 직접 실행하지 않고 callback만 전달받는다.

## 접근성

- 모든 icon-only action은 비어 있지 않은 `contentDescription`을 가져야 한다.
- 설정 action은 `AppTheme.icons.Settings`의 기어 glyph를 사용한다.
- action의 visual container가 40dp여도 실제 터치 영역은 최소 48dp다.
- 텍스트 action은 글자 크기 확대 시 잘리지 않아야 한다.
- 제목은 heading semantics를 제공한다.
- navigation action의 focus order가 제목과 우측 action보다 앞서야 한다.

## 테스트

- 제목-only 구성
- navigation icon 유무에 따른 정렬
- 모바일 상세 화면의 뒤로가기 callback과 접근성 label
- 태블릿에서 navigation icon 표시/생략 구성
- 아이콘 및 텍스트 action callback
- action 0/1/2/N개 배치와 spacing
- subtitle 표시 및 미표시
- start/center title alignment
- divider on/off
- 긴 제목 ellipsis
- Image Viewer의 Close/Download callback, 파일명 ellipsis와 2줄 구조
- Video Player의 Close callback, 빈 action 영역과 2줄 구조
- Image Viewer와 Video Player의 고정 inverse Light/Dark Preview
- RTL에서 navigation/action 순서
- 48dp touch target 및 content description
- Light/Dark Preview
- phone/tablet Preview
- Classic/Hybrid/Terminal 각각의 phone/tablet 구조 동일성
- Hybrid badge와 title의 동일 baseline
- tablet primary pane 제목 `기기 연결`
- 모든 태블릿 2단 모드의 primary pane Settings callback 및 secondary pane 미노출

## 완료 조건

- Feature, navigation, BLE, chat 모델을 참조하지 않는다.
- Material `TopAppBar`를 사용해도 공개 API는 Material 타입에 불필요하게 결합하지 않는다.
- `IconButton`을 action 슬롯 안에서 조합할 수 있다.
- 설정 버튼은 호출 화면이 callback으로 주입한다.

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [App Bar 디자인 PNG 열기](../screenshots/common/10-app-bar.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/10-app-bar.png" alt="01A 컴포넌트 카탈로그 · App Bar" width="680" />

- HTML 화면명: 모든 `기기 연결` 모바일·태블릿 화면
- HTML 화면명: Classic/Developer Hybrid/Terminal Log 채팅 상세
- HTML 화면명: `이미지 상세 · 핀치 줌`, `영상 플레이어`
- HTML 상단: `01A · COMPONENT CATALOG`의 `10 · APP BAR`
