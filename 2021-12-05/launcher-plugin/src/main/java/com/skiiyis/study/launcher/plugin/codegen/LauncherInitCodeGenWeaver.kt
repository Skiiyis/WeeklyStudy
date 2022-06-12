package com.skiiyis.study.launcher.plugin.codegen

import com.quinn.hunter.transform.asm.BaseWeaver
import com.quinn.hunter.transform.asm.ExtendClassWriter
import com.skiiyis.study.launcher.annotation.LauncherInitPlugin
import com.skiiyis.study.launcher.plugin.annotationprocessor.LaunchTaskAnnotationProcessor
import com.skiiyis.study.launcher.plugin.annotationprocessor.LaunchTaskTriggerAnnotationProcessor
import com.skiiyis.study.launcher.plugin.annotationprocessor.LaunchTransactionAnnotationProcessor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.io.InputStream

class LauncherInitCodeGenWeaver : BaseWeaver() {

    override fun isWeavableClass(fullQualifiedClassName: String): Boolean {
        return fullQualifiedClassName == "${LauncherInitPlugin::class.java.name}.class"
    }

    override fun weaveSingleClassToByteArray(inputStream: InputStream?): ByteArray {
        val cr = ClassReader(inputStream)
        val cn = ClassNode()
        cr.accept(cn, EXPAND_FRAMES)

        // ----
        cn.methods.find { it.name == LauncherInitPlugin::init.name }?.also {
            LaunchTaskTriggerAnnotationProcessor.generateCode(it.instructions)
            LaunchTransactionAnnotationProcessor.generateCode(it.instructions)
            LaunchTaskAnnotationProcessor.generateCode(it.instructions)
        }
        // ----

        val cw = ExtendClassWriter(classLoader, COMPUTE_MAXS)
        cn.accept(cw)
        return cw.toByteArray()
    }
}