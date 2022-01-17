package com.skiiyis.study.launcher

class Launcher private constructor() {

    private val taskTriggers = HashMap<String, ILaunchTaskTrigger>()
    private val launchScenes = HashMap<String, ILaunchScene>()

    companion object {
        val instance by lazy { Launcher() }
    }

    fun registerTaskTrigger(taskType: String, trigger: ILaunchTaskTrigger) {
        val t = LauncherHooks.registerTaskTriggerHook?.invoke(taskType, trigger) ?: trigger
        taskTriggers[taskType] = t
    }

    fun getTaskTrigger(taskType: String): ILaunchTaskTrigger? {
        return taskTriggers[taskType]
    }

    fun addTask(task: LaunchTask) {
        val t = LauncherHooks.addTaskHook?.invoke(task) ?: task
        val launchScene = getLaunchScene(t.name())
        checkNotNull(launchScene)
        launchScene.addTask(task)
    }

    fun registerLaunchScene(sceneName: String, launchScene: ILaunchScene) {
        val l = LauncherHooks.registerLaunchSceneHook?.invoke(sceneName, launchScene) ?: launchScene
        launchScenes[sceneName] = l
    }

    fun getLaunchScene(sceneName: String): ILaunchScene? {
        return launchScenes[sceneName]
    }
}