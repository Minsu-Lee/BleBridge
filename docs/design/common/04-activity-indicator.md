# 04. ActivityIndicator 구현 프롬프트

## 작업 브랜치

`feature/designsystem/activity-indicator` 브랜치를 생성한 뒤 이 프롬프트 작업을 시작한다.

```bash
git checkout -b feature/designsystem/activity-indicator
```

## Claude Code에게 요청

`core:designsystem`에 Idle/Connecting/Advertising/Scanning을 시각화하는 BLE activity indicator를 구현해줘. 첫 진입 상태에서는 애니메이션이 없어야 하고, 광고·스캔 중에만 pulse가 동작해야 한다.

01A 계약: Variant `Phone/Tablet/Inline`, State `Idle/Connecting/Advertising/Scanning`.

## API 샘플

```kotlin
enum class ActivityIndicatorVariant {
    Phone,
    Tablet,
    Inline,
}

@Composable
fun ActivityIndicator(
    status: ConnectionActivityStatus,
    modifier: Modifier = Modifier,
    variant: ActivityIndicatorVariant =
        ActivityIndicatorVariant.Phone,
    contentDescription: String? = null,
)
```

## Defaults 샘플

```kotlin
internal object ActivityIndicatorDefaults {
    const val INDICATOR_TAG = "blebridge_activity_indicator"
    const val PULSE_TAG = "blebridge_activity_pulse"
    val PhoneSize = 88.dp
    val TabletSize = 70.dp
    val PhoneCoreSize = 64.dp
    val TabletCoreSize = 52.dp
}
```

## 필수 토큰

- `AppTheme.activityColors.forStatus(status)`
- `AppTheme.motion.progressRotationMillis`
- `AppTheme.motion.waitingRotationMillis`
- `AppTheme.motion.clientPulseMillis`
- `AppTheme.motion.brandPulseMillis`
- `AppTheme.motion.activityPulseStartScale`
- `AppTheme.motion.activityPulseEndScale`
- `AppTheme.motion.activityPulseAlpha`
- pulse 적용 여부: `AppTheme.activityMotion.usesPulse(status)`
- icon: `AppTheme.icons.Logo`
- shape: `AppTheme.radius.full`

## 상태 규칙

- Idle: neutral core, pulse/spinner 없음
- Connecting: spinner, pulse 없음
- Advertising: Server 화면이지만 advertising purple 또는 디자인에서 지정한 activity color 사용
- Scanning: Client activity color + pulse
- 시스템 animation duration scale이 0이면 반복 animation을 실행하지 않고 정적 core만 표시
- 상태 변경 시 이전 infinite transition이 남지 않아야 한다.
- Phone/Tablet/Inline 크기는 Defaults가 소유하고 호출자가 임의 dp를 전달하지 않는다.

## 테스트

- Idle pulse node 미노출
- Advertising/Scanning pulse node 노출
- Connecting progress semantics
- 상태 변경 후 올바른 description
- reduced-motion 환경을 가능한 범위에서 분리 가능한 animation policy로 테스트
- Phone/Tablet/Inline variant별 크기와 semantics

## 사용 예시

```kotlin
ActivityIndicator(
    status = if (isScanning) {
        ConnectionActivityStatus.Scanning
    } else {
        ConnectionActivityStatus.Idle
    },
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

- [Activity Indicator 디자인 PNG 열기](../screenshots/common/04-activity-indicator.png)
<img src="/Users/jackson/AndroidStudioProjects/BleBridge/docs/design/screenshots/common/04-activity-indicator.png" alt="01A 컴포넌트 카탈로그 · Activity Indicator" width="680" />

- HTML 화면명: `서버 (첫 진입·중지)`, `서버 (광고·대기)`, `클라이언트 (첫 진입·스캔 중지)`, `클라이언트 (스캔)`
