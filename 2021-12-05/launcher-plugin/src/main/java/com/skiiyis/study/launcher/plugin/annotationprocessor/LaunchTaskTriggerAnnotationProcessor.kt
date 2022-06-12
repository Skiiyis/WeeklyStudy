package com.skiiyis.study.launcher.plugin.annotationprocessor

import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.annotation.ALaunchTaskTrigger
import com.skiiyis.study.launcher.plugin.Constants.iLauncherTaskTriggerInternalName
import com.skiiyis.study.launcher.plugin.Constants.launcherDesc
import com.skiiyis.study.launcher.plugin.Constants.launcherInternalName
import com.skiiyis.study.launcher.plugin.Constants.launcherRegisterTaskTriggerMethodDesc
import com.skiiyis.study.launcher.plugin.FindAnnotationIndex
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

object LaunchTaskTriggerAnnotationProcessor : AnnotationProcessor {

    private val taskTriggerMetaDatas = mutableListOf<LaunchTaskTriggerMetaData>()

    override fun collect(classInternalName: String, annotationValues: List<Any>) {
        taskTriggerMetaDatas.add(
            LaunchTaskTriggerMetaData(
                classInternalName = classInternalName,
                name = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTaskTrigger::name.name
                        )
                ].toString()
            )
        )
    }


    /**
    GETSTATIC com/skiiyis/study/launcher/Launcher.INSTANCE : Lcom/skiiyis/study/launcher/Launcher;
    LDC "taskType"
    NEW com/skiiyis/study/launcher/impl/BackgroundTaskTrigger
    DUP
    INVOKESPECIAL com/skiiyis/study/launcher/impl/BackgroundTaskTrigger.<init> ()V
    CHECKCAST com/skiiyis/study/launcher/ILaunchTaskTrigger
    INVOKEVIRTUAL com/skiiyis/study/launcher/Launcher.registerTaskTrigger (Ljava/lang/String;Lcom/skiiyis/study/launcher/ILaunchTaskTrigger;)V
     */
    override fun generateCode(instructions: InsnList) {
        taskTriggerMetaDatas.forEach {
            val newInstructions = listOf(
                FieldInsnNode(Opcodes.GETSTATIC, launcherInternalName, "INSTANCE", launcherDesc),
                LdcInsnNode(it.name),
                TypeInsnNode(Opcodes.NEW, it.classInternalName),
                InsnNode(Opcodes.DUP),
                MethodInsnNode(Opcodes.INVOKESPECIAL, it.classInternalName, "<init>", "()V", false),
                TypeInsnNode(Opcodes.CHECKCAST, iLauncherTaskTriggerInternalName),
                MethodInsnNode(Opcodes.INVOKEVIRTUAL, launcherInternalName, Launcher::registerTaskTrigger.name, launcherRegisterTaskTriggerMethodDesc, false)
            )
            newInstructions.reversed()
            newInstructions.forEach {
                instructions.add(it)
            }
        }
    }

    data class LaunchTaskTriggerMetaData(
        val classInternalName: String,
        val name: String
    )
}