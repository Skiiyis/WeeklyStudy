package com.skiiyis.libprocessor

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Process::class)
class ArgsProcessor : AbstractProcessor() {
    // fullname > args
    private val buildMap = HashMap<TypeElement, ArgsCodeBuilder>()

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        for (element in roundEnv.getElementsAnnotatedWith(Args::class.java)) {
            if (element !is VariableElement) {
                continue
            }
            val classElement = (element.enclosingElement as TypeElement)
            val builder = buildMap.getOrElse(classElement) { ArgsCodeBuilder() }
            val args = element.getAnnotation(Args::class.java)
            if (args.required) {
                builder.requiredParams.add(
                    ArgsCodeBuilder.VariableMetaData(
                        element.simpleName.toString(),
                        element.asType().toString()
                    )
                )
            } else {
                builder.optionalParams.add(
                    ArgsCodeBuilder.VariableMetaData(
                        element.simpleName.toString(),
                        element.asType().toString()
                    )
                )
            }
            buildMap[classElement] = builder
        }
        return genCode()
    }

    private fun genLauncherClassFile(it: Map.Entry<TypeElement, ArgsCodeBuilder>) {
        // key: MainActivity -> MainActivityArgs
        val launcherClassSimpleName = "${it.key.qualifiedName}Launcher"
        val launcherOptionalParamClassSimpleName = "${it.key.qualifiedName}Launcher_Optional"
        val launcherJfo =
            processingEnv.filer.createSourceFile(launcherClassSimpleName, it.key)
        // todo
        val packageName = it.key.qualifiedName.split(".").dropLast(1).joinToString(".")

        val launcherClassName = ClassName.get(packageName, launcherClassSimpleName)
        val activityClassName = ClassName.get("android.app", "Activity")
        val launcherOptionalParamClassName =
            ClassName.get(packageName, launcherOptionalParamClassSimpleName)

        val requireContextMethod = MethodSpec.methodBuilder("requireContext")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .addParameter(activityClassName, "activity")
            .returns(launcherClassName)
            .addStatement("this.activity = activity;")
            .addStatement("return this;")
            .build()

        val requireParamsMethod = MethodSpec.methodBuilder("requireParams")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .apply {
                it.value.requiredParams.forEach {
                    addParameter(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                }
            }
            .returns(launcherOptionalParamClassName)
            .addStatement("return new \$T(activity, ${it.value.requiredParams.map { it.variableName }
                .joinToString(",")});", launcherOptionalParamClassName)
            .build()

        val constructorMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .build()

        val staticCreateMethod = MethodSpec.methodBuilder("create")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addParameter(activityClassName, "activity")
            .returns(launcherClassName)
            .addStatement("return new \$T().requireContext(activity);", launcherClassName)
            .build()

        val launcherTypeSpec = TypeSpec.classBuilder(launcherClassSimpleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(activityClassName, "activity", Modifier.PRIVATE)
            .addMethod(constructorMethod)
            .addMethod(staticCreateMethod)
            .addMethod(requireParamsMethod)
            .addMethod(requireContextMethod)
            .build()

        val javaFile = JavaFile.builder(packageName, launcherTypeSpec)
            .build()

        javaFile.writeTo(launcherJfo.openWriter())
    }

    private fun genLauncherOptionalClassFile(it: Map.Entry<TypeElement, ArgsCodeBuilder>) {
        // key: MainActivity -> MainActivityArgs
        val launcherClassSimpleName = "${it.key.qualifiedName}Launcher"
        val launcherOptionalParamClassSimpleName = "${it.key.qualifiedName}Launcher_Optional"
        val launcherJfo =
            processingEnv.filer.createSourceFile(launcherClassSimpleName, it.key)
        val packageName = it.key.qualifiedName.split(".").dropLast(1).joinToString(".")

        val launcherClassName = ClassName.get(packageName, launcherClassSimpleName)
        val activityClassName = ClassName.get("android.app", "Activity")
        val launcherOptionalParamClassName =
            ClassName.get(packageName, launcherOptionalParamClassSimpleName)

        val constructorMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.DEFAULT)
            .addParameter(activityClassName, "activity")
            .apply {
                it.value.requiredParams.forEach {
                    addParameter(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                }
            }
            .addStatement("this.activity = activity;")
            .apply {
                it.value.requiredParams.forEach {
                    addStatement("this.${it.variableName} = ${it.variableName};")
                }
            }
            .build()

        val optionalMethods = it.value.optionalParams.map {
            MethodSpec.methodBuilder(it.variableName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                .returns(launcherOptionalParamClassName)
                .addStatement("this.${it.variableName} = ${it.variableName};")
                .addStatement("return this;")
                .build()
        }

        val launchMethod = MethodSpec.methodBuilder("launch")
            .addModifiers(Modifier.PUBLIC)
            .returns(Void::class.java)
            .addStatement("android.content.Intent intent = new android.content.Intent(activity, ${it.key.qualifiedName}.class);")
            .apply {
                it.value.requiredParams.forEach {
                    addStatement("intent.putExtra(\"${it.variableName}\", ${it.variableName});")
                }
                it.value.optionalParams.forEach {
                    addStatement("intent.putExtra(\"${it.variableName}\", ${it.variableName});")
                }
            }
            .addStatement("activity.startActivity(intent);")
            .build()

        val launcherTypeSpec = TypeSpec.classBuilder(launcherOptionalParamClassSimpleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(activityClassName, "activity")
            .apply {
                it.value.requiredParams.forEach {
                    addField(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                }
                it.value.optionalParams.forEach {
                    addField(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                }
            }
            .addMethods(optionalMethods)
            .addMethod(launchMethod)
            .addMethod(constructorMethod)
            .build()

        val javaFile = JavaFile.builder(packageName, launcherTypeSpec)
            .build()

        javaFile.writeTo(launcherJfo.openWriter())
    }

    private fun genParserClassFile(it: Map.Entry<TypeElement, ArgsCodeBuilder>) {
        // key: MainActivity -> MainActivityArgs
        val parserClassSimpleName = "${it.key.qualifiedName}Parser"
        val launcherJfo =
            processingEnv.filer.createSourceFile(parserClassSimpleName, it.key)
        val packageName = it.key.qualifiedName.split(".").dropLast(1).joinToString(".")

        val parseMethod = MethodSpec.methodBuilder("parse")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .addParameter(ClassName.bestGuess(it.key.qualifiedName.toString()), "activity")
            .returns(Void::class.java)
            .addStatement("android.content.Intent intent = activity.getIntent();")
            .apply {
                it.value.requiredParams.forEach {
                    addStatement("activity.${it.variableName} = (${it.variableFullClassName}) intent.getExtras().get(\"${it.variableName}\");")
                }
                it.value.optionalParams.forEach {
                    addStatement("activity.${it.variableName} = (${it.variableFullClassName}) intent.getExtras().get(\"${it.variableName}\");")
                }
            }
            .build()

        val launcherTypeSpec = TypeSpec.classBuilder(parserClassSimpleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(parseMethod)
            .build()

        val javaFile = JavaFile.builder(packageName, launcherTypeSpec)
            .build()

        javaFile.writeTo(launcherJfo.openWriter())
    }

    private fun genCode(): Boolean {
        if (buildMap.isEmpty()) return false
        buildMap.forEach {
            genLauncherClassFile(it)
            genLauncherOptionalClassFile(it)
            genParserClassFile(it)
        }
        return true
    }

    class ArgsCodeBuilder {
        // 需要保存 name,className,default值
        val requiredParams = mutableSetOf<VariableMetaData>()
        val optionalParams = mutableSetOf<VariableMetaData>()

        // requireParams(id:java.lang.String)
        data class VariableMetaData(
            val variableName: String,
            val variableFullClassName: String
        )
    }
}