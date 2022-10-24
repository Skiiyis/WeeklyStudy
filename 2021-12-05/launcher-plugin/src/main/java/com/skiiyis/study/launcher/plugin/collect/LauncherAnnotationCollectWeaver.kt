package com.skiiyis.study.launcher.plugin.collect

import com.quinn.hunter.transform.asm.BaseWeaver
import com.skiiyis.study.launcher.annotation.ALaunchTask
import com.skiiyis.study.launcher.annotation.ALaunchTaskTrigger
import com.skiiyis.study.launcher.annotation.ALaunchTransaction
import com.skiiyis.study.launcher.plugin.annotationprocessor.LaunchTaskAnnotationProcessor
import com.skiiyis.study.launcher.plugin.annotationprocessor.LaunchTaskTriggerAnnotationProcessor
import com.skiiyis.study.launcher.plugin.annotationprocessor.LaunchTransactionAnnotationProcessor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.io.InputStream

class LauncherAnnotationCollectWeaver : BaseWeaver() {

    companion object {
        val LaunchTaskDesc: String = Type.getDescriptor(ALaunchTask::class.java)
        val LaunchTaskTriggerDesc: String = Type.getDescriptor(ALaunchTaskTrigger::class.java)
        val LaunchTransactionDesc: String = Type.getDescriptor(ALaunchTransaction::class.java)
    }

    override fun weaveSingleClassToByteArray(inputStream: InputStream?): ByteArray {
        val ret = super.weaveSingleClassToByteArray(inputStream)
        val cr = ClassReader(ret)
        val cn = ClassNode()
        cr.accept(cn, 0)

        // ----
        cn.visibleAnnotations?.forEach {
            when (it.desc) {
                LaunchTaskDesc -> {
                    LaunchTaskAnnotationProcessor.collect(cn.name, it.values)
                }
                LaunchTaskTriggerDesc -> {
                    LaunchTaskTriggerAnnotationProcessor.collect(cn.name, it.values)
                }
                LaunchTransactionDesc -> {
                    LaunchTransactionAnnotationProcessor.collect(cn.name, it.values)
                }
            }
        }
        // ----
        return ret
    }
}