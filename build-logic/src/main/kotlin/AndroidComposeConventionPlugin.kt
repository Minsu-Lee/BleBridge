package com.jackson.blebridge.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("blebridge.android.library")
        configureCompose()
    }
}
