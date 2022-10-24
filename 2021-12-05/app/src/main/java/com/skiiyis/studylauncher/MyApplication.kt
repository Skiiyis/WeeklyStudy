package com.skiiyis.studylauncher

import android.app.Application
import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.LauncherInitPlugin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LauncherInitPlugin.init()
        Launcher.beginTransaction(ColdLaunchTransaction.NAME)?.commit()

        /*Launcher.registerLaunchTransactionGenerator("cold", ColdLaunchTransaction::class.java)
        Launcher.registerTaskTrigger("background", ColdLaunchBackgroundTaskTrigger())

        // 插件生成代码
        Launcher.addTask(LaunchTask.Builder(ThirdLaunchTask()).name("third").order(1).taskType("background").targetProcess("main").transactionName("cold").build())
        Launcher.addTask(LaunchTask.Builder(SecondLaunchTask()).name("second").order(1).taskType("background").targetProcess("main").transactionName("cold").dependOn(Launcher.requireTask("third")).build())
        Launcher.beginTransaction(ColdLaunchTransaction.NAME)?.commit()*/
    }
}