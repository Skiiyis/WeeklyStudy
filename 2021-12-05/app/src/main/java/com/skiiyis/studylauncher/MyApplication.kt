package com.skiiyis.studylauncher

import android.app.Application
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.LauncherInitPlugin
import com.skiiyis.study.launcher.impl.BackgroundTaskTrigger
import com.skiiyis.study.launcher.impl.LaunchTransaction

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LauncherInitPlugin.init()
        Launcher.instance.beginTransaction(ColdLaunchTransaction.NAME)?.commit()

    }
}