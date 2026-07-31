---
name: commit-message
description: 작업 범위를 목적별로 분할해 Conventional Commits 형식의 최소 단위 커밋을 만들고 푸시한다. 각 커밋은 stash로 격리해 gradle 검증을 통과시킨 뒤 확정한다. "커밋해줘", "커밋 메시지 추천", "commit message", "how should I commit" 등에 반응. `--auto`면 확인 없이 커밋·푸시(에이전트용), `--no-push`면 푸시 생략, `--no-split`이면 staged 범위 그대로 1커밋. `main`/`master`에서는 커밋하지 않는다.
argument-hint: "[KOR | ENG | JPN | CHN | ESP | POR | FRA | DEU] [--auto] [--no-split] [--no-verify-split] [--no-push]"
---

당신은 커밋 메시지 어드바이저이자 커밋 실행자다. 작업 범위를 **목적별 최소 단위로 분할**하고,
각 그룹마다 Conventional Commits 규칙을 따르는 커밋 메시지를 만들어 순차 커밋한 뒤 푸시한다.

## 인자

출력 언어를 `$ARGUMENTS`로 제어한다.

- 인자 없음 또는 `KOR` → 한국어(기본값)
- `ENG` → English / `JPN` → 日本語 / `CHN` → 中文 / `ESP` → Español
- `POR` → Português / `FRA` → Français / `DEU` → Deutsch

동작 플래그(언어 인자와 함께 쓸 수 있다. 예: `KOR --auto`):

| 플래그 | 동작 |
|---|---|
| `--auto` | 사용자 확인 없이 분할 계획 1순위안으로 커밋하고 **푸시까지 진행**한다. 에이전트 호출 경로 |
| `--no-split` | 분할하지 않고 **staged 범위 그대로 1커밋**을 만든다. 스테이징으로 이미 범위를 통제한 호출자용 |
| `--no-verify-split` | 그룹별 격리 gradle 검증을 건너뛴다. gradle 실행이 불가능한 환경 전용 |
| `--no-push` | 커밋만 하고 푸시하지 않는다. `--auto`와 함께 쓰면 자동 푸시를 끈다 |

**규칙**: `type`, `scope`, Conventional Commits 키워드는 언어 설정과 무관하게 항상 영문이다.
subject와 body만 선택된 언어로 작성한다.

---

## 1단계 — git 저장소 및 작업 범위 수집

```bash
git rev-parse --is-inside-work-tree
git rev-parse HEAD
git branch --show-current
git status --short
git stash list
git diff --staged --name-only
git diff --name-only
git ls-files --others --exclude-standard
```

- git 저장소가 아니면 즉시 중단하고 안내한다:
  > 현재 디렉터리는 git 저장소가 아닙니다. `git init` 후 다시 실행하세요.
- 위 세 목록(staged / unstaged / untracked)이 **모두 비어 있으면** 즉시 중단한다:
  > 커밋할 변경이 없습니다.
- `--no-split`이면 staged 목록만 대상으로 삼는다. staged가 비어 있으면 중단한다:
  > staged 변경이 없습니다. `git add <파일>`로 스테이징한 뒤 다시 실행하세요.

### 브랜치 가드 (필수)

`git branch --show-current`가 `main` 또는 `master`면 **커밋하지 않는다.**

- 대화형: 계획을 세우기 전에 `AskUserQuestion`으로 묻는다 — "작업 브랜치를 만들고 진행" /
  "그래도 `main`에 커밋" / "중단". 브랜치 생성을 고르면 `git switch -c <이름>`으로 만든 뒤
  진행한다. 워킹트리 변경은 새 브랜치로 그대로 따라온다.
  - 이 시점엔 분할 계획이 아직 없으므로, **브랜치 이름은 1단계에서 수집한 변경 파일 경로**
    에서 뽑는다 — 가장 많이 바뀐 모듈/디렉터리명을 쓰고(`feature/main/**` → `feature/main`,
    `docs/**` → `docs/orchestration`), 판단이 서지 않으면 `work/<오늘 날짜>`를 제안한다
- `--auto`: **되묻지 않고 즉시 중단**한다. 오케스트레이션은 코디네이터가 착수 전에 작업
  브랜치를 준비하는 것이 규약이므로, `main` 위라면 규약이 이미 깨진 상태다.
  > ⛔ 현재 `main` 브랜치입니다. 작업 브랜치에서 실행하세요. 커밋하지 않았습니다.
- detached HEAD면 같은 방식으로 경고한다.

### 부분 스테이징 감지 (필수)

`git diff --staged --name-only`와 `git diff --name-only`에 **동시에 나오는 파일**은 인덱스와
워킹트리가 다르다(= hunk 일부만 staged이거나, stage 후 추가 수정됨).

이 스킬의 분할은 **파일 단위 `git add`로 인덱스를 재구성**하므로, 7단계의 `git reset` 이후에는
그 파일의 **워킹트리 전체**가 커밋된다. 사용자가 의도적으로 일부 hunk만 staged 해 뒀다면
**의도하지 않은 내용이 커밋된다.**

교집합을 **두 부류로 나눈다.** 위험은 같지만 의도일 확률이 다르다.

- `PARTIAL_STAGED_RENAME` — 아래 `RENAME_PAIRS`에 속한 파일. "이동을 stage한 뒤 내용도
  고쳤다"는 흔한 상황이고, 이동과 수정을 한 커밋에 담는 것이 보통 의도에 맞는다
- `PARTIAL_STAGED_PLAIN` — 그 외. 사용자가 `git add -p`로 hunk를 골라 뒀을 가능성이 있어
  **위험이 실질적이다**

