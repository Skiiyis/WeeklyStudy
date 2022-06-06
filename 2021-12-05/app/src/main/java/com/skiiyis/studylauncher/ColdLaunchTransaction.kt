package com.skiiyis.studylauncher

import com.skiiyis.study.launcher.annotation.ALaunchTransaction
import com.skiiyis.study.launcher.impl.LaunchTransaction

@ALaunchTransaction(ColdLaunchTransaction.NAME)
class ColdLaunchTransaction : LaunchTransaction() {
    companion object {
        const val NAME = "cold"
    }
}