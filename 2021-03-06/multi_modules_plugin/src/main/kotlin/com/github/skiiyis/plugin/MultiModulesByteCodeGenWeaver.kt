package com.github.skiiyis.plugin

import com.quinn.hunter.transform.asm.BaseWeaver
import org.objectweb.asm.*

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

        override fun visitMethod(
            access: Int,
            name: String?,
            desc: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val mv = super.visitMethod(access, name, desc, signature, exceptions)
            if (name == "<clinit>") {
                return MultiModulesByteCodeGenMethodVisitor(mv)
            }
            return mv
        }
    }

    class MultiModulesByteCodeGenMethodVisitor(mv: MethodVisitor) :
        MethodVisitor(Opcodes.ASM5, mv) {

        override fun visitInsn(opcode: Int) {
            if (opcode == Opcodes.RETURN) {
                MultiModulesAnnotationMap.cache.forEach {
                    mv.visitFieldInsn(
                        Opcodes.GETSTATIC,
                        "com/skiiyis/center/PluginManager",
                        "cache",
                        "Ljava/util/HashMap;"
                    );
                    mv.visitLdcInsn(Type.getType(it.key))
                    mv.visitTypeInsn(Opcodes.NEW, it.value)
                    mv.visitInsn(Opcodes.DUP)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        it.value,
                        "<init>",
                        "()V",
                        false
                    )
                    mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        "java/util/HashMap",
                        "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                        false
                    );
                }
            }
            super.visitInsn(opcode)
        }
    }
}