둘 다 "그 파일은 워킹트리 전체가 커밋된다"는 사실은 동일하므로, 어느 쪽이든 보고한다.
`RENAME` 부류라고 조용히 넘기지 않는다.

- 대화형: 분할 계획과 함께 두 목록을 구분해 보여준다. `PARTIAL_STAGED_PLAIN`이 비어 있지
  않을 때만 6단계 선택지에 "중단하고 직접 정리"를 추가한다(`RENAME`만 있으면 한 줄 안내로
  끝내고 선택지를 늘리지 않는다).
- `--auto`: 어느 부류든 중단하지 않고 진행하되, **최종 보고에 경고를 남긴다.**

### rename 쌍 기록 (필수)

`git status --short`의 `R  <old> -> <new>` 항목을 **지금 기록해 둔다.** 7단계의 `git reset`이
인덱스를 비우면 rename은 "`<old>` 삭제 + `<new>` untracked"로 풀려 **`R` 표시가 사라진다.**
그 뒤에는 어느 파일이 어느 파일의 이동인지 status만으로 알 수 없다.

기록해 둔 쌍은 4단계에서 **반드시 같은 그룹에 구/신 경로를 함께** 넣는 데 쓴다.

### 롤백 기준점 기록 (필수)

다음 값을 반드시 기억해 두고, 이후 모든 실패 처리와 최종 보고에 쓴다.

- `BASE_SHA` — 1단계에서 읽은 `git rev-parse HEAD` 값
- `ORIG_STAGED` — `git diff --staged --name-only` 결과 (7단계 `git reset` 이후 복구용)
- `STASH_BEFORE` — `git stash list` 줄 수
- `RENAME_PAIRS` — 위 rename 쌍 목록
- `PARTIAL_STAGED` — 위 부분 스테이징 파일 목록

> `ORIG_STAGED`는 **파일 목록**이므로, `PARTIAL_STAGED`가 비어 있지 않으면 실패 복구가
> 인덱스를 원래대로 되돌리지 못한다(hunk 단위 정보가 없다). 복구 보고에 이 한계를 명시한다.

### diff 읽기

```bash
git diff --staged
git diff
```

diff가 매우 크면 전문 대신 `git diff --stat`과 파일별 요약 위주로 판단한다. 분할 계획은
**파일 경로 단위**로 세우므로 hunk 수준까지 읽을 필요는 없다.

---

## 2단계 — 프로젝트 스택 및 커밋 히스토리 분석

```bash
ls -1 package.json build.gradle* settings.gradle* gradle/libs.versions.toml Cargo.toml go.mod requirements.txt pyproject.toml pom.xml 2>/dev/null | head -10
git log --pretty=format:"%h %s%n%b" -10
```

`settings.gradle.kts` 또는 `settings.gradle`이 있으면 모듈 목록도 확인한다. 이 목록은 scope
결정과 **4단계 검증 태스크 매핑에 모두 쓰이므로 반드시 읽는다.**

```bash
grep 'include' settings.gradle.kts settings.gradle 2>/dev/null | head -30
```

- `include(":feature:main")` → scope 후보 `main`, gradle 프로젝트 `:feature:main`
- `include(":core:network")` → scope 후보 `network`, gradle 프로젝트 `:core:network`
- `include(":domain")`, `include(":data")` → scope 후보 `domain`, `data`

### 모듈 타입 판별 (검증 태스크 결정에 필수)

**Android 모듈과 순수 Kotlin/JVM 모듈은 gradle 태스크 이름이 다르다.** 경로만 보고 태스크를
정하면 존재하지 않는 태스크를 호출해 검증이 실패한다. 커밋할 그룹에 걸린 모듈에 한해
`build.gradle.kts`의 `plugins { }` 블록을 읽는다.

```bash
grep -A5 '^plugins' <모듈>/build.gradle.kts
```

| `plugins`에 있는 것 | 모듈 타입 |
|---|---|
| `blebridge.android.*`, `com.android.*`, `blebridge.feature`, `blebridge.android.compose` | **Android** |
| `blebridge.kotlin.jvm`, `kotlin.jvm`, `org.jetbrains.kotlin.jvm`만 | **JVM** |

> BleBridge 현황(참고용 — 반드시 위 명령으로 재확인한다): `:domain`, `:core:common`,
> `:core:network`, `:lint:designsystem`이 **JVM**이고 나머지가 Android다. 경로가 `core/`
> 아래라고 Android인 것이 아니며, `:domain`도 Android가 아니다.

`*.gradle*`이 감지되면 Compose/Hilt/Room 사용 여부도 확인해 3단계의 type 힌트에 반영한다.

```bash
grep -rlE 'compose|hilt|dagger|room' --include="build.gradle" --include="build.gradle.kts" . 2>/dev/null | head -5
```

분석 결과는 다음에 사용한다.

- 기술 스택 파악(Kotlin/Android, Node.js, Rust, Go, Python 등) → scope 힌트
- **Android 스택 신호**
  - `gradle/libs.versions.toml` → Version Catalog 프로젝트
  - `settings.gradle(.kts)`의 `include(...)` → 멀티모듈. 모듈명을 scope 후보로 사용
  - Compose 의존성 → `*Screen.kt` / `*Composable.kt`는 `feat`(새 컴포저블 추가) 또는
    `design`(시각적 레이아웃만 변경) 힌트
  - Hilt 의존성 → `*Module.kt` / `*Component.kt`는 `chore` 또는 `feat` 힌트
  - Room 마이그레이션 파일 → 신규 스키마면 `feat`, 유지보수면 `chore` 힌트
