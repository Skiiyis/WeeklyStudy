package com.github.skiiyis.hotfix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class HotFixCheckUtil {
    public static final String PATCH_NAME = "patch.dex";
    public static final String FIX_CLASS_NAME = "com.github.skiiyis.hotfix.Patch";
    public static final String FIX_MAIN_ACTIVITY = "com.github.skiiyis.hotfix.MainActivity";
    public static final String PATCH_MAIN_ACTIVITY = "com.github.skiiyis.hotfix.MainActivityPatch";

    public static void checkAndHotFix(Context ctx) {
        try {
            DexClassLoader dexClassLoader =
                    new DexClassLoader(new File(ctx.getCacheDir(), PATCH_NAME).getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, ctx.getClass().getClassLoader());
            Class mainActivityClazz = dexClassLoader.loadClass(FIX_MAIN_ACTIVITY);
            Class mainActivityPatchClazz = dexClassLoader.loadClass(PATCH_MAIN_ACTIVITY);
            Field f = mainActivityClazz.getField("__change");
            f.set(null, mainActivityPatchClazz.newInstance());
        } catch (Throwable e) {
            Log.e("HotFix", "Load dexFile error!!", e);
        }
    }
}
