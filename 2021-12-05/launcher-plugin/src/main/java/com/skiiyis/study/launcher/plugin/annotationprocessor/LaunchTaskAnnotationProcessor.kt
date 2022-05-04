package com.skiiyis.study.launcher.plugin.annotationprocessor

object LaunchTaskAnnotationProcessor : AnnotationProcessor {

    private val taskMetaDatas = mutableListOf<LaunchTaskMetaData>()

    override fun collect(className: String, annotationValues: List<Any>) {
        taskMetaDatas.add(
            LaunchTaskMetaData(
                className = className,
                transactionName = annotationValues[0].toString(),
                name = annotationValues[0].toString(),
                targetProcess = annotationValues[0] as Array<String>,
                taskType = annotationValues[0].toString(),
                order = annotationValues[0] as Int,
                dependOn = annotationValues[0] as Array<String>
            )
        )
    }

    override fun generateCode() {

    }

    data class LaunchTaskMetaData(
        val className: String,

        val transactionName: String,
        val name: String,
        val targetProcess: Array<String>,
        val taskType: String,
        val order: Int,
        val dependOn: Array<String>,
    )
}