# 경로별 type 힌트

**읽는 조건**: SKILL.md 3번에서 어떤 그룹의 `type`이 **둘 이상으로 갈려 정하지 못할 때만.**
경로만 보고 바로 정해지면(`*.md` → `docs`, `*Test.kt` → `test`) 읽지 않는다.

여러 후보가 적힌 항목은 **변경 내용**으로 가른다 — 새로 만들었으면 `feat`,
기존 동작을 고쳤으면 `fix`, 동작이 그대로면 `refactor`.

## 공통

| 경로 패턴 | type 후보 |
|---|---|
| `*test*`, `*spec*`, `__tests__/**`, `*Test.kt`, `*Spec.*` | `test` |
| `.github/workflows/**`, `.gitlab-ci.yml`, `.circleci/**` | `ci` |
| `*.md`, `docs/**`, `*.txt` | `docs` |
| `package.json`, `*.gradle*`, `libs.versions.toml`, `Cargo.toml`, `go.mod`, `pom.xml` | `build`, `chore` |
| `Dockerfile`, `docker-compose*`, `*.tf` | `chore`, `ci` |
| `build-logic/**` | `build`, `chore` |
| `lint/**` | `chore`, `build` |

## Android / 클린 아키텍처

| 경로 패턴 | type 후보 |
|---|---|
| `**/*ViewModel.kt`, `**/presentation/**ViewModel.kt` | `feat`, `fix`, `refactor` |
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

## 헷갈리기 쉬운 경계

- **`design` vs `feat`** — 컴포저블의 **시각적 표현만** 바뀌었으면 `design`, 동작·상태·API가
  바뀌었으면 `feat`. 새 컴포넌트 추가는 `feat`
- **`style` vs `design`** — `style`은 코드 포맷·공백(로직·표현 무변경), `design`은 UI 결과물의
  시각 변경. 들여쓰기 정리는 `style`, 색·간격 변경은 `design`
- **`build` vs `chore`** — 빌드 결과물에 영향을 주면 `build`(의존성 버전, 컨벤션 플러그인),
  개발 편의·툴링이면 `chore`(IDE 설정, `.gitignore`)
- **`refactor` vs `fix`** — 겉보기 동작이 그대로면 `refactor`. 버그가 사라졌으면 `fix`
- **`chore` 남용 주의** — 어디에도 안 맞을 때의 기본값으로 쓰지 않는다. 위 표에서 먼저 찾는다
