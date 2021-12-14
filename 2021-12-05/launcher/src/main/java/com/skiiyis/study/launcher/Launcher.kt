package com.skiiyis.study.launcher

import android.app.Application
import android.os.Looper

class Launcher private constructor(val ctx: Application) {
    private val tasks = mutableListOf<LaunchTask>()

    companion object {
        fun getInstance(ctx: Application): Launcher {
            return Launcher(ctx)
        }
    }

    fun addTask(task: LaunchTask) {
        checkUiThread()
        if (task.dependOn().isNotEmpty()) return
        tasks.add(task)
    }

    fun execute() {
        //FIXME curProcessName
        //1、筛选当前进程下 task；2、匹配 scene 执行任
        tasks.filter { task -> task.targetProcess().filter { it == "curProcessName" }.isNotEmpty() }
            .forEach {
                getLaunchScene(it.scene()).execute(it)
            }
    }

    fun getLaunchScene(sceneName: String): LaunchScene {

    }

    private fun checkUiThread() {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            throw IllegalStateException("addTask 需要在主线程执行")
        }
    }
}