package com.skiiyis.study.launcher.impl

import com.skiiyis.study.launcher.ILaunchTaskTrigger
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class BackgroundTaskTrigger : ILaunchTaskTrigger {

    private val executor: Executor by lazy {
        Executors.newCachedThreadPool()
    }

    override fun execute(command: Runnable) {
        executor.execute(command)
    }
}