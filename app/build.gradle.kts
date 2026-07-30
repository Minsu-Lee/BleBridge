plugins {
    id("blebridge.android.application")
}

android {
    namespace = "com.jackson.blebridge"

    defaultConfig {
        applicationId = "com.jackson.blebridge"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":data"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:main"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    debugImplementation(project(":feature:sample"))
    debugImplementation(project(":data:sample"))
}
