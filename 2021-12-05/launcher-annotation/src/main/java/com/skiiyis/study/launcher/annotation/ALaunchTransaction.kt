package com.skiiyis.study.launcher.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ALaunchTransaction(
    val name: String
)