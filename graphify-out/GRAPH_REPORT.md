# Graph Report - BleBridge  (2026-08-04)

## Corpus Check
- 214 files · ~92,004 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 974 nodes · 1377 edges · 112 communities (74 shown, 38 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 169 edges (avg confidence: 0.82)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `52fe030e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- 프로젝트 문서 인덱스
- MviViewModel
- build-logic/README.md
- NetworkError
- Common Component Roadmap
- graphify skill (Codex integration)
- BLETransferApp Design Canvas
- graphify skill
- Extraction subagent prompt template (rules + schema)
- FeatureDesignSystemDetectorTest
- AGENTS.md agent instructions document
- AppTheme
- CatFactApi
- graphify query/path/explain reference
- CodeRabbit review configuration (.coderabbit.yaml)
- ChatHeaderTitle
- MainViewModel
- RandomCatFactFailure
- CatFactPagingSourceTest
- ConnectionActivityStatus
- TDD 오케스트레이션 파이프라인 계약
- graphify reference: extra exports and benchmark
- SplashViewModel
- graphify reference: GitHub clone and cross-repo merge
- AndroidApplicationConventionPlugin
- graphify reference: add a URL and watch a folder
- CatFact
- FakeRandomCatFactRepository
- ChatContext
- GetCatFactsPageUseCase
- CatFactPage
- AppIcons.kt
- ConnectionRole
- LoadingDots
- SplashBrandContent
- SplashContent
- MainDispatcherExtension
- MainDispatcherExtension
- MainDispatcherExtension
- RandomCatFactErrorMapperTest
- AdaptiveTwoPane Component
- AndroidHiltConventionPlugin
- AndroidLibraryConventionPlugin
- AndroidTestConventionPlugin
- FeatureConventionPlugin
- KotlinJvmConventionPlugin
- UnitTestConventionPlugin
- ChatChrome Component
- ConnectionRoleProvider (역할 결정 이후 navigation/session 범위)
- gradlew
- findActivity
- ExampleInstrumentedTest
- BleBridgeApplication
- debugSampleScreen
- NeveraResultTest
- MediaTokens
- Motion
- AppTypography
- DesignSystemIssueRegistry
- App Icon (Play Store)
- ColorPalette.kt
- AppFontFamilies.kt
- AppColors
- AppGradients
- Spacing
- splash/SplashDefaults.kt
- SplashTokens.kt
- AppColors (semantic Light/Dark 매핑)
- AppFontFamilies (primitive internal 폰트)
- MainDefaults.kt
- SampleCatsScreenTest.kt
- SampleScreenTest.kt
- SampleCatsDefaults.kt
- SampleDefaults.kt
- main/SplashDefaults.kt
- App Icon (hdpi) - Blue rounded square with white Wi-Fi/broadcast signal glyph
- Round app launcher icon (hdpi): blue circle background with a white Wi-Fi/BLE-style radiating signal glyph
- BleBridge app launcher icon (mdpi) — blue rounded-square icon with a white Bluetooth-like/signal wave symbol
- BleBridge round launcher icon (mdpi): blue circular badge with a white Wi-Fi/signal-wave glyph, the mdpi-density asset for the app's round icon variant
- App launcher icon (xhdpi) - blue rounded-square Wi-Fi/signal wave icon
- Round launcher icon (xhdpi): blue circular badge with a white Wi-Fi/broadcast signal arc icon and a small dot, representing the BleBridge app icon at xhdpi density
- App launcher icon (xxhdpi): blue rounded-square icon depicting a Wi-Fi/signal wave arc with a solid dot, symbolizing BLE/wireless bridging
- BleBridge round app launcher icon (xxhdpi): a blue circular badge with a white Wi-Fi/signal-style radiating arc icon, representing BLE/wireless connectivity branding
- App launcher icon (xxxhdpi density) — Android app icon image asset for the highest-density mipmap bucket
- App launcher icon (round, xxxhdpi): blue circular badge with a stylized Wi-Fi/signal wave and dot symbol, representing the BleBridge app icon at the xxxhdpi density for round icon variants
- CatFactRepositoryImplTest
- SampleViewModel
- NeveraResult
- SampleError
- SampleMutation
- SampleScreen
- SampleCatsScreen
- SampleErrorContent
- delegate-to-codex.sh

## God Nodes (most connected - your core abstractions)
1. `AppTheme()` - 40 edges
2. `BLETransferApp Design Canvas` - 38 edges
3. `CatFact` - 20 edges
4. `graphify skill (Codex integration)` - 19 edges
5. `RandomCatFactFailure` - 18 edges
6. `프로젝트 문서 인덱스` - 18 edges
7. `MviViewModel` - 16 edges
8. `NeveraResult` - 15 edges
9. `에이전트 개발 가이드` - 15 edges
10. `Common Component Roadmap` - 15 edges

## Surprising Connections (you probably didn't know these)
- `CodeRabbit review configuration (.coderabbit.yaml)` --semantically_similar_to--> `Honesty Rules (never invent edges, never hide cohesion, always show token cost)`  [INFERRED] [semantically similar]
  .coderabbit.yaml → .codex/skills/graphify/SKILL.md
- `SampleCatsAppendErrorPreview()` --calls--> `AppTheme()`  [INFERRED]
  feature/sample/src/main/kotlin/com/jackson/blebridge/feature/sample/cats/component/state/SampleCatsAppendError.kt → core/designsystem/src/main/kotlin/com/jackson/blebridge/core/designsystem/theme/AppTheme.kt
- `SampleCatsAppendLoadingPreview()` --calls--> `AppTheme()`  [INFERRED]
  feature/sample/src/main/kotlin/com/jackson/blebridge/feature/sample/cats/component/state/SampleCatsAppendLoading.kt → core/designsystem/src/main/kotlin/com/jackson/blebridge/core/designsystem/theme/AppTheme.kt
- `SampleCatsErrorContentPreview()` --calls--> `AppTheme()`  [INFERRED]
  feature/sample/src/main/kotlin/com/jackson/blebridge/feature/sample/cats/component/state/SampleCatsErrorContent.kt → core/designsystem/src/main/kotlin/com/jackson/blebridge/core/designsystem/theme/AppTheme.kt
- `SampleCatsScreenPreview()` --calls--> `AppTheme()`  [INFERRED]
  feature/sample/src/main/kotlin/com/jackson/blebridge/feature/sample/cats/SampleCatsScreen.kt → core/designsystem/src/main/kotlin/com/jackson/blebridge/core/designsystem/theme/AppTheme.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **commit-message 스킬과 세 참조 문서(hooks/path-hints/recovery)의 조건부 로딩 구조** — claude_skills_commit_message_skill_commit_message, claude_skills_commit_message_references_hooks_pre_commit_hook_handling, claude_skills_commit_message_references_path_hints_type_hints, claude_skills_commit_message_references_recovery_failure_recovery [EXTRACTED 1.00]
- **BleBridge 에이전트 git/PR/지식그래프 워크플로우 스킬 3종** — claude_skills_commit_message_skill_commit_message, claude_skills_create_pr_skill_create_pr, claude_skills_graphify_skill_graphify [INFERRED 0.75]
- **Steps 2.5-3C semantic extraction pipeline** — claude_skills_graphify_references_transcribe_transcribe, claude_skills_graphify_references_extraction_spec_extraction_spec, claude_skills_graphify_references_update_update [INFERRED 0.75]
- **Graph auto-rebuild trigger mechanisms** — claude_skills_graphify_references_add_watch_add_watch, claude_skills_graphify_references_hooks_hooks, claude_skills_graphify_references_update_update [INFERRED 0.75]
- **Graphify AST + Semantic + Merge extraction pipeline** — codex_skills_graphify_skill_step3_extraction, codex_skills_graphify_skill_step3_parta_ast, codex_skills_graphify_skill_step3_partb_semantic, codex_skills_graphify_skill_step3_partc_merge [INFERRED 0.85]
- **Graphify opt-in export flags (wiki/neo4j/falkordb/svg/graphml/mcp/benchmark)** — codex_skills_graphify_references_exports_wiki, codex_skills_graphify_references_exports_neo4j, codex_skills_graphify_references_exports_falkordb, codex_skills_graphify_references_exports_svg, codex_skills_graphify_references_exports_graphml, codex_skills_graphify_references_exports_mcp_server, codex_skills_graphify_references_exports_benchmark [INFERRED 0.85]
- **Graphify corpus ingestion flow (add URL + watch folder)** — codex_skills_graphify_references_add_watch_graphify_add, codex_skills_graphify_references_add_watch_ingest_function, codex_skills_graphify_references_add_watch_supported_url_types, codex_skills_graphify_references_add_watch_watch_command [INFERRED 0.75]
- **graphify --update pipeline: incremental detection, conditional transcription, merge** — codex_skills_graphify_references_update_incremental_update, codex_skills_graphify_references_update_build_merge, codex_skills_graphify_references_transcribe_overview [EXTRACTED 1.00]
- **graphify self-improving loop: expand query, save-result outcomes, reflect/LESSONS.md, hook keeps it fresh** — codex_skills_graphify_references_query_vocab_expansion, codex_skills_graphify_references_query_save_result, codex_skills_graphify_references_query_reflect_lessons, codex_skills_graphify_references_hooks_git_commit_hook [EXTRACTED 1.00]
- **PR lifecycle automation: template body, auto-assign workflow, create-pr skill** — github_pull_request_template_pr_template, github_workflows_pr_auto_assign_workflow, claude_skills_create_pr_skill_create_pr [INFERRED 0.75]
- **Modules depending on core:common per README.md dependency graph** — readme_domain, readme_data, readme_feature_splash, readme_feature_main, readme_feature_sample, readme_data_sample, readme_core_network [INFERRED 0.85]
- **Gradle convention plugin composition chain (kotlin.jvm/test.android -> test.unit -> android.library -> android.compose)** — build_logic_readme_plugin_kotlin_jvm, build_logic_readme_plugin_test_unit, build_logic_readme_plugin_test_android, build_logic_readme_plugin_android_library, build_logic_readme_plugin_android_compose [EXTRACTED 1.00]
- **Agent-tooling routing configuration across BleBridge** — serena_project_config, agents_document, claude_document [INFERRED 0.75]
- **Primitive → Semantic/Contextual → Component 토큰 계층** — docs_design_system_readme_primitive, docs_design_system_readme_semantic, docs_design_system_readme_contextual, docs_design_system_readme_component [EXTRACTED 1.00]
- **AppTheme→ConnectionRoleProvider→ChatModeProvider 범위 조합 흐름** — docs_design_system_readme_apptheme, docs_design_system_readme_connectionroleprovider, docs_design_system_readme_chatmodeprovider, docs_design_system_readme_chatcontext [EXTRACTED 1.00]
- **MVI 계약 타입군 (State/Intent/Mutation/SideEffect/ViewModel)** — core_mvi_readme_mvistate, core_mvi_readme_mviintent, core_mvi_readme_mvimutation, core_mvi_readme_mvisideeffect, core_mvi_readme_mviviewmodel [EXTRACTED 1.00]
- **Media viewer AppBar + IconButton + MediaPlaybackControls composition** — docs_design_common_10_app_bar_appbar, docs_design_common_02_icon_button_iconbutton, docs_design_common_11_media_playback_controls_mediaplaybackcontrols [EXTRACTED 1.00]
- **Components with ActionButton as a foundation dependency** — docs_design_common_01_action_button_actionbutton, docs_design_common_03_segmented_control_segmentedcontrol, docs_design_common_07_choice_dialog_choicedialog, docs_design_common_08_settings_field_settingsfield [EXTRACTED 1.00]
- **AppTheme -> ConnectionRoleProvider -> ChatModeProvider token flow architecture** — docs_design_blebridgedesignsystem_apptheme, docs_design_blebridgedesignsystem_connectionroleprovider, docs_design_blebridgedesignsystem_chatmodeprovider, docs_design_blebridgedesignsystem_rolecolors, docs_design_blebridgedesignsystem_chattokens [EXTRACTED 1.00]
- **BleBridge TDD 오케스트레이션 파이프라인 (feature-analyst -> testcase-author -> tdd-implementer -> code-reviewer)** — docs_orchestration_feature_analyst_featureanalyst, docs_orchestration_testcase_author_testcaseauthor, docs_orchestration_tdd_implementer_tddimplementer, docs_orchestration_code_reviewer_codereviewer [EXTRACTED 1.00]
- **채팅 화면을 구성하는 core:ui 컴포넌트군 (ChatChrome/MessageBubble/ChatInput/TransferCard)** — docs_design_ui_02_chat_chrome_chatheadertitle, docs_design_ui_03_message_bubble_messagebubble, docs_design_ui_05_chat_input_chatinput, docs_design_ui_04_transfer_card_transfercard [INFERRED 0.85]
- **Feature 화면 아키텍처 컨벤션 문서군 (Feature UI/Navigation/Feature 모듈 그룹)** — docs_feature_readme_featureuiconvention, docs_navigation_readme_navigationconvention, feature_readme_featuremodulegroup [INFERRED 0.75]

## Communities (112 total, 38 thin omitted)

### Community 0 - "프로젝트 문서 인덱스"
Cohesion: 0.05
Nodes (70): app 모듈, Convention Plugin: blebridge.android.compose, Convention Plugin: blebridge.android.hilt, Convention Plugin: blebridge.android.library, Convention Plugin: blebridge.kotlin.jvm, Build logic 문서 (build-logic/README.md), core:common 모듈, NetworkError (+62 more)

### Community 1 - "MviViewModel"
Cohesion: 0.16
Nodes (10): MviActionScope, MviViewModel, Flow, INTENT, Lifecycle, MUTATION, SIDE_EFFECT, STATE (+2 more)

### Community 2 - "build-logic/README.md"
Cohesion: 0.07
Nodes (51): Allowed: feature routes, data DI bindings, top-level design system application; Forbidden: direct repository impl calls, business rules, feature UI implementation, app uses Convention Plugin blebridge.android.application, debug variant bundles feature:sample/data:sample and registers blebridge-debug://sample deep link to a random Cat Fact screen, BleBridgeApplication (Hilt app), MainActivity (system splash + Compose entry), BleBridgeApp (assembles SplashRoute/MainRoute), Other modules must not depend on app, app module purpose: assemble APK, Application, Activity, Hilt graph, Compose NavHost, release build: R8 minify/shrink enabled; proguard-rules.pro keeps stacktrace, Kotlin metadata, domain/core:common, @HiltViewModel; Hilt/coroutines/kotlinx.serialization/Navigation Compose covered by bundled consumer proguard rules so no extra rules added, Common Android settings: compile SDK 37, min SDK 30, Java 17; Application uses target SDK 36 (+43 more)

### Community 3 - "NetworkError"
Cohesion: 0.11
Nodes (10): EmptyBodyError, HttpError, InvalidResponseError, NetworkConnectionError, NetworkError, TimeoutError, UnknownError, ApiCallExecutor (+2 more)

### Community 4 - "Common Component Roadmap"
Cohesion: 0.08
Nodes (46): Rationale: core:designsystem vs core:ui module boundary, Common Component Roadmap, AppTheme, Rationale: layered token architecture avoids theme duplication per mode, ChatContext, ChatModeProvider, AppTheme.chatTokens, ConnectionRoleProvider (+38 more)

### Community 5 - "graphify skill (Codex integration)"
Cohesion: 0.17
Nodes (12): graphify skill (Codex integration), Interpreter guard for subcommands (.graphify_python re-resolution), /graphify query - BFS/DFS traversal against existing graph.json, Step 1 - Ensure graphify installed / detect Python interpreter, Step 2.5 - Video/audio transcription, Step 2 - Detect files (corpus size/type gate), Step 4.5 - Graph health check (dangling/missing/collapsed edge diagnostics), Step 4 - Build graph, cluster, analyze, generate outputs (+4 more)

### Community 6 - "BLETransferApp Design Canvas"
Cohesion: 0.08
Nodes (39): BLETransferApp Design Canvas, Action Button component, Activity Indicator component, Adaptive Two Pane component, App Bar component, AppColors (42 fields, Light/Dark), AppTypography (14 styles), Attachment Sheet component (+31 more)

### Community 7 - "graphify skill"
Cohesion: 0.07
Nodes (38): /graphify trigger routes to graphify skill, pre-commit 훅 재포맷 흡수 절차, 경로별 Conventional Commits type 힌트, 검증/커밋/stash pop 실패 복구와 중단 보고, commit-message: gitflow protected-branch guard, commit-message skill, commit-message: Conventional Commits type/scope 규칙, commit-message: 최종 1회 푸시 단계 (+30 more)

### Community 8 - "Extraction subagent prompt template (rules + schema)"
Cohesion: 0.24
Nodes (11): Confidence rubric: EXTRACTED=1.0, INFERRED={0.95..0.55}, AMBIGUOUS=0.1-0.3, graphify reference: extraction subagent prompt (compact), Hyperedge rule: 3+ nodes sharing concept/flow/pattern, max 3 per chunk, Required output JSON schema (nodes/edges/hyperedges/tokens), Node ID format: {stem}_{entity}, full repo-relative path stem, semantically_similar_to rule for non-structural shared-problem concepts, Extraction subagent prompt template (rules + schema), Step 3 - Extract entities and relationships (structural + semantic) (+3 more)

### Community 9 - "FeatureDesignSystemDetectorTest"
Cohesion: 0.09
Nodes (12): Issue, JavaContext, FeatureDesignSystemDetector, UElementHandler, Detector, FeatureDesignSystemDetectorTest, Detector, LintDetectorTest (+4 more)

### Community 10 - "AGENTS.md agent instructions document"
Cohesion: 0.14
Nodes (21): docs/design/BLETransferApp.dc.html and component prompts are target design, may be ahead of current code, Consult docs/agent/README.md for structural change, multi-module work, or doc conflicts, Select only needed docs from docs/README.md per task, AGENTS.md agent instructions document, Dirty graphify-out/ files after hooks/incremental updates are expected, not a reason to skip graphify, graphify-out/ knowledge graph usage: prefer query/path/explain over GRAPH_REPORT.md or raw grep, /graphify slash command triggers installed graphify skill/instructions first, Check target module's README first before editing (+13 more)

### Community 11 - "AppTheme"
Cohesion: 0.15
Nodes (7): AppThemeTest, contrastRatio(), Color, AppTheme(), IconSize, Radius, ControlSize

### Community 12 - "CatFactApi"
Cohesion: 0.14
Nodes (9): CatFactApi, bindCatFactRepository(), CatFactNetworkModule, CatFactRepository, CatFactPageResponse, CatFactResponse, OkHttpClient, Response (+1 more)

### Community 13 - "graphify query/path/explain reference"
Cohesion: 0.19
Nodes (16): graphify claude install/uninstall (native CLAUDE.md integration), graphify hook install/uninstall/status (post-commit hook), graphify hooks reference (commit hook & CLAUDE.md integration), BFS traversal mode (broad context, nearest neighbors), DFS traversal mode (--dfs, trace a chain/dependency path), graphify query/path/explain reference, graphify reflect --if-stale / LESSONS.md self-improving loop, graphify save-result (writes Q&A back into the graph) (+8 more)

### Community 14 - "CodeRabbit review configuration (.coderabbit.yaml)"
Cohesion: 0.17
Nodes (15): auto_review enabled, base branch develop, drafts excluded, Auto PR title using Conventional Commits format, code_generation.unit_tests path_instructions (JUnit5/JUnit4 split for generated tests), CodeRabbit review configuration (.coderabbit.yaml), issue_enrichment.auto_enrich enabled, knowledge_base.web_search enabled, src/androidTest instrumented/Compose UI test rules (JUnit4, no ViewModel/Hilt), data layer review rules (Repository impl, DataSource, mapper, CancellationException) (+7 more)

### Community 15 - "ChatHeaderTitle"
Cohesion: 0.18
Nodes (15): 03 Message Bubble Screenshot, 05-chat-input.png - ChatInput core:ui component screenshot, DeviceListItem, DeviceListItem UI Screenshot, ChatHeaderTitle, ConnectionRoleBadge, DateDivider, MessageBubble (+7 more)

### Community 16 - "MainViewModel"
Cohesion: 0.05
Nodes (25): MviIntent, MviMutation, MviSideEffect, MviState, MainScreenTest, Modifier, MainRoute(), MainScreen() (+17 more)

### Community 17 - "RandomCatFactFailure"
Cohesion: 0.15
Nodes (12): header(), toRandomCatFactFailure(), Client, EmptyBody, InvalidResponse, Network, RandomCatFactFailure, RateLimited (+4 more)

### Community 18 - "CatFactPagingSourceTest"
Cohesion: 0.32
Nodes (4): CatFactPagingSourceTest, FakeCatFactRepository, CatFactRepository, PagingSource

### Community 19 - "ConnectionActivityStatus"
Cohesion: 0.21
Nodes (9): ConnectionActivityMotion, ConnectionActivityColors, ConnectionActivityStatus, Advertising, Connecting, Idle, Scanning, ConnectionActivityStatusColors (+1 more)

### Community 20 - "TDD 오케스트레이션 파이프라인 계약"
Cohesion: 0.35
Nodes (13): Feature UI 구성 컨벤션 (Route/Screen/Content), 네비게이션 컨벤션, code-reviewer, feature-analyst, 오케스트레이션 킥오프 프롬프트, TDD 오케스트레이션 파이프라인 계약, TDD 오케스트레이션 문서 인덱스, tdd-implementer (+5 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.25
Nodes (9): Step 8 - Token reduction benchmark (graphify benchmark, >5000 words), graphify reference: extra exports and benchmark, Step 7a - FalkorDB export / push (OpenCypher, GRAPH.QUERY), Step 7c - GraphML export (graphify export graphml), Step 7d - MCP stdio server (query_graph, get_node, get_neighbors, god_nodes, shortest_path...), Step 7 - Neo4j export / push (Cypher, MERGE, bolt://), Step 7b - SVG export (graphify export svg), Step 6b - Wiki export (graphify export wiki) (+1 more)

### Community 22 - "SplashViewModel"
Cohesion: 0.06
Nodes (23): BleBridgeApp(), Modifier, MainActivity, Bundle, ComponentActivity, ProviderModule, AndroidAppInfoProvider, AppInfoProvider (+15 more)

### Community 23 - "graphify reference: GitHub clone and cross-repo merge"
Cohesion: 0.33
Nodes (7): graphify clone <github-url> [--branch] - single/multi repo clone, Cross-repo graph merge flow (clone each, extract each, merge-graphs), graphify reference: GitHub clone and cross-repo merge, graphify extract ./subfolder/ - per-subfolder extraction CLI, graphify merge-graphs <...> --out - combine graph.json files, Multi-subfolder / monorepo merge flow (per-subfolder graphify-out), Step 0 - GitHub repo clone and multi-path merge

### Community 24 - "AndroidApplicationConventionPlugin"
Cohesion: 0.20
Nodes (7): AndroidApplicationConventionPlugin, Plugin, Project, AndroidComposeConventionPlugin, Plugin, Project, configureCompose()

### Community 25 - "graphify reference: add a URL and watch a folder"
Cohesion: 0.33
Nodes (6): Debounce (default 3s) before triggering rebuild, graphify reference: add a URL and watch a folder, /graphify add <url> - fetch URL into corpus then --update, graphify.ingest.ingest() - fetch/save URL to ./raw, Supported URL types: YouTube, Twitter/X, arXiv, PDF, images, webpage, --watch - background folder watcher, auto-rebuild on changes

### Community 27 - "CatFact"
Cohesion: 0.17
Nodes (8): CatFact, CatFactPagingSource, PagingSource, CatFactRepository, CatFactRepository, LoadParams, LoadResult, PagingState

### Community 28 - "FakeRandomCatFactRepository"
Cohesion: 0.24
Nodes (4): GetRandomCatFactUseCase, FakeRandomCatFactRepository, CatFactRepository, SampleViewModelTest

### Community 30 - "ChatContext"
Cohesion: 0.36
Nodes (9): ChatColors, ChatContext(), ChatMode, Classic, DeveloperHybrid, Terminal, ChatModeProvider(), ChatTokens (+1 more)

### Community 32 - "CatFactPage"
Cohesion: 0.22
Nodes (3): toDomain(), CatFactPage, CatFactRepository

### Community 33 - "AppIcons.kt"
Cohesion: 0.48
Nodes (6): AppIcons, fill(), stroke(), svgIcon(), SvgPath, ImageVector

### Community 34 - "ConnectionRole"
Cohesion: 0.48
Nodes (6): ConnectionRole, Client, Server, ConnectionRoleColors, ConnectionRoleProvider(), forRole()

### Community 35 - "LoadingDots"
Cohesion: 0.38
Nodes (5): LoadingDot(), LoadingDotInactivePreview(), Modifier, LoadingDots(), LoadingDotsPreview()

### Community 36 - "SplashBrandContent"
Cohesion: 0.38
Nodes (5): Modifier, SplashBrandContent(), SplashBrandContentIdlePreview(), SplashLogo(), SplashLogoIdlePreview()

### Community 37 - "SplashContent"
Cohesion: 0.38
Nodes (5): Modifier, SplashContent(), SplashContentIdlePreview(), SplashFooter(), SplashFooterIdlePreview()

### Community 39 - "MainDispatcherExtension"
Cohesion: 0.33
Nodes (4): AfterEachCallback, BeforeEachCallback, ExtensionContext, MainDispatcherExtension

### Community 40 - "MainDispatcherExtension"
Cohesion: 0.33
Nodes (4): AfterEachCallback, BeforeEachCallback, ExtensionContext, MainDispatcherExtension

### Community 41 - "MainDispatcherExtension"
Cohesion: 0.33
Nodes (4): AfterEachCallback, BeforeEachCallback, ExtensionContext, MainDispatcherExtension

### Community 43 - "AdaptiveTwoPane Component"
Cohesion: 0.33
Nodes (6): AdaptiveTwoPane Component, Primary Pane Region, Secondary Pane Region, Usage Note: Device, Chat, Settings tablet layout, DesignSystem Category Tag, 09 Adaptive Two Pane Screenshot

### Community 44 - "AndroidHiltConventionPlugin"
Cohesion: 0.50
Nodes (3): AndroidHiltConventionPlugin, Plugin, Project

### Community 45 - "AndroidLibraryConventionPlugin"
Cohesion: 0.50
Nodes (3): AndroidLibraryConventionPlugin, Plugin, Project

### Community 46 - "AndroidTestConventionPlugin"
Cohesion: 0.50
Nodes (3): AndroidTestConventionPlugin, Plugin, Project

### Community 47 - "FeatureConventionPlugin"
Cohesion: 0.50
Nodes (3): FeatureConventionPlugin, Plugin, Project

### Community 48 - "KotlinJvmConventionPlugin"
Cohesion: 0.50
Nodes (3): KotlinJvmConventionPlugin, Plugin, Project

### Community 49 - "UnitTestConventionPlugin"
Cohesion: 0.50
Nodes (3): Plugin, Project, UnitTestConventionPlugin

### Community 50 - "ChatChrome Component"
Cohesion: 0.40
Nodes (5): ChatChrome Component, core:ui Module, Date Divider Element, Role Badge (SERVER pill), 02 Chat Chrome Screenshot

### Community 51 - "ConnectionRoleProvider (역할 결정 이후 navigation/session 범위)"
Cohesion: 0.67
Nodes (4): AppTheme (MainActivity.setContent 배치), ChatContext (Preview/UI test 편의 API), ChatModeProvider (ChatRoute 범위), ConnectionRoleProvider (역할 결정 이후 navigation/session 범위)

### Community 52 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 106 - "CatFactRepositoryImplTest"
Cohesion: 0.16
Nodes (4): CatFactRepositoryImpl, CatFactRepository, CatFactRepositoryImplTest, MockWebServer

### Community 107 - "SampleViewModel"
Cohesion: 0.26
Nodes (6): CatsClicked, Initialize, RetryClicked, SampleIntent, SampleViewModel, toUiError()

### Community 108 - "NeveraResult"
Cohesion: 0.42
Nodes (10): Failure, T, map(), mapFailure(), mapSuccess(), NeveraResult, Success, E (+2 more)

### Community 109 - "SampleError"
Cohesion: 0.20
Nodes (10): Client, EmptyBody, InvalidResponse, Network, RateLimited, SampleError, Server, Timeout (+2 more)

### Community 110 - "SampleMutation"
Cohesion: 0.40
Nodes (4): Failed, Loaded, Loading, SampleMutation

### Community 111 - "SampleScreen"
Cohesion: 0.25
Nodes (7): Modifier, LoadingContent(), SampleUiState, Modifier, SampleRoute(), SampleScreen(), SampleScreenPreview()

### Community 112 - "SampleCatsScreen"
Cohesion: 0.08
Nodes (22): debugSampleScreen(), NavController, CatFactItem(), CatFactItemDefaults, CatFactItemPreview(), Modifier, Modifier, SampleCatsAppendError() (+14 more)

### Community 115 - "SampleErrorContent"
Cohesion: 0.60
Nodes (4): Modifier, message(), SampleErrorContent(), SampleErrorContentPreview()

## Knowledge Gaps
- **180 isolated node(s):** `delegate-to-codex.sh script`, `HttpError`, `NetworkConnectionError`, `TimeoutError`, `EmptyBodyError` (+175 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **38 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AppTheme()` connect `AppTheme` to `AppIcons.kt`, `ConnectionRole`, `AppColors`, `AppGradients`, `Spacing`, `LoadingDots`, `SplashBrandContent`, `SplashContent`, `SampleScreen`, `SampleCatsScreen`, `MainViewModel`, `ConnectionActivityStatus`, `SampleErrorContent`, `SplashViewModel`, `MediaTokens`, `Motion`, `AppTypography`, `ChatContext`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `CatFact` connect `CatFact` to `CatFactPage`, `CatFactRepositoryImplTest`, `SampleScreen`, `SampleCatsScreen`, `CatFactPagingSourceTest`, `FakeRandomCatFactRepository`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **Why does `SampleScreenPreview()` connect `SampleScreen` to `CatFact`, `AppTheme`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Are the 25 inferred relationships involving `AppTheme()` (e.g. with `.onCreate()` and `.`BLE 동작 상태는 역할과 독립된 semantic color를 제공한다`()`) actually correct?**
  _`AppTheme()` has 25 INFERRED edges - model-reasoned connections that need verification._
- **What connects `delegate-to-codex.sh script`, `HttpError`, `NetworkConnectionError` to the rest of the system?**
  _180 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `프로젝트 문서 인덱스` be split into smaller, more focused modules?**
  _Cohesion score 0.053830227743271224 - nodes in this community are weakly interconnected._
- **Should `build-logic/README.md` be split into smaller, more focused modules?**
  _Cohesion score 0.06753246753246753 - nodes in this community are weakly interconnected._