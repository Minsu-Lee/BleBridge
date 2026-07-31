# 프로젝트 문서 인덱스

README는 빠른 진입과 현재 모듈의 책임을 설명하고, 길어지는 구현·운영 규칙은 이 `docs`
아래의 주제별 문서에서 관리합니다.

## 아키텍처와 개발 컨벤션

| 문서 | 내용 |
|---|---|
| [Feature UI 구성](feature/README.md) | Route, Screen, 선택적 Content와 component 분리·승격 기준 |
| [Navigation](navigation/README.md) | navigation route, composable Route, app NavHost 책임 |
| [Test](test/README.md) | JUnit 5, coroutine, MVI SideEffect와 Compose UI 테스트 |
| [Design system](design-system/README.md) | Primitive/Semantic/Contextual/Component token 상세 규칙 |
| [Custom lint](../lint/README.md) | Feature 디자인시스템 경계의 자동 검사 |
| [에이전트 개발 가이드](agent/README.md) | Claude Code, Codex와 Orca 작업자의 문서 라우팅·작업 원칙·완료 조건 |
| [TDD 오케스트레이션](orchestration/orchestration-tdd.md) | 멀티 에이전트 TDD 파이프라인 계약(역할·산출물·루프 게이팅, 역할 계약 문서) |

## 디자인 구현 자료

| 경로 | 내용 |
|---|---|
| [BLETransferApp.dc.html](design/BLETransferApp.dc.html) | 최신 화면 디자인, 토큰과 component catalog |
| [공통 컴포넌트 프롬프트](design/common) | `core:designsystem` 구현 순서와 상세 계약 |
| [일반 컴포넌트 프롬프트](design/ui) | `core:ui` 구현 순서와 상세 계약 |
| [컴포넌트 로드맵](design/00-common-component-roadmap.md) | 의존 관계와 권장 개발 순서 |

## 모듈 문서

- [App](../app/README.md)
- [Core](../core/README.md)
- [Domain](../domain/README.md)
- [Data](../data/README.md)
- [Feature](../feature/README.md)
- [Build logic](../build-logic/README.md)

## 문서 관리 원칙

- 모듈 README에는 책임, 의존성, 주요 진입점과 검증 명령만 둡니다.
- 여러 모듈에 적용되는 규칙과 긴 예시는 `docs/<topic>/README.md`로 분리합니다.
- 코드 이름, 패키지, Gradle task 또는 모듈 의존성이 변경되면 관련 README를 같은
  변경에서 갱신합니다.
- 구현 예정 내용은 현재 구현과 구분해 “구현 전” 또는 “계획”으로 표시합니다.
- 절대 경로는 Android Studio 이미지 미리보기처럼 필요한 경우에만 사용하고 일반 문서
  링크는 프로젝트 상대 경로를 사용합니다.
