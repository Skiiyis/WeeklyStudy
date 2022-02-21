package com.skiiyis.study.launcher

object LauncherHooks {
    var addTaskHook: ((LaunchTask) -> LaunchTask)? = null
    var registerTaskTriggerHook: ((String, ILaunchTaskTrigger) -> ILaunchTaskTrigger)? = null
    var generateLaunchSceneHook: ((String, ILaunchScene) -> ILaunchScene)? = null
    var beforeTaskRunHook: ((LaunchTask) -> Unit)? = null
    var afterTaskRunHook: ((LaunchTask) -> Unit)? = null
}