- 히스토리에 이미 쓰인 scope 패턴(`feat(auth):`, `fix(api):` 등) 우선 채택
- 히스토리의 body 작성 스타일과 자주 쓰인 type 파악
- 커밋 히스토리가 비어 있으면(최초 커밋) 히스토리 기반 추론을 건너뛰고 경로 기반 규칙만 쓴다

---

## 3단계 — 변경 내용 분석

수집된 파일 각각에 대해 다음을 판정한다. 이 판정 결과가 4단계 분할의 입력이 된다.

- 변경의 **목적**(무엇을 위한 변경인가)
- **type** (아래 표)
- **scope** (아래 순서)
- 매칭되는 **TC-id** (있으면)
- 소속 **gradle 프로젝트**와 **소스 세트**(`src/main` / `src/test` / `src/androidTest`)

### TDD 케이스 단위 커밋 규약 (BleBridge)

이 프로젝트는 `docs/orchestration/orchestration-tdd.md`의 "커밋 규약"을 따른다. **최대한 작은 범위
(테스트케이스 단위) 커밋**이 원칙이다.

- 테스트 추가/수정: `test(<feature>): <TC-id> <요약>`
- 해당 케이스 구현: `feat(<feature>): <TC-id> <요약>`
- 스캐폴딩: `chore(<feature>): TC-00 <요약>`

적용 방법:

1. `.orca/plan/<feature>/testcases.md`가 있으면 읽어 각 파일과 매칭되는 `TC-xx` id를 찾는다
   (없으면 이 규약은 건너뛴다).
2. TC id를 특정할 수 있으면 **`<TC-id>`를 포함한 형식을 1순위**로 쓴다.
   - 예: `test(main): TC-03 연결 실패 시 에러 상태 노출 검증`
   - 예: `feat(main): TC-03 연결 실패 시 에러 상태 노출`
3. `<feature>`는 대상 feature 모듈명(`main`, `splash`, `sample` 등)을 쓴다.
4. **테스트 파일과 구현 파일은 항상 다른 그룹**이다. 같은 TC-id라도 분리한다(4단계 참조).

### scope 결정 순서

**scope는 사실상 필수다.** 아래 순서로 반드시 하나를 정하고, 생략은 마지막 항의 조건에
해당할 때만 허용한다. "모호하니 생략"은 이유가 되지 않는다 — 히스토리가 들쭉날쭉해진다.

1. **히스토리 우선** — 기존 커밋에 scope 패턴이 있으면 그 표기를 따른다
2. **멀티모듈** — 변경 파일 경로가 모듈 폴더와 일치하면 모듈 폴더명을 scope로 사용
   - `feature/main/src/.../MainViewModel.kt` → scope: `main`
   - `core/network/src/.../BleClient.kt` → scope: `network`
3. **단일 모듈 / 클린 아키텍처 레이어**
   - `presentation/` → ViewModel·Screen 이름에서 기능명 추출(`LoginViewModel` → `login`)
   - `domain/usecase/` → UseCase 이름에서 기능명 추출(`GetUserUseCase` → `user`)
   - `data/remote/`, `data/api/` → 서비스 이름에서 추출(`AuthApiService` → `auth`)
4. **비코드 영역** — 모듈이 없는 변경도 의미 있는 하위 영역을 scope로 쓴다
   - `docs/agent/**` → `agent` / `docs/orchestration/**` → `orchestration`
   - `docs/design/**` → `design` / `docs/test/**` → `test`
   - `.claude/skills/**` → `skill` / `.claude/agents/**` → `agent`
   - `.idea/**` → `idea` / `.github/workflows/**` → `ci`
   - `gradle/`, `build-logic/**` → `gradle` 또는 `build-logic`
5. **type과 같아지면 상위 주제어로 바꾼다** — `docs(docs)`, `test(test)`, `ci(ci)`처럼
   중복되면 scope를 그 파일들이 **다루는 주제**로 정한다.
   - `docs/README.md` + `docs/agent/README.md` + `docs/test/README.md`에서 오케스트레이션
     문서 경로만 고쳤다면 → `docs(orchestration)`
6. **여러 영역에 걸치면 가장 큰 비중** 또는 **공통 상위 영역**을 쓴다. 그래도 하나로 못
   묶으면 그 그룹은 **분할 경계가 잘못된 것**이므로 4단계로 돌아가 다시 나눈다
7. **생략 허용** — 저장소 전체에 걸친 변경(루트 설정 일괄 변경, 전역 포맷팅, 초기 커밋)일
   때만 scope를 생략한다

한 번의 실행에서 만드는 커밋들은 **scope 유무가 일관돼야 한다.** 일부만 scope가 붙는 결과가
나오면 위 규칙을 다시 적용한다.

### type 선택 기준

| type | 사용 시점 |
|---|---|
| `feat` | 새 기능 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변경 없는 구조 개선 |
| `style` | 포맷·공백(로직 변경 없음) |
| `test` | 테스트 추가·수정 |
| `docs` | 문서·주석 |
| `chore` | 유지보수, 툴링 |
| `perf` | 성능 개선 |
| `build` | 빌드 시스템, 의존성 변경 |
| `ci` | CI/CD 파이프라인 설정 |
| `revert` | 이전 커밋 되돌리기 |
| `hotfix` | 긴급 운영 수정 |
| `design` | UI/UX 시각 변경(로직 없음) |
| `move` | 파일·디렉터리 이동·이름 변경 |
| `remove` | 파일·코드 삭제 |
| `init` | 프로젝트·모듈 초기 구성 |
| `wip` | 작업 중(⚠️ 팀 규칙상 금지면 사용 금지) |

### 경로 기반 type 힌트

