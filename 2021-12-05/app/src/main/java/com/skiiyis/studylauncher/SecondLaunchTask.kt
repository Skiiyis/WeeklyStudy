package com.skiiyis.studylauncher

import android.util.Log

class SecondLaunchTask : Runnable {
    override fun run() {
        Log.e("LaunchTask","I'm a launch task")
    }
}