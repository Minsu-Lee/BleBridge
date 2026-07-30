# BLE Bridge 공통 컴포넌트 개발 로드맵

이 문서는 [`BLETransferApp.dc.html`](./BLETransferApp.dc.html) 전체 디자인에서 추출한 공통 컴포넌트를 모듈 경계별로 나눈 권장 구현 순서다. 먼저 `common`의 디자인시스템 primitive를 구현한 뒤 `ui`의 앱 공용 compound component를 구현한다.

## 공통 원칙

- 도메인과 동작을 모르는 시각적 primitive와 layout은 `core:designsystem`에 둔다.
- BLE·채팅·전송처럼 앱 의미를 포함하고 여러 Feature에서 공유되는 compound component는 `core:ui`에 둔다.
- Feature의 ViewModel, Intent, UiState, navigation 또는 domain 모델을 `core:designsystem`과 `core:ui`에서 참조하지 않는다.
- 색상·글꼴·간격·radius·icon은 `AppTheme` API를 사용한다.
- 토큰 이름과 실제 값은 `BLETransferApp.dc.html`의 `01D · 전체 토큰 레퍼런스 (완전판)`을 기준으로 하며, 존재하지 않는 별칭이나 임의의 magic value를 추가하지 않는다.
- 역할 색상은 `ConnectionRoleProvider`, 채팅 표현은 `ChatModeProvider` 범위에서 소비한다.
- BLE 동작 상태는 `AppTheme.activityColors.forStatus(status)`를 사용한다.
- interactive component는 최소 48dp 터치 영역, content description, enabled semantics를 보장한다.
- 각 컴포넌트에는 Preview와 Android Compose UI test를 추가한다.
- test tag와 시간·횟수 같은 구현 상수는 해당 화면 또는 컴포넌트 소유 모듈의 `internal object <Component>Defaults`로 모은다. Splash 표현 상수는 `core:ui`의 `SplashDefaults`, 화면 전환 시간은 Feature의 `SplashDefaults`가 각각 소유한다.
- 공통 컴포넌트가 자체 비즈니스 상태를 소유하지 않도록 state hoisting을 적용한다.
- 채팅 상세 AppBar에는 설정 및 서버/클라이언트 전환 action을 두지 않는다.
- 설정 진입 action은 기기 연결 AppBar에만 두고 `AppIcons.Settings`의 기어 아이콘을 사용한다.
- 이미지 상세와 영상 플레이어의 AppBar는 `core:designsystem` App Bar를 조합한다.
- 재생 컨트롤은 player/decoder/BLE 상태를 모르는 controlled primitive이므로 `core:designsystem`이 담당하고, 실제 재생 엔진과 lifecycle은 Feature가 담당한다.

## 1단계 · core:designsystem

| 순서 | 컴포넌트 | 파일 | 선행 의존 |
|---:|---|---|---|
| 01 | Action Button | [`common/01-action-button.md`](./common/01-action-button.md) | foundation |
| 02 | Icon Button | [`common/02-icon-button.md`](./common/02-icon-button.md) | foundation |
| 03 | Segmented Control | [`common/03-segmented-control.md`](./common/03-segmented-control.md) | 01 |
| 04 | BLE Activity Indicator | [`common/04-activity-indicator.md`](./common/04-activity-indicator.md) | 02 |
| 05 | Status Banner | [`common/05-status-banner.md`](./common/05-status-banner.md) | foundation |
| 06 | Empty State | [`common/06-empty-state.md`](./common/06-empty-state.md) | foundation |
| 07 | Choice Dialog | [`common/07-choice-dialog.md`](./common/07-choice-dialog.md) | 01 |
| 08 | Settings Field | [`common/08-settings-field.md`](./common/08-settings-field.md) | 01 |
| 09 | Adaptive Two Pane Scaffold | [`common/09-adaptive-two-pane.md`](./common/09-adaptive-two-pane.md) | foundation |
| 10 | App Bar | [`common/10-app-bar.md`](./common/10-app-bar.md) | 02 |
| 11 | Media Playback Controls | [`common/11-media-playback-controls.md`](./common/11-media-playback-controls.md) | 02, 10 |

## 2단계 · core:ui

| 순서 | 컴포넌트 | 파일 | 선행 의존 |
|---:|---|---|---|
| 01 | Device List Item | [`ui/01-device-list-item.md`](./ui/01-device-list-item.md) | common 01, 02 |
| 02 | Chat Chrome | [`ui/02-chat-chrome.md`](./ui/02-chat-chrome.md) | common 02, 05 |
| 03 | Message Bubble | [`ui/03-message-bubble.md`](./ui/03-message-bubble.md) | ui 02 |
| 04 | Transfer Card | [`ui/04-transfer-card.md`](./ui/04-transfer-card.md) | common 02, ui 03 |
| 05 | Chat Input | [`ui/05-chat-input.md`](./ui/05-chat-input.md) | common 01, 02 |
| 06 | Attachment Sheet | [`ui/06-attachment-sheet.md`](./ui/06-attachment-sheet.md) | common 02 |

## 검증 명령

```bash
./gradlew :core:designsystem:compileDebugKotlin
./gradlew :core:designsystem:lintDebug
./gradlew :core:designsystem:assembleDebugAndroidTest
./gradlew :core:ui:compileDebugKotlin
./gradlew :core:ui:lintDebug
./gradlew :core:ui:assembleDebugAndroidTest
```

연결된 emulator/device가 있으면 다음도 실행한다.

```bash
./gradlew :core:designsystem:connectedDebugAndroidTest
./gradlew :core:ui:connectedDebugAndroidTest
```

## 디자인 샘플

- 전체 디자인: [`BLETransferApp.dc.html`](./BLETransferApp.dc.html)
- 클래식 채팅: HTML 화면명 `클래식 메신저 모드`
- 전송 카드와 입력: 각 채팅 모드 및 `전송 실패 · 재시도` 화면
- 개발 하이브리드: HTML 화면명 `개발 하이브리드 모드`
- 터미널: HTML 화면명 `터미널 로그형 모드`
