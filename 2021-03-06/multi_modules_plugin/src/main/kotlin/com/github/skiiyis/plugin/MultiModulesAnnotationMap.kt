package com.github.skiiyis.plugin

object MultiModulesAnnotationMap {
    val cache: HashMap<String, String> = hashMapOf()
    var `implement`: String? = null
    var `interface`: String? = null
        set(value) {
            `implement`?.also {
                cache[value!!] = it
            }
            `implement` = null
            field = null
        }
}