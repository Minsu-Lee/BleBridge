plugins {
    id("blebridge.android.compose")
}

android {
    namespace = "com.jackson.blebridge.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
}
