package com.skiiyis.study.launcher.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ALaunchTransaction(
    val name: String
)