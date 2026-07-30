plugins {
    `kotlin-dsl`
}

group = "com.jackson.blebridge.buildlogic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("bleBridgeTestUnit") {
            id = "blebridge.test.unit"
            implementationClass = "com.jackson.blebridge.buildlogic.UnitTestConventionPlugin"
        }
        register("bleBridgeTestAndroid") {
            id = "blebridge.test.android"
            implementationClass = "com.jackson.blebridge.buildlogic.AndroidTestConventionPlugin"
        }
        register("bleBridgeKotlinJvm") {
            id = "blebridge.kotlin.jvm"
            implementationClass = "com.jackson.blebridge.buildlogic.KotlinJvmConventionPlugin"
        }
        register("bleBridgeAndroidLibrary") {
            id = "blebridge.android.library"
            implementationClass = "com.jackson.blebridge.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("bleBridgeAndroidCompose") {
            id = "blebridge.android.compose"
            implementationClass = "com.jackson.blebridge.buildlogic.AndroidComposeConventionPlugin"
        }
        register("bleBridgeAndroidHilt") {
            id = "blebridge.android.hilt"
            implementationClass = "com.jackson.blebridge.buildlogic.AndroidHiltConventionPlugin"
        }
        register("bleBridgeFeature") {
            id = "blebridge.feature"
            implementationClass = "com.jackson.blebridge.buildlogic.FeatureConventionPlugin"
        }
        register("bleBridgeAndroidApplication") {
            id = "blebridge.android.application"
            implementationClass = "com.jackson.blebridge.buildlogic.AndroidApplicationConventionPlugin"
        }
    }
}
