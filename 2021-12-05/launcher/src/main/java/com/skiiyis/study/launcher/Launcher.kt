package com.skiiyis.study.launcher

import com.skiiyis.study.launcher.impl.LaunchTransaction

object Launcher {

    private val taskTriggers = HashMap<String, ILaunchTaskTrigger>()
    private val tasks = mutableSetOf<LaunchTask>()
    private val launchTransactionGenerators = HashMap<String, Class<out LaunchTransaction>>()

    fun registerTaskTrigger(taskType: String, trigger: ILaunchTaskTrigger) {
        val t = LauncherHooks.registerTaskTriggerHook?.invoke(taskType, trigger) ?: trigger
        taskTriggers[taskType] = t
    }

    fun getTaskTrigger(taskType: String): ILaunchTaskTrigger? {
        return taskTriggers[taskType]
    }

    fun addTask(task: LaunchTask) {
        tasks.add(task)
    }

    fun requireTask(name: String): LaunchTask {
        return tasks.find { it.name() == name } ?: throw IllegalArgumentException("Could not found task")
    }

    fun registerLaunchTransactionGenerator(
        transactionName: String,
        transactionClass: Class<out LaunchTransaction>
    ) {
        launchTransactionGenerators[transactionName] = transactionClass
    }

    fun beginTransaction(transactionName: String): ILaunchTransaction? {
        val transaction = launchTransactionGenerators[transactionName]?.newInstance() ?: return null
        return (LauncherHooks.generateLaunchTransactionHook?.invoke(transactionName, transaction)
            ?: transaction).also { t ->
            tasks.filter { it.transactionName() == transactionName }
                .forEach { t.addTask(it) }
        }
    }
}