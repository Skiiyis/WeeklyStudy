package com.skiiyis.libprocessor

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Processor::class)
open class ArgsProcessor : AbstractProcessor() {
    // fullname > args
    private val buildMap = HashMap<TypeElement, ArgsCodeBuilder>()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            AutoInject::class.java.name,
            Required::class.java.name,
            Optional::class.java.name
        ).also {
            println("SupportedAnnotationTypes: $it")
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported().also {
            println("getSupportedSourceVersion: $it")
        }
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        if (annotations.isNullOrEmpty()) return true
        for (element in roundEnv.getElementsAnnotatedWith(AutoInject::class.java)) {
            if (ElementKind.CLASS != element.kind) {
                continue
            }
            val classElement = element as TypeElement
            val builder =
                buildMap.getOrElse(classElement) { ArgsCodeBuilder(element.enclosingElement.toString()) }
            collectRequiredParams(element, builder)
            collectOptionalParams(element, builder)
            buildMap[classElement] = builder
        }
        return genCode()
    }

    private fun collectRequiredParams(
        element: TypeElement,
        builder: ArgsCodeBuilder
    ) {
        val enclosedElements = element.enclosedElements
        for (enclosedElement in enclosedElements) {
            if (enclosedElement !is VariableElement) {
                continue
            }
            enclosedElement.getAnnotation(Required::class.java) ?: continue
            builder.requiredParams.add(
                ArgsCodeBuilder.VariableMetaData(
                    enclosedElement.simpleName.toString(),
                    enclosedElement.asType().toString()
                )
            )
        }
    }

    private fun collectOptionalParams(
        element: TypeElement,
        builder: ArgsCodeBuilder
    ) {
        val enclosedElements = element.enclosedElements
        for (enclosedElement in enclosedElements) {
            if (enclosedElement !is VariableElement) {
                continue
            }
            enclosedElement.getAnnotation(Optional::class.java) ?: continue
            builder.optionalParams.add(
                ArgsCodeBuilder.VariableMetaData(
                    enclosedElement.simpleName.toString(),
                    enclosedElement.asType().toString()
                )
            )
        }
    }

    private fun genCode(): Boolean {
        if (buildMap.isEmpty()) return false
        genLauncherCode()
        genParserCode()
        return true
    }

    private fun genParserCode() {
        buildMap.forEach { it ->
            val className = it.key.simpleName
            val builder = it.value
            val parserSimpleName = "${className}Parser"
            val classBuilder = TypeSpec.classBuilder(parserSimpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

            //injectMethodSpec
            val injectIntentMethodSpec = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.bestGuess(it.key.asType().toString()), "activity")
                .addParameter(ClassName.bestGuess("android.content.Intent"), "intent")
                .apply {
                    builder.requiredParams.forEach {
                        addStatement(parserStatement(it))
                    }
                    builder.optionalParams.forEach {
                        addStatement(parserStatement(it))
                    }
                }.returns(TypeName.VOID)
                .build()
            val injectMethodSpec = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.bestGuess(it.key.asType().toString()), "activity")
                .addStatement("android.content.Intent intent = activity.getIntent()")
                .addStatement("inject(activity, intent)")
                .returns(TypeName.VOID)
                .build()
            val classSpec = classBuilder.addMethod(injectIntentMethodSpec)
                .addMethod(injectMethodSpec)
                .build()
            JavaFile.builder(builder.packageName, classSpec)
                .build().writeTo(processingEnv.filer)
        }
    }

    private fun parserStatement(it: ArgsCodeBuilder.VariableMetaData) =
        "try {" +
                "    activity.${it.variableName} = (${it.variableFullClassName}) intent.getExtras().get(\"${it.variableName}\");" +
                "} catch (Exception e) {e.printStackTrace(); }"

    private fun genLauncherCode() {
        buildMap.forEach { it ->
            val className = it.key.simpleName
            val builder = it.value

            val requestCode = "requestCode"
            val launcherSimpleName = "${className}Launcher"
            val launcherClass = ClassName.bestGuess(launcherSimpleName)

            // classBuilder
            val classBuilder = TypeSpec
                .classBuilder(launcherSimpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(TypeName.INT, requestCode, Modifier.PRIVATE)
                .apply {
                    builder.requiredParams.forEach {
                        addField(
                            ClassName.bestGuess(it.variableFullClassName),
                            it.variableName,
                            Modifier.PRIVATE,
                            Modifier.FINAL
                        )
                    }
                    builder.optionalParams.forEach {
                        addField(
                            ClassName.bestGuess(it.variableFullClassName),
                            it.variableName,
                            Modifier.PRIVATE
                        )
                    }
                }

            val classSpec = classBuilder
                //constructor
                .addMethod(buildConstructorMethod(builder))
                //builder
                .addMethod(buildBuilderMethod(builder, launcherClass))
                .apply {
                    //optionalMethodBuilder
                    builder.optionalParams.forEach {
                        classBuilder.addMethod(
                            optionalMethodSpec(
                                launcherClass,
                                ClassName.bestGuess(it.variableFullClassName),
                                it.variableName
                            )
                        )
                    }
                }
                //requestCode
                .addMethod(optionalMethodSpec(launcherClass, TypeName.INT, requestCode))
                //activityStart
                .addMethod(buildActivityStartMethod(it))
                //fragmentStart
                .addMethod(buildFragmentStartMethod(it))
                .build()

            JavaFile.builder(builder.packageName, classSpec)
                .build().writeTo(processingEnv.filer)
        }
    }

    private fun buildConstructorMethod(builder: ArgsCodeBuilder): MethodSpec? {
        return MethodSpec
            .constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .apply {
                builder.requiredParams.forEach {
                    addParameter(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                }
            }.apply {
                builder.requiredParams.forEach {
                    addStatement("this.\$N = \$N", it.variableName, it.variableName)
                }
            }
            .build()
    }

    private fun buildBuilderMethod(
        builder: ArgsCodeBuilder,
        launcherClass: ClassName?
    ): MethodSpec? {
        return MethodSpec.methodBuilder("builder")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .apply {
                builder.requiredParams.forEach {
                    addParameter(ClassName.bestGuess(it.variableFullClassName), it.variableName)
                }
            }
            .addStatement(
                "return new \$T(${
                    builder.requiredParams.joinToString(",") { it.variableName }
                })", launcherClass
            )
            .returns(launcherClass)
            .build()
    }

    private fun buildActivityStartMethod(it: Map.Entry<TypeElement, ArgsCodeBuilder>): MethodSpec? {
        val intentClass = ClassName.bestGuess("android.content.Intent")
        return MethodSpec.methodBuilder("start")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.bestGuess("android.app.Activity"), "activity")
            .addStatement(
                "\$T intent = new \$T(activity, ${it.key.qualifiedName}.class)",
                intentClass, intentClass
            ).apply {
                it.value.requiredParams.forEach {
                    addStatement("intent.putExtra(\"${it.variableName}\", ${it.variableName})")
                }
                it.value.optionalParams.forEach {
                    addStatement("intent.putExtra(\"${it.variableName}\", ${it.variableName})")
                }
            }
            .addStatement("if (requestCode >= 0) {activity.startActivityForResult(intent, requestCode);} else {activity.startActivity(intent);}")
            .returns(TypeName.VOID)
            .build()
    }

    private fun buildFragmentStartMethod(it: Map.Entry<TypeElement, ArgsCodeBuilder>): MethodSpec? {
        val intentClass = ClassName.bestGuess("android.content.Intent")
        return MethodSpec.methodBuilder("start")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.bestGuess("androidx.fragment.app.Fragment"), "fragment")
            .addStatement("android.content.Context context")
            .addStatement("if (android.os.Build.VERSION.SDK_INT >= 23) {context = fragment.getContext();} else {context = fragment.getActivity();}assert context != null")
            .addStatement(
                "\$T intent = new \$T(context, ${it.key.qualifiedName}.class)",
                intentClass, intentClass
            ).apply {
                it.value.requiredParams.forEach {
                    addStatement("intent.putExtra(\"${it.variableName}\", ${it.variableName})")
                }
                it.value.optionalParams.forEach {
                    addStatement("intent.putExtra(\"${it.variableName}\", ${it.variableName})")
                }
            }
            .addStatement("if (requestCode >= 0) {fragment.startActivityForResult(intent, requestCode);} else {fragment.startActivity(intent);}")
            .returns(TypeName.VOID)
            .build()
    }

    private fun optionalMethodSpec(
        launcherClass: ClassName,
        paramType: TypeName,
        paramName: String
    ) =
        MethodSpec.methodBuilder(paramName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(paramType, paramName)
            .addStatement("this.\$N = \$N", paramName, paramName)
            .addStatement("return this")
            .returns(launcherClass)
            .build()

    class ArgsCodeBuilder constructor(val packageName: String = "") {

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