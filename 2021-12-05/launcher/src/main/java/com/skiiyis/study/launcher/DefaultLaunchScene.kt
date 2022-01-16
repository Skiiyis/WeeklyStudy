package com.skiiyis.study.launcher

class DefaultLaunchScene(private val launcher: Launcher) : LaunchScene {

    private val taskStatus = HashMap<LaunchTask, String>()

    private fun execute(task: LaunchTask) {
        val trigger = launcher.getTaskTrigger(task.taskType())
            ?: throw IllegalArgumentException("not found match trigger")
        // TODO 校验循环依赖
        if (task.dependOn().none { taskStatus[task] != "DONE" }) {
            // 没有依赖的情况下执行task本身
            trigger.execute {
                task.run()
                // 执行完本身后看一下上面有没有被依赖方，执行被依赖方task
                val beDependencies = task.beDepended() ?: return@execute
                synchronized(beDependencies) {
                    taskStatus[task] = "DONE"
                    if (beDependencies.dependOn().none { taskStatus[task] != "DONE" }) {
                        execute(beDependencies)
                    }
                }
            }
        } else {
            // 有依赖的情况下先去执行所有的依赖task
            task.dependOn().forEach {
                execute(it)
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
        taskStatus[task] = "NOT_START"
    }

    override fun execute() {
        taskStatus.filter { it.value != "DONE" }
            .keys
            .sortedBy { it.order() }
            .forEach {
                execute(it)
            }
    }
}