plugins {
    id("blebridge.kotlin.jvm")
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.gson)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
}
