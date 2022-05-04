package com.skiiyis.study.launcher.plugin.annotationprocessor

object LaunchTaskTriggerAnnotationProcessor : AnnotationProcessor {

    private val taskTriggerMetaDatas = mutableListOf<LaunchTaskTriggerMetaData>()

    override fun collect(className: String, annotationValues: List<Any>) {
        taskTriggerMetaDatas.add(
            LaunchTaskTriggerMetaData(
                className = className,
                name = annotationValues[0].toString()
            )
        )
    }

    override fun generateCode() {

    }

    data class LaunchTaskTriggerMetaData(
        val className: String,
        val name: String
    )
}