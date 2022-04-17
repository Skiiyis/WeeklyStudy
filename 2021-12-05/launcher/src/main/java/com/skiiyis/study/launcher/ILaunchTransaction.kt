package com.skiiyis.study.launcher

interface ILaunchTransaction {
    fun addTask(task: LaunchTask)
    fun commit()
}