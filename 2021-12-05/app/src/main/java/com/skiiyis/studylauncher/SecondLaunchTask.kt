package com.skiiyis.studylauncher

import com.skiiyis.study.launcher.LaunchAt

@LaunchAt(name = "second")
class SecondLaunchTask : Runnable {
    override fun run() {
        println("I'm a launch task")
    }
}