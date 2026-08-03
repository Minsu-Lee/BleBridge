---
name: create-pr
description: 현재 브랜치와 `origin/develop`의 차이를 분석해 자체 코드 리뷰와 범위별 검증을 수행한 뒤, `.github/pull_request_template.md` 형식으로 본문을 작성하고 사용자 확인 후 Pull Request를 생성한다. "PR 만들어줘", "pull request 생성", "PR 올려줘", "PR 작성" 등에 반응. `--draft`면 draft PR로 생성한다.
argument-hint: "[작업 배경 설명] [--draft] — 배경 생략 시 커밋·diff에서 추론"
---

# create-pr Skill

## 역할

1. 현재 브랜치와 `origin/develop`의 차이를 분석한다.
2. 변경 범위에 맞는 **컴파일·테스트·lint**를 실행한다.
3. **자체 코드 리뷰**로 크리티컬 문제를 판별한다.
4. 크리티컬 문제 또는 검증 실패가 있으면 → 원인과 수정 방향을 제시하고 **PR 생성을 중단**한다.
5. 문제가 없으면 → PR 본문을 작성해 보여주고, **사용자 확인 후** PR을 생성한다.

**PR 생성 전 반드시 사용자 확인을 받는다. 무단으로 `git push`나 `gh pr create`를 실행하지 않는다.**

> 이 저장소는 fork/upstream 구조가 아니다. 원격은 `origin`(`Minsu-Lee/BleBridge`) 하나이며
> base는 항상 같은 저장소의 브랜치다.

프로젝트 규칙은 이 문서에 복제하지 않고 링크한다. 판단이 애매하면
[`docs/agent/README.md`](../../../docs/agent/README.md)의 아키텍처 경계와 작업 원칙을 기준으로 한다.

---

## 실행 절차

### Step 1: 브랜치 가드

```bash
git branch --show-current
git status --short
```

- 현재 브랜치가 `main` / `develop` 이거나 detached HEAD면 즉시 중단한다:
  > 현재 브랜치가 `{브랜치명}`입니다. `develop`에서 딴 작업 브랜치에서 PR을 생성해주세요.
- 커밋되지 않은 변경이 있으면 중단하고 안내한다:
  > 커밋되지 않은 변경이 있습니다. `commit-message` 스킬로 먼저 커밋해주세요.
  (`graphify-out/**`만 dirty한 경우는 예외로 보고 진행해도 된다.)

#### base 브랜치 결정

| 현재 브랜치 | base |
|---|---|
| `feature/*`, `fix/*`, `refactor/*`, `chore/*`, `docs/*` | `develop` |
| `hotfix/*`, `release/*` | `main` — 사용자에게 한 번 확인한다 |

이후 절차의 `$BASE`는 `origin/<base 브랜치>`다.

### Step 2: 원격 최신화

```bash
git fetch origin
```

로컬 `develop`이 stale해도 비교 기준이 흔들리지 않도록 항상 원격 ref(`$BASE`)로 비교한다.

### Step 3: 커밋과 diff 확인

```bash
git log $BASE..HEAD --oneline
git diff $BASE...HEAD --stat
git diff $BASE...HEAD
```

- 커밋이 없으면 즉시 중단한다:
  > `{base}` 브랜치와 차이가 없습니다. 변경사항을 커밋 후 다시 시도해주세요.
- `--stat` 결과로 **변경된 모듈 집합**을 파악한다. Step 4 검증 범위 선택에 쓴다.

### Step 4: 변경 범위 검증

[`docs/agent/README.md`](../../../docs/agent/README.md)의 검증 원칙에 따라 **변경한 범위에 가장 가까운
검증부터** 실행한다. 전체 빌드를 무조건 돌리지 않는다.

| 변경 경로 | 실행할 검증 |
|---|---|
| `docs/**`, `*.md`, `.claude/**`, `.github/**`만 변경 | Gradle 검증 생략 |
| `build-logic/**` | `./gradlew -p build-logic compileKotlin` + 영향받는 모듈 컴파일 |
| `lint/designsystem/**` | `./gradlew :lint:designsystem:test` |
| `core/designsystem/**`, `core/ui/**` | `./gradlew :core:designsystem:compileDebugKotlin :core:ui:compileDebugKotlin` |
| `domain/**` (Kotlin JVM 모듈) | `./gradlew :domain:test` — `compileDebugKotlin`은 없다 |
| `data/**` | `./gradlew :data:testDebugUnitTest` |
| `feature/<x>/**` | `./gradlew :feature:<x>:testDebugUnitTest :feature:<x>:lintDebug` |
| `app/**`, 모듈 의존성·Gradle 변경, 3개 이상 모듈 변경 | `./gradlew :app:assembleDebug :app:lintDebug` |

