package com.github.skiiyis.hotfix;

interface HotFixProxy {

    // 1. origin 为热修复的原对象 MainActivity
    // 2. methodDesc 为热修复方法签名
    // 3. params 为热修复方法的参数
    Object fix(Object origin, String methodDesc);

    // 检查哪些方法需要热修复
    boolean checkFix(Object origin, String methodDesc);
}