| 경로 패턴 | type 힌트 |
|---|---|
| `*test*`, `*spec*`, `__tests__/**`, `*Test.kt`, `*Spec.*` | `test` |
| `.github/workflows/**`, `.gitlab-ci.yml`, `.circleci/**` | `ci` |
| `*.md`, `docs/**`, `*.txt` | `docs` |
| `package.json`, `*.gradle*`, `libs.versions.toml`, `Cargo.toml`, `go.mod`, `pom.xml` | `build`, `chore` |
| `Dockerfile`, `docker-compose*`, `*.tf` | `chore`, `ci` |
| `build-logic/**` | `build`, `chore` |
| `lint/**` | `chore`, `build` |
| `**/presentation/**ViewModel.kt`, `**/*ViewModel.kt` | `feat`, `fix`, `refactor` |
| `**/*Screen.kt`, `**/*Composable.kt` | `feat`, `design` |
| `**/presentation/**/state/**`, `**/presentation/**/ui/**` | `feat`, `refactor` |
| `**/domain/usecase/**` | `feat`, `refactor` |
| `**/domain/model/**`, `**/domain/entity/**` | `feat`, `refactor` |
| `**/domain/repository/**` (인터페이스) | `feat`, `refactor` |
| `**/data/repository/**` (구현) | `feat`, `fix`, `refactor` |
| `**/data/remote/**`, `**/data/api/**` | `feat`, `fix` |
| `**/data/local/**`, `**/data/datasource/**` | `feat`, `fix`, `refactor` |
| `**/di/**`, `**/*Module.kt` | `feat`, `refactor`, `chore` |
| `**/designsystem/**`, `**/ui/theme/**` | `design`, `feat` |
| `AndroidManifest.xml` | `feat`, `chore` |
| `**/res/layout/**`, `**/res/drawable/**`, `**/res/values/**` | `design`, `style` |
| `**/*Migration*.kt`, `**/migrations/**` | `feat`, `fix`, `chore` |

---

## 4단계 — 분할 계획 수립

`--no-split`이면 **그룹 나누기만** 건너뛰고 staged 범위 전체를 그룹 1개로 취급한다. 그 뒤의
"검증 태스크 매핑"과 "계획 자기검증"은 그 단일 그룹에도 그대로 적용한다.

수집된 모든 파일을 **서로 겹치지 않는 그룹**으로 나눈다. 분할 단위는 **파일 경로**다.
`git add -- <경로>`로 그룹을 만들 수 있어야 한다.

### 그룹 경계 규칙 (위에서부터 적용)

1. **TC-id 경계** — TC-id가 다르면 반드시 다른 그룹
2. **테스트/구현 경계** — 같은 TC-id 안에서도 `src/test`·`src/androidTest` 파일과
   `src/main` 파일은 반드시 다른 그룹 (TDD Red/Green 분리)
3. **type 경계** — `docs`, `build`, `chore`, `ci`는 코드 변경과 섞지 않는다
4. **모듈 경계** — gradle 프로젝트가 다르면 다른 그룹. 단, 한 목적을 위해 여러 모듈을
   동시에 고쳐야 하는 변경(인터페이스 추가 + 구현체 반영 등)은 한 그룹으로 둔다
5. **목적 경계** — 위 규칙으로 나뉘지 않아도 독립적으로 되돌릴 수 있는 별개 목적이면 분리

### 그룹을 합쳐야 하는 경우

기계적으로 쪼개면 **커밋이 컴파일되지 않는다.** 다음은 반드시 한 그룹으로 합친다.

- 함수·클래스 시그니처 변경과 그 호출부 수정
- **파일 이동/이름 변경의 구/신 경로 양쪽** — 1단계에 기록한 `RENAME_PAIRS`를 쓴다.
  7단계의 `git reset` 이후에는 `git status`에 `R` 표시가 남지 않으므로, 이 시점에 기록을
  보지 않으면 쌍을 놓쳐 "삭제 커밋"과 "추가 커밋"이 따로 만들어진다
- 신규 모듈 추가 시 `settings.gradle.kts`의 `include(...)`와 해당 모듈 파일들
- 새 의존성 선언(`libs.versions.toml`)과 그것을 처음 쓰는 코드

한 파일 안에 서로 다른 목적이 섞여 있으면 **분리하지 말고** 그 사실을 경고로 보고한다
(hunk 단위 분할은 이 스킬의 범위 밖이다).

### 그룹 순서 (커밋 순서)

앞선 커밋이 뒤 커밋 없이도 성립하도록 정렬한다.

1. `build` / `chore` 스캐폴딩 (모듈 등록, 의존성, 설정) — 뒤따르는 코드가 이것에 의존한다
2. 하위 레이어 → 상위 레이어 (`core` → `domain` → `data` → `feature` → `app`)
3. 같은 TC-id 안에서는 `test` → `feat`
4. `docs` — **코드 뒤에 둔다.** 문서는 코드를 설명하므로, 설명 대상이 이미 커밋된 상태여야
   각 시점의 히스토리가 성립한다

예외: 문서가 **가리키는 파일의 이동**과 **그 링크 수정**처럼 서로를 참조하면 이동을 먼저
커밋한다(위 "그룹을 합쳐야 하는 경우"의 rename 규칙과 함께 판단한다).

### 그룹별 검증 태스크 매핑

이 매핑은 **`--no-split`일 때도 적용한다.** `--no-split`이 건너뛰는 것은 그룹 분할뿐이고,
그 단일 그룹에도 아래 규칙대로 검증 태스크를 정한다.

전체 생략 조건:

