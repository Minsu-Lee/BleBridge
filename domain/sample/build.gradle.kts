plugins {
    id("blebridge.kotlin.jvm")
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.javax.inject)
}
