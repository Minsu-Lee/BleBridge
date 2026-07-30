package com.jackson.blebridge.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        pluginManager.apply("blebridge.android.compose")
        pluginManager.apply("blebridge.android.hilt")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        dependencies {
            "implementation"(project(":core:common"))
            "implementation"(project(":core:designsystem"))
            "implementation"(project(":core:ui"))
            "implementation"(project(":core:mvi"))
            "implementation"(project(":domain"))
            "implementation"(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
            "implementation"(libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
            "implementation"(libs.findLibrary("androidx-navigation-compose").get())
            "implementation"(libs.findLibrary("androidx-hilt-navigation-compose").get())
            "implementation"(libs.findLibrary("kotlinx-coroutines-android").get())
            "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            "testImplementation"(libs.findLibrary("turbine").get())
            "lintChecks"(project(":lint:designsystem"))
        }

        extensions.configure<LibraryExtension> {
            lint {
                abortOnError = true
                baseline = file("lint-baseline.xml")
            }
        }
    }
}
