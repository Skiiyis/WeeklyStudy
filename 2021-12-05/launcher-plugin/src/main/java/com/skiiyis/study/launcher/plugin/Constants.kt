package com.skiiyis.study.launcher.plugin

import com.skiiyis.study.launcher.ILaunchTaskTrigger
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import org.objectweb.asm.Type

object Constants {
    val launcherDesc = Type.getDescriptor(Launcher::class.java)
    val launchTaskBuilderDesc = Type.getDescriptor(LaunchTask.Builder::class.java)


    val launchTaskBuilderBuildMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("build") })
    val launchTaskBuilderDepenOnMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("dependOn") })
    val launchTaskTaskTypeDepenOnMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("taskType") })
    val launchTaskTransactionNameMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("transactionName") })
    val launchTaskTragetProcessMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("targetProcess") })
    val launchTaskOrderMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("order") })
    val launchTaskNameDepenOnMethodDesc =
        Type.getMethodDescriptor(LaunchTask.Builder::class.java.methods.find { it.name.contains("name") })

    val launcherRegisterTaskTriggerMethodDesc =
        Type.getMethodDescriptor(Launcher::class.java.methods.find { it.name.contains("registerTaskTrigger") })
    val launcherRegisterLaunchTransactionGeneratorMethodDesc =
        Type.getMethodDescriptor(Launcher::class.java.methods.find { it.name.contains("registerLaunchTransactionGenerator") })
    val launcherAddTaskMethodDesc =
        Type.getMethodDescriptor(Launcher::class.java.methods.find { it.name.contains("addTask") })
    val launcherRequireTaskMethodDesc =
        Type.getMethodDescriptor(Launcher::class.java.methods.find { it.name.contains("requireTask") })
    val iLauncherTaskTriggerDesc = Type.getDescriptor(ILaunchTaskTrigger::class.java)

}