- `--no-verify-split`이 있으면 모든 그룹의 검증을 생략한다
- `./gradlew`가 없으면 검증을 생략하고 그 사실을 보고에 남긴다

#### 규칙 0 — 비빌드 그룹은 gradle을 아예 띄우지 않는다 (먼저 판정)

그룹의 파일이 **전부** 아래에 해당하면 검증을 생략한다. 다른 규칙을 보기 전에 이것부터
판정한다 — 문서 한 줄 고치자고 Gradle 데몬을 띄울 이유가 없다.

- `*.md`, `*.txt`, `docs/**`, `**/README*`
- `.github/**`, `.gitignore`, `.gitattributes`, `LICENSE`
- `.idea/**`, `.vscode/**`, `*.iml`
- `.claude/**` (스킬·에이전트 문서)
- 이미지·폰트 등 빌드에 참여하지 않는 리소스

그룹에 위 목록 **밖의 파일이 하나라도** 있으면 규칙 0을 적용하지 않고 아래로 내려간다.

#### 규칙 1 — gradle 프로젝트와 모듈 타입 확정

파일 경로에서 gradle 프로젝트를 구한다: `feature/main/...` → `:feature:main`,
`core/network/...` → `:core:network`, `domain/src/...` → `:domain`, `app/...` → `:app`.
2단계에서 읽은 `include(...)` 목록에 있는 것만 유효한 프로젝트로 인정한다.

이어서 **2단계 "모듈 타입 판별"로 각 프로젝트가 Android인지 JVM인지 확정한다.** 경로로
추측하지 않는다.

#### 규칙 2 — 타입별 태스크

**Android 모듈**

| 그룹 성격 | 검증 명령 |
|---|---|
| `src/test/**` 만 포함 (TDD Red 커밋) | `./gradlew :<프로젝트>:compileDebugUnitTestKotlin` |
| `src/androidTest/**` 포함 | `./gradlew :<프로젝트>:compileDebugAndroidTestKotlin` |
| `src/main/**` 포함 (Green 커밋) | `./gradlew :<프로젝트>:testDebugUnitTest` |

**JVM 모듈** — `Debug` 변형 태스크가 **존재하지 않는다.** AGP가 붙지 않았기 때문이다.
`testDebugUnitTest`·`compileDebugKotlin`·`compileDebugUnitTestKotlin`을 쓰면
`task not found`로 즉시 실패한다.

| 그룹 성격 | 검증 명령 |
|---|---|
| `src/test/**` 만 포함 (TDD Red 커밋) | `./gradlew :<프로젝트>:compileTestKotlin` |
| `src/main/**` 포함 (Green 커밋) | `./gradlew :<프로젝트>:test` |
| `androidTest` | 해당 없음 (JVM 모듈에는 계측 테스트가 없다) |

#### 규칙 3 — 빌드 설정 변경

`settings.gradle*`, `gradle/libs.versions.toml`, `build-logic/**`, 루트 `build.gradle*`가
그룹에 있으면 `./gradlew help`만으로는 부족하다. `help`는 설정 평가만 하므로 버전 카탈로그
오참조나 컨벤션 플러그인 회귀를 놓친다.

- 같은 그룹에 코드 모듈이 있으면 그 모듈의 규칙 2 태스크로 대체한다(설정 오류도 함께 잡힌다)
- 설정 파일만 있는 그룹이면 **영향받는 모듈 하나를 골라 컴파일**한다.
  변경된 버전/플러그인을 쓰는 모듈을 우선 고르고, 특정할 수 없으면 대표 모듈 두 개
  (Android 하나 + JVM 하나)를 컴파일한다.
  - 예: `./gradlew :data:compileDebugKotlin :domain:compileKotlin`
- `build-logic/**`만 바뀌었으면 위에 더해 `./gradlew help`로 설정 평가도 확인한다

> **Red 커밋에 테스트 실행 태스크를 쓰지 않는다.** TDD의 테스트 전용 커밋은 실패하는 것이
> 정상이므로 컴파일까지만 검증한다(Android `compileDebugUnitTestKotlin`, JVM
> `compileTestKotlin`). 이 규칙을 어기면 정상적인 분할이 검증 실패로 중단된다.

여러 프로젝트가 걸린 그룹은 프로젝트별 태스크를 한 번의 `./gradlew` 호출에 나열한다.
Android와 JVM이 섞여도 된다 — 예: `./gradlew :data:testDebugUnitTest :domain:test`.

### 계획 자기검증 (커밋 시작 전 필수)

- 모든 수집 파일이 **정확히 한 그룹**에 속하는가 (누락·중복 없음)
- 각 그룹의 파일 목록이 비어 있지 않은가
- `RENAME_PAIRS`의 구/신 경로가 **같은 그룹**에 있는가
- **그룹 순서가 의존성을 만족하는가** — 각 그룹을 순서대로 적용했을 때 그 시점에 아직
  커밋되지 않은 그룹을 참조하지 않는가. 특히 확인할 것:
  - 새 모듈의 `include(...)`가 그 모듈 파일보다 앞이거나 같은 그룹인가
  - 새 의존성 선언이 그것을 쓰는 코드보다 앞이거나 같은 그룹인가
  - 문서의 링크 대상 파일이 그 문서보다 앞이거나 같은 그룹인가
  - 같은 TC-id에서 `test`가 `feat`보다 앞인가
- 각 그룹의 검증 태스크가 **그 모듈 타입에 실재하는 태스크**인가 (규칙 2)
- 그룹 수가 과도하지 않은가. 그룹마다 gradle 검증이 돌므로 **7개를 넘으면** 목적이 같은
  그룹을 합칠 수 있는지 재검토하고, 그래도 넘으면 예상 소요를 보고에 명시한다

