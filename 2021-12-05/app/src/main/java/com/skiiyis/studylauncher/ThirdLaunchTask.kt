package com.skiiyis.studylauncher

import com.skiiyis.study.launcher.LaunchAt

@LaunchAt(name = "third", dependOn = ["second"])
class ThirdLaunchTask : Runnable {
    override fun run() {
        println("I'm a third task")
    }
}