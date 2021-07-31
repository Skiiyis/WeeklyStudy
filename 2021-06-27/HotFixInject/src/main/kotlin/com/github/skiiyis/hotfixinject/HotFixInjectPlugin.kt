package com.github.skiiyis.hotfixinject

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class HotFixInjectPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appExtension = project.properties["android"] as AppExtension
        appExtension.registerTransform(
            HotFixInjectTransform(appExtension)
        )
    }
}