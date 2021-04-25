package com.skiiyis.libprocessor

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Args(val required: Boolean = false)