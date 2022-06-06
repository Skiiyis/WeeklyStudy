package com.skiiyis.study.launcher.impl

import com.skiiyis.study.launcher.ILaunchTransaction
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.util.TaskDependencyChecker
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class LaunchTransaction : ILaunchTransaction {

    companion object {
        const val STATUS_WAIT = 0
        const val STATUS_EXECUTING = -1
        const val STATUS_DONE = -2
    }

    protected val tasks = mutableSetOf<LaunchTask>()
    protected var isExecuting = false
    protected val executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun addTask(task: LaunchTask) {
        if (isExecuting) {
            throw IllegalAccessException("Transaction already executing, could not add tasks")
        }
        tasks.add(task)
    }

    private fun executeTasks(taskWithStatus: MutableMap<LaunchTask, Int>) {
        executor.execute {
            taskWithStatus.filter {
                it.value == STATUS_WAIT
            }.keys.sortedBy { it.order() }.forEach {
                taskWithStatus[it] = STATUS_EXECUTING
                Launcher.getTaskTrigger(it.taskType())!!.execute {
                    it.run()
                    executor.execute {
                        taskWithStatus[it] = STATUS_DONE
                        it.beDepended()?.forEach {
                            if (it.dependOn()!!.all { taskWithStatus[it] == STATUS_DONE }) {
                                taskWithStatus[it] = STATUS_WAIT
                            }
                        }
                        executeTasks(taskWithStatus)
                    }
                }
            }
        }
    }

    override fun commit() {
        if (isExecuting) return
        isExecuting = true
        executor.execute {
            val taskWithStatus = TaskDependencyChecker.checkAndMergeTasks(tasks)
            taskWithStatus.keys.map { it.taskType() }.forEach {
                Launcher.getTaskTrigger(it)
                    ?: throw IllegalArgumentException("Not found match trigger")
            }
            executeTasks(taskWithStatus)
        }
    }
}