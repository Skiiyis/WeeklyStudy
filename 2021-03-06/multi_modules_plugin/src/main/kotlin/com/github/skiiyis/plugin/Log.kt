package com.github.skiiyis.plugin

object Log {

    private const val TAG = "MultiModulePlugin"

    fun info(tag: String, any: Any) {
        println("[$TAG]$tag :$any")
    }

    fun error(tag: String, vararg any: Any) {
        println("[$TAG]$tag :$any")
    }
}