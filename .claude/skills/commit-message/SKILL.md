---
name: commit-message
description: 변경을 목적별로 나눠 Conventional Commits 형식으로 커밋하고 마지막에 한 번 푸시한다. "커밋해줘", "커밋 메시지 추천", "commit message", "how should I commit" 등에 반응. `--auto`면 확인 없이 커밋·푸시(에이전트용). `main`/`master`에서는 커밋하지 않는다.
argument-hint: "[--auto] [ENG]"
---

변경을 **목적별 최소 단위로 나눠 순차 커밋**하고, 모든 커밋이 끝나면 **마지막에 한 번 푸시**한다.

## 인자

- `--auto` — 사용자 확인 없이 1순위 계획으로 커밋하고 푸시까지 진행한다(멀티 에이전트 워커용).
  `AskUserQuestion`을 쓸 수 없는 호출자는 항상 이 모드를 쓴다.
- `ENG` — subject/body를 영어로 쓴다. 기본은 한국어.

`type`·`scope`·Conventional Commits 키워드는 언어와 무관하게 항상 영문이다.

---

## 1. 준비

```bash
git rev-parse --is-inside-work-tree
git branch --show-current
git rev-parse HEAD
git status --short
git stash list
ls .git/hooks/pre-commit .husky lefthook.yml .pre-commit-config.yaml 2>/dev/null
```

중단 조건:

- git 저장소가 아니면 → `현재 디렉터리는 git 저장소가 아닙니다.`
- 변경이 하나도 없으면 → `커밋할 변경이 없습니다.`

> 마지막 명령이 **무언가를 출력하면 pre-commit 훅이 있는 것**이므로
> [`references/hooks.md`](references/hooks.md)를 읽고 그 절차를 5번 루프에 반영한다.
> 아무것도 안 나오면 읽지 않는다. (2026-07-31 기준 이 저장소에는 훅이 없다.)

### 브랜치 가드

현재 브랜치가 `main`·`master`이거나 detached HEAD면 **커밋하지 않는다.**

- `--auto` — 되묻지 않고 즉시 중단한다. 자동 커밋이 `main`에 쌓이는 경로를 만들지 않는다.
  > ⛔ 현재 `main` 브랜치입니다. 작업 브랜치에서 실행하세요. 커밋하지 않았습니다.
- 대화형 — `AskUserQuestion`으로 묻는다: "작업 브랜치 생성" / "그래도 `main`에 커밋" / "중단".
  브랜치 생성을 고르면 `git switch -c <이름>`으로 만든다(워킹트리 변경은 따라온다). 이름은
  변경 파일 경로에서 뽑고(`feature/main/**` → `feature/main`), 애매하면 `work/<날짜>`.

### 변경 수집

```bash
git diff --staged --name-only
git diff --name-only
git ls-files --others --exclude-standard
git diff --staged; git diff
```

**지금 기록해 둘 것** (이후 단계에서 쓴다):

- `BASE_SHA` — `git rev-parse HEAD`. 최종 보고의 롤백 명령에 쓴다
- `RENAME_PAIRS` — `git status --short`의 `R <old> -> <new>` 목록. 5번의 `git reset` 이후에는
  `R` 표시가 "삭제 + untracked"로 풀려 사라지므로, 지금 적어두지 않으면 쌍을 놓친다
- `STASH_BEFORE` — `git stash list` 줄 수

diff가 크면 전문 대신 `--stat`과 파일별 요약으로 판단한다.

---

## 2. 검증 (커밋 전 1회)

**코드 변경이 없으면 이 단계를 건너뛴다.** 변경이 전부 아래에 해당하면 gradle을 띄우지 않는다.

`*.md`, `*.txt`, `docs/**`, `**/README*`, `.github/**`, `.gitignore`, `.idea/**`, `.vscode/**`,
`.claude/**`, 이미지·폰트 등 빌드 비참여 리소스

`./gradlew`가 없으면 검증을 생략하고 보고에 남긴다.

### 모듈 타입 판별 (필수)

**Android 모듈과 순수 JVM 모듈은 태스크 이름이 다르다.** 경로로 추측하면 존재하지 않는
태스크를 호출해 실패한다. 변경된 모듈의 `plugins` 블록을 읽는다.

