# BleBridge 에이전트 지침

- 수정 대상 모듈의 `README.md`를 먼저 확인한다.
- 작업별 상세 문서는 `docs/README.md`에서 필요한 문서만 선택해 읽는다.
- 구조 변경, 다중 모듈 작업 또는 문서 간 충돌이 있을 때
  `docs/agent/README.md`를 확인한다.
- `docs/design/BLETransferApp.dc.html`과 컴포넌트 프롬프트는 목표 설계이며 현재
  코드보다 앞서 있을 수 있다.
- 실제 Gradle·패키지·구현을 확인하되, 문서와 다르면 임의로 한쪽을 정답으로 선택하지
  않는다.
- 코드, 공개 API, 패키지, Gradle 또는 컨벤션을 변경하면 관련 README도 확인한다.
- 변경 범위에 맞는 테스트와 lint를 실행하고, 실패하거나 실행하지 못한 항목을 결과에
  명시한다.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).
