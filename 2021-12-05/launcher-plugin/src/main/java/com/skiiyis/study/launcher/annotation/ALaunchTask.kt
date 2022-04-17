package com.skiiyis.study.launcher.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ALaunchTask(
    val transactionName: String,
    val name: String,
    val targetProcess: Array<String>,
    val taskType: String,
    val order: Int,
    val dependOn: Array<String>,
)