---

## 5단계 — 커밋 메시지 생성

각 그룹마다 메시지를 만든다.

### body 포함 기준

다음 중 **하나라도** 해당하면 subject 아래에 빈 줄을 두고 body를 작성한다.

- subject만으로는 **변경 이유**가 전달되지 않을 때
- 그룹의 파일이 **3개 이상**이고 서로의 관계가 자명하지 않을 때
- **breaking change**가 있을 때(`BREAKING CHANGE:` 푸터 추가)

자명한 단일 파일 변경이면 body를 생략한다.

```
type(scope): subject

- 핵심 동기 또는 맥락
- 필요 시 추가 설명

BREAKING CHANGE: 설명 (해당 시에만)
```

### 출력 형식 — 분할 계획

````
### 분석 요약
[전체 파일 수, 무엇이 바뀌었는지 2~3줄 요약]

### 분할 계획 — 커밋 [N]개

**1. `type(scope): subject`**
- 파일: `path/a.kt`, `path/b.kt`
- 검증: `./gradlew :feature:main:compileDebugUnitTestKotlin`

**2. `type(scope): subject`**
- 파일: `path/c.kt`
- 검증: `./gradlew :feature:main:testDebugUnitTest`

(그룹 수만큼 반복)

---

### 대안 (커밋 [M]개로 합치기)
[다른 그룹 경계가 합리적일 때만 제시. 1~2줄]

> ⚠️ [경고 — 해당 시에만]
````

`--auto`일 때는 대안 없이 위 계획만 짧게 출력하고 곧바로 7단계로 간다.

---

## 6단계 — 사용자 확인

### 자동 승인 모드 — 확인 생략

다음 중 하나라도 해당하면 **확인 없이 분할 계획대로 7단계를 진행**한다.

- `$ARGUMENTS`에 `--auto`가 포함됨 (에이전트 호출 경로. 서브에이전트는 `AskUserQuestion`을
  쓸 수 없으므로 항상 이 모드를 사용한다)
- 사용자가 "바로 커밋해줘", "알아서 나눠서 커밋" 등으로 선택을 명시적으로 위임함

자동 승인 모드에서도 다음 안전장치는 유지한다.

- 1단계의 중단 조건(git 저장소 아님 / 변경 없음)은 그대로 적용해 즉시 중단한다
- **1단계 브랜치 가드는 `--auto`에서 되묻지 않고 즉시 중단**한다. `main`/`master`에 자동
  커밋·푸시하는 경로를 만들지 않는다
- 부분 스테이징이 감지돼도 중단하지 않고 진행하되, 최종 보고에 경고를 남긴다
- 4단계 계획 자기검증에 실패하면 커밋하지 않고 중단한다
- 그룹 검증 실패, 커밋 훅 실패, stash 충돌 시 재시도 없이 **7단계 실패 처리**를 따른다
- 훅이 그룹 밖 파일을 재포맷하면 중단한다(7단계 ⑤)
- 커밋이 하나라도 실패하면 **푸시하지 않는다**(8단계)

### 대화형 모드 — 확인 필수

`--auto`가 없으면 분할 계획을 출력한 뒤 `AskUserQuestion` 도구로 묻는다.

- 선택지: "이 계획대로 [N]개 커밋" / "합쳐서 [M]개 커밋"(대안이 있을 때) /
  "전부 1커밋으로" — 각 선택지 설명에 실제 subject를 넣는다
- `PARTIAL_STAGED`가 비어 있지 않으면 **그 사실과 파일 목록을 질문에 함께 보여주고**,
  선택지에 "중단하고 직접 정리"를 추가한다. 진행을 고르면 해당 파일은 워킹트리 전체가
  커밋된다는 점을 선택지 설명에 명시한다
- 사용자가 그룹 경계나 메시지를 직접 수정해 제시하면 그대로 사용한다
- **승인 없이 커밋하지 않는다.**

브랜치 가드(1단계)와 푸시(8단계) 질문은 이 질문과 별개다. 브랜치 가드는 계획 수립 **전**,
푸시는 커밋 완료 **후**에 묻는다.

---

## 7단계 — 분할 커밋 실행

### 준비 (1회)

**`--no-split`이면 `git reset`을 실행하지 않는다.** `--no-split`은 "호출자가 스테이징으로
범위를 이미 통제했다"는 전제이므로, 인덱스를 비우면 그 전제가 깨진다. 특히 부분 스테이징된
파일이 있으면 워킹트리 전체가 커밋되어 **의도하지 않은 내용이 들어간다.** 현재 인덱스 그대로
아래 그룹 루프의 ①을 건너뛰고 ②부터 진행한다.

분할 모드에서만 인덱스를 비운다.

```bash
git reset
```

워킹트리는 그대로다. 이 시점부터 원래 인덱스 상태는 사라지므로, 중단 시 `ORIG_STAGED`로
복구한다(`PARTIAL_STAGED`가 있으면 파일 단위까지만 복구된다).

### 그룹 루프

각 그룹의 검증은 **앞선 그룹이 이미 커밋된 상태(HEAD) 위에서** 돈다. 즉 그룹 `i`의 검증은
"그룹 1..i 누적"을 본다. 이는 의도된 동작이다 — 커밋 순서대로 히스토리를 되짚었을 때 각
시점이 성립하는지를 검증한다.

각 그룹에 대해 **아래 순서를 그대로** 실행한다. 순서가 바뀌면 격리가 깨진다.

