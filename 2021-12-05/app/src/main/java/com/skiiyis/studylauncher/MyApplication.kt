package com.skiiyis.studylauncher

import android.app.Application
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.impl.BackgroundTaskTrigger
import com.skiiyis.study.launcher.impl.LaunchTransaction

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Launcher.instance.also {
            it.registerTaskTrigger("background", BackgroundTaskTrigger())
            it.registerLaunchTransactionGenerator("cold") { LaunchTransaction(it) }
        }

        // 插件生成代码
        val aidlTask = LaunchTask.Builder(ThirdLaunchTask("aidl"))
            .transactionName("cold")
            .name("aidl")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(4)
            .build()

        val compileTask = LaunchTask.Builder(ThirdLaunchTask("compile"))
            .transactionName("cold")
            .name("compile")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(5)
            .build()

        val libTask = LaunchTask.Builder(ThirdLaunchTask("lib"))
            .transactionName("cold")
            .name("lib")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(6)
            .build()

        val buildTask = LaunchTask.Builder(ThirdLaunchTask("build"))
            .transactionName("cold")
            .name("build")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(2)
            .dependOn(aidlTask)
            .dependOn(compileTask)
            .build()

        val resourceTask = LaunchTask.Builder(ThirdLaunchTask("resource"))
            .transactionName("cold")
            .name("resource")
            .targetProcess(listOf("main"))
            .taskType("background")
            .dependOn(compileTask)
            .dependOn(libTask)
            .order(3)
            .build()

        val assembleTask = LaunchTask.Builder(SecondLaunchTask())
            .transactionName("cold")
            .name("assemble")
            .targetProcess(listOf("main"))
            .taskType("background")
            .dependOn(buildTask)
            .order(1)
            .dependOn(resourceTask)
            .build()

//
//        Launcher.instance.addTask(secondTask)
//        Launcher.instance.addTask(thirdTask)

        Launcher.instance.beginTransaction("cold")?.also {
            it.addTask(assembleTask)
            it.addTask(buildTask)
            it.commit()
        }
    }
}