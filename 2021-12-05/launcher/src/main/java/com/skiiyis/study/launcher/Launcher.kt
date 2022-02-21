package com.skiiyis.study.launcher

class Launcher private constructor() {

    private val taskTriggers = HashMap<String, ILaunchTaskTrigger>()
    private val tasks = mutableSetOf<LaunchTask>()
    private val launchSceneGenerators = HashMap<String, () -> ILaunchScene>()

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
        tasks.add(task)
    }

    fun registerLaunchScene(sceneName: String, launchSceneGenerator: () -> ILaunchScene) {
        launchSceneGenerators[sceneName] = launchSceneGenerator
    }

    fun generateLaunchScene(sceneName: String): ILaunchScene? {
        val scene = launchSceneGenerators[sceneName]?.invoke() ?: return null
        return (LauncherHooks.generateLaunchSceneHook?.invoke(sceneName, scene) ?: scene).also {scene->
            tasks.filter { it.scene() == sceneName }.forEach { scene.addTask(it) }
        }
    }
}