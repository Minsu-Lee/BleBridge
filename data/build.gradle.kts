plugins {
    id("blebridge.android.library")
    id("blebridge.android.hilt")
}

android {
    namespace = "com.jackson.blebridge.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain"))
    implementation(libs.kotlinx.coroutines.android)
}
