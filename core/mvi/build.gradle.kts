plugins {
    id("blebridge.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.jackson.blebridge.core.mvi"
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.timber)
}