```bash
set -o pipefail
./gradlew <선택한 task> 2>&1 | tail -40
```

- 실패 시 → 실패한 task와 오류 요약을 출력하고 PR 생성을 **중단**한다.
- 에뮬레이터 등 외부 환경이 필요해 실행하지 못한 검증은 **미실행 사유와 함께** Step 7 확인 화면에
  명시한다. 실행한 것처럼 쓰지 않는다.
- 성공한 검증 명령은 PR 본문의 "중점 리뷰 사항"이나 확인 화면에 그대로 남긴다.

### Step 5: 자체 코드 리뷰 + 제목·라벨 추론

diff를 아래 **크리티컬 기준**으로 검토한다.
1개라도 발견되면 → **Step 6-A**. 모두 통과하면 → **Step 6-B**.

#### 크리티컬 기준 (BleBridge)

| 분류 | 체크 항목 |
|---|---|
| **보안** | 하드코딩된 비밀번호·API 키·토큰, `local.properties` 값 커밋 |
| **크래시** | `!!` 남용, 미검증 캐스팅, NPE 위험 경로 |
| **BLE 리소스** | scan/GATT/Bluetooth 콜백 해제 누락, 권한 분기 없이 BLE API 호출 |
| **lifecycle** | ViewModel 밖에서 시작한 coroutine, Composable/싱글턴의 Activity Context 장기 보유 |
| **아키텍처 경계** | `feature:*`가 다른 feature·`data`·`app` 직접 참조, Composable에서 Repository 직접 호출, `domain`에 Android·data 구현 의존 ([경계 정의](../../../docs/agent/README.md)) |
| **MVI** | Intent/Mutation 경로를 우회한 상태 변경, 일회성 동작을 SideEffect 대신 state로 처리 ([`core/mvi/README.md`](../../../core/mvi/README.md)) |
| **디자인시스템 경계** | Feature의 foundation token 직접 참조, raw `Color`/`.dp`/`.sp`, 정책 대상 Material3 직접 사용 — `:lint:designsystem`의 ERROR 3종 ([`lint/README.md`](../../../lint/README.md)) |
| **검사 우회** | 신규 위반을 `lint-baseline.xml`에 추가, 근거 없는 `@Suppress`, 테스트 비활성화 |
| **빌드 파괴** | 미정의 참조, 시그니처 불일치, import 오류 |
| **문서 동기화** | 공개 API·패키지·모듈 의존성·Gradle·컨벤션을 바꿨는데 관련 README 미갱신 |

#### 비크리티컬 (참고만, PR 차단 안 함)

변수명 컨벤션, 불필요한 주석, 중복 로직, 미사용 import, 테스트 보강 여지 등.

#### PR 제목 추론

이 저장소의 커밋은 Conventional Commits(한국어 subject)를 쓰고, PR 제목도 같은 형식을 따른다.
브랜치명을 기계적으로 변환하지 말고 diff와 커밋 메시지에서 추론한다.

```
<type>(<scope>): <한국어 요약>
```

- `type`·`scope`는 커밋과 동일한 규칙을 쓴다 (`commit-message` 스킬 참고).
- 커밋이 하나면 그 subject를 그대로 쓸 수 있다. 여러 개면 **가장 큰 목적 하나**로 묶는다.
- 목적이 둘 이상 섞여 하나로 묶이지 않으면, 브랜치를 나누는 편이 나은지 사용자에게 알린다.
- 50자 이내로 간결하게 쓴다.

| ❌ | ✅ |
|---|---|
| `feature/designsystem/action-button` | `feat(designsystem): action-button 컴포넌트 추가` |
| `버그 수정` | `fix(main): 재연결 시 GATT 콜백이 중복 등록되던 문제 수정` |

#### 라벨 (선택)