```bash
grep -A5 '^plugins' <모듈>/build.gradle.kts
```

| plugins | 타입 | 태스크 |
|---|---|---|
| `blebridge.android.*`, `blebridge.feature`, `com.android.*` | Android | `testDebugUnitTest`, `compileDebugKotlin`, `compileDebugUnitTestKotlin` |
| `blebridge.kotlin.jvm`, `kotlin.jvm`만 | **JVM** | `test`, `compileKotlin`, `compileTestKotlin` |

> BleBridge 현황(반드시 재확인): `:domain`, `:core:common`, `:core:network`,
> `:lint:designsystem`이 **JVM**. 나머지는 Android. `:domain:testDebugUnitTest`는 존재하지 않는다.

### 실행

변경된 모듈만 한 번의 호출에 나열한다. Android/JVM을 섞어도 된다.

```bash
./gradlew :data:testDebugUnitTest :domain:test
```

- 테스트가 아직 없는 모듈은 컴파일 태스크만 쓴다
- 빌드 설정(`settings.gradle*`, `libs.versions.toml`, `build-logic/**`)만 바뀌었으면
  영향 모듈을 컴파일한다: `./gradlew :data:compileDebugKotlin :domain:compileKotlin`
- **TDD Red 상태**(실패하는 테스트를 방금 추가함)면 테스트 실행 대신 컴파일 태스크를 쓴다.
  Red는 실패가 정상이라 테스트를 돌리면 정상 작업이 막힌다

**검증에 실패하면 커밋하지 않고 중단한다.** 출력을 그대로 보여준다. 재시도하지 않는다.

---

## 3. 분할 계획

**원칙: 목적이 다르면 나눈다.** 되돌릴 때 따로 되돌리고 싶은 단위가 곧 커밋 단위다.
기계적으로 잘게 쪼개지 말고, 목적이 같으면 파일이 여러 개여도 한 커밋으로 둔다.

TDD 케이스 작업이면 추가로: `.orca/plan/<feature>/testcases.md`에서 TC-id를 찾고,
**테스트(`src/test`·`src/androidTest`)와 구현(`src/main`)은 항상 다른 커밋**으로 나눈다.
`test(<feature>): TC-xx …` → `feat(<feature>): TC-xx …` 순서.

### 반드시 한 커밋으로 합칠 것

쪼개면 그 시점 커밋이 성립하지 않는다.

- 시그니처 변경과 그 호출부 수정
- 파일 이동/이름 변경의 구·신 경로 양쪽 (`RENAME_PAIRS`)
- 새 모듈의 `include(...)`와 그 모듈 파일
- 새 의존성 선언과 그것을 처음 쓰는 코드

### 한 파일에 주제가 섞였을 때

`stash`는 **파일 단위**로만 격리한다. 한 파일 안의 서로 다른 주제는 stash로 나눌 수 없다.

- **`--auto`** — 나누지 않는다. 파일 전체를 한 커밋에 넣고
  `⚠️ <파일>에 주제 N개 혼재`를 보고에 남긴다
- **대화형** — hunk 분할을 제안하고 **승인받은 뒤에만** 실행한다 (아래 5번의 hunk 절차)

### 커밋 순서

앞 커밋이 뒤 커밋 없이도 성립하도록 정렬한다.

1. 빌드/설정 스캐폴딩 (모듈 등록, 의존성)
2. 하위 → 상위 레이어 (`core` → `domain` → `data` → `feature` → `app`)
3. 같은 TC-id에서는 `test` → `feat`
4. `docs` — 설명 대상이 이미 커밋돼 있도록 뒤에 둔다.
   단 문서가 가리키는 **파일의 이동**은 링크 수정보다 먼저

### 메시지

```
type(scope): subject

- 동기나 맥락 (필요할 때만)
```

body는 subject만으로 이유가 안 드러나거나, 파일이 3개 이상이고 관계가 자명하지 않거나,
breaking change일 때(`BREAKING CHANGE:` 푸터) 쓴다.

