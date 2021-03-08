package com.github.skiiyis.plugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.quinn.hunter.transform.HunterTransform
import com.quinn.hunter.transform.RunVariant
import org.gradle.api.Project

const val KV_NAME = "multi-modules-kv"

class MultiModulesKVTransform(project: Project) : HunterTransform(project) {

    override fun transform(
        context: Context?,
        inputs: MutableCollection<TransformInput>?,
        referencedInputs: MutableCollection<TransformInput>?,
        outputProvider: TransformOutputProvider?,
        isIncremental: Boolean
    ) {
        this.bytecodeWeaver = MultiModulesByteCodeCollectWeaver()
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
    }

    override fun getRunVariant(): RunVariant {
        return super.getRunVariant()
    }

    override fun inDuplcatedClassSafeMode(): Boolean {
        return true
    }

    override fun getName(): String {
        return KV_NAME
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.EMPTY_SCOPES
    }
}