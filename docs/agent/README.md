# 에이전트 개발 가이드

Claude Code, Codex와 Orca가 생성한 작업 에이전트가 BleBridge의 동일한 프로젝트 규칙을
따르기 위한 공통 진입 문서입니다. `AGENTS.md`와 `CLAUDE.md`에는 이 내용을 복제하지
않고 이 문서를 안내하는 최소 지침만 둡니다.

## 시작 절차

1. 사용자 요청과 변경 권한을 먼저 확인합니다. 검토·분석 요청은 코드 변경 권한으로
   확대하지 않습니다.
2. 루트 [`README.md`](../../README.md)와 대상 모듈의 `README.md`를 확인합니다.
3. 아래 문서 라우팅 표에서 작업에 필요한 상세 가이드만 추가로 읽습니다.
4. 실제 `settings.gradle.kts`, 모듈 `build.gradle.kts`, 패키지와 기존 구현을 확인한 뒤
   작업합니다.
5. 변경 범위에 맞는 테스트·lint·컴파일을 실행하고 문서 영향도 함께 검토합니다.

## 문서 라우팅

| 작업 | 필수 문서 |
|---|---|
| 프로젝트 구조·모듈 의존성 | 루트 [`README.md`](../../README.md), 대상 모듈 README |
| Feature 화면·Compose UI | [`feature/README.md`](../../feature/README.md), [Feature UI 구성](../feature/README.md) |
| Navigation | [Navigation](../navigation/README.md), [`app/README.md`](../../app/README.md) |
| ViewModel·MVI | [`core/mvi/README.md`](../../core/mvi/README.md), [Test](../test/README.md) |
| 디자인 foundation·token | [`core/designsystem/README.md`](../../core/designsystem/README.md), [디자인시스템 상세 가이드](../design-system/README.md) |
| `core:designsystem` 컴포넌트 | 위 디자인시스템 문서, [`docs/design/common`](../design/common), [최종 디자인](../design/BLETransferApp.dc.html) |
| `core:ui` 컴포넌트·Content | [`core/ui/README.md`](../../core/ui/README.md), [`docs/design/ui`](../design/ui), [Feature UI 구성](../feature/README.md) |
| Domain·Data·Network | [`domain/README.md`](../../domain/README.md), [`data/README.md`](../../data/README.md), [`core/network/README.md`](../../core/network/README.md) 중 대상 범위 |
| Gradle convention·모듈 생성 | [`build-logic/README.md`](../../build-logic/README.md), 대상 모듈 README |
| 테스트 | [Test](../test/README.md), 대상 모듈 README |
| TDD 오케스트레이션(멀티 에이전트) | [TDD 오케스트레이션](../orchestration/orchestration-tdd.md), [Test](../test/README.md) |
| 디자인시스템 경계 오류 | [`lint/README.md`](../../lint/README.md), [디자인시스템 상세 가이드](../design-system/README.md) |
| 문서 변경 | 이 문서의 “문서 관리 규칙”, [`docs/README.md`](../README.md) |

컴포넌트 구현 프롬프트는 해당 컴포넌트를 실제로 구현하거나 수정할 때만 읽습니다. 단순
Feature 작업에서 모든 디자인 프롬프트를 한꺼번에 읽지 않습니다.

## 문서와 구현의 관계

- `README.md`와 `docs`는 프로젝트 컨벤션의 기준입니다.
- Gradle과 소스 코드는 현재 구현 상태의 근거입니다.
- [`BLETransferApp.dc.html`](../design/BLETransferApp.dc.html)과 컴포넌트 프롬프트에는
  아직 프로젝트 코드에 반영되지 않은 목표 디자인이 포함될 수 있습니다.
- 문서와 구현이 다르다고 해서 어느 한쪽을 임의로 덮어쓰지 않습니다. 현재 구현과 목표
  설계를 구분하고, 사용자 요청 범위에 따라 코드 또는 문서를 수정합니다.
- 서로 다른 문서의 규칙이 충돌하면 더 구체적인 작업 문서와 대상 모듈 README를 우선
  확인합니다. 해결되지 않거나 결과가 크게 달라지는 경우 사용자에게 알립니다.

## 아키텍처 경계

- `app`은 Application, Activity, Hilt graph와 NavHost를 조립하는 composition root입니다.
- `feature:*`는 다른 feature, data, app을 직접 참조하지 않습니다.
- Feature UI는 `Route → Screen → 선택적 core:ui Content` 경계를 따릅니다.
- 화면 이동은 callback으로 요청하고 `app`의 NavHost가 `NavController`를 소유합니다.
- Feature의 상태 변경은 `MviViewModel`의 Intent와 Mutation 경로를 따르고 일회성 동작은
  SideEffect로 표현합니다.
