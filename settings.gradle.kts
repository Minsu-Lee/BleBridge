pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BleBridge"
include(":app")
include(":domain")
include(":data")
include(":core:common")
include(":core:network")
include(":core:designsystem")
include(":core:ui")
include(":core:mvi")
include(":feature:splash")
include(":feature:main")
include(":domain:sample")
include(":data:sample")
include(":feature:sample")
include(":lint:designsystem")
