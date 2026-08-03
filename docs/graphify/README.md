# Graphify 운영 가이드

Graphify는 프로젝트의 코드와 문서 관계를 `graphify-out/graph.json`에 저장해 Claude Code와
Codex가 코드베이스 질문의 탐색 범위를 빠르게 좁히도록 돕습니다. 전체 소스를 대신하는
정답 저장소가 아니므로, 그래프에서 찾은 관계는 실제 Gradle 설정과 구현으로 확인합니다.

## 신규 환경 설정

저장소를 clone한 뒤 Graphify CLI를 사용할 수 있는 환경에서 다음 명령을 한 번 실행합니다.

```bash
graphify hook install
graphify hook status
```

설치 명령은 로컬 저장소에 다음 항목을 설정합니다.

- `post-commit`: 커밋에서 변경된 코드의 그래프를 백그라운드에서 갱신
- `post-checkout`: 브랜치 전환 후 코드 그래프를 백그라운드에서 갱신
- merge driver: `graphify-out/graph.json` 병합 처리

Git hook과 로컬 Git config는 clone으로 전달되지 않으므로 담당자마다 설치해야 합니다.
저장소의 `.gitattributes`는 merge driver 적용 대상을 공유하지만 로컬 driver 설치를
대신하지 않습니다.

## 평상시 사용

`graphify-out/graph.json`이 있으면 Claude Code와 Codex는 코드 구조, 호출 관계, 데이터
흐름 같은 코드베이스 질문에 앞서 Graphify query를 사용합니다. 일반적인 개발 질문을 위해
매번 전체 `/graphify`를 호출할 필요는 없습니다.

직접 조회할 때는 목적에 따라 다음 명령을 사용합니다.

```bash
graphify query "BLE 연결 상태가 UI까지 전달되는 흐름"
graphify path "연결 상태" "화면 상태"
graphify explain "MviViewModel"
```

전체 `/graphify`는 그래프가 없거나 손상된 경우, 추출 규칙이 크게 변경된 경우에만 다시
실행합니다. 일부 파일만 변경됐다면 다음 증분 갱신을 우선합니다.

```bash
graphify update .
```

## 자동 갱신과 커밋

설치된 hook은 커밋과 브랜치 전환을 막지 않도록 백그라운드에서 동작하며 코드 파일만
구조적으로 갱신합니다. 문서, PDF, 이미지의 의미 분석은 hook 대상이 아니므로 필요한 경우
`graphify update .`를 별도로 실행합니다.

hook 실행 기록은 다음 위치에서 확인합니다.

```text
~/.cache/graphify-rebuild.log
```

`post-commit` 갱신은 원래 커밋이 완료된 뒤 실행되므로 갱신된 그래프 파일은 해당 커밋에
자동 포함되지 않습니다. 백그라운드 작업이 끝난 뒤 변경을 검토하고 다음 커밋에 포함하거나,
아직 공유하지 않은 로컬 커밋이라면 필요에 따라 amend합니다.

저장소에는 팀이 조회할 핵심 결과만 커밋합니다.

```text
graphify-out/graph.json
graphify-out/GRAPH_REPORT.md
.gitattributes
```

캐시, 로컬 Python 경로, manifest, HTML 시각화와 토큰 비용 기록 등 재생성 가능한 실행
산출물은 `.gitignore`로 제외합니다.

## 주의사항

- Graphify 결과는 현재 소스보다 오래됐을 수 있으므로 구체적인 판단은 실제 구현으로
  검증합니다.
- 전체 그래프를 생성하기 전에 로컬 설정과 비밀 정보가 스캔 대상에 포함되지 않았는지
  확인합니다. 생성된 `graph.json`도 커밋 전에 민감 정보를 점검합니다.
- rebase, merge, cherry-pick 중에는 hook이 자동 갱신을 건너뜁니다. 작업 완료 후 필요하면
  `graphify update .`를 실행합니다.
- 일시적으로 hook을 건너뛸 때는 해당 Git 명령에 `GRAPHIFY_SKIP_HOOK=1`을 지정합니다.

설정 상태 확인과 제거는 다음 명령을 사용합니다.

```bash
graphify hook status
graphify hook uninstall
```