| type | 사용 시점 |
|---|---|
| `feat` | 새 기능 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변경 없는 구조 개선 |
| `style` | 코드 포맷·공백(로직 변경 없음) |
| `test` | 테스트 추가·수정 |
| `docs` | 문서·주석 |
| `chore` | 유지보수, 툴링 |
| `build` | 빌드 시스템, 의존성 변경 |
| `ci` | CI/CD 파이프라인 설정 |
| `design` | UI/UX 시각 변경(로직 없음) |
| `perf` | 성능 개선 |
| `move` | 파일·디렉터리 이동·이름 변경 |
| `remove` | 파일·코드 삭제 |
| `init` | 프로젝트·모듈 초기 구성 |
| `revert` | 이전 커밋 되돌리기 |

경로로 바로 정해지는 것 — `*.md`·`docs/**`→`docs`, `*Test.kt`→`test`,
`*.gradle*`·`libs.versions.toml`→`build`, `.github/workflows/**`→`ci`.

> **type이 둘 이상으로 갈려 정하지 못할 때만**
> [`references/path-hints.md`](references/path-hints.md)를 읽는다. 경로별 상세 힌트와
> `design`/`feat`, `style`/`design`, `build`/`chore` 경계 판단 기준이 있다.

**scope는 사실상 필수다.** 순서대로 정한다:

1. 히스토리에 쓰인 표기가 있으면 그것
2. gradle 모듈명 — `feature/main/**`→`main`, `core/network/**`→`network`
3. 비코드 영역 — `docs/orchestration/**`→`orchestration`, `.claude/skills/**`→`skill`,
   `.idea/**`→`idea`, `build-logic/**`→`build-logic`
4. `docs(docs)`처럼 type과 겹치면 그 파일들이 **다루는 주제**로 바꾼다
5. 저장소 전역 변경일 때만 생략한다

한 번의 실행에서 만드는 커밋들은 **scope 유무가 일관돼야 한다.**

### 계획 점검 (커밋 전 필수)

- 모든 파일이 정확히 한 그룹에 속하는가 (누락·중복 없음)
- `RENAME_PAIRS`의 구·신 경로가 같은 그룹인가
- 순서대로 적용했을 때 아직 커밋 안 된 그룹을 참조하지 않는가
- 그룹이 7개를 넘으면 목적이 같은 것을 합칠 수 있는지 다시 본다

---

## 4. 확인

- **`--auto`** — 확인 없이 5번으로 간다. 계획만 짧게 출력한다
- **대화형** — 계획을 출력하고 `AskUserQuestion`으로 묻는다:
  "이 계획대로 N개 커밋" / "합쳐서 M개" / "전부 1커밋". 한 파일에 주제가 섞였으면
  "hunk로 나누기" 선택지를 추가한다. **승인 없이 커밋하지 않는다.**

계획 출력 형식:

```
### 분석 요약
[파일 수, 무엇이 바뀌었는지 2~3줄]

### 커밋 [N]개
1. `type(scope): subject` — `path/a.kt`, `path/b.kt`
2. `type(scope): subject` — `path/c.kt`

검증: [실행한 gradle 명령 / 생략 사유]
> ⚠️ [경고 — 해당 시에만]
```

---

## 5. 커밋

인덱스를 비우고 시작한다. 워킹트리는 그대로다.

```bash
git reset
```

각 그룹마다 **아래 순서 그대로** 실행한다.

```bash
# ① 이 그룹만 스테이징
git add -- <그룹 파일들>

# ② 나머지를 치워 격리 (untracked 포함)
git stash push --keep-index --include-untracked -m "commit-message-split"
git rev-parse stash@{0}      # ← STASH_SHA로 기록
git status --short           # 이 그룹 파일만 보여야 한다

# ③ 커밋
git commit -F - <<'EOF'
type(scope): subject
EOF

# ④ 복구 — stash@{0}이 STASH_SHA와 같을 때만 pop
git rev-parse stash@{0}
git stash pop
```

주의할 점:

- ②는 마지막 그룹이라 치울 게 없어 보여도 **stash를 만든다**(`--keep-index`라도 staged
  내용이 stash에 들어간다). 그래서 ④의 pop은 **항상** 실행한다. 예외적으로 출력이
  `No local changes to save`면 stash가 없는 것이므로 pop하지 않는다
