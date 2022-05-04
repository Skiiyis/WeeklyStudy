package com.skiiyis.study.launcher.plugin

import com.android.build.gradle.AppExtension
import com.skiiyis.study.launcher.plugin.collect.LauncherAnnotationCollectTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class LauncherPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val appExtension = project.properties["android"] as AppExtension
        appExtension.registerTransform(
            LauncherAnnotationCollectTransform(project),
            Collections.EMPTY_LIST
        )
        project.afterEvaluate {
        }
    }
}