package com.skiiyis.studylauncher

import android.util.Log
import com.skiiyis.study.launcher.annotation.ALaunchTask

@ALaunchTask(
    transactionName = ColdLaunchTransaction.NAME,
    name = "second",
    targetProcess = ["main"],
    taskType = "background",
    order = 1,
    dependOn = ["third"]
)
class SecondLaunchTask : Runnable {
    override fun run() {
        Log.e("LaunchTask", "I'm a launch task")
    }
}