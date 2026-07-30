# Core Network 모듈

Retrofit API 호출에서 공통으로 발생하는 HTTP, 연결, timeout, 응답 변환 오류를
`NetworkError`로 변환하고 `NeveraResult`로 반환합니다.

- `core:common`의 `NeveraResult`, `NetworkError` 사용
- Retrofit/OkHttp/Gson과 coroutine 의존
- Android API, feature, data 구현에는 의존하지 않음
- Convention Plugin: `blebridge.kotlin.jvm`

```kotlin
apiCall {
    api.getSomething()
}.map(
    transformSuccess = { it.toDomain() },
    transformFailure = { it.toDomainError() },
)
```

```bash
./gradlew :core:network:test
```
