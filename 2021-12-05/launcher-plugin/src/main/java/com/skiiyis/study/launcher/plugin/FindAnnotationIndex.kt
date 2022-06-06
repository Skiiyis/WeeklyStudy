package com.skiiyis.study.launcher.plugin

object FindAnnotationIndex {

    fun findValueIndex(annotationValues: List<Any>, target: String): Int {
        return annotationValues.indexOfFirst {
            it.toString() == target
        }.let {
            if (it == -1) throw IllegalArgumentException("could not found annotation")
            it + 1
        }
    }
}