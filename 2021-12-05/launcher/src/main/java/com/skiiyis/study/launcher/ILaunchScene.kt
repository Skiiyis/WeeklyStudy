package com.skiiyis.study.launcher

interface ILaunchScene {
    fun addTask(task: LaunchTask)
    fun execute()
}