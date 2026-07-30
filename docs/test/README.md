# 테스트 컨벤션

## 스택

- JUnit 5 (Jupiter)
- MockK
- kotlinx-coroutines-test
- [Turbine](https://github.com/cashapp/turbine) (Flow 단정)

`blebridge.test.unit` / `blebridge.test.android` / `blebridge.feature` Convention Plugin으로 전체 모듈에 일관되게 배선됩니다. 자세한 내용은 [Gradle Convention Plugin](../../build-logic/README.md)을 참고하세요.

## ViewModel(MVI) 테스트

`MviViewModel`은 `orbit-test` 없이 `MutableStateFlow` + `Channel`로 직접 구현되어 있습니다(구조는 [core/mvi/README.md](../../core/mvi/README.md) 참고). 검증 방식은 다음과 같습니다.

- `state`(`StateFlow`)는 `.value`로 직접 검증합니다.
- `sideEffect`(`Flow`, 1회성 이벤트)는 Turbine의 `test { awaitItem() }`로 검증합니다.

### Dispatchers.Main 설치

`MviViewModel`이 `viewModelScope`(`Dispatchers.Main.immediate`)를 사용하므로, JVM 유닛 테스트에서는 `Dispatchers.Main`을 직접 설치해야 합니다. 아래 `MainDispatcherExtension`을 각 feature의 `src/test`에 동일하게 복사해서 씁니다.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherExtension(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext) {
        Dispatchers.resetMain()
    }
}
```

### 사용 예시

```kotlin
class SplashViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `...`() = runTest(mainDispatcherExtension.testDispatcher) {
        val viewModel = SplashViewModel(FakeAppInfoProvider)

        viewModel.sideEffect.test {
            viewModel.handleIntent(SplashIntent.Initialize)
            advanceTimeBy(SplashDefaults.DURATION_MILLIS)
            runCurrent()

            assertEquals(SplashSideEffect.NavigateToMain, awaitItem())
        }

        assertEquals(expectedState, viewModel.state.value)
    }
}
```

`runTest`에 `mainDispatcherExtension.testDispatcher`를 그대로 넘겨야 `Dispatchers.Main`과 가상 시간을 공유합니다. 넘기지 않으면 `viewModelScope`에서 실행되는 코루틴이 `advanceTimeBy`/`runCurrent`의 영향을 받지 않습니다.

## TDD 산출물 규약

멀티 에이전트 TDD 파이프라인([TDD 오케스트레이션](../agent/orchestration-tdd.md))에서
테스트를 먼저 설계할 때 쓰는 산출물 규약입니다. 사람이 직접 TDD를 진행할 때도 동일하게
활용할 수 있습니다.

### 유닛 케이스 vs UI 케이스

테스트케이스는 두 종류로 구분해 관리합니다.

- **유닛(`unit`)**: ViewModel(MVI) 로직. 위 "ViewModel(MVI) 테스트" 규약을 따릅니다.
  `state`는 `.value`, `sideEffect`는 Turbine으로 검증하고 `MainDispatcherExtension`을
  사용합니다. 위치는 `src/test/`(예: `XxxViewModelTest.kt`).
- **UI(`ui`)**: Compose 화면. `<Xxx>Screen`(stateless)을 **ViewModel·Hilt 없이** 직접
  렌더해, `UiState`와 콜백만 주입하고 상호작용·표시를 검증합니다. 위치는
  `src/androidTest/`(예: `XxxScreenTest.kt`). 요소 지정은 `<Screen>Defaults`의 테스트
  태그(`XxxDefaults.XXX_TAG`)를 사용하고 문자열을 하드코딩하지 않습니다.

### 산출물 (비커밋: `.orca/plan/<feature>/`)

- `mvp.md` — MVP 기능 목록(우선순위·범위·비범위).
- `testcases.md` — **순서 있는 테스트케이스 체크리스트**. 각 항목에 `id`(예: `TC-01`),
  유형(`unit`/`ui`), 대상 파일, 한 줄 설명, 상태 마커(`[ ]` 대기 / `[dev]` 구현 중 /
  `[review]` 리뷰 중 / `[x]` 완료)를 둡니다. 케이스는 의존·개발 순서로 정렬하며,
  Compose 화면은 [컴포넌트 로드맵](../design/00-common-component-roadmap.md)의 권장
  순서를 참고합니다.

### 주석 스텁

테스트케이스는 구현 전 대상 테스트 파일에 **주석 또는 `@Disabled` 스텁**으로 미리 seed합니다.
각 스텁에는 `testcases.md`의 `id`와 검증 의도를 남겨, 구현 에이전트가 케이스를 하나씩
Red→Green으로 채워가게 합니다. 스텁 seed는 프로덕션 소스를 수정하지 않고 `src/test`·
`src/androidTest`에만 둡니다.
