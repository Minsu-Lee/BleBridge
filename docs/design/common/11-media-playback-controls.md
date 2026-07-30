# 11. MediaPlaybackControls 구현 프롬프트

## 작업 브랜치

`feature/designsystem/media-playback-controls` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/media-playback-controls
```

## Claude Code에게 요청

영상 플레이어에서 재사용할 도메인 비의존 playback control layout을 `core:designsystem`에 구현해줘. Media AppBar는 별도 컴포넌트를 만들지 않고 같은 모듈의 `AppBar` inverse 구성을 조합한다. 이미지 상세와 영상 플레이어는 같은 AppBar layout을 사용하되, 이미지만 Download action을 제공하고 영상은 action 없이 Close만 제공한다. 실제 video player engine, decoder, 재생 lifecycle과 BLE 전송 상태는 Feature가 소유한다.

01A 계약: Variant `Progress/Time/PlayPause/Skip`, State `Loading/Ready/Playing/Paused/Error`.

## API 샘플

```kotlin
enum class MediaPlaybackState {
    Loading,
    Ready,
    Playing,
    Paused,
    Error,
}

@Composable
fun MediaPlaybackControls(
    state: MediaPlaybackState,
    positionLabel: String,
    durationLabel: String,
    progress: Float,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    modifier: Modifier = Modifier,
    errorLabel: String? = null,
)
```

## Defaults 샘플

```kotlin
internal object MediaPlaybackControlsDefaults {
    const val CONTROLS_TAG = "blebridge_video_controls"
    const val PROGRESS_TAG = "blebridge_video_progress"
    const val AUTO_HIDE_MILLIS = 3_000L
    const val SKIP_SECONDS = 5
    val ControlSpacing = 32.dp
    val PrimaryControlSize = 68.dp
}
```

## 디자인

- media presentation은 고정 dark/inverse이며 앱 Light/Dark Theme와 별개
- chrome: `AppTheme.media.chromeBackground`
- content: `AppTheme.media.content`, 보조 content는 `AppTheme.media.mutedContent`
- progress: `AppTheme.media.controlProgress`, track은 `AppTheme.media.controlTrack`
- scrim: `AppTheme.media.scrim`
- scrim 위 content 대비 보장
- BLE, 채팅, 전송 도메인 타입을 참조하지 않는 순수 controlled component
- 재생 상태, 표시 시간, 정규화된 progress와 callback만 입력받고 player state를 소유하지 않음
- Image Viewer AppBar: Close + 파일명/크기·해상도 + Download
- Video Player AppBar: Close + 파일명/크기·codec·duration, 우측 action 없음
- 두 AppBar 모두 고정 inverse surface이며 `AppBar`의 navigation/title/subtitle/actions 슬롯으로 구성
- Pause/Skip은 `IconButton`을 사용
- progress는 0~1 clamp
- edge-to-edge system bar inset
- auto hide timer는 별도 state holder/helper로 분리하고 lifecycle에 안전하게 구현
- zoom/pan/video decoding 라이브러리를 `core:designsystem`에 추가하지 않는다.

## 테스트

- play/pause icon
- Loading/Ready/Playing/Paused/Error 상태
- seek callback
- skip callbacks
- progress clamp
- auto-hide timer는 virtual time 또는 분리된 policy test
- content description
- 영상 AppBar에 Download action이 노출되지 않는지 composition Preview에서 확인

## 디자인 원본 및 해석 기준

- 전체 디자인 원본: `/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/BLETransferApp.dc.html`
- 구현 전에 HTML의 `01 · DESIGN SYSTEM`, `01A · COMPONENT CATALOG`, `01B · COMPONENT STATE & VARIANT GUIDE`와 해당 컴포넌트가 사용된 실제 화면을 확인한다.
- 첨부 PNG는 컴포넌트를 빠르게 식별하기 위한 요약 자료이며, 전체 상태와 레이아웃을 대표하지 않는다.
- 기준이 충돌하면 `프롬프트의 명시적 API·상태 계약 → HTML의 디자인 토큰 및 State/Variant 가이드 → HTML의 최신 모바일·태블릿 실제 화면 → 컴포넌트 카탈로그 PNG` 순으로 적용한다.
- HTML에 표현되지 않은 동작을 임의로 추가하지 않는다.
- `core:designsystem` 컴포넌트의 Defaults/Tokens가 `AppTheme`의 공개 Semantic/Contextual 토큰을 조합한다. Primitive는 `internal`이며 Feature는 완성된 Component API와 Component token만 사용한다.

## 디자인 참조

- [Media Playback Controls 디자인 PNG 열기](../screenshots/common/11-media-playback-controls.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/11-media-playback-controls.png" alt="01A 컴포넌트 카탈로그 · Media Playback Controls" width="680" />

- HTML 화면명: `영상 플레이어`
- Image/Video AppBar 참조: [`../common/10-app-bar.md`](../common/10-app-bar.md)
