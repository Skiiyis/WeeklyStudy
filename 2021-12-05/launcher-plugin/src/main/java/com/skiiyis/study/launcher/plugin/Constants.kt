package com.skiiyis.study.launcher.plugin

import com.skiiyis.study.launcher.ILaunchTaskTrigger
import com.skiiyis.study.launcher.LaunchTask
import com.skiiyis.study.launcher.Launcher
import org.objectweb.asm.Type
import kotlin.reflect.jvm.javaMethod

object Constants {
    // Lcom/skiiyis/study/launcher/Launcher;
    val launcherDesc = Type.getDescriptor(Launcher::class.java)

    // com/skiiyis/study/launcher/Launcher
    val launcherInternalName = Type.getInternalName(Launcher::class.java)

    val launchTaskBuilderDesc = Type.getDescriptor(LaunchTask.Builder::class.java)


    val launchTaskBuilderBuildMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::build.name)
    val launchTaskBuilderDependOnMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::dependOn.name)
    val launchTaskTaskTypeDependOnMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::taskType.name)
    val launchTaskTransactionNameMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::transactionName.name)
    val launchTaskTargetProcessMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::targetProcess.name)
    val launchTaskOrderMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::order.name)
    val launchTaskNameDependOnMethodDesc =
        getMethodDescriptor(LaunchTask.Builder::class.java, LaunchTask.Builder::name.name)

    val launcherRegisterTaskTriggerMethodDesc =
        getMethodDescriptor(Launcher::class.java, Launcher::registerTaskTrigger.name)

    val launcherRegisterLaunchTransactionGeneratorMethodDesc =
        getMethodDescriptor(Launcher::class.java, Launcher::registerLaunchTransactionGenerator.name)
    val launcherAddTaskMethodDesc = getMethodDescriptor(Launcher::class.java, Launcher::addTask.name)
    val launcherRequireTaskMethodDesc = getMethodDescriptor(Launcher::class.java, Launcher::requireTask.name)

    val iLauncherTaskTriggerDesc = Type.getDescriptor(ILaunchTaskTrigger::class.java)

    // com/skiiyis/study/launcher/ILaunchTaskTrigger
    val iLauncherTaskTriggerInternalName = Type.getInternalName(ILaunchTaskTrigger::class.java)

    private fun getMethodDescriptor(clazz: Class<*>, methodName: String): String {
        return Type.getMethodDescriptor(clazz.methods.find { it.name == methodName })
    }
}