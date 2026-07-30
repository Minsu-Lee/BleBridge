plugins {
    id("blebridge.android.library")
    id("blebridge.android.hilt")
}

android {
    namespace = "com.jackson.blebridge.data.sample"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":domain:sample"))
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    testImplementation(libs.okhttp.mockwebserver)
}
