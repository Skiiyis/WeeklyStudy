package com.skiiyis.studylauncher

import com.skiiyis.study.launcher.annotation.ALaunchTaskTrigger
import com.skiiyis.study.launcher.impl.BackgroundTaskTrigger

@ALaunchTaskTrigger(name = "background")
class ColdLaunchBackgroundTaskTrigger : BackgroundTaskTrigger()