package com.skiiyis.study.launcher.plugin.annotationprocessor

import org.objectweb.asm.tree.InsnList

interface AnnotationProcessor {

    fun collect(classInternalName: String, annotationValues: List<Any>)
    fun generateCode(instructions: InsnList)
}