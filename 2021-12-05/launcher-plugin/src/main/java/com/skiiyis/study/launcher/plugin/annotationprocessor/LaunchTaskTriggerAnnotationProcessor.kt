package com.skiiyis.study.launcher.plugin.annotationprocessor

import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.annotation.ALaunchTaskTrigger
import com.skiiyis.study.launcher.plugin.Constants.iLauncherTaskTriggerDesc
import com.skiiyis.study.launcher.plugin.Constants.launcherDesc
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

    override fun collect(className: String, annotationValues: List<Any>) {
        taskTriggerMetaDatas.add(
            LaunchTaskTriggerMetaData(
                className = className,
                name = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTaskTrigger::name.name
                        )
                ].toString()
            )
        )
    }

    /*
    LINENUMBER 17 L2
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
            instructions.add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    launcherDesc,
                    "registerTaskTrigger",
                    launcherRegisterTaskTriggerMethodDesc,
                    false
                )
            )
            instructions.add(
                TypeInsnNode(
                    Opcodes.CHECKCAST,
                    iLauncherTaskTriggerDesc,
                )
            )
            instructions.add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    it.className,
                    "<init>",
                    "()V",
                    false
                )
            )
            instructions.add(
                InsnNode(Opcodes.DUP)
            )
            instructions.add(
                TypeInsnNode(
                    Opcodes.NEW,
                    it.className
                )
            )
            instructions.add(
                LdcInsnNode(
                    it.name
                )
            )
            instructions.add(
                FieldInsnNode(
                    Opcodes.GETSTATIC,
                    Launcher::class.qualifiedName,
                    "INTANCE",
                    launcherDesc //problem
                )
            )
        }
    }

    data class LaunchTaskTriggerMetaData(
        val className: String,
        val name: String
    )
}