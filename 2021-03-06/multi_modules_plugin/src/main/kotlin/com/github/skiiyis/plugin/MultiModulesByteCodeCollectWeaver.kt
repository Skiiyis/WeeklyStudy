package com.github.skiiyis.plugin

import com.quinn.hunter.transform.asm.BaseWeaver
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class MultiModulesByteCodeCollectWeaver : BaseWeaver() {

    override fun isWeavableClass(fullQualifiedClassName: String): Boolean {
        return fullQualifiedClassName.startsWith("com.skiiyis") &&
                super.isWeavableClass(fullQualifiedClassName)
    }

    override fun wrapClassWriter(classWriter: ClassWriter): ClassVisitor {
        return MultiModulesByteCodeVisitor(classWriter)
    }

    class MultiModulesByteCodeVisitor(classWriter: ClassWriter) :
        ClassVisitor(Opcodes.ASM5, classWriter) {

        private lateinit var `implement`: String

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            `implement` = name
            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor {
            val vs = super.visitAnnotation(desc, visible)
            return if (desc == "Lcom/skiiyis/center/PluginImpl;")
                MultiModulesByteCodeAnnotationVisitor(`implement`, vs)
            else
                vs
        }
    }

    // Class
    class MultiModulesByteCodeAnnotationVisitor(
        private val `implement`: String,
        av: AnnotationVisitor
    ) :
        AnnotationVisitor(Opcodes.ASM5, av) {

        // -> 筛选PluginImpl的 value 内容
        override fun visit(name: String?, value: Any?) {
            MultiModulesAnnotationMap.cache[value.toString()] = `implement`
            super.visit(name, value)
        }
    }
}