- `domain`은 Android와 data 구현에 의존하지 않으며 repository 계약을 소유합니다.
- `data`는 domain 계약 구현과 datasource, mapper, DI binding을 소유합니다.
- `core:designsystem`은 foundation, contextual token과 범용 디자인 컴포넌트를 소유합니다.
- `core:ui`는 디자인시스템을 조합한 앱 공통 UI와 완성형 Content를 소유합니다.
- Feature는 foundation token, raw 디자인 값, 정책 대상 Material3 컴포넌트를 직접
  사용하지 않습니다. 이 경계는 `:lint:designsystem`이 검사합니다.

세부 구조와 예외는 위 문서 라우팅 표의 해당 문서를 기준으로 하며, 이 요약만으로 구현
세부사항을 추정하지 않습니다.

## 작업 원칙

- 기존 변경은 사용자 작업으로 간주하고 관련 없는 파일을 되돌리거나 정리하지 않습니다.
- 요청 범위를 넘어선 리팩터링, dependency 추가, 공개 API 변경을 임의로 수행하지 않습니다.
- 새로운 구조를 만들기 전에 기존 패턴과 공용 API를 검색합니다.
- Feature에 임시 raw color, dp/sp 또는 Material 컴포넌트를 추가해 lint를 우회하지 않습니다.
- 기존 lint baseline은 과도기 기록입니다. 신규 위반을 숨기기 위해 baseline을 다시
  생성하거나 항목을 추가하지 않습니다.
- 공개 API, 패키지, 모듈 의존성, 컨벤션을 변경하면 관련 README도 같은 작업에서
  갱신합니다.
- 디자인과 실제 코드가 다른 것은 결함이라고 단정하지 않습니다. 디자인 최종화 후 구현
  예정인지 확인하고 현재 요청 범위를 따릅니다.

## 검증

변경한 범위에 가장 가까운 검증부터 실행합니다. 루트에서 Gradle Wrapper를 사용합니다.

```bash
./gradlew :<module>:compileDebugKotlin
./gradlew :<module>:testDebugUnitTest
./gradlew :<feature>:lintDebug
```

모듈 종류에 따라 task 이름이 다를 수 있으므로 해당 모듈 README와 Gradle task를
확인합니다. 대표적인 프로젝트 검증은 다음과 같습니다.

```bash
./gradlew -p build-logic compileKotlin
./gradlew :lint:designsystem:test
./gradlew :core:designsystem:compileDebugKotlin
./gradlew :core:ui:compileDebugKotlin
./gradlew :app:assembleDebug :app:lintDebug
```

에뮬레이터나 외부 환경이 필요한 검증을 실행하지 못하면 성공한 검증과 미실행 사유를
명확하게 보고합니다.

## 완료 조건

- 사용자 요청 범위가 구현 또는 문서에 반영됐습니다.
- 관련 모듈의 컴파일·테스트·lint를 위험도에 맞게 실행했습니다.
- 신규 lint baseline이나 불필요한 suppression으로 문제를 숨기지 않았습니다.
- 패키지·API·Gradle·컨벤션 변경에 따른 문서 영향을 확인했습니다.
- 목표 디자인과 현재 구현의 차이를 결과에 명확히 구분했습니다.
- 실패한 검증, 남은 작업과 외부 blocker를 숨기지 않고 전달했습니다.

## 문서 관리 규칙

- 모듈 README에는 책임, 의존성, 주요 진입점과 대표 검증 명령을 둡니다.
- 여러 모듈에 적용되는 긴 규칙과 예제는 `docs/<topic>/README.md`로 분리합니다.
- `AGENTS.md`, `CLAUDE.md`, Skill과 작업 프롬프트에 프로젝트 규칙을 복제하지 않고 이
  문서 및 주제별 문서를 링크합니다.
- 현재 구현과 구현 예정 사항을 같은 표현으로 섞지 않습니다. 계획은 “구현 전”, “목표
  디자인” 또는 “예정”으로 표시합니다.
- 링크는 저장소 상대 경로를 사용합니다. Android Studio 이미지 미리보기처럼 도구 제약이
  있는 경우에만 절대 경로를 사용합니다.
- 문서를 추가하거나 이동하면 [`docs/README.md`](../README.md)의 인덱스를 갱신합니다.

## Orca 작업

Orca 글로벌 Skill은 이 문서를 복제하지 않고, 작업 유형에 맞춰 이 문서와 문서 라우팅 표의
대상 문서를 읽도록 안내하는 역할만 맡습니다. Orca가 아닌 환경에서 직접 실행한 Claude
Code와 Codex는 각각 루트 `CLAUDE.md`, `AGENTS.md`를 통해 동일한 규칙으로 진입합니다.

TDD 기반 멀티 에이전트 파이프라인(기획/분석·테스트케이스·개발/구현·코드리뷰)의 역할
분담·산출물·루프 게이팅 규약은 [TDD 오케스트레이션](../orchestration/orchestration-tdd.md)에 정의합니다.
