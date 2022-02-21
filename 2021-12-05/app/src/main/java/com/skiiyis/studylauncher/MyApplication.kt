package com.skiiyis.studylauncher

import android.app.Application
import com.skiiyis.study.launcher.impl.BackgroundTaskTrigger
import com.skiiyis.study.launcher.impl.LaunchScene
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Launcher.instance.also {
            it.registerTaskTrigger("background", BackgroundTaskTrigger())
            it.registerLaunchScene("cold") { LaunchScene(it) }
        }

        // 插件生成代码
        val aidlTask = LaunchTask.Builder(ThirdLaunchTask("aidl"))
            .scene("cold")
            .name("aidl")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(4)
            .build()

        val compileTask = LaunchTask.Builder(ThirdLaunchTask("compile"))
            .scene("cold")
            .name("compile")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(5)
            .build()

        val libTask = LaunchTask.Builder(ThirdLaunchTask("lib"))
            .scene("cold")
            .name("lib")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(6)
            .build()

        val buildTask = LaunchTask.Builder(ThirdLaunchTask("build"))
            .scene("cold")
            .name("build")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(2)
            .dependOn(aidlTask)
            .dependOn(compileTask)
            .build()

        val resourceTask = LaunchTask.Builder(ThirdLaunchTask("resource"))
            .scene("cold")
            .name("resource")
            .targetProcess(listOf("main"))
            .taskType("background")
            .dependOn(compileTask)
            .dependOn(libTask)
            .order(3)
            .build()

        val assembleTask = LaunchTask.Builder(SecondLaunchTask())
            .scene("cold")
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

        Launcher.instance.generateLaunchScene("cold")?.also {
            it.addTask(assembleTask)
            it.addTask(buildTask)
            it.execute()
        }
    }
}