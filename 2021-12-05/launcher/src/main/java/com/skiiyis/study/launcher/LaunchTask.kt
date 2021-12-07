package com.skiiyis.study.launcher

interface LaunchTask : Runnable {

    fun name(): String
    fun taskType(): String
    fun dependOn(): List<LaunchTask>
    fun beDepended(): LaunchTask?
    fun targetProcess(): List<String>

}