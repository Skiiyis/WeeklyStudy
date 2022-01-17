package com.skiiyis.studylauncher

import android.app.Application
import com.skiiyis.study.launcher.impl.BackgroundTaskTrigger
import com.skiiyis.study.launcher.impl.LaunchScene
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.LauncherHooks

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Launcher.instance.also {
            it.registerTaskTrigger("taskType", BackgroundTaskTrigger())
            it.registerLaunchScene("cold", LaunchScene(it))
        }

        // 插件生成代码
        val secondTask = LaunchTask.Builder(SecondLaunchTask())
            .scene("cold")
            .name("second")
            .targetProcess(listOf("main"))
            .taskType("background")
            .build()
        val thirdTask = LaunchTask.Builder(ThirdLaunchTask())
            .scene("cold")
            .name("third")
            .targetProcess(listOf("main"))
            .taskType("background")
            .order(0)
            .dependOn(secondTask)
            .build()

        Launcher.instance.addTask(secondTask)
        Launcher.instance.getLaunchScene("cold")?.also {
            it.addTask(secondTask)
            it.addTask(thirdTask)
            it.execute()
        }
    }
}