이 저장소에는 기본 GitHub 라벨만 있고 릴리즈 노트 자동화가 없다. **라벨 없이 생성하는 것이 기본**이다.
`gh label list`로 확인해 명확히 맞는 라벨(`bug`, `enhancement`, `documentation`)이 있을 때만 제안하고,
없는 라벨을 새로 만들지 않는다.

---

### Step 6-A: 크리티컬 문제 보고 (PR 중단)

아래 형식으로 출력하고 종료한다. PR은 생성하지 않는다.

```
## 코드 리뷰 결과 — 크리티컬 문제 발견

PR 생성을 중단합니다. 아래 문제를 수정한 뒤 다시 시도해주세요.

---

### 🔴 [문제 제목]
- **파일**: `경로/파일명.kt:N`
- **원인**: [왜 문제인지]
- **수정 방향**: [어떻게 고쳐야 하는지]
```

### Step 6-B: PR 본문 초안 작성

[`.github/pull_request_template.md`](../../../.github/pull_request_template.md)의 섹션 구조를 그대로
따른다. 템플릿은 각 섹션 아래 `###` 소제목을 두는 형식이므로, 항목마다 `### <소제목>` + 설명 본문으로
채운다. 항목 수는 실제 내용에 맞춰 늘리거나 줄인다.

작업 배경($ARGUMENTS)이 비어 있으면 `$BASE..HEAD` 커밋과 diff에서 추론한다.

```markdown
## 🌁 작업 배경

### [배경 소제목]
[왜 이 작업을 했는지 — 요구사항, 문제 상황, 선행 작업]

## ✨ 주요 변경 사항

### [변경 소제목]
[무엇을 어떻게 바꿨는지 — 파일/모듈 단위가 아니라 목적 단위로]

## 🔥 중점 리뷰 사항

### [리뷰 포인트 소제목]
[리뷰어가 집중해서 볼 부분 — 설계 결정, 트레이드오프, Step 5의 비크리티컬 지적]

### 검증
- `./gradlew ...` ✅
- (미실행 항목이 있으면 사유와 함께 명시)

## 📸 스크린샷(Optional)
> UI 변경이 없으면 이 섹션을 삭제한다.

## ⚓️ References
- [관련 문서·이슈·PR — 없으면 `-`]
```

- 목표 디자인([`BLETransferApp.dc.html`](../../../docs/design/BLETransferApp.dc.html))과 현재 구현의
  차이를 남긴 경우, "중점 리뷰 사항"에 **구현 전/예정**으로 구분해 적는다.

### Step 7: 사용자 확인

아래 형식으로 출력하고 **반드시 사용자의 명시적 승인을 기다린다**:

```
## 코드 리뷰 통과 ✅

### 🟡 참고 사항 (PR 차단 안 함)
- `파일명.kt:N` — [내용]

### 검증 결과
- `./gradlew ...` ✅ / ❌ / ⚠️ 미실행([사유])

---

## PR 생성 준비

**제목**: `<type>(<scope>): 요약`
**Base**: `develop` ← **Head**: `현재 브랜치명`
**라벨**: `없음` 또는 `[제안 라벨]`
**푸시 필요 여부**: [origin에 브랜치 없음 → push 필요 / 최신 상태]

**본문 미리보기**:
---
[작성된 PR 본문 전체]
---

위 내용으로 PR을 생성할까요? (Yes / No)
수정이 필요하면 원하는 내용을 말씀해주세요.
```

### Step 8: 푸시와 PR 생성

사용자가 **Yes**(또는 수정 후 재확인)를 하면 진행한다.

```bash
# 1) 원격 브랜치가 없거나 뒤처져 있으면 먼저 푸시한다
git push -u origin HEAD

# 2) 멀티라인 Markdown은 shell quoting 문제를 피해 파일로 전달한다
BODY=$(mktemp -t pr_body).md
cat > "$BODY" << 'EOF'
[작성된 PR 본문 전체]
EOF

gh pr create --base develop --head "$(git branch --show-current)" \
  --title "<제목>" --body-file "$BODY"
```

- `--draft` 인자를 받았으면 `--draft`를 추가한다.
- 라벨을 확정했으면 `--label "<라벨>"`을 추가한다. 저장소에 없는 라벨을 지정하면 `gh`가 실패한다.
- 생성 후 PR URL을 출력한다.
