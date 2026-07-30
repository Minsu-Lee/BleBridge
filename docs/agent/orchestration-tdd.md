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
| 기획/분석 | `feature-analyst` | Opus, effort high | `.orca/plan/<feature>/analysis.md` |
| 테스트케이스 | `testcase-author` | Sonnet | `.orca/plan/<feature>/mvp.md`, `testcases.md` + 테스트 주석 스텁 |
| 개발/구현 | `tdd-implementer` | Sonnet | 프로덕션·테스트 코드, `testcases.md` 상태 갱신 |
| 코드리뷰 | `code-reviewer` (또는 Codex GPT-5.5 워커) | Codex GPT-5.5 / Sonnet wrapper | `.orca/plan/<feature>/review/*.md` |

## 파이프라인 개요

```
feature-analyst  →  testcase-author  →  ┌─ tdd-implementer(케이스 N) ─┐
                                        │                             │  케이스 루프
                                        └─ code-reviewer(케이스 N) ───┘
                                              ↓ 모든 케이스 완료
                                        code-reviewer(최종 전체 리뷰 1회)
```

## 산출물 규약 (비커밋: `/.orca/plan/<feature>/`)

`.orca/`는 `.gitignore` 대상입니다. `<feature>`는 대상 feature 모듈명(예: `main`, `chat`).

- `analysis.md` — 분석·설계(기획 산출). 아래 "analysis.md 필수 항목" 참조.
- `mvp.md` — MVP 기능 목록(우선순위·범위·비범위).
- `testcases.md` — **순서 있는 테스트케이스 체크리스트**. 각 항목:
  - `id`(예: `TC-01`), 유형(`unit`/`ui`), 대상 파일(예: `.../XxxViewModelTest.kt`),
    한 줄 설명, 상태 마커 `[ ]`(대기) / `[dev]`(구현 중) / `[review]`(리뷰 중) /
    `[x]`(완료).
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

1. 코디네이터가 `tdd-implementer`에 케이스 N 디스패치(`dispatch --inject`).
2. dev가 케이스 N 기능 + 테스트 구현 → 범위 gradle 테스트 green → `worker_done`.
3. 코디네이터가 `code-reviewer`에 케이스 N 리뷰 디스패치.
4. 리뷰 pass → 코디네이터가 dev에 케이스 N+1 디스패치. 리뷰 이슈 → dev에 수정 재디스패치.
5. 모든 케이스 완료 후 코디네이터가 `code-reviewer`에 **최종 전체 리뷰 1회** 디스패치.

수동 루프 예:

```bash
orca orchestration task-create --spec "TC-01: <설명>" --json
orca orchestration dispatch --task <dev_task> --to <dev_handle> --inject --json
orca orchestration check --wait --types worker_done,escalation,decision_gate --timeout-ms 900000 --json
orca orchestration dispatch --task <review_task> --to <review_handle> --inject --json
orca orchestration check --wait --types worker_done,escalation,decision_gate --timeout-ms 900000 --json
# 리뷰 pass면 다음 케이스로
```

## worker_done 페이로드 규약

각 역할은 자기 터미널에서 완료 시 1회 `worker_done`을 보고합니다(실패해도 보고).

```bash
orca orchestration send --to <coordinator_handle> --type worker_done \
  --subject "<짧은 상태>" --body "<한 일 / 발견 / 남은 것>" \
  --payload '{"taskId":"<task>","dispatchId":"<dispatch>","filesModified":["path"],"reportPath":".orca/plan/<feature>/..."}' --json
```

- `feature-analyst` / `testcase-author`: `reportPath`에 산출 문서 경로.
- `tdd-implementer`: `filesModified` + 갱신된 `testcases.md` 상태 + green 로그 요약.
- `code-reviewer`: `reportPath`에 리뷰 문서, pass/이슈 요약. **review-only 완료는 코디네이터의
  파일 편집 권한을 부여하지 않습니다** — 수정은 dev 재디스패치로 처리.

## 커밋 규약 (git 사용 시)

> 전제: 현재 작업 디렉터리가 git 저장소일 때만 적용. 미초기화면
> [코드리뷰 대안](#git-미초기화-시-대안) 참조.

- **최대한 작은 범위(케이스 단위) 커밋.** 테스트 추가와 구현을 케이스별로 분리:
  - 테스트: `test(<feature>): <TC-id> <요약>`
  - 구현: `feat(<feature>): <TC-id> <요약>`
- 작은 커밋은 `codex review --base <기준브랜치>` 최종 리뷰와 세션 종료 후 이어가기에 유리합니다.
- **커밋은 `commit-message` 스킬로 수행합니다**(`.claude/skills/commit-message/SKILL.md`).
  스킬이 staged diff를 분석해 `<TC-id>` 포함 후보를 제안하고, 승인된 메시지로 커밋합니다.
- **에이전트 호출 시에는 `--auto` 인자를 붙입니다.** 서브에이전트는 `AskUserQuestion`을 쓸 수
  없으므로, `--auto`가 1순위 후보로 확인 없이 커밋합니다(호출 에이전트의 `tools`에 `Skill`이
  포함돼 있어야 합니다). 사용자가 직접 실행할 때는 인자 없이 호출해 후보 중에서 고릅니다.
- raw `git commit`은 스킬을 쓸 수 없는 상황에서만 폴백으로 사용합니다.
- 스킬은 **푸시하지 않습니다.** 푸시는 사용자가 별도로 지시할 때만 수행합니다.

## Codex GPT-5.5 리뷰 워커 지침

코드리뷰는 두 형태로 준비됩니다.

- **(a) Claude wrapper** — `.claude/agents/code-reviewer.md`가 Bash로 `codex review`를
  실행·해석. 4개 워커를 모두 Claude로 기동할 때 사용.
- **(b) Codex 네이티브 워커** — 리뷰만 실제 Codex GPT-5.5로 기동할 때 사용.

### Codex 네이티브 워커 기동

```bash
orca worktree create --name <feature>-review --no-parent --json   # 또는 active worktree 재사용
orca terminal create --worktree id:<wt> \
  --command 'codex --model gpt-5.5 -c model_reasoning_effort="high"' --json
orca terminal wait --terminal <h> --for tui-idle --timeout-ms 60000 --json
orca terminal send --terminal <h> \
  --text 'codex review --uncommitted "<프로젝트 리뷰 지침>"' --enter --json
```

Codex 워커는 루트 [`AGENTS.md`](../../AGENTS.md) → [`docs/agent/README.md`](README.md) →
이 문서 순으로 동일 규칙에 진입합니다.

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
