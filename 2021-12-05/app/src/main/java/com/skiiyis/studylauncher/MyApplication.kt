package com.skiiyis.studylauncher

import android.app.Application
import com.skiiyis.study.launcher.annotation.LauncherInitPlugin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LauncherInitPlugin.init()
/*
        Launcher.registerTaskTrigger("taskType", BackgroundTaskTrigger())
        Launcher.registerLaunchTransactionGenerator("cold", ColdLaunchTransaction::class.java)

        // 插件生成代码
        Launcher.addTask(LaunchTask.Builder(SecondLaunchTask()).name("second").taskType("background").build())
        Launcher.addTask(LaunchTask.Builder(SecondLaunchTask()).name("second").order(Int.MAX_VALUE).order(1).taskType("background").dependOn(Launcher.requireTask("second")).dependOn(Launcher.requireTask("third")).build())
        Launcher.beginTransaction(ColdLaunchTransaction.NAME)?.commit()*/
    }
}