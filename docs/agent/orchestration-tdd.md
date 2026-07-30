# TDD 오케스트레이션 파이프라인 계약

Orca `orchestration` 스킬로 멀티 에이전트 환경을 구성해 **TDD 기반 피처 개발**을 진행할 때
4개 역할 에이전트가 동일한 규약으로 맞물리게 하는 계약 문서입니다.

이 문서는 프로젝트 규칙을 복제하지 않습니다. 아키텍처·MVI·테스트·디자인시스템 규칙은
[`docs/agent/README.md`](README.md)의 시작 절차와 문서 라우팅 표를 그대로 따르고, 이
문서는 **역할 분담·산출물·핸드오프·루프 게이팅**만 정의합니다.

## 역할 에이전트

정의는 커밋되는 `.claude/agents/*.md`에 있고, 작업 산출물은 비커밋 폴더에 둡니다.

| 역할 | 에이전트 | 모델(권장) | 주 산출물 |
|---|---|---|---|
| 기획/분석 | `feature-analyst` | Opus | `.orca/plan/<feature>/analysis.md` |
| 테스트케이스 | `testcase-author` | Sonnet | `.orca/plan/<feature>/mvp.md`, `testcases.md` + 테스트 스텁 |
| 개발/구현 | `tdd-implementer` | Sonnet | 프로덕션·테스트 코드, `testcases.md` 상태 갱신 |
| 코드리뷰 | `code-reviewer` (또는 Codex GPT-5.5 워커) | Codex GPT-5.5 / Sonnet wrapper | `.orca/plan/<feature>/review/*.md` |

모델은 **권장값**입니다. 에이전트 정의(`.claude/agents/*.md`)의 frontmatter로는 `model`만
지정할 수 있고 reasoning effort는 지정할 수 없습니다. 높은 effort가 필요하면 워커 기동
명령에서 설정합니다(예: Codex `-c model_reasoning_effort="high"`).

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
   - **pass** → 코디네이터가 dev에 **커밋 디스패치**(케이스 N, 테스트/구현 2커밋) →
     완료 보고를 받으면 코디네이터가 `testcases.md`의 케이스 N을 `[x]`로 갱신 → 케이스 N+1
     디스패치.
   - **이슈** → dev에 수정 재디스패치. 수정 후 3번으로 돌아갑니다.
5. 모든 케이스 완료 후 코디네이터가 `code-reviewer`에 **최종 전체 리뷰 1회** 디스패치.

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

분리는 **스테이징 단계에서** 보장합니다. `commit-message` 스킬의 `--auto`는 되묻지 않고 staged
범위를 1커밋으로 만들기 때문에, 테스트와 구현을 함께 stage하면 분리 규약이 그대로 깨집니다.

```bash
git add <테스트 파일들> && <commit-message 스킬 --auto>   # 1) test(...) 커밋
git add <구현 파일들>   && <commit-message 스킬 --auto>   # 2) feat(...) 커밋
```

두 번의 호출 사이에 다른 파일이 섞이지 않도록 `git status --short`로 확인합니다.

### 스킬 호출과 폴백

- 커밋은 `commit-message` 스킬로 수행합니다(`.claude/skills/commit-message/SKILL.md`).
  Claude 에이전트는 **Skill 도구**로 호출하며, 호출하는 에이전트의 frontmatter `tools`에
  `Skill`이 포함돼 있어야 합니다(현재 `tdd-implementer`만 포함).
- **에이전트는 항상 `--auto` 인자를 붙입니다.** 서브에이전트는 `AskUserQuestion`을 쓸 수 없어
  대화형 확인이 불가능합니다. 사용자가 직접 실행할 때는 인자 없이 호출해 후보 중에서 고릅니다.
- **폴백**: 스킬이 노출되지 않는 실행 환경(예: Codex 네이티브 워커, Skill 도구 미보유 워커)
  에서는 raw `git commit`으로 위 형식을 그대로 지킵니다. 형식이 결정적이므로 결과물은
  동일합니다. 폴백을 썼다는 사실은 `worker_done` 보고에 남깁니다.
- 스킬도 폴백도 **푸시하지 않습니다.** 푸시는 사용자가 별도로 지시할 때만 수행합니다.

## Codex GPT-5.5 워커 지침

각 역할은 두 형태로 기동할 수 있습니다.

- **(a) Claude wrapper** — `.claude/agents/<role>.md`를 Claude 서브에이전트로 기동. 기본값.
- **(b) Codex 네이티브 워커** — 해당 역할을 Codex GPT-5.5 터미널 워커로 기동.

**역할 계약은 도구 중립입니다.** `.claude/agents/*.md`의 **본문**(진입 문서·절차·경계·보고
규약)은 Claude 전용이 아니라 역할 계약 그 자체입니다. Codex 워커는 frontmatter(`tools`,
`model`)만 무시하고 본문을 그대로 따릅니다. 따라서 어느 역할을 Codex로 바꿔도 규칙이
갈라지지 않습니다.

| 역할 | Claude wrapper | Codex 네이티브 | 비고 |
|---|---|---|---|
| `feature-analyst` | 권장 | 가능 | 문서 산출만 하므로 전환 부담 적음 |
| `testcase-author` | 가능 | 가능 | 산출물이 문서+테스트 스텁이라 전환 부담 적음 |
| `tdd-implementer` | 권장 | 가능 | 커밋 시 `commit-message` 스킬 대신 raw git 폴백 필요 |
| `code-reviewer` | 가능 | 권장 | `codex review` 네이티브 실행 |

Codex 네이티브로 전환할 때 유의할 점:

- **`commit-message` 스킬을 쓸 수 없습니다.** Claude Code 스킬이라 Codex 세션에 노출되지
  않습니다. 위 "스킬 호출과 폴백"의 raw git 폴백을 씁니다.
- **dev와 reviewer를 둘 다 Codex로 두면 리뷰 독립성이 떨어집니다.** 같은 모델이 자기 산출물을
  검토하게 되므로, 최소한 한쪽은 다른 모델 계열로 두는 편이 이슈 검출률에 유리합니다.
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
  --text '.claude/agents/<role>.md의 본문을 역할 계약으로 삼아 <TC-id>를 수행하라. frontmatter는 무시한다.' --enter --json
```

`--model gpt-5.5`는 codex-cli 0.145.0에서 동작을 확인했습니다. 로컬 기본 모델
(`~/.codex/config.toml`)이 다르면 이 플래그가 우선합니다.

Codex 워커는 루트 [`AGENTS.md`](../../AGENTS.md) → [`docs/agent/README.md`](README.md) →
이 문서 → 역할 계약(`.claude/agents/<role>.md`) 순으로 동일 규칙에 진입합니다.

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
- git 커밋이 있으면 같은 워크트리에서 `claude --agent <name>` 재기동으로 이어서 개발할 수
  있습니다. 오케스트레이션 "종료"는 메시징·상태 레이어의 종료일 뿐 워크트리·산출물을
  삭제하지 않습니다.
