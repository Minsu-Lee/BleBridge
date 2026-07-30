plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)

    testImplementation(libs.android.lint.tests)
    testImplementation(libs.android.lint.api)
    testImplementation(libs.android.lint.checks)
    testImplementation(libs.junit4)
}

tasks.jar {
    manifest {
        attributes["Lint-Registry-v2"] =
            "com.jackson.blebridge.lint.designsystem.DesignSystemIssueRegistry"
    }
}
