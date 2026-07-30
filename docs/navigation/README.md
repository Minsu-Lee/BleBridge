# 네비게이션 컨벤션

feature는 **자신의 Route와 진입점만 정의**하고, 실제 화면 전환(`NavController.navigate()`, 트랜지션, `popUpTo`)은 전부 app 모듈의 `BleBridgeApp.kt` NavHost에서만 처리합니다. feature 코드 안에서 `NavController`를 직접 다루지 않습니다.

## `navigation/<Feature>Navigation.kt`

Navigation 파일은 feature 루트의 `navigation` 패키지에 둡니다. 화면 구현 패키지와
destination 선언 패키지를 분리하면 navigation route 타입과 composable Route 함수의
동명 사용도 import에서 명확히 구분됩니다.

현재 `feature:sample`의 `main/navigation`, `cats/navigation`은 debug 예제에 남은 기존
구조입니다. 제품 feature를 추가할 때는 이 경로를 템플릿으로 사용하지 않습니다.

```kotlin
@Serializable
data object SplashRoute

fun NavGraphBuilder.splashScreen(onNavigateToMain: () -> Unit) {
    composable<SplashRoute> {
        SplashRoute(onNavigateToMain = onNavigateToMain)
    }
}
```

- **Navigation route**: `@Serializable data object <Feature>Route`. 경로 인자가 필요하면
  `data class`로 선언합니다. 인자가 없는 진입점은 항상 `data object`를 씁니다.
- **Composable Route**: 같은 이름의 `<Feature>Route(...)`가 ViewModel과 SideEffect를
  연결하고 stateless `<Feature>Screen(...)`을 호출합니다. 타입과 함수는 패키지가 다르며,
  Navigation 파일은 composable Route 함수만 호출합니다.
- **NavGraphBuilder 확장 함수**: 다른 화면으로 이동해야 하면
  `onNavigateToXxx: () -> Unit` callback을 받아 composable Route 함수에 전달합니다.
  feature 화면은 이동 대상의 navigation route 타입을 알지 못합니다.

## 화면에서 콜백 호출 시점

이동은 stateful composable `Route`가 `SideEffect`를 수신했을 때만 트리거합니다. 즉시
이벤트가 아니라 ViewModel이 `postSideEffect`로 명시적으로 신호를 보내야 하며, Route는
`collectSideEffect`에서 callback을 호출합니다.

```kotlin
viewModel.collectSideEffect { effect ->
    when (effect) {
        SplashSideEffect.NavigateToMain -> onNavigateToMain()
    }
}
```

## app NavHost에서의 실제 등록

`app/src/main/kotlin/com/jackson/blebridge/BleBridgeApp.kt`가 유일하게 `NavController`를
소유하고 `navigate()` / `popUpTo` / `launchSingleTop`을 호출합니다. 지금
`feature:splash`는 화면이 하나뿐이라 아래처럼 `splashScreen()`에 callback만 넘깁니다.

```kotlin
NavHost(
    navController = navController,
    startDestination = SplashRoute,
    ...
) {
    splashScreen(
        onNavigateToMain = {
            navController.navigate(MainRoute) {
                popUpTo<SplashRoute> { inclusive = true }
                launchSingleTop = true
            }
        },
    )
    mainScreen()
}
```

- **신규 feature 모듈을 추가할 때** 이 NavHost에 `<feature>Screen(...)` 호출을 새로 추가하고, 그 feature로 진입시키는 다른 화면의 콜백을 여기서 연결합니다.
- feature는 다른 feature를 직접 참조하지 않으므로([`feature/README.md`](../../feature/README.md#의존성) 의존성 다이어그램 참고), 두 feature 사이의 이동은 항상 app NavHost가 중개자 역할을 합니다.

## 같은 모듈에 화면이 여러 개일 때

도메인 화면 모듈에 화면이 늘어나면 feature 루트 `navigation` 패키지 안에서 파일을
destination별로 나눕니다. 예를 들어 `feature:splash`에 `permission` 화면이 생기면
`navigation/SplashNavigation.kt`와 `navigation/PermissionNavigation.kt`가 각각 자신의
destination만 등록합니다.

```kotlin
fun NavGraphBuilder.splashScreen(
    onNavigateToPermission: () -> Unit,
) {
    composable<SplashRoute> {
        SplashRoute(onNavigateToPermission = onNavigateToPermission)
    }
}

fun NavGraphBuilder.permissionScreen(
    onNavigateToMain: () -> Unit,
) {
    composable<PermissionRoute> {
        PermissionRoute(onNavigateToMain = onNavigateToMain)
    }
}
```

app NavHost가 두 destination을 등록하고 이동을 연결합니다.

```kotlin
splashScreen(
    onNavigateToPermission = {
        navController.navigate(PermissionRoute)
    },
)
permissionScreen(
    onNavigateToMain = {
        navController.navigate(MainRoute) {
            popUpTo<SplashRoute> { inclusive = true }
            launchSingleTop = true
        }
    },
)
```
