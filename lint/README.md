# Custom lint

BleBridge의 모듈 경계와 디자인시스템 사용 규칙을 빌드 단계에서 검증합니다.

## `lint:designsystem`

`blebridge.feature` convention plugin이 모든 feature 모듈에 `lintChecks`로 연결합니다.

| Issue ID | 차단 대상 |
|---|---|
| `FoundationTokenInFeature` | Feature의 `AppTheme` foundation token 직접 참조 |
| `MaterialComponentInFeature` | Feature의 정책 대상 Material3 컴포넌트 직접 사용 |
| `RawDesignValueInFeature` | Feature의 raw `Color`, `.dp`, `.sp` 사용 |

모든 Issue는 `ERROR`입니다. 기존 과도기 코드는 feature별 `lint-baseline.xml`로 관리하지만,
신규 코드는 baseline에 항목을 추가하지 않고 `core:ui` 컴포넌트 사용으로 해결합니다.

```bash
./gradlew :lint:designsystem:test
./gradlew lintDebug
```
