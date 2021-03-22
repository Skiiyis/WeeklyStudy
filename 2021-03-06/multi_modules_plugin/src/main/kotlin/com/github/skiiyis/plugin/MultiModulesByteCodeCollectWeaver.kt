package com.github.skiiyis.plugin

import com.quinn.hunter.transform.asm.BaseWeaver
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class MultiModulesByteCodeCollectWeaver : BaseWeaver() {

    override fun isWeavableClass(fullQualifiedClassName: String): Boolean {
        return fullQualifiedClassName.startsWith(CUSTOM_PACKAGE_NAME_PRE) &&
                super.isWeavableClass(fullQualifiedClassName)
    }

    override fun wrapClassWriter(classWriter: ClassWriter): ClassVisitor {
        return MultiModulesByteCodeVisitor(classWriter)
    }

    class MultiModulesByteCodeVisitor(classWriter: ClassWriter) :
        ClassVisitor(Opcodes.ASM5, classWriter) {

        private lateinit var `implement`: String
        private var interfaces: Array<out String>? = null

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            `implement` = name
            this.interfaces = interfaces
            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor {
            val vs = super.visitAnnotation(desc, visible)
            return if (desc == PLUGIN_IMPL_CLASS_DESC)
                MultiModulesByteCodeAnnotationVisitor(interfaces, `implement`, vs)
            else
                vs
        }
    }

    // Class
    class MultiModulesByteCodeAnnotationVisitor(
        private val interfaces: Array<out String>?,
        private val `implement`: String,
        av: AnnotationVisitor
    ) :
        AnnotationVisitor(Opcodes.ASM5, av) {

        // -> 筛选PluginImpl的 value 内容
        override fun visit(name: String?, value: Any?) {
            val valueDesc = value.toString()
            interfaces?.forEach {
                if (isHitTargetClass(it, valueDesc)) {
                    MultiModulesAnnotationMap.cache[valueDesc] = `implement`
                }
            }
            super.visit(name, value)
        }

        /**
         * 命中目标class
         * @param interface com/skiiyis/center/Plugin
         * @param valueDesc Lcom/skiiyis/center/Plugin;
         */
        private fun isHitTargetClass(`interface`: String, valueDesc: String): Boolean {
            if (`interface`.isEmpty() || valueDesc.isEmpty()) {
                return false
            }
            return `interface`.substring(1, `interface`.length - 1) == valueDesc
        }
    }

    companion object {
        private const val PLUGIN_IMPL_CLASS_DESC = "Lcom/skiiyis/center/PluginImpl;"
        private const val CUSTOM_PACKAGE_NAME_PRE = "com.skiiyis"
    }
}