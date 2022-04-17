package com.skiiyis.studylauncher

import android.util.Log
import com.skiiyis.study.launcher.annotation.ALaunchTask

@ALaunchTask(
    transactionName = ColdLaunchTransaction.NAME,
    name = "third",
    targetProcess = ["main"],
    taskType = "background",
    order = 1,
    dependOn = []
)
class ThirdLaunchTask : Runnable {
    override fun run() {
        Log.e("LaunchTask", "I'm third task")
    }
}