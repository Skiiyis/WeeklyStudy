package com.skiiyis.study.launcher.plugin.annotationprocessor

object LaunchTransactionAnnotationProcessor : AnnotationProcessor {

    private val taskTransactionMetaDatas = mutableListOf<LaunchTransactionMetaData>()

    override fun collect(className: String, annotationValues: List<Any>) {
        taskTransactionMetaDatas.add(
            LaunchTransactionMetaData(
                className = className,
                name = annotationValues[0].toString()
            )
        )
    }

    override fun generateCode() {

    }

    data class LaunchTransactionMetaData(
        val className: String,
        val name: String
    )
}