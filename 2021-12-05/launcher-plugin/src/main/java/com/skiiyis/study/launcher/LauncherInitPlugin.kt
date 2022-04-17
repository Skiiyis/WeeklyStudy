package com.skiiyis.study.launcher

object LauncherInitPlugin {
    fun init() {
        Launcher.instance.also {
            it.registerTaskTrigger("background", BackgroundTaskTrigger())
            it.registerLaunchTransactionGenerator("cold") { LaunchTransaction(it) }
        }

        // 插件生成代码
        val aidlTask = LaunchTask.Builder(SecondLaunchTask())
            .transactionName("cold")
            .name("aidl")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(4)
            .build()

        val compileTask = LaunchTask.Builder(ThirdLaunchTask())
            .transactionName("cold")
            .name("compile")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(5)
            .build()

        Launcher.instance.addTask(aidlTask)
        Launcher.instance.addTask(compileTask)

    }
}