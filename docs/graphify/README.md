# Graphify 운영 가이드

Graphify는 프로젝트의 코드와 문서 관계를 `graphify-out/graph.json`에 저장해 Claude Code와
Codex가 코드베이스 질문의 탐색 범위를 빠르게 좁히도록 돕습니다. 전체 소스를 대신하는
정답 저장소가 아니므로, 그래프에서 찾은 관계는 실제 Gradle 설정과 구현으로 확인합니다.

## 로컬 사용 원칙

`graphify-out/` 전체가 커밋 대상에서 제외되므로 Graphify Git hook은 설치하지 않습니다.
그래프가 필요한 담당자만 Graphify CLI를 설치하고 수동으로 생성하거나 갱신합니다.

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

## 자동 갱신과 커밋 (비커밋, 2026-08-23부터)

`graphify-out/`(`graph.json`·`GRAPH_REPORT.md` 포함) 전체는 **`.gitignore` 대상이라 더 이상
커밋하지 않습니다.** 모든 담당자·브랜치가 각자 로컬에서 재생성하는 산출물이라, PR마다 이
스냅샷을 커밋하면 서로 다른 시점의 재생성 결과가 매번 git 충돌을 일으켰습니다(같은 파일을
가리키는 스냅샷 두 개가 다를 뿐인데도 매번 수동 재생성·해결이 필요했음). 그래프는 각자의
워킹트리에만 존재하며 팀원 간 공유되지 않습니다 — 필요하면 `graphify update .`로 로컬에서
언제든 다시 만들 수 있습니다.

커밋이나 브랜치 전환 시 자동 갱신하지 않습니다. 로컬 그래프가 필요한 시점에만
`graphify update .`를 실행합니다.

## 주의사항

- Graphify 결과는 현재 소스보다 오래됐을 수 있으므로 구체적인 판단은 실제 구현으로
  검증합니다.
- 전체 그래프를 생성하기 전에 로컬 설정과 비밀 정보가 스캔 대상에 포함되지 않았는지
  확인합니다(비커밋이라 원격에 올라가지는 않지만, 로컬 산출물에도 민감 정보를 남기지 않습니다).
- 기존 환경에 hook이 남아 있다면 다음 명령으로 제거합니다.

```bash
graphify hook uninstall
graphify hook status
```
