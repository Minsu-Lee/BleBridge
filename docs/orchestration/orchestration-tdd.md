# TDD 오케스트레이션 파이프라인 계약

Orca `orchestration` 스킬로 멀티 에이전트 환경을 구성해 **TDD 기반 피처 개발**을 진행할 때
4개 역할 에이전트가 동일한 규약으로 맞물리게 하는 계약 문서입니다.

이 문서는 프로젝트 규칙을 복제하지 않습니다. 아키텍처·MVI·테스트·디자인시스템 규칙은
[`docs/agent/README.md`](../agent/README.md)의 시작 절차와 문서 라우팅 표를 그대로 따르고, 이
문서는 **역할 분담·산출물·핸드오프·루프 게이팅**만 정의합니다.

## 역할 에이전트

역할 계약 문서는 커밋되는 `docs/orchestration/<role>.md`(이 폴더)에 있고, 작업 산출물은
비커밋 폴더에 둡니다. 이 문서들은 **역할 계약(참조 문서)**이며 `.claude/agents/`의 로드형
서브에이전트가 아닙니다 — 워커에 문서 경로를 주입해 사용합니다(아래 "워커 기동" 참조).

| 역할 | 에이전트 | 모델(프로젝트 기본) | 주 산출물 |
|---|---|---|---|
| 기획/분석 | `feature-analyst` | Claude Opus wrapper | `.orca/plan/<feature>/analysis.md` |
| 테스트케이스 | `testcase-author` | Claude Sonnet wrapper | `.orca/plan/<feature>/mvp.md`, `testcases.md` + 테스트 스텁 |
| 개발/구현 | `tdd-implementer` | Claude Sonnet wrapper (+`codex` 위임) | 프로덕션·테스트 코드, `testcases.md` 상태 갱신 |
| 코드리뷰 | `code-reviewer` | Claude Sonnet wrapper (+`codex review` 위임) | `.orca/plan/<feature>/review/*.md` |

모델은 **권장값**입니다. 각 역할 문서(`docs/orchestration/<role>.md`)의 frontmatter는 권장
`model`·`tools`를 기록해 두지만, 이 문서들은 로드형 서브에이전트가 아니므로 frontmatter가
자동 적용되지 않습니다 — 실제 모델·effort는 **워커 기동 명령**에서 지정합니다(예: Codex
`-c model_reasoning_effort="high"`).

## 파이프라인 개요

```
feature-analyst  →  testcase-author  →  ┌─ tdd-implementer  (케이스 N: Red→Green, 커밋 안 함) ─┐
                                        │  code-reviewer    (케이스 N: --uncommitted 리뷰)    │ 케이스
                                        │  tdd-implementer  (케이스 N: test/feat 2커밋)       │ 루프
                                        └─ 코디네이터     ─ (testcases.md → [x], 다음 케이스) ───┘
                                              ↓ 모든 케이스 완료
                                        code-reviewer(최종 전체 리뷰 1회)
```

**구현 → 리뷰 → 커밋** 순서가 핵심입니다. 커밋을 먼저 하면 `codex review --uncommitted`가
빈 diff를 보게 됩니다.

## 호출 방식과 기본값 (별도 입력 없이 동작)

**호출**: `orca` CLI로 orchestration 스킬을 구동해 멀티 에이전트 환경을 구성합니다
(`orca terminal create` + `orca orchestration task-create/dispatch/check`). 개발 요청은 보통
**md 프롬프트 하나**로 코디네이터에게 전달하며, **그 요청 외의 별도 입력을 강제하지 않습니다.**
아래 값은 요청에서 자동 판별하거나 기본값을 쓰고, **정말 모호할 때만** 사용자에게 확인합니다.
요청 md가 어떤 값을 명시하면 그것을 우선합니다. 진행 로그에 어떤 기본값을 썼는지 남깁니다.

- **워커 런타임(프로젝트 기본) = 전부 Claude(Sonnet) 세션.** 그래서 `commit-message` 등 Claude
  스킬을 항상 씁니다. codex가 필요한 역할(리뷰 등)은 Sonnet 워커가 `codex` CLI를 호출→**완료
  대기**→결과를 받아 스킬로 마무리하는 **(a′) 위임 wrapper**로 돕니다. 네이티브 codex 워커(b)는
  스킬을 못 쓰므로 기본이 아닙니다. `feature-analyst`만 품질용으로 Opus 세션을 권장합니다.
