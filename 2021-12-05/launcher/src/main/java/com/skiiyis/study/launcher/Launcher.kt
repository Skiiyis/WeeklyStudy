package com.skiiyis.study.launcher

class Launcher private constructor() {

    private val taskTriggers = HashMap<String, ILaunchTaskTrigger>()
    private val tasks = mutableSetOf<LaunchTask>()
    private val launchTransactionGenerators = HashMap<String, () -> ILaunchTransaction>()

    companion object {
        val instance by lazy { Launcher() }
    }

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

    fun registerLaunchTransactionGenerator(transactionName: String, launchTransactionGenerator: () -> ILaunchTransaction) {
        launchTransactionGenerators[transactionName] = launchTransactionGenerator
    }

    fun beginTransaction(transactionName: String): ILaunchTransaction? {
        val transaction = launchTransactionGenerators[transactionName]?.invoke() ?: return null
        return (LauncherHooks.generateLaunchTransactionHook?.invoke(transactionName, transaction) ?: transaction).also { t->
            tasks.filter { it.transactionName() == transactionName }
                .forEach { t.addTask(it) }
        }
    }
}