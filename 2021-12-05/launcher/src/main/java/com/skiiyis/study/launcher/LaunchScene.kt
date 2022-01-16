package com.skiiyis.study.launcher

interface LaunchScene {
    fun addTask(task: LaunchTask)
    fun execute()
}