- **트랙 자동 판별**: 요청 대상으로 정합니다 — `docs/design/common|ui` 프롬프트/`core:*` →
  컴포넌트, `domain`/`data` 로직만 → 도메인·데이터, feature 화면 → feature. 혼합이면
  ①도메인 → ②컴포넌트 → ③feature 순서([적용 트랙](#적용-트랙)).
- **작업 브랜치 기본**: **`develop`에서 딴** `feature/<타깃-slug>`(gitflow). 현재 보호
  브랜치(`main`/`master`/`develop`)면 신규 생성, Orca worktree 전용 브랜치가 있으면 재사용
  ([작업 브랜치 준비](#작업-브랜치-준비-착수-전-precondition)).
- **리뷰 base 기본**: 고정값이 아니라 **작업 브랜치가 갈라져 나온 부모**를 쓴다 —
  feature/release는 `origin/develop`, hotfix는 `origin/main`. remote가 없으면
  `--uncommitted`만으로 진행.
- **타임아웃 기본**: 첫 디스패치·`TC-00` `1800000`(30분), 이후 `900000`(15분).
- **커밋/푸시**: 케이스당 2커밋(컴포넌트 1커밋)을 `commit-message --auto --no-push` **1회 호출**로.
  스킬이 커밋 전 gradle 검증 1회 → 목적별 분할 커밋까지만 하고 **푸시는 생략**한다. 커밋은 작업
  브랜치에 로컬로 쌓이고, **푸시는 파이프라인 맨 끝에서 코디네이터가 한 번만** 수행한다(아래
  "최종 푸시"). 보호 브랜치(`main`/`master`/`develop`) 위라면 스킬이 `--auto` 커밋을 거부한다.

즉 사용자는 **개발 요청 md만** 주면 되고, 나머지는 코디네이터가 이 기본값으로 채웁니다.

## 적용 트랙

이 파이프라인은 세 트랙을 지원합니다. **같은 4개 에이전트가 수행**하고, 착수 전 코디네이터가
트랙을 정해 모든 워커에 알립니다.

- **feature 트랙(기본)**: 새 feature 화면을 MVI로 개발. 이 문서의 모든 기본 규약이 이 트랙
  기준입니다.
- **컴포넌트 트랙**: `core:designsystem`·`core:ui`의 공용 컴포넌트를 [`docs/design`](../design)의
  컴포넌트 프롬프트(`common/*`, `ui/*`)와 [컴포넌트 로드맵](../design/00-common-component-roadmap.md)
  으로 개발. 아래 ["컴포넌트 트랙 규약"](#컴포넌트-트랙-규약-coredesignsystem--coreui)의 델타를
  적용합니다.
- **도메인/데이터 트랙**: UI 없이 `domain`·`data`의 순수 로직(repository·유스케이스·모델·구현)을
  개발. 아래 ["도메인/데이터 트랙 규약"](#도메인데이터-트랙-규약-domain--data)의 델타를 적용합니다.

트랙 판별:
- 요청이 `docs/design/common|ui`의 프롬프트거나 대상이 `core:designsystem`/`core:ui`면
  **컴포넌트 트랙**.
- 대상이 `domain`/`data`의 로직뿐이고 화면·MVI가 없으면 **도메인/데이터 트랙**.
- feature 화면/모듈이면 **feature 트랙**.

**한 요청에 여러 트랙이 섞이면** 의존 방향(아래→위)대로 순서를 잡습니다:
**① 도메인/데이터 → ② 컴포넌트 → ③ feature 화면.** 각 단계를 완전한 케이스 루프(구현→리뷰→커밋)
로 끝낸 뒤 다음 단계로 갑니다. feature 화면(③)은 자신이 의존하는 도메인 계약(①)과 컴포넌트(②)가
먼저 green이어야 착수합니다. 컴포넌트 사이 순서는 [로드맵](../design/00-common-component-roadmap.md)을
따릅니다.

## 작업 브랜치 준비 (착수 전 precondition)

이 프로젝트는 **gitflow**를 씁니다. 개발 케이스를 디스패치하기 전에 **코디네이터가 작업
브랜치를 확정**합니다. 보호 브랜치(`main`/`master`/`develop`)에 직접 커밋하지 않습니다.

- 확인: `git rev-parse --is-inside-work-tree`, `git branch --show-current`.
- **분기 기준은 `develop`입니다.** 최신 상태에서 딴 뒤 착수합니다.

  ```bash
  git fetch origin
  git switch -c feature/<slug> origin/develop
  ```

- feature 트랙: `feature/<feature>` 브랜치를 생성/확인. **Orca worktree로 이미 전용 브랜치가
  있으면 그것을 재사용**하고 중복 생성하지 않습니다.
- 컴포넌트 트랙: 각 컴포넌트 프롬프트가 지정한 브랜치를 씁니다
  (`feature/designsystem/<x>`, `feature/ui/<x>`). 역시 `develop`에서 땁니다.
- **기준 브랜치 확정**: 최종 리뷰 `codex review --base <기준브랜치>`의 base는 **작업 브랜치가
  갈라져 나온 부모**입니다 — feature·release·컴포넌트 트랙은 `origin/develop`, hotfix는
  `origin/main`. `origin/main`으로 고정하면 `develop`이 앞선 만큼의 남의 커밋까지 diff에
  섞여 리뷰 범위가 부풀어 오릅니다.
- 케이스별 커밋은 `commit-message --auto --no-push`로 이 작업 브랜치에 **로컬로만 쌓입니다**
  (케이스마다 푸시하지 않음). 보호 브랜치 위라면 스킬이 `--auto`에서 커밋을 거부하므로, 작업
  브랜치 준비는 착수 전 precondition입니다.
- **최종 푸시(파이프라인 끝 1회)**: 케이스가 모두 끝나 최종 리뷰까지 통과하면 코디네이터가
  작업 브랜치를 **한 번** 푸시합니다(첫 푸시는 `-u origin <브랜치>`, `--force` 금지, 거부되면
  재시도·rebase 없이 보고). 이후 작업 브랜치를 **`develop`으로 머지**합니다(`main` 직행 금지).
  릴리스 시 `develop` → `main`은 사용자가 판단합니다.

## 산출물 규약 (비커밋: `/.orca/plan/<feature>/`)

`.orca/`는 `.gitignore` 대상입니다. `<feature>`는 대상 feature 모듈명(예: `main`, `chat`).

- `analysis.md` — 분석·설계(기획 산출). 아래 "analysis.md 필수 항목" 참조.
- `mvp.md` — MVP 기능 목록(우선순위·범위·비범위).
- `testcases.md` — **순서 있는 테스트케이스 체크리스트**. 각 항목:
  - `id`(예: `TC-01`), 유형(`unit`/`ui`/`setup`), 대상 파일(예: `.../XxxViewModelTest.kt`),
    한 줄 설명, 상태 마커 `[ ]`(대기) / `[dev]`(구현 중) / `[review]`(리뷰 중) /
    `[x]`(완료). 마커별 갱신 주체는 아래 "상태 마커 소유권" 표를 따릅니다.
- `review/<id>.md` — 케이스별 리뷰 결과. `review/final.md` — 최종 전체 리뷰.

테스트케이스 목록·MVP·주석 스텁의 상세 컨벤션은
[`docs/test/README.md`](../test/README.md)의 "TDD 산출물 규약"을 기준으로 합니다.

### analysis.md 필수 항목

- 대상 모듈 판단 + **신규 모듈 여부**. 신규면 `settings.gradle.kts` 등록과
  `blebridge.feature` 스캐폴딩 명세, 모듈 형태(도메인 화면 모듈 vs 컨테이너 모듈,
  [`feature/README.md`](../../feature/README.md) 기준).
- **MVI 4계약 초안**: `<Screen>UiState / Intent / Mutation / SideEffect`
  ([`core/mvi/README.md`](../../core/mvi/README.md)).
- UI 경계: `Route → Screen → 선택적 core:ui Content` 분해
  ([Feature UI 구성](../feature/README.md)).
- Navigation: `<Feature>Route`, `NavGraphBuilder` 확장, app NavHost 콜백
  ([Navigation](../navigation/README.md)).
- 필요한 디자인시스템/`core:ui` 컴포넌트와 [최종 디자인](../design/BLETransferApp.dc.html),
  [`docs/design`](../design) 프롬프트 매핑.
- Domain·Data 계약 영향(repository interface in `domain`, 구현 in `data`).
- **목표 디자인과 현재 구현의 차이**를 구분해 명시.

## 케이스 루프 게이팅 (Orca 코디네이터 책임)

케이스별 진행은 **개별 에이전트가 서로를 직접 호출하지 않고 Orca 코디네이터가 게이팅**합니다.

**리뷰는 커밋 전 작업물을 대상으로 합니다.** dev는 스스로 커밋하지 않고, 코디네이터가 리뷰
pass 이후에 커밋을 별도 디스패치합니다(이유는 아래 "리뷰-커밋 순서" 참조).

1. 코디네이터가 `tdd-implementer`에 케이스 N 디스패치(`dispatch --inject`).
2. dev가 케이스 N을 Red→Green으로 구현 → 범위 gradle 검증 통과 → **커밋하지 않고**
   `worker_done`.
3. 코디네이터가 `code-reviewer`에 케이스 N 리뷰 디스패치(`codex review --uncommitted`).
4. 리뷰 결과 분기:
   - **pass** → 코디네이터가 dev에 **커밋 디스패치**(케이스 N, 테스트/구현 2커밋,
     `--auto --no-push`로 **커밋만**) → 완료 보고를 받으면 코디네이터가 `testcases.md`의 케이스
     N을 `[x]`로 갱신 → 케이스 N+1 디스패치.
   - **이슈** → dev에 수정 재디스패치. 수정 후 3번으로 돌아갑니다.
5. 모든 케이스 완료 후 코디네이터가 `code-reviewer`에 **최종 전체 리뷰 1회** 디스패치.
6. 최종 리뷰까지 pass면 코디네이터가 **최종 푸시 1회**(작업 브랜치를 `git push`, 첫 푸시는
   `-u origin <브랜치>`). 케이스별로는 `--no-push`라 이때 처음 원격에 올라갑니다. 이후
   `develop` 머지 판단으로 넘어갑니다("작업 브랜치 준비"의 머지 규약).

### 상태 마커 소유권

`testcases.md`의 상태 마커는 다음 주체가 갱신합니다. 중복 갱신하지 않습니다.

| 마커 | 의미 | 갱신 주체 |
|---|---|---|
| `[ ]` | 대기 | `testcase-author` (초기 생성) |
| `[dev]` | 구현 중 | `tdd-implementer` (케이스 착수 시) |
| `[review]` | 리뷰 중 | `tdd-implementer` (green 후 완료 보고 시) |
| `[x]` | 완료 | **Orca 코디네이터** (리뷰 pass + 커밋 완료 확인 후) |

`[x]`만 코디네이터 소관입니다. dev는 자기 케이스를 완료로 올리지 않고, code-reviewer는
`testcases.md`를 수정하지 않습니다.

### 재시도 제한과 에스컬레이션

- **같은 케이스의 수정 재디스패치는 최대 2회**(최초 구현 포함 총 3회 시도)까지 허용합니다.
- 3회째 리뷰에서도 이슈가 남으면 루프를 멈추고 사용자에게 에스컬레이션합니다. 그때
  `.orca/plan/<feature>/review/<TC-id>.md`에 남은 이슈와 시도 이력을 정리해 둡니다.
- dev가 같은 원인으로 2회 연속 gradle 검증에 실패해도 즉시 에스컬레이션합니다(자체 재시도
  금지).
- 케이스를 건너뛰지 않습니다. 막힌 케이스는 사용자 판단을 받고 `testcases.md`에 보류
  사유를 남깁니다.

### 대기 타임아웃

- 첫 디스패치와 신규 모듈 생성 케이스는 KSP·Hilt 첫 빌드 때문에 오래 걸립니다 —
  `--timeout-ms 1800000`(30분).
- 이후 일반 케이스는 `--timeout-ms 900000`(15분)로 충분합니다.
- 타임아웃이 나면 자동 재디스패치하지 않고 워커 터미널 상태를 먼저 확인합니다.

수동 루프 예:

```bash
orca orchestration task-create --spec "TC-01: <설명>" --json
orca orchestration dispatch --task <dev_task> --to <dev_handle> --inject --json
orca orchestration check --wait --types worker_done,escalation,decision_gate --timeout-ms 1800000 --json
orca orchestration dispatch --task <review_task> --to <review_handle> --inject --json
orca orchestration check --wait --types worker_done,escalation,decision_gate --timeout-ms 900000 --json
# 리뷰 pass → dev에 커밋 디스패치 → 완료 확인 후 코디네이터가 testcases.md를 [x]로 갱신
```

## 파이프라인 지연 요인과 속도 개선

지연 요인:

- **Gradle/KSP/Hilt 콜드 빌드** — 가장 큼. 첫 디스패치·`TC-00`·모듈 첫 테스트에서 애노테이션
  처리로 오래 걸립니다.
- **직렬 케이스 루프** — dev→리뷰→커밋→다음이 케이스 단위로 순차(TDD 의존성상 불가피).
- **codex 위임 왕복** — dev·reviewer가 `codex` CLI로 나갔다 오는 모델 홉 + `tui-idle` 대기.
- **케이스마다 리뷰** — 케이스당 `codex review` 호출 오버헤드.
- **디스패치 왕복** — `check --wait`/`terminal wait`의 고정 오버헤드.

개선:

- **같은 worktree에서 Gradle 데몬 warm 유지** — 모든 케이스를 한 worktree에서 돌려 데몬·
  configuration-cache·build-cache를 재사용하면 콜드 빌드 반복을 피합니다
  (`org.gradle.caching=true`, configuration cache).
- **첫 빌드 pre-warm** — 첫 디스패치 전에 대상 모듈 컴파일을 한 번 돌려 KSP/Hilt를 데워
  둡니다. Android 모듈은 `./gradlew :<모듈>:compileDebugKotlin`, 순수 JVM 모듈은
  `./gradlew :<모듈>:compileKotlin`입니다(아래 모듈 타입 주의 참조).
- **태스크 스코프 최소화(이미 규약)** — `:feature:<x>:testDebugUnitTest`처럼 모듈·태스크 한정.
  전체 빌드 금지.
- **codex 위임 선택적** — 단순 케이스는 Sonnet이 직접 처리해 모델 홉 1개를 줄이고, `codex`는
  복잡 케이스·리뷰에만 씁니다.
- **독립 케이스 병렬화** — 서로 의존 없는 **트랙/피처**는 워커를 나눠 병렬 진행합니다(같은 피처
  내 케이스는 순차 유지). 혼합 요청의 ①도메인·②컴포넌트가 독립이면 병렬 가능.
- **유닛 테스트 병렬 실행** — JUnit5 parallel execution으로 모듈 내 테스트 시간을 줄입니다.
- **타임아웃은 상한**일 뿐 대기시간이 아닙니다 — 정상 완료 시 `worker_done` 즉시 다음으로 진행.

## 신규 모듈 스캐폴딩 (TC-00)

`analysis.md`가 신규 feature 모듈을 요구하면, 그 스캐폴딩을 **`testcases.md`의 첫 케이스
`TC-00`(유형 `setup`)으로 명시**하고 `tdd-implementer`가 일반 케이스와 동일한 루프로
처리합니다. 코디네이터가 직접 파일을 만들지 않습니다 — 코디네이터는 프로젝트 컨벤션 문서를
읽지 않고, 그 산출물이 코드리뷰 게이트를 건너뛰기 때문입니다.

`TC-00` 범위:

- `settings.gradle.kts`에 모듈 `include` 추가
- 모듈 `build.gradle.kts`(`blebridge.feature` 등 컨벤션 플러그인 적용)
- 최소 패키지 골격과 `MainDispatcherExtension` 복사본(`testcase-author`가 seed하지 않은 경우)
- 검증: `./gradlew :feature:<x>:compileDebugKotlin`

`TC-00`은 테스트가 없으므로 Red→Green 대신 **컴파일 성공**을 게이트로 삼고, 커밋 타입은
`chore(<feature>): TC-00 모듈 스캐폴딩`을 씁니다. 이때만 dev의 "요청 범위를 넘는 의존성 추가
금지" 경계가 `analysis.md`에 명세된 범위 안에서 완화됩니다.

## 컴포넌트 트랙 규약 (core:designsystem / core:ui)

feature 트랙과 **달라지는 부분만** 정의합니다. 나머지(코디네이터 게이팅, 리뷰-커밋 순서,
재시도 제한, worker_done, 작업 브랜치 precondition)는 동일합니다. `<component>`는 컴포넌트
이름(예: `action-button`, `device-list-item`)입니다.

| 항목 | feature 트랙 | 컴포넌트 트랙 |
|---|---|---|
| 대상 모듈 | `feature:<x>` | `core:designsystem`(`common/*`), `core:ui`(`ui/*`) |
| 산출 위치 | `.orca/plan/<feature>/` | `.orca/plan/<component>/` |
| analysis | MVI 4계약·Route→Screen·Navigation | **없음.** 컴포넌트 프롬프트를 소스로 공개 API·Variant/State·사용 토큰·Defaults·선행 의존(로드맵)·완료 조건 요약 + 목표디자인/현재구현 차이 |
| 케이스 유형 | `unit`(ViewModel), `ui`(Screen) | `component`(Compose UI). ViewModel이 없어 `unit` 케이스 없음(순수 로직 헬퍼가 있을 때만 예외) |
| 테스트 위치 | `src/test` + `src/androidTest` | `core/<mod>/src/androidTest/.../<Component>Test.kt` (JUnit4, `@Ignore` 스텁) |
| lint 경계 | feature에서 raw `Color`/`.dp`/`.sp`·토큰 **금지** | **반전**: `AppTheme` Semantic/Contextual 토큰 조합과 `Defaults`의 `.dp` 정의가 정상·필수. (그 lint는 feature에만 배선돼 core엔 적용 안 됨.) 단 primitive는 `internal`, public API에 raw `Color`/`TextStyle`/magic dp 불필요 노출 금지 |
| 자동 게이트 | `:feature:<x>:testDebugUnitTest`(+UI 컴파일) | `:core:<mod>:compileDebugKotlin`, `:lintDebug`, `:assembleDebugAndroidTest`(**컴파일까지**). `connectedDebugAndroidTest`는 기기 필요 → 수동 |
| Red 관측 | 유닛 테스트 실패 관측 | 계측 테스트는 기기 없이 실행 불가 → Red는 컴파일+수동. 자동 게이트는 컴파일까지 |
| 커밋 | `test(...)`+`feat(...)` 2커밋 | `feat(designsystem\|ui): <component> <요약>` (컴포넌트/프롬프트 단위 작은 커밋). 계측 Red를 자동 관측할 수 없어 테스트 선커밋은 강제하지 않음 |
| 브랜치 | `feature/<feature>` | 프롬프트 지정 `feature/designsystem\|ui/<x>` |

리뷰(`codex review`) 추가 관점 — 컴포넌트 트랙:

- **core 모듈에서 `AppTheme` 토큰 사용은 정상**입니다. feature 경계 규칙을 그대로 들이대
  오탐하지 않습니다.
- public API에 raw `Color`/`TextStyle`/magic dp가 불필요하게 노출되지 않았는지.
- Preview가 Light/Dark × 주요 Variant/State를 포함하는지.
- state hoisting(컴포넌트가 자체 비즈니스 상태를 소유하지 않음), 최소 48dp 터치 영역과
  content description.

## 도메인/데이터 트랙 규약 (domain / data)

UI·MVI 없이 `domain`·`data`의 순수 로직만 개발할 때, feature 트랙과 **달라지는 부분만**
정의합니다. 나머지(코디네이터 게이팅, 리뷰-커밋 순서, 재시도 제한, worker_done, 작업 브랜치
precondition, 케이스당 2커밋)는 동일합니다. `<mod>`는 `domain` 또는 `data`입니다.

| 항목 | feature 트랙 | 도메인/데이터 트랙 |
|---|---|---|
| 대상 모듈 | `feature:<x>` | `domain`, `data`(또는 분리 모듈) |
| 산출 위치 | `.orca/plan/<feature>/` | `.orca/plan/<타깃-slug>/` (예: `device-repository`) |
| analysis | MVI 4계약·Route→Screen·Navigation | **MVI/Route/Navigation 없음.** repository interface(`domain`)·구현(`data`)·유스케이스·모델 계약, 의존 방향(`data`→`domain`), 목표디자인/현재구현 차이만 |
| 케이스 유형 | `unit`(ViewModel), `ui`(Screen) | `unit` **만**(순수 로직). UI/`androidTest` 케이스 없음 |
| 테스트 위치 | `src/test` + `src/androidTest` | `<mod>/src/test`(JUnit5, `@Disabled` 스텁)만 |
| 자동 게이트 | `:feature:<x>:testDebugUnitTest`(+UI 컴파일) | **모듈 타입에 따라 다름**(아래 주의). `:data`는 Android → `:data:testDebugUnitTest`, `:domain`은 순수 JVM → `:domain:test`. `androidTest`·`assembleDebugAndroidTest` 없음 |
| Red 관측 | 유닛/계측 | 유닛 테스트 **실패 관측 가능**(기기 불필요) |
| 배선 마무리 | app NavHost + Hilt | **NavHost 없음.** DI 바인딩(`data` 구현 → `domain` interface)만 확인/추가 |
| 커밋 | `test(...)`+`feat(...)` 2커밋 | 동일. `test(<mod>): TC-xx …` / `feat(<mod>): TC-xx …` |
| 브랜치 | `feature/<feature>` | 작업 브랜치(예: `feature/<name>`) |

> ⚠️ **gradle 태스크는 모듈 타입에 따라 이름이 다릅니다.** `build.gradle.kts`의 `plugins`가
> `blebridge.kotlin.jvm`이면 **순수 JVM 모듈이라 AGP 태스크가 존재하지 않습니다** —
> `testDebugUnitTest`·`compileDebugKotlin`·`compileDebugUnitTestKotlin`을 쓰면 `task not found`로
> 실패합니다. 대응 태스크는 `test`·`compileKotlin`·`compileTestKotlin`입니다.
> 현재 JVM 모듈: `:domain`, `:core:common`, `:core:network`, `:lint:designsystem`.
> 나머지(`:data`, `:core:mvi`, `:core:designsystem`, `:core:ui`, `:feature:*`, `:app`)는 Android입니다.
> 게이트 명령을 쓰기 전에 대상 모듈의 `plugins` 블록을 확인하세요.

리뷰(`codex review`) 관점 — 도메인/데이터 트랙: MVI 경계·`Route→Screen`·디자인시스템 lint는
**해당 없음**입니다. 대신 **의존 방향(`data`→`domain`, `domain`은 안드로이드/프레임워크 비의존)**,
계약 안정성(공개 interface·모델 변경 시 README 동시 갱신), 코루틴/스레딩 경계, 에러 매핑을 봅니다.

## worker_done 페이로드 규약

각 역할은 자기 터미널에서 완료 시 1회 `worker_done`을 보고합니다(실패해도 보고).

```bash
orca orchestration send --to <coordinator_handle> --type worker_done \
  --subject "<짧은 상태>" --body "<한 일 / 발견 / 남은 것>" \
  --payload '{"taskId":"<task>","dispatchId":"<dispatch>","filesModified":["path"],"reportPath":".orca/plan/<feature>/..."}' --json
```

- `feature-analyst` / `testcase-author`: `reportPath`에 산출 문서 경로.
- `tdd-implementer`: `filesModified` + 갱신된 `testcases.md` 상태 + 검증 로그 요약.
  UI 케이스는 컴파일까지만 검증했음을 명시(`connectedDebugAndroidTest` 미실행 사유 포함).
- `code-reviewer`: `reportPath`에 리뷰 문서, pass/이슈 요약. **review-only 완료는 코디네이터의
  코드 편집 권한을 부여하지 않습니다** — 코드 수정은 dev 재디스패치로 처리합니다. 단
  `testcases.md`의 `[x]` 마커 갱신은 위 "상태 마커 소유권"대로 코디네이터가 직접 수행합니다.

## 커밋 규약 (git 사용 시)

> 전제: 현재 작업 디렉터리가 git 저장소일 때만 적용. 미초기화면
> [코드리뷰 대안](#git-미초기화-시-대안) 참조.

### 리뷰-커밋 순서 (반드시 이 순서)

`codex review --uncommitted`는 **staged·unstaged·untracked 변경만** 리뷰합니다. dev가 green
직후 커밋해 버리면 리뷰어가 볼 diff가 비어 리뷰가 무의미해집니다. 따라서:

```
dev 구현 → green(커밋 안 함) → code-reviewer 리뷰 → pass → dev 커밋 디스패치 → 코디네이터가 [x]
```

- **dev는 자기 판단으로 커밋하지 않습니다.** 커밋은 코디네이터의 별도 디스패치로만 수행합니다.
- 리뷰 이슈가 나오면 커밋 없이 수정 → 재리뷰합니다. 미완성 상태를 커밋으로 굳히지 않습니다.

### 케이스당 2커밋 분리

**최대한 작은 범위(케이스 단위) 커밋.** 테스트와 구현을 분리합니다.

- 테스트: `test(<feature>): <TC-id> <요약>`
- 구현: `feat(<feature>): <TC-id> <요약>`
- 스캐폴딩: `chore(<feature>): TC-00 <요약>`

분리는 **`commit-message` 스킬이 담당합니다.** 스킬은 `--auto`에서도 워킹트리 전체를 목적별
그룹으로 나누고, 테스트(`src/test`)와 구현(`src/main`)을 항상 다른 커밋으로 분리합니다.
따라서 워커는 스테이징 없이 한 번만 호출하면 됩니다.

```bash
<commit-message 스킬 --auto>   # test(...) → feat(...) 순서로 자동 분할 커밋
```

- 스킬은 **커밋을 시작하기 전에 gradle 검증을 1회** 돌립니다(변경된 모듈만). Red 상태에서는
  테스트 실행 대신 컴파일 태스크를 씁니다 — 실패가 정상이라 테스트를 돌리면 막힙니다.
- 각 커밋은 `git stash push --keep-index -u`로 파일 단위 격리해 만듭니다. 다른 그룹의 파일이
  섞이지 않게 하는 장치이며, **한 파일 안의 주제를 나누지는 못합니다.**
- 스킬의 인자는 `--auto` 하나뿐입니다(그리고 영어 출력용 `ENG`). 워커는 항상 `--auto`를 씁니다.
- 검증 실패·커밋 훅 실패·stash 충돌은 스킬이 재시도 없이 중단하고 롤백 명령
  (`git reset --soft <BASE_SHA>`)과 함께 보고합니다. 이 경우 케이스를 완료로 표시하지 말고
  `worker_done`에 실패 내용을 그대로 담습니다.

### 스킬 호출과 폴백

- 커밋은 `commit-message` 스킬로 수행합니다(`.claude/skills/commit-message/SKILL.md`).
  Sonnet 워커 세션은 기본적으로 **Skill 도구**를 보유하므로 그대로 호출합니다. 역할 문서는
  로드형 서브에이전트가 아니라 frontmatter `tools`가 강제되지 않으니, 워커의 실제 도구 세트를
  따릅니다(순수 `codex` 네이티브 워커는 Skill 미보유 → 폴백).
- **오케스트레이션 워커는 항상 `--auto --no-push`를 붙입니다.** `--auto`는 대화형 확인을 못 하는
  워커용, `--no-push`는 케이스마다 푸시하지 않고 **커밋만 로컬로 쌓기** 위함입니다(푸시는 파이프라인
  끝에서 코디네이터가 1회). 사용자가 직접 실행할 때는 인자 없이 호출해 분할 계획을 확인·푸시합니다.
- **폴백**: 스킬이 노출되지 않는 실행 환경(예: Codex 네이티브 워커, Skill 도구 미보유 워커)
  에서는 raw `git commit`으로 위 형식을 그대로 지킵니다. 이때는 자동 분할·격리 검증이 없으므로
  **스테이징으로 test/구현을 직접 나눠** 2커밋을 만듭니다. 폴백을 썼다는 사실은 `worker_done`
  보고에 남깁니다. 단, **Sonnet wrapper가
  구현만 `codex` CLI에 위임한 경우는 폴백 대상이 아닙니다** — 커밋이 바깥 Sonnet 런타임에서
  일어나 스킬을 그대로 씁니다(세 모드 구분은 아래 "Codex GPT-5.5 워커 지침").
- **푸시**: 파이프라인에서는 케이스별 커밋에 `--no-push`를 줘 **푸시를 생략**하고, 코디네이터가
  **최종 리뷰 pass 후 작업 브랜치를 1회 푸시**합니다("케이스 루프 게이팅" 6단계). `--force` 계열은
  쓰지 않으며, 거부되면 재시도·`pull --rebase` 우회 없이 보고합니다. (`--no-push` 없이 사용자가
  직접 스킬을 부르면 스킬이 그 호출 끝에 한 번 푸시합니다.) **폴백(raw `git commit`) 경로도
  푸시하지 않습니다** — 최종 푸시는 코디네이터가 담당합니다.

## 오케스트레이션 멀티 에이전트 구성 구동 방법

`orca` CLI로 이 파이프라인을 실제로 띄우는 순서와, 코디네이터에게 주는 **구동 프롬프트**를
정리합니다. 세부 규약은 위 절들을 따르며 여기서 재서술하지 않습니다.

### 전제 확인

- git 저장소 + (권장) `origin` 리모트, `.gitignore`에 `/.orca/`.
- `orca`, `codex` CLI 사용 가능. 워커는 모두 Claude 세션(analyst=Opus, 나머지=Sonnet).

### 1) 워커 터미널 기동 (모두 Claude 세션)

```bash
# active worktree(또는 신규) 위에서 역할별 터미널 4개
orca terminal create --worktree active --title feature-analyst --command 'claude --model opus'   --json
orca terminal create --worktree active --title testcase-author --command 'claude --model sonnet' --json
orca terminal create --worktree active --title tdd-implementer --command 'claude --model sonnet' --json
orca terminal create --worktree active --title code-reviewer   --command 'claude --model sonnet' --json

# 각 터미널이 tui-idle 되면 역할 계약을 주입해 대기시킴 (<role>·<h>를 역할별로 대입)
orca terminal wait --terminal <h> --for tui-idle --timeout-ms 60000 --json
orca terminal send --terminal <h> \
  --text 'docs/orchestration/<role>.md의 본문을 네 역할 계약으로 삼아라. 디스패치가 오면 그 케이스만 수행하고 worker_done을 보고한 뒤 대기하라.' --enter --json
```

- dev·reviewer는 무거운 구현·리뷰를 `codex` / `codex review`에 위임(완료 대기 후 이어서 처리).
  케이스 커밋은 Sonnet 런타임에서 `commit-message --auto --no-push`(커밋만)로 수행하고, 푸시는
  코디네이터가 최종 1회만 합니다.

### 2) 코디네이터 구동 프롬프트

아래 프롬프트 하나를 **코디네이터**(이 파이프라인을 모는 Claude 세션)에게 줍니다. `<개발 요청>`만
채우면 나머지는 이 계약 문서의 [기본값](#호출-방식과-기본값-별도-입력-없이-동작)으로 자동
진행됩니다.

```text
너는 이 저장소의 Orca 오케스트레이션 코디네이터다.
먼저 docs/orchestration/orchestration-tdd.md 를 읽고 그 규약을 그대로 따른다.

[개발 요청]
<여기에 만들 것을 서술. 예:
 - "feature:chat 채팅 화면을 MVI로 신규 개발"
 - "docs/design/common/01-action-button.md 컴포넌트 구현"
 - "domain에 DeviceRepository 계약과 data 구현 추가">

[운용 규칙]
- 별도 입력을 나에게 되묻지 말고 계약 문서의 "호출 방식과 기본값"에 따라
  트랙·작업 브랜치(origin/develop에서 딴 feature/<slug>)·리뷰 base(분기 부모 =
  feature면 origin/develop)·타임아웃을 자동으로 정한다. 정말 모호할 때만 한 번 확인한다.
- 워커는 모두 Claude 세션으로 띄운다: analyst=Opus, 나머지=Sonnet.
  각 터미널에 역할 계약 docs/orchestration/<role>.md 경로를 주입한다.
- dev·reviewer의 무거운 작업은 Sonnet 워커가 codex / codex review 에 위임하고
  완료를 기다렸다가 이어서 처리한다. 케이스 커밋은 Sonnet에서 commit-message --auto --no-push 로
  한다(커밋만, 푸시는 안 함).
- 착수 전 작업 브랜치를 준비한다(보호 브랜치 main/master/develop 직접 커밋 금지).

[진행]
계약 문서의 "케이스 루프 게이팅"대로:
feature-analyst → testcase-author →
(tdd-implementer 구현 → code-reviewer --uncommitted 리뷰 → pass 시 dev 커밋 →
 코디네이터가 testcases.md 를 [x]로 갱신) 케이스 루프 →
모든 케이스 완료 후 code-reviewer 최종 전체 리뷰 1회 →
최종 리뷰 pass면 코디네이터가 작업 브랜치를 git push 1회(첫 푸시 -u origin <브랜치>).
산출물은 .orca/plan/<타깃-slug>/ 에 남긴다. 케이스 커밋은 dev가 commit-message --auto --no-push로
커밋만 하고(푸시 안 함), 푸시는 위 최종 1회뿐이다.
```

### 3) 루프 구동

코디네이터는 ["케이스 루프 게이팅"](#케이스-루프-게이팅-orca-코디네이터-책임)의 `dispatch --inject`
→ `check --wait` 순서로 케이스 루프를 돌립니다. 타임아웃·재시도·에스컬레이션은 해당 절의
기본값을 따릅니다.

## Codex GPT-5.5 워커 지침

각 역할은 세 형태로 기동할 수 있습니다.

- **(a) Claude wrapper** — Claude 세션(`claude`/Orca 터미널)을 기동하고 `docs/orchestration/<role>.md`
  의 본문을 역할 계약으로 주입합니다. 기본값. Sonnet(또는 Opus) 런타임이 본문 절차를 직접
  수행합니다. (이 문서들은 `.claude/agents/`의 로드형 서브에이전트가 아니므로 `claude --agent`로
  자동 로드되지 않습니다 — 기동 프롬프트에 경로를 명시합니다.)
- **(a′) Codex 위임 wrapper(하이브리드)** — (a)와 같은 Sonnet Claude 에이전트로 기동하되,
  무거운 구현·리뷰만 Bash로 `codex` CLI에 위임합니다. 런타임은 여전히 Sonnet Claude Code라
  `codex` 서브프로세스가 반환된 뒤 커밋 등 나머지를 **바깥 Sonnet에서** 이어서 처리합니다.
- **(b) Codex 네이티브 워커** — 해당 역할을 Codex GPT-5.5 터미널 워커로 기동. Claude 런타임이
  없습니다.

스킬 사용 가부는 **"누가 커밋을 실행하는 런타임인가"**로 갈립니다((a)·(a′)=Sonnet에서 커밋 →
스킬 사용, (b)=폴백). 상세는 위 ["스킬 호출과 폴백"](#스킬-호출과-폴백).

**역할 계약은 도구 중립입니다.** `docs/orchestration/<role>.md`의 **본문**(진입 문서·절차·경계·보고
규약)은 Claude 전용이 아니라 역할 계약 그 자체입니다. Claude 워커든 Codex 워커든 frontmatter
(`tools`, `model`)는 권장값일 뿐이고 본문을 그대로 따릅니다. 따라서 어느 역할을 Codex로 바꿔도 규칙이
갈라지지 않습니다.

**프로젝트 운용 기본 = 모든 역할을 Claude(Sonnet) 세션으로 띄웁니다**(analyst만 Opus). codex가
필요한 역할은 그 Sonnet 워커가 `codex` CLI를 위임 호출((a′))합니다. 네이티브 codex 워커(b)는
스킬을 못 써 기본이 아니며 아래 표의 "가능" 열은 대안 선택지입니다.

| 역할 | 프로젝트 기본 | 대안 | 비고 |
|---|---|---|---|
| `feature-analyst` | Claude(Opus) wrapper | Codex 네이티브 가능 | 문서 산출만 — 스킬 불필요 |
| `testcase-author` | Sonnet wrapper | Codex 위임/네이티브 | 산출물이 문서+테스트 스텁 |
| `tdd-implementer` | Sonnet wrapper (+`codex` 위임) | Codex 네이티브(스킬 불가) | 커밋을 Sonnet에서 `commit-message`로 수행 |
| `code-reviewer` | Sonnet wrapper (+`codex review` 위임) | Codex 네이티브 | 리뷰 실행은 항상 `codex review` |

유의점:

- **리뷰 독립성**: dev와 reviewer가 모두 `codex`(GPT-5.5)에 위임하면 실제 검토 모델이 생성 모델과
  같아 caveat이 남습니다. 이슈 검출률이 중요하면 reviewer의 위임을 다른 effort로 두거나 한쪽을
  순수 Sonnet(위임 없이)으로 돌려 계열을 분리하는 것을 고려합니다.
- 워커 기동 프롬프트에 **역할 계약 파일 경로를 명시**해야 합니다(아래 기동 예 참조).

### Codex 네이티브 워커 기동

```bash
orca worktree create --name <feature>-<role> --no-parent --json   # 또는 active worktree 재사용
orca terminal create --worktree id:<wt> \
  --command 'codex --model gpt-5.5 -c model_reasoning_effort="high"' --json
orca terminal wait --terminal <h> --for tui-idle --timeout-ms 60000 --json

# 리뷰 역할
orca terminal send --terminal <h> \
  --text 'codex review --uncommitted "<프로젝트 리뷰 지침>"' --enter --json

# 그 외 역할(분석·테스트케이스·구현)
orca terminal send --terminal <h> \
  --text 'docs/orchestration/<role>.md의 본문을 역할 계약으로 삼아 <TC-id>를 수행하라. frontmatter는 무시한다.' --enter --json
```

`--model gpt-5.5`는 codex-cli 0.145.0에서 동작을 확인했습니다. 로컬 기본 모델
(`~/.codex/config.toml`)이 다르면 이 플래그가 우선합니다.

Codex 워커는 루트 [`AGENTS.md`](../../AGENTS.md) → [`docs/agent/README.md`](../agent/README.md) →
이 문서 → 역할 계약(`docs/orchestration/<role>.md`) 순으로 동일 규칙에 진입합니다.

### 리뷰 관점(증분·최종 공통)

- MVI 경계: 상태 변경은 Intent→Mutation, 일회성은 SideEffect. 4역할을 한 sealed로 병합 금지.
- UI 경계: `Route → Screen → 선택적 core:ui Content`. Screen은 ViewModel/NavController를 받지 않음.
- 디자인시스템 lint 경계: feature에서 raw `Color`/`.dp`/`.sp`·foundation token·정책 Material3 금지.
- 공개 API/패키지/의존성 변경 시 관련 README 동시 갱신 여부.
- lint baseline 재생성·suppression으로 위반 은폐 금지.

### 리뷰 실행

- 증분(케이스별): `codex review --uncommitted "<지침>"`.
- 최종(전체 1회): `codex review --base <기준브랜치> "<지침>"`.

### git 미초기화 시 대안

`codex review --uncommitted`/`--base`는 git 저장소를 전제로 합니다. 미초기화면 리뷰 전
`git init` 후 진행하거나, 리뷰 대상 파일 경로·diff를 명시 프롬프트로 전달하는 방식으로
대체하고, 그 사실을 리뷰 문서에 기록합니다.

## 오케스트레이션 종료 후 연속성

- 산출물은 `.orca/plan/<feature>/`에 남아 세션·오케스트레이션 종료 후에도 유지됩니다.
- git 커밋이 있으면 같은 워크트리에서 일반 `claude`/`codex` 세션에 역할 계약
  (`docs/orchestration/<role>.md`)을 주입해 이어서 개발할 수 있습니다. 오케스트레이션 "종료"는
  메시징·상태 레이어의 종료일 뿐 워크트리·산출물을 삭제하지 않습니다.
