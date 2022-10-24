package com.skiiyis.study.launcher.plugin.annotationprocessor

import com.skiiyis.study.launcher.Launcher
import com.skiiyis.study.launcher.annotation.ALaunchTransaction
import com.skiiyis.study.launcher.plugin.Constants.launcherDesc
import com.skiiyis.study.launcher.plugin.Constants.launcherInternalName
import com.skiiyis.study.launcher.plugin.Constants.launcherRegisterLaunchTransactionGeneratorMethodDesc
import com.skiiyis.study.launcher.plugin.FindAnnotationIndex
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

object LaunchTransactionAnnotationProcessor : AnnotationProcessor {

    private val taskTransactionMetaDatas = mutableListOf<LaunchTransactionMetaData>()

    override fun collect(classInternalName: String, annotationValues: List<Any>) {
        taskTransactionMetaDatas.add(
            LaunchTransactionMetaData(
                classInternalName = classInternalName,
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
            val newInstructions = listOf(
                FieldInsnNode(Opcodes.GETSTATIC, launcherInternalName, "INSTANCE", launcherDesc),
                LdcInsnNode(it.name),
                LdcInsnNode(Type.getObjectType(it.classInternalName)),
                MethodInsnNode(Opcodes.INVOKEVIRTUAL, launcherInternalName, Launcher::registerLaunchTransactionGenerator.name,  launcherRegisterLaunchTransactionGeneratorMethodDesc, false)
            )
            newInstructions.reversed().forEach {
                instructions.insert(it)
            }
        }
    }

    data class LaunchTransactionMetaData(
        val classInternalName: String,
        val name: String
    )
}