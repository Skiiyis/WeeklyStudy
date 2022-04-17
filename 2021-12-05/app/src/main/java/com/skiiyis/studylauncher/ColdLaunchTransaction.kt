package com.skiiyis.studylauncher

import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.annotation.ALaunchTransaction
import com.skiiyis.study.launcher.impl.LaunchTransaction

@ALaunchTransaction(ColdLaunchTransaction.NAME)
class ColdLaunchTransaction(private val launcher: Launcher) : LaunchTransaction(launcher) {
    companion object {
        const val NAME = "cold"
    }
}