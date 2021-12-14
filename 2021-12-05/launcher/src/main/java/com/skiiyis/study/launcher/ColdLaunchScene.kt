package com.skiiyis.study.launcher

class ColdLaunchScene : LaunchScene {

    private val taskStatus = HashMap<LaunchTask, String>()

    override fun execute(task: LaunchTask) {
        // TODO 校验循环依赖
        // TODO beDepended 链下的 task 需要 process、taskType、scene 场景一致？好像没必要，但如何处理 findTaskTrigger 问题
        if (task.dependOn().none { taskStatus[task] != "DONE" }) {
            // 没有依赖的情况下执行task本身
            findTaskTrigger(task).execute {
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

    override fun findTaskTrigger(task: LaunchTask): LaunchTaskTrigger {

    }
}