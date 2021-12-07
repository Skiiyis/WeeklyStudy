package com.skiiyis.study.launcher

interface LaunchScene {
    fun execute(task: LaunchTask)
    fun findTaskTrigger(task: LaunchTask): LaunchTaskTrigger
}