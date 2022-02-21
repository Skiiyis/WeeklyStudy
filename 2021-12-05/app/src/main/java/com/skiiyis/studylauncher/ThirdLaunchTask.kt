package com.skiiyis.studylauncher

import android.util.Log

class ThirdLaunchTask(val content: String) : Runnable {
    override fun run() {
        Log.e("LaunchTask", "I'm a ${content} task")
    }
}