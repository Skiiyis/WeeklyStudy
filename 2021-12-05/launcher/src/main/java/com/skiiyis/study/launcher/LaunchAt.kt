package com.skiiyis.study.launcher

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LaunchAt(
    val name: String,
    val taskType: String = "background",
    val dependOn: Array<String> = [],
    val beDepended: Array<String> = [],
    val targetProcess: Array<String> = ["*"],
    val order: Int = 1000
)