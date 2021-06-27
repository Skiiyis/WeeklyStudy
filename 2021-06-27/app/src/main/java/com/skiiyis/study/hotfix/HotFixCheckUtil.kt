package com.skiiyis.study.hotfix

import android.content.Context
import android.util.Log
import dalvik.system.DexClassLoader

object HotFixCheckUtil {

    const val FIX_CLASS_NAME = "com.skiiyis.study.hotfix.HotFix"

    // 1. 查找指定的dex是否存在
    // 2. 加载该dex
    // 3. 加载dex中的指定类
    fun checkAndHotFix(ctx: Context) {
        try {
            val dexFilePath = "I'm a path"
            val dexClassLoader =
                DexClassLoader(dexFilePath, ctx.cacheDir.absolutePath, null, ctx.classLoader)
            dexClassLoader.loadClass(FIX_CLASS_NAME)
        } catch (e: Throwable) {
            Log.e("HotFix", "Load dexFile error!!", e)
        }
    }
}