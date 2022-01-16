package com.skiiyis.study.launcher

import android.os.Handler
import android.os.Looper

class MainThreadTaskTrigger : LaunchTaskTrigger {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }
}