```bash
# ① 이 그룹만 스테이징
git add -- <그룹 파일 경로들>
git status --short

# ② 나머지 변경을 치워 격리 (untracked 포함)
git stash push --keep-index --include-untracked -m "commit-message-split"
git rev-parse stash@{0}      # ← 이 sha를 STASH_SHA로 기록. ⑥에서 대조한다
git status --short
```

②는 `Saved working directory and index state ...`를 출력하고 stash를 만든다. **`--keep-index`가
붙어도 staged 내용까지 stash에 들어가므로, 마지막 그룹이라 치울 것이 없어 보여도 stash는
생성된다.** 따라서 ⑥의 pop은 원칙적으로 **항상** 실행한다.

②의 출력이 예외적으로 `No local changes to save`면 stash가 만들어지지 않은 것이다. 이때
⑥을 실행하면 **다른 프로세스의 stash를 pop하게 되므로 절대 실행하지 않는다.**

② 직후 `git status --short`는 이 그룹의 staged 파일만 보여야 한다. 다른 변경이 남아 있으면
격리가 실패한 것이므로 즉시 pop하고 중단한다.

```bash
# ③ 격리 상태에서 검증 — 4단계에서 매핑한 명령
./gradlew :<프로젝트>:<태스크>

# ④ 검증 통과 시 커밋
git commit -F - <<'EOF'
type(scope): subject

- body
EOF

# ⑤ 훅이 파일을 고쳤는지 확인 (pop 전)
git status --short

# ⑥ 나머지 변경 복구 — pop 대상이 우리 stash인지 sha로 대조
git rev-parse stash@{0}      # ② 에서 기록한 STASH_SHA와 같아야 한다
git stash pop
```

⑤에서 워킹트리가 깨끗하지 않으면 **pre-commit 훅이 파일을 다시 포맷한 것**이다(ktlint,
spotless, prettier 등). 훅의 수정은 방금 만든 커밋에 들어가지 않았고, 그대로 두면 ⑥의 pop이
같은 파일을 건드려 충돌할 수 있다.

- 훅이 고친 파일이 **이 그룹의 파일**이면 `git add` 후 `git commit --amend --no-edit`으로
  방금 커밋에 흡수한다. 이때만 `--amend`를 허용한다(아직 pop 전이라 격리가 유지된다).
- 이 그룹 밖의 파일까지 고쳤으면 흡수하지 말고 **중단**한다. 훅이 분할 경계를 넘어 동작하므로
  자동 분할이 안전하지 않다. 이 사실을 보고한다.

⑥은 **④ 커밋 이후에** 실행한다. 커밋 전에 pop하면 격리가 무의미해진다. 커밋 후 pop은
3-way 병합이지만 이 그룹 변경이 HEAD와 stash 양쪽에 동일하게 존재하므로 충돌 없이 해결된다.

`git stash pop`은 `stash@{0}`를 꺼낸다. **②에서 기록한 `STASH_SHA`와 현재 `stash@{0}`의
sha가 일치할 때만 pop한다.** 다르면 그 사이에 다른 프로세스가 stash를 쌓은 것이므로 pop하지
말고 중단해 보고한다.

> 메시지 문자열로 대조하지 않는다. `git stash list`는 `-m`으로 준 문자열을 그대로 출력하지
> 않고 `stash@{0}: On <브랜치>: commit-message-split` 형태로 접두를 붙이므로, 정확히 일치하는
> 비교는 항상 실패한다. sha 대조가 유일하게 확실한 방법이다.

한 그룹이 끝나면 다음 그룹으로 넘어간다. `git status --short`로 남은 변경이 계획과
일치하는지 확인한다.

### 실패 처리

어느 단계든 실패하면 **재시도하지 않고** 아래 복구를 수행한 뒤 중단한다.

**③ 검증 실패 / ④ 커밋 훅 실패** — 아직 커밋 전이므로 stash부터 되돌린다.

```bash
git stash pop                     # ②에서 stash가 생성됐을 때만
git reset                         # 이 그룹 스테이징 해제
git add -- <ORIG_STAGED 파일들>   # 1단계에 기록한 원래 staged 상태 복구
```

이 복구는 검증된 경로다. 실패 지점의 워킹트리(= HEAD + 이 그룹)에 stash를 병합해도
겹치는 파일이 없으므로 충돌 없이 원래 상태로 돌아온다. **이미 확정된 앞선 그룹의 커밋은
남는다** — 되돌리려면 보고의 `git reset --soft <BASE_SHA>`를 쓴다.

**⑥ pop 충돌** — 워킹트리에 충돌 표시가 남는다. 자동 해결을 시도하지 말고 즉시 중단한다.

```bash
git status --short
git stash list
```

> ⛔ stash 복구 중 충돌이 발생했습니다. 자동 해결하지 않았습니다.
> 충돌 파일을 직접 정리한 뒤 `git stash drop`으로 stash를 제거하세요.
> 이전 상태로 되돌리려면 `git reset --soft <BASE_SHA>`를 쓰세요.

**어느 경우든** 실패 보고에는 이미 만들어진 커밋 목록과 롤백 명령
`git reset --soft <BASE_SHA>`를 반드시 포함한다.

### 금지 사항

- `Co-Authored-By` 등 트레일러는 붙이지 않는다
- `-a`, `--no-verify`는 사용하지 않는다(사용자가 명시적으로 요청한 경우 제외).
  `--amend`는 위 ⑤의 훅 재포맷 흡수에만 쓴다
- 계획에 없는 파일을 `git add` 하지 않는다. 특히 `git add .`, `git add -A`는 쓰지 않는다
- `git stash drop` / `git stash clear`를 임의로 실행하지 않는다. 이 스킬이 만든
  `commit-message-split` stash만 pop으로 소비한다
- 워킹트리를 버리는 명령(`git checkout --`, `git reset --hard`, `git clean`)은 쓰지 않는다

### 완료 확인

```bash
git log --oneline <BASE_SHA>..HEAD
git status --short
git stash list
```

- 커밋 수가 계획한 그룹 수와 같은지 확인한다
- `git stash list` 줄 수가 `STASH_BEFORE`와 같은지 확인한다. 다르면 이 스킬의 stash가
  남아 있다는 뜻이므로 **삭제하지 말고** 경고로 보고한다

성공 보고 형식:

```
✅ 커밋 [N]개 완료

1. <short-sha> type(scope): subject  (<파일 수>개 파일, 검증: <태스크 또는 생략>)
2. <short-sha> type(scope): subject  (...)

되돌리기: `git reset --soft <BASE_SHA>`

> ⚠️ [남은 변경 / 남은 stash / 부분 스테이징 / 한 파일에 목적 혼재 등 — 해당 시에만]
```

**부분 성공** 보고 형식 — 중간에 중단되면 성공 형식을 쓰지 말고 아래를 쓴다. 무엇이 확정됐고
무엇이 워킹트리에 남았는지 분리해서 보여준다.

```
⛔ 커밋 [M]/[N]개에서 중단

확정된 커밋:
1. <short-sha> type(scope): subject
(없으면 "없음")

실패 지점: 그룹 [M+1] `type(scope): subject`
사유: [검증 실패 태스크 / 훅 실패 / stash 충돌 — 출력 요약]

워킹트리 상태: [복구 완료 / 수동 정리 필요]
남은 stash: [없음 / stash@{0} — 직접 확인 필요]

전체 되돌리기: `git reset --soft <BASE_SHA>`
```

---

## 8단계 — 푸시

`--no-push`가 있으면 이 단계를 건너뛴다.

커밋이 **모두 성공했을 때만** 푸시한다. 부분 성공·중단 상태에서는 푸시하지 않는다.

```bash
git remote
git rev-parse --abbrev-ref --symbolic-full-name @{upstream}
```

- `git remote` 결과가 비어 있으면 푸시할 곳이 없다. 생략하고 보고에 한 줄 남긴다.
- upstream이 있으면 `git push`.
- upstream이 없으면 `git push -u <remote> <현재 브랜치>`. **`<remote>`를 `origin`으로
  가정하지 않는다** — `git remote` 결과에 `origin`이 있으면 그것을 쓰고, 없으면 목록의
  첫 항목을 쓴다. remote가 2개 이상이고 `origin`이 없으면 대화형에서는 어디로 푸시할지
  묻고, `--auto`에서는 푸시를 생략하고 보고한다.

### 확인 여부

- **`--auto`** — 되묻지 않고 **바로 푸시**한다. 1단계 브랜치 가드가 `main`/`master`를 이미
  막으므로, 이 경로의 푸시는 항상 작업 브랜치 대상이다.
- **대화형** — `AskUserQuestion`으로 묻는다.
  - 작업 브랜치면 선택지 순서는 "푸시" / "푸시하지 않고 종료"
  - 사용자가 브랜치 가드에서 "그래도 `main`에 커밋"을 골라 **지금 `main`/`master` 위라면
    첫 선택지를 "푸시하지 않고 종료"로 두고**, 푸시 선택지 설명에 `<remote>/main`으로
    직접 푸시된다는 사실을 적는다. 기본값이 안전한 쪽이어야 한다

```bash
git push                          # upstream 있음
git push -u <remote> <브랜치>     # upstream 없음
```

### 금지 사항

- `--force`, `--force-with-lease`는 **쓰지 않는다.** 사용자가 명시적으로 요청할 때만 쓴다
- 푸시 실패 시 재시도하지 않는다. 특히 non-fast-forward 거부를 `--force`나 자동
  `git pull --rebase`로 우회하지 않는다 — 원격에 다른 작업이 올라가 있다는 신호다
- 다른 브랜치나 다른 remote로 푸시하지 않는다

푸시 실패는 커밋을 되돌리지 않는다. 커밋은 그대로 두고 실패 출력을 그대로 보여주며 보고한다.

```
✅ 커밋 [N]개 완료 (위 목록)
⛔ 푸시 실패: [git 출력 요약]
   커밋은 로컬에 남아 있습니다. 원격 상태를 확인한 뒤 직접 푸시하세요.
```

성공 시 보고에 한 줄 덧붙인다.

```
🚀 푸시 완료: <remote>/<브랜치>
```

---

## 언어 출력 규칙

**분석 요약**, **이유** 설명, 커밋 **subject**, **body**를 `$ARGUMENTS`로 지정된 언어로 쓴다.

- `type`, `scope`, Conventional Commits 형식 → 항상 영문
- 인자 없음 또는 `KOR` → 한국어
- `ENG` → 영어 / `JPN` → 일본어 / `CHN` → 중국어 / `ESP` → 스페인어
- `POR` → 포르투갈어 / `FRA` → 프랑스어 / `DEU` → 독일어

**모든 언어 공통 문체 규칙**: 영어 명령형에 대응하는 간결한 명사형·명령형으로 쓴다.
완결형 서술 문장은 쓰지 않는다.

- KOR: `추가합니다` ✗ → `추가` ✓
- JPN: `追加しました` ✗ → `追加` ✓
- CHN: `进行了添加` ✗ → `添加` ✓
- DEU: `wurde hinzugefügt` ✗ → `Hinzufügen` ✓
- ESP/POR/FRA: 원형 사용(`añadir`, `adicionar`, `ajouter`)
