# 오케스트레이션 킥오프 프롬프트

Orca 오케스트레이션 TDD 파이프라인을 실제로 띄우는 **복붙 전용 코디네이터 프롬프트**입니다.
`[개발 요청]`만 채워 코디네이터 세션에 붙여넣으면, 나머지는 계약
[`orchestration-tdd.md`](orchestration-tdd.md)의 기본값으로 자동 진행됩니다.

## 사용법

1. 아래 프롬프트 블록을 복사합니다.
2. `[개발 요청]`을 만들 것으로 교체합니다(트랙·브랜치·모델 등은 자동 판별되므로 굳이 안 써도 됨).
3. 코디네이터로 띄운 Claude 세션(이 파이프라인을 모는 세션)에 붙여넣습니다.

세부 규약(트랙·루프 게이팅·커밋/푸시·워커 기동 상세)은 계약
[`orchestration-tdd.md`](orchestration-tdd.md)가 단일 출처입니다. 이 파일은 그 계약을 실행시키는
진입 프롬프트만 담습니다.

## 킥오프 프롬프트

```text
너는 이 저장소의 Orca 오케스트레이션 코디네이터다.
먼저 `orca skills get orchestration`으로 orchestration 스킬 가이드를 로드하고,
docs/orchestration/orchestration-tdd.md 를 읽어 그 규약을 그대로 따른다.

[개발 요청]
<여기에 만들 것을 서술. 예:
 - "feature:chat 채팅 화면을 MVI로 신규 개발"
 - "docs/design/common/01-action-button.md 컴포넌트 구현"
 - "domain에 DeviceRepository 계약과 data 구현 추가">

[운용 규칙]
- 별도 입력을 나에게 되묻지 말고 계약 문서의 "호출 방식과 기본값"에 따라
  트랙·작업 브랜치(origin/develop에서 딴 feature/<slug>)·리뷰 base(분기 부모 =
  feature면 origin/develop)·타임아웃을 자동으로 정한다. 정말 모호할 때만 한 번 확인한다.
- 워커를 역할별 모델로 띄운다: feature-analyst=Claude Sonnet, tdd-implementer=Claude Sonnet,
  testcase-author=Codex 네이티브, code-reviewer=Codex 네이티브(코디네이터 자신도 Sonnet).
  기준은 "commit-message 스킬을 쓰는가" — dev만 커밋에 스킬이 필요해 Claude로 남긴다.
  레이아웃은 코디네이터 아래로 수직 분할 후 수평 분할해 4개 워커를 배치하고 패널 제목을
  에이전트명으로 지정한다(계약 "1) 워커 터미널 기동"의 split 시퀀스). 각 터미널에 역할 계약
  docs/orchestration/<role>.md 경로를 주입한 뒤 terminal wait --for tui-idle 로 로드를 확인하고
  첫 디스패치를 보낸다.
- tdd-implementer(Sonnet)는 무거운 구현을 `codex exec "..."`(비대화형)에 위임하고, 커밋은
  이 Sonnet 런타임에서 처리한다. code-reviewer·testcase-author는 Codex 네이티브라 위임 wrapper
  없이 자기 런타임에서 `codex review`/문서 작성을 직접 한다. 무인자 `codex`는 대화형 TUI라
  Bash 위임(`codex exec`/`codex review`)에는 쓰지 않는다.
- 케이스 커밋은 dev(Sonnet)에서 commit-message --auto --no-push 로 한다(커밋만, 푸시는 안 함).
- 워커를 띄우기 전(또는 최소한 첫 커밋 전)에 작업 브랜치를 origin/develop에서 딴
  feature/<slug>로 전환한다. 워커들이 worktree를 공유하므로 브랜치 전환은 전 터미널에
  반영된다. 보호 브랜치(main/master/develop) 직접 커밋은 금지다.
- "케이스" 단위는 트랙마다 다르다(계약 "케이스 루프 게이팅" 참조): feature=동작 1개,
  도메인/데이터=유닛 1개, 컴포넌트=컴포넌트 1개(테스트는 그 안 체크리스트). 컴포넌트를
  테스트마다 쪼개 per-case로 돌리지 않는다.

[진행]
계약 문서의 "케이스 루프 게이팅"대로:
feature-analyst → testcase-author →
(tdd-implementer 구현 → code-reviewer --uncommitted 리뷰 → pass 시 dev 커밋 →
 코디네이터가 testcases.md 를 [x]로 갱신) 케이스 루프 →
모든 케이스 완료 후 code-reviewer 최종 전체 리뷰 1회 →
최종 리뷰 pass면 코디네이터가 작업 브랜치를 git push 1회(첫 푸시 -u origin <브랜치>) →
그 다음 develop 머지를 판단한다.
산출물은 .orca/plan/<타깃-slug>/ 에 남긴다. 케이스 커밋은 dev가 commit-message --auto --no-push로
커밋만 하고(푸시 안 함), 푸시는 위 최종 1회뿐이다.

[혼합 요청]
한 요청에 여러 트랙이 섞이면 의존 방향대로 ① 도메인/데이터 → ② 컴포넌트 → ③ feature 화면
순서로, 각 단계를 완전한 케이스 루프로 끝낸 뒤 다음 단계로 간다.
```

## 워커 기동 상세

프롬프트만으로 코디네이터가 orca CLI로 워커를 띄웁니다. 수동 기동 명령(terminal create/wait/send,
orchestration dispatch/check)이 필요하면 계약
[§오케스트레이션 멀티 에이전트 구성 구동 방법](orchestration-tdd.md#오케스트레이션-멀티-에이전트-구성-구동-방법)을
참고하세요. CLI 스니펫 원문은 이 파일에 중복하지 않습니다.
