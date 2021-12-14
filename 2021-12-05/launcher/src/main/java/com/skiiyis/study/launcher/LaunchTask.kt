package com.skiiyis.study.launcher

import androidx.annotation.IntDef

interface LaunchTask : Runnable {

    fun name(): String
    fun taskType(): String
    fun dependOn(): List<LaunchTask>
    fun beDepended(): LaunchTask?
    fun targetProcess(): List<String>
    var taskStatus: TaskStatus
    fun scene():String

}

@IntDef(
    TaskStatus.INVALID,
    TaskStatus.START,
    TaskStatus.RUNNING,
    TaskStatus.DONE
)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class TaskStatus {
    companion object {
        const val INVALID = 0
        const val START = 10
        const val RUNNING = 20
        const val DONE = 30
    }
}