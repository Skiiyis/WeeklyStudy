package com.skiiyis.study.launcher.plugin.annotationprocessor

interface AnnotationProcessor {

    fun collect(className: String, annotationValues: List<Any>)
    fun generateCode()
}