- ④는 **③ 이후에** 한다. 커밋 전에 pop하면 격리가 무의미하다
- stash 식별은 **sha로만** 한다. `git stash list`는 `stash@{0}: On <브랜치>: …`로 접두를
  붙이므로 메시지 문자열 비교는 항상 어긋난다. sha가 다르면 다른 작업의 stash이므로
  pop하지 말고 중단한다

### hunk 분할 (대화형 전용)

한 파일 안의 주제를 나눌 때만 쓴다. `--auto`에서는 쓰지 않는다.

```bash
git diff -- <파일>                    # hunk 확인
# 이번 주제의 hunk만 담은 패치를 작성
git apply --cached --check <패치>     # 먼저 검증
git apply --cached <패치>
git diff --staged -- <파일>           # 무엇이 들어갔는지 반드시 눈으로 확인
```

`--check`가 실패하면 **패치를 고쳐 재시도하지 말고 중단**한다. 남은 hunk는 워킹트리에
그대로 있으므로 다음 그룹으로 넘어간다. 이후는 위 ②~④와 동일하다.

### 실패 처리

**재시도하지 않는다.** 이미 만든 커밋은 남는다.

> 검증·커밋·`stash pop`이 **실제로 실패했을 때** [`references/recovery.md`](references/recovery.md)를
> 읽고 그 절차를 따른다. 복구 명령과 중단 보고 형식이 거기 있다. 정상 흐름에서는 읽지 않는다.

### 금지

- `Co-Authored-By` 등 트레일러
- `-a`, `--no-verify`
- `--amend` — 단 pre-commit 훅이 있는 저장소에서
  [`references/hooks.md`](references/hooks.md)의 재포맷 흡수 절차만 예외로 허용한다
- `git add .` / `git add -A` — 계획에 없는 파일을 넣지 않는다
- `git stash drop` / `clear` 임의 실행 — 이 스킬이 만든 stash만 pop으로 소비한다
- `git checkout --`, `git reset --hard`, `git clean` — 워킹트리를 버리지 않는다

---

## 6. 푸시

**커밋이 전부 성공했을 때만, 마지막에 한 번** 푸시한다. 중간에 중단됐으면 푸시하지 않는다.

```bash
git remote
git rev-parse --abbrev-ref --symbolic-full-name @{upstream}
```

- remote가 없으면 생략하고 보고에 남긴다
- upstream이 있으면 `git push`, 없으면 `git push -u <remote> <브랜치>`.
  `<remote>`는 `origin`이 있으면 `origin`, 없으면 목록의 첫 항목. 후보가 여럿이고 `origin`이
  없으면 대화형은 묻고 `--auto`는 생략한다

확인 여부:

- **`--auto`** — 묻지 않고 바로 푸시한다
- **대화형** — `AskUserQuestion`으로 묻는다. 작업 브랜치면 "푸시" / "푸시 안 함" 순서로,
  1번에서 "그래도 `main`에 커밋"을 골라 지금 `main` 위라면 **"푸시 안 함"을 첫 선택지**로
  두고 `<remote>/main`에 직접 푸시된다는 사실을 설명에 적는다

`--force` 계열은 쓰지 않는다. 푸시가 거부되면 재시도하거나 `pull --rebase`로 우회하지 않고
중단한다 — 원격에 다른 작업이 올라가 있다는 신호다. 커밋은 로컬에 그대로 둔다.

---

## 보고

성공:

```
✅ 커밋 [N]개 완료
1. <sha> type(scope): subject  (<파일 수>개)
2. <sha> type(scope): subject

검증: [gradle 명령 / 생략 사유]
🚀 푸시: <remote>/<브랜치>   (또는 "푸시하지 않음")
되돌리기: git reset --soft <BASE_SHA>

> ⚠️ [주제 혼재 / 남은 stash 등 — 해당 시에만]
```

중단했을 때는 이 형식을 쓰지 않는다 — [`references/recovery.md`](references/recovery.md)의
중단 보고 형식을 쓴다.

마무리 확인: 커밋 수가 계획과 같은가, `git status`가 예상대로인가,
`git stash list` 줄 수가 `STASH_BEFORE`와 같은가(다르면 삭제하지 말고 경고).

subject·body는 완결형 서술을 쓰지 않고 명사형·명령형으로 짧게 쓴다
(`추가합니다` ✗ → `추가` ✓ / `add` ✓).
