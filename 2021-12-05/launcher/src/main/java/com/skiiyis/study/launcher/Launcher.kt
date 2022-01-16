package com.skiiyis.study.launcher

class Launcher private constructor() {

    private val taskTriggers = HashMap<String, LaunchTaskTrigger>()
    private val launchScenes = HashMap<String, LaunchScene>()

    companion object {
        val instance by lazy { Launcher() }
    }

    fun registerTaskTrigger(taskType: String, trigger: LaunchTaskTrigger) {
        taskTriggers[taskType] = trigger
    }

    fun getTaskTrigger(taskType: String): LaunchTaskTrigger? {
        return taskTriggers[taskType]
    }

    fun addTask(task: LaunchTask) {
        val launchScene = getLaunchScene(task.name())
        checkNotNull(launchScene)
        launchScene.addTask(task)
    }

    fun registerLaunchScene(sceneName: String, launchScene: LaunchScene) {
        launchScenes[sceneName] = launchScene
    }

    fun getLaunchScene(sceneName: String): LaunchScene? {
        return launchScenes[sceneName]
    }
}