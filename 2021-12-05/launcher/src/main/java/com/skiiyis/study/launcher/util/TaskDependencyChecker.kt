package com.skiiyis.study.launcher.util

import com.skiiyis.study.launcher.LaunchTask

object TaskDependencyChecker {

    // 查找是否有循环依赖，层序遍历
    private fun check(
        task: LaunchTask,
        pathTask: MutableSet<LaunchTask>,
        totalTasks: MutableSet<LaunchTask>
    ) {
        if (pathTask.contains(task)) throw IllegalArgumentException("Find ring in task dependencies")
        totalTasks.add(task)
        task.dependOn()?.forEach {
            pathTask.add(task)
            check(it, pathTask, totalTasks)
            pathTask.remove(task)
        }
    }

    // 检查是否有循环依赖且按入度输出
    fun checkAndMergeTasks(tasks: MutableSet<LaunchTask>): MutableMap<LaunchTask, Int> {
        val ret = mutableMapOf<LaunchTask, Int>()
        val totalTasks = mutableSetOf<LaunchTask>()
        tasks.forEach {
            val pathTasks = mutableSetOf<LaunchTask>()
            check(it, pathTasks, totalTasks)
        }
        // 根据依赖找入度
        while (ret.size != totalTasks.size) {
            totalTasks.subtract(ret.keys).forEach {
                if (ret[it] == null) {
                    val dependOnTasks = it.dependOn()
                    if (dependOnTasks.isNullOrEmpty()) {
                        ret[it] = 0
                    } else if (ret.keys.containsAll(dependOnTasks)) {
                        ret[it] = dependOnTasks.maxOf { ret[it]!! } + 1
                    }
                }
            }
        }
        return ret
    }
}