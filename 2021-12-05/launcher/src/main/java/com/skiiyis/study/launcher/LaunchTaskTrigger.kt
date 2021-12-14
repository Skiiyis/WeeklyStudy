package com.skiiyis.study.launcher

import java.util.concurrent.Executor

interface LaunchTaskTrigger : Executor{

    override fun execute(command: Runnable?) {
        command?.run()
    }
}