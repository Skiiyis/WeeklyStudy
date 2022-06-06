package com.skiiyis.study.launcher.plugin.annotationprocessor

import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.annotation.ALaunchTransaction
import com.skiiyis.study.launcher.plugin.Constants.launcherDesc
import com.skiiyis.study.launcher.plugin.Constants.launcherRegisterLaunchTransactionGeneratorMethodDesc
import com.skiiyis.study.launcher.plugin.Constants.launcherRegisterTaskTriggerMethodDesc
import com.skiiyis.study.launcher.plugin.FindAnnotationIndex
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

object LaunchTransactionAnnotationProcessor : AnnotationProcessor {

    private val taskTransactionMetaDatas = mutableListOf<LaunchTransactionMetaData>()

    override fun collect(className: String, annotationValues: List<Any>) {
        taskTransactionMetaDatas.add(
            LaunchTransactionMetaData(
                className = className,
                name = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTransaction::name.name
                        )
                ].toString()
            )
        )
    }
    
    /*
    LINENUMBER 20 L3
    GETSTATIC com/skiiyis/study/launcher/Launcher.INSTANCE : Lcom/skiiyis/study/launcher/Launcher;
    LDC "cold"
    LDC Lcom/skiiyis/studylauncher/ColdLaunchTransaction;.class
    INVOKEVIRTUAL com/skiiyis/study/launcher/Launcher.registerLaunchTransactionGenerator (Ljava/lang/String;Ljava/lang/Class;)V
    */

    override fun generateCode(instructions: InsnList) {
        taskTransactionMetaDatas.forEach {
            instructions.add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    launcherDesc,
                    "registerLaunchTransactionGenerator",
                    launcherRegisterLaunchTransactionGeneratorMethodDesc,
                    false
                )
            )
            instructions.add(
                LdcInsnNode(
                    "${it.className}.class"
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

    data class LaunchTransactionMetaData(
        val className: String,
        val name: String
    )
}