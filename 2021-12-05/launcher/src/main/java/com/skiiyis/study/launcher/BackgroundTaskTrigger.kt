package com.skiiyis.study.launcher

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BackgroundTaskTrigger : LaunchTaskTrigger {

    private val executor: Executor by lazy {
        Executors.newCachedThreadPool()
    }

    override fun execute(command: Runnable) {
        executor.execute(command)
    }
}