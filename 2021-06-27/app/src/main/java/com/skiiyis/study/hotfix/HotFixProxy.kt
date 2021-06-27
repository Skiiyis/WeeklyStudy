package com.skiiyis.study.hotfix

interface HotFixProxy {
    // 1. origin 为热修复的原对象 MainActivity
    // 2. params 为热修复方法的参数
    fun fix(origin: Any, vararg params: Any?): Any?
}