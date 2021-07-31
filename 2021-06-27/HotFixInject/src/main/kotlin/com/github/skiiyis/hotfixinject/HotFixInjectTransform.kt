package com.github.skiiyis.hotfixinject

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.NotFoundException
import javassist.bytecode.AccessFlag
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile

class HotFixInjectTransform(private val android: AppExtension) : Transform() {

    override fun getName(): String {
        return "HotFixInject"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation) {
        val classPool = ClassPool.getDefault()
        // 所有的 class 都加入到classPool
        android.bootClasspath.forEach {
            classPool.appendClassPath(it.absolutePath)
        }
        transformInvocation.inputs.forEach {
            it.jarInputs.forEach {
                processJar(classPool, it, transformInvocation)
            }
            it.directoryInputs.forEach {
                processDir(classPool, it, transformInvocation)
            }
        }
        /*transformInvocation.inputs.forEach {
            it.jarInputs.forEach {
                deleteHotFixProxy(it, transformInvocation)
            }
            it.directoryInputs.forEach {
                deleteHotFixProxy(it, transformInvocation)
            }
        }*/
    }

    private fun deleteHotFixProxy(
        it: QualifiedContent,
        transformInvocation: TransformInvocation
    ) {
        val outputLocation = transformInvocation.outputProvider.getContentLocation(
            it.name,
            it.contentTypes,
            it.scopes,
            Format.DIRECTORY
        )
        deleteHotFixProxyClass(outputLocation)
    }

    private fun deleteHotFixProxyClass(file: File) {
        if (file.isFile && file.endsWith("HotFixProxy.class")) {
            file.delete()
        } else if (file.isDirectory) {
            file.listFiles().forEach {
                deleteHotFixProxyClass(it)
            }
        }
    }

    private fun injectHotFixContent(classPool: ClassPool): CtClass? {
        val mainCtClass =
            classPool.getOrNull("com.github.skiiyis.hotfix.MainActivity") ?: return null

        // 找到 MainActivity 插入__change对象
        val hotfixProxyCtClass = classPool.getOrNull("com.github.skiiyis.hotfix.HotFixProxy")
        var hasField = true
        try {
            if (mainCtClass.getField("__change") != null) {
                hasField = false
            }
        } catch (e: NotFoundException) {
            hasField = false
        }
        if (hasField) return mainCtClass

        mainCtClass.addField(CtField(hotfixProxyCtClass, "__change", mainCtClass).also {
            it.modifiers = AccessFlag.STATIC or AccessFlag.PUBLIC
        })

        // 找到 MainActivity 给需要热修的方法前加入热修判断逻辑
        val needHotFixMethod = mainCtClass.getDeclaredMethod("problemMethod")
        needHotFixMethod.insertBefore(
            "if (__change != null) {\n" +
                    "            if (__change.checkFix(this, \"problemMethod()Ljava.lang.String;\")) {\n" +
                    "                return (String) __change.fix(this, \"problemMethod()Ljava.lang.String;\");\n" +
                    "            }\n" +
                    "}"
        )
        return mainCtClass
    }

    private fun processJar(
        classPool: ClassPool,
        jarInput: JarInput,
        transformInvocation: TransformInvocation
    ) {
        classPool.appendClassPath(jarInput.file.absolutePath)
        val hotFixInjectClass = injectHotFixContent(classPool)
        val outputLocation = transformInvocation.outputProvider.getContentLocation(
            jarInput.name,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.DIRECTORY
        )
        JarFile(jarInput.file).also { jar ->
            jar.stream().forEach {
                // 只处理 class 文件
                if (!it.isDirectory && it.name.endsWith(".class")) {
                    val outputFile = File(outputLocation, it.name)
                    if (!outputFile.parentFile.exists()) {
                        outputFile.parentFile.mkdirs()
                    }
                    val outputStream = FileOutputStream(File(outputLocation, it.name))
                    // 如果是我们的热修复的类，从classPool中的字节码生成文件到目标地址
                    // 如果不是我们热修复的类，复制jar包中的字节码文件到目标地址
                    val inputStream =
                        if (hotFixInjectClass != null && hotFixInjectClass.name == it.name) {
                            ByteArrayInputStream(hotFixInjectClass.toBytecode())
                        } else {
                            jar.getInputStream(it)
                        }
                    val buf = ByteArray(1024)
                    while (inputStream.read(buf) > 0) {
                        outputStream.write(buf)
                    }
                    inputStream.close()
                    outputStream.close()
                }
            }
        }
    }

    private fun processDir(
        classPool: ClassPool,
        it: DirectoryInput,
        transformInvocation: TransformInvocation
    ) {
        classPool.appendClassPath(it.file.absolutePath)
        val hotFixInjectClass = injectHotFixContent(classPool)
        val outputLocation = transformInvocation.outputProvider.getContentLocation(
            it.name,
            it.contentTypes,
            it.scopes,
            Format.DIRECTORY
        )
        // 直接复制全部的 class 文件目录
        FileUtils.copyDirectory(it.file, outputLocation)
        // 找到目录中是否有我们热修复的对应的 class 文件，从 classPool 的字节码生成文件覆盖掉源文件
        ArrayList<File>().also { res ->
            findHotFixFile(outputLocation, hotFixInjectClass?.name!!, res)
            if (res.isEmpty()) {
                return@also
            }
            val fos = FileOutputStream(res[0])
            fos.write(hotFixInjectClass.toBytecode())
            fos.flush()
            fos.close()
        }
    }

    private fun findHotFixFile(file: File, targetFileName: String, result: MutableList<File>) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                findHotFixFile(it, targetFileName, result)
            }
        } else {
            if (file.absolutePath.contains(targetFileName.replace(".", "/"))) {
                result.add(file)
            }
        }
    }
}