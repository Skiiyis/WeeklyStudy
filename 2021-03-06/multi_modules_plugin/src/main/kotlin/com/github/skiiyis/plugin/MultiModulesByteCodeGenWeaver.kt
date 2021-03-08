package com.github.skiiyis.plugin

import com.quinn.hunter.transform.asm.BaseWeaver
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class MultiModulesByteCodeGenWeaver : BaseWeaver() {

    override fun isWeavableClass(fullQualifiedClassName: String): Boolean {
        return fullQualifiedClassName.startsWith("com.skiiyis.center.PluginManager") &&
                super.isWeavableClass(fullQualifiedClassName)
    }

    override fun wrapClassWriter(classWriter: ClassWriter): ClassVisitor {
        return MultiModulesByteCodeGenClassVisitor(classWriter)
    }

    class MultiModulesByteCodeGenClassVisitor(classWriter: ClassWriter) :
        ClassVisitor(Opcodes.ASM5, classWriter) {


    }
}