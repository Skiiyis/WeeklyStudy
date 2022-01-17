package com.skiiyis.study.launcher.impl

import com.skiiyis.study.launcher.ILaunchScene
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import java.util.concurrent.Executors

class LaunchScene(private val launcher: Launcher) : ILaunchScene {

    private val taskStatus = HashMap<LaunchTask, String>()
    private val executor = Executors.newSingleThreadExecutor()

    private fun execute(task: LaunchTask) {
        //[C,A]
        taskStatus[task] = "EXECUTING"
        val trigger = launcher.getTaskTrigger(task.taskType())
            ?: throw IllegalArgumentException("not found match trigger")
        // TODO 校验循环依赖
        if (task.dependOn()?.none { taskStatus[task] != "DONE" } == true) {
            // 没有依赖的情况下执行task本身
            trigger.execute {
                task.run()
                executor.execute {
                    taskStatus[task] = "DONE"
                    // 执行完本身后看一下上面有没有被依赖方，执行被依赖方task
                    // A -> B, C -> B , AC
                    task.beDepended()?.forEach { beDependency ->
                        if (beDependency.dependOn()?.none { taskStatus[task] != "DONE" } == true) {
                            execute(beDependency)
                        }
                    }
                }
            }
        } else {
            // 有依赖的情况下先去执行所有的依赖task
            task.dependOn()?.forEach {
                if (taskStatus[task] != "EXECUTING" && taskStatus[task] != "DONE") {
                    execute(it)
                }
            }
        }
    }

    override fun addTask(task: LaunchTask) {
        if (launcher.getLaunchScene(task.scene()) != this) {
            throw IllegalAccessException("Task scene not match!")
        }
        val status = taskStatus[task]
        if (status != "DONE") {
            return
        }
    }

    override fun execute() {
        executor.execute {
            taskStatus.filter { it.value != "DONE" }
                .keys
                .sortedBy { it.order() }
                .forEach {
                    execute(it)
                }
        }
    }
}