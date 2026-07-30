package com.jackson.blebridge.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        pluginManager.apply("blebridge.test.unit")

        dependencies {
            "androidTestImplementation"(libs.findLibrary("androidx-junit").get())
            "androidTestImplementation"(libs.findLibrary("androidx-test-runner").get())
            "androidTestImplementation"(libs.findLibrary("androidx-test-rules").get())
            "androidTestImplementation"(libs.findLibrary("androidx-espresso-core").get())
        }
    }
}
