# 실패 복구와 중단 보고

**읽는 조건**: SKILL.md 2번 검증, 5번 커밋, 또는 `git stash pop`이 **실제로 실패했을 때만.**
정상 흐름에서는 읽지 않는다.

공통 원칙: **재시도하지 않는다.** 아래 복구를 한 번 수행하고 중단해 보고한다.
이미 확정된 앞선 커밋은 남는다 — 전체를 되돌리려면 `git reset --soft <BASE_SHA>`.

## 검증 실패 (2번)

아직 아무것도 커밋하지 않았고 인덱스도 건드리지 않은 상태다. 워킹트리 그대로 두고 중단한다.

```
⛔ 검증 실패로 커밋하지 않았습니다.
   명령: <실행한 gradle 명령>
   <출력 요약>
```

gradle 태스크가 `task not found`로 실패했다면 **모듈 타입을 잘못 판정한 것**이다.
SKILL.md 2번의 판별표로 돌아가 `plugins` 블록을 다시 읽는다(JVM 모듈에 `testDebugUnitTest`를
쓰지 않았는지 확인).

## 커밋 실패 (5번 ③)

stash로 격리된 상태에서 커밋이 실패했다. **먼저 stash를 되돌린다.**

```bash
git rev-parse stash@{0}     # STASH_SHA와 같은지 확인
git stash pop
git reset                   # 이 그룹 스테이징 해제
```

워킹트리는 이 시점에 1번 시작 상태로 돌아온다(앞선 그룹이 커밋된 만큼은 제외).

> 원래 일부 파일이 staged였다면 그 인덱스 상태까지는 복원되지 않는다. `git reset`이
> hunk 단위 정보를 버리기 때문이다. 보고에 이 한계를 명시한다.

## stash pop 충돌 (5번 ④)

워킹트리에 충돌 표시(`<<<<<<<`)가 남는다. **자동 해결을 시도하지 않는다.**

```bash
git status --short
git stash list
```

```
⛔ stash 복구 중 충돌이 발생했습니다. 자동 해결하지 않았습니다.
   충돌 파일: <목록>
   충돌을 직접 정리한 뒤 `git stash drop`으로 stash를 제거하세요.
   이전 상태로 되돌리려면 `git reset --soft <BASE_SHA>`를 쓰세요.
```

`git stash drop`을 대신 실행하지 않는다 — 사용자가 내용을 확인한 뒤 판단할 일이다.

## stash pop이 "local changes would be overwritten"로 거부됨 (5번 ④, 충돌 마커 없음)

위 "stash pop 충돌"과 다른 케이스다 — 워킹트리에 `<<<<<<<` 충돌 마커가 생기지 않고, git이
pop 자체를 **미리 거부**한다. 원인은 대개 **비동기 post-commit 훅**(예: graphify 자동 재생성)이
직전 커밋 직후 백그라운드에서 같은 파일(전형적으로 `graphify-out/graph.json`,
`graphify-out/GRAPH_REPORT.md`)을 다시 써서, stash가 담고 있던 버전과 현재 워킹트리 버전이
둘 다 "커밋되지 않은 변경"으로 충돌하기 때문이다(2026-08-23 icon-button 실행에서 실측).

**여기서도 재시도하지 않는다.** 대신 stash 내용이 이미 다른 곳에 안전하게 반영돼 있는지부터
확인한다:

```bash
git rev-parse stash@{0}                       # STASH_SHA와 같은지 확인
git stash show -p stash@{0} --stat             # 무엇이 들어있는지 확인
git diff stash@{0} HEAD -- <이미 커밋했다고 보는 파일들>   # 출력 없으면 이미 HEAD와 동일
```

- **stash 속 코드 변경분이 이미 HEAD에 그대로 있다면**(직전 그룹 커밋과 동일) 그 부분은 안전하다.
  남은 건 stash 속 graphify 스냅샷뿐인데, post-commit 훅이 만든 **현재 워킹트리의 graphify
  파일이 더 최신**(방금 커밋 이후 상태를 반영)이므로, stash를 강제로 합치려 하지 말고 **현재
  워킹트리 버전을 그대로 `chore(graphify): 지식 그래프 갱신`으로 커밋**한다(SKILL.md의 Graphify
  특례 그대로).
- 커밋 후 stash가 더 이상 고유 정보를 담고 있지 않으면(위 diff가 비어 있으면) `git stash drop
  stash@{0}`으로 정리한다. **diff에 뭔가 남아 있다면 drop하지 말고** 위 "stash sha 불일치"와
  동일하게 사용자 확인을 받는다.
- 이 경로를 썼다면 중단 보고 대신, 정상 완료 보고에 "⚠️ graphify post-commit 훅과 stash pop이
  경합해 <조치 내용>으로 정리함"을 한 줄 남긴다(완전한 중단은 아니므로 실패 형식을 쓰지 않는다).

## stash sha 불일치 (5번 ④)

`stash@{0}`이 `STASH_SHA`와 다르면 그 사이에 **다른 프로세스가 stash를 쌓은 것**이다.
pop하면 남의 작업을 꺼내게 되므로 실행하지 않는다.

```
⛔ stash@{0}이 이 스킬이 만든 stash가 아닙니다.
   기대: <STASH_SHA>  현재: <실제 sha>
   `git stash list`를 확인한 뒤 직접 정리하세요. 커밋은 여기서 멈췄습니다.
```

## hunk 분할 실패 (5번)

`git apply --cached --check`가 실패하면 패치를 고쳐 재시도하지 않는다. 해당 파일은
분할하지 않고 **파일 전체를 한 커밋에 넣는 계획으로 되돌아간다.** 그 사실을 보고한다.

## 중단 보고 형식

성공 형식을 쓰지 말고 아래를 쓴다. 확정된 것과 남은 것을 분리해 보여준다.

```
⛔ 커밋 [M]/[N]에서 중단

확정된 커밋:
1. <sha> type(scope): subject
(없으면 "없음")

실패 지점: 그룹 [M+1] `type(scope): subject`
사유: [검증 실패 / 커밋 실패 / pop 충돌 / sha 불일치 — 출력 요약]

워킹트리: [복구 완료 / 수동 정리 필요]
남은 stash: [없음 / stash@{0} — 직접 확인 필요]

전체 되돌리기: git reset --soft <BASE_SHA>
```

**중단 상태에서는 푸시하지 않는다**(SKILL.md 6번).
