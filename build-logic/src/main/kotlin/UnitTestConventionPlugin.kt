package com.jackson.blebridge.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class UnitTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }

        dependencies {
            "testImplementation"(libs.findLibrary("junit-jupiter").get())
            "testImplementation"(libs.findLibrary("kotlinx-coroutines-test").get())
            "testImplementation"(libs.findLibrary("mockk").get())
            "testRuntimeOnly"(libs.findLibrary("junit-jupiter-engine").get())
            "testRuntimeOnly"(libs.findLibrary("junit-platform-launcher").get())
        }
    }
}
