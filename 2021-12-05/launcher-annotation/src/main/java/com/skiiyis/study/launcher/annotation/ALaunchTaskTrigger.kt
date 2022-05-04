package com.skiiyis.study.launcher.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ALaunchTaskTrigger(
    val name: String
)