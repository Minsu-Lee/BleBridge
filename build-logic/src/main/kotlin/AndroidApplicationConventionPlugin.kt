package com.jackson.blebridge.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("blebridge.android.hilt")
        pluginManager.apply("blebridge.test.android")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        configureCompose()

        configure<ApplicationExtension> {
            compileSdk = 37
            defaultConfig {
                minSdk = 30
                targetSdk = 36
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}
