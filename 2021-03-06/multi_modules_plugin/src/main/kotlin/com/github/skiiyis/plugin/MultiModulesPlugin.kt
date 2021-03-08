package com.github.skiiyis.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class MultiModulesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appExtension = project.properties["android"] as AppExtension
        appExtension.registerTransform(
            MultiModulesKVTransform(project),
            Collections.EMPTY_LIST
        )
        appExtension.registerTransform(
            MultiModulesCodeGenTransform(project),
            listOf(KV_NAME)
        )
        project.afterEvaluate {
            Log.info("after", "have some log")
        }
    }
}