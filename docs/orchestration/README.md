# TDD 오케스트레이션

Orca `orchestration`으로 멀티 에이전트 TDD 파이프라인을 구성할 때 쓰는 문서 묶음입니다.
여기의 역할 문서(`<role>.md`)는 **역할 계약(참조 문서)**이며 `.claude/agents/`의 로드형
서브에이전트가 아닙니다 — 워커(Claude/Codex) 기동 프롬프트에 경로를 주입해 사용합니다.

프로젝트 공통 규칙은 복제하지 않고 [`docs/agent/README.md`](../agent/README.md)의 시작 절차와
문서 라우팅 표를 따릅니다.

## 문서

| 문서 | 내용 |
|---|---|
| [파이프라인 계약](orchestration-tdd.md) | 역할·산출물·핸드오프·루프 게이팅·세 트랙·커밋 규약·워커 기동 |
| [feature-analyst](feature-analyst.md) | 기획/분석 역할 계약 |
| [testcase-author](testcase-author.md) | 테스트케이스 역할 계약 |
| [tdd-implementer](tdd-implementer.md) | 개발/구현 역할 계약 |
| [code-reviewer](code-reviewer.md) | 코드리뷰(`codex review`) 역할 계약 |

기동 방법과 트랙별 규약은 [파이프라인 계약](orchestration-tdd.md)을 기준으로 합니다.
