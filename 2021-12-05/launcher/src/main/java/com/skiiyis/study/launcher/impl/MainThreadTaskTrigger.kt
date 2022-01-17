package com.skiiyis.study.launcher.impl

import android.os.Handler
import android.os.Looper
import com.skiiyis.study.launcher.ILaunchTaskTrigger

class MainThreadTaskTrigger : ILaunchTaskTrigger {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }
}