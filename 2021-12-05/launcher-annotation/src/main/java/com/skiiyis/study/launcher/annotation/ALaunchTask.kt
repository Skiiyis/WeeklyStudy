package com.skiiyis.study.launcher.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ALaunchTask(
    val transactionName: String,
    val name: String,
    val targetProcess: Array<String> = [],
    val taskType: String,
    val order: Int = Int.MIN_VALUE,
    val dependOn: Array<String> = [],
)