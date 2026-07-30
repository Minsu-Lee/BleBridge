plugins {
    id("blebridge.feature")
}

android {
    namespace = "com.jackson.blebridge.feature.sample"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:sample"))
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.androidx.paging.testing)
}
