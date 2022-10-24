package com.skiiyis.study.launcher.plugin.annotationprocessor

import com.skiiyis.study.launcher.annotation.ALaunchTask
import com.skiiyis.study.launcher.plugin.Constants
import com.skiiyis.study.launcher.plugin.FindAnnotationIndex
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

object LaunchTaskAnnotationProcessor : AnnotationProcessor {

    private val taskMetaDatas = mutableMapOf<String, LaunchTaskMetaData>()

    override fun collect(classInternalName: String, annotationValues: List<Any>) {
        taskMetaDatas.put(
            annotationValues[
                    FindAnnotationIndex.findValueIndex(
                        annotationValues,
                        ALaunchTask::name.name
                    )
            ].toString(),
            LaunchTaskMetaData(
                classInternalName = classInternalName,
                transactionName = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTask::transactionName.name
                        )
                ].toString(),
                name = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTask::name.name
                        )
                ].toString(),
                targetProcess = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTask::targetProcess.name
                        )
                ] as List<String>,
                taskType = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTask::taskType.name
                        )
                ].toString(),
                order = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTask::order.name
                        )
                ] as Int,
                dependOn = annotationValues[
                        FindAnnotationIndex.findValueIndex(
                            annotationValues,
                            ALaunchTask::dependOn.name
                        )
                ] as List<String>
            )
        )
    }

    override fun generateCode(instructions: InsnList) {
        val taskRet = checkAndMergeTasks(taskMetaDatas.values.toMutableSet())
        var depth : Int = taskRet.maxBy { it.value }!!.value
        while (depth >= 0) {
            for (key in taskRet.keys) {
                if (taskRet[key] == depth) {
                    generateAddTaskCode(key, instructions)
                }
            }
            depth--
        }
    }

    private fun generateAddTaskCode(taskData: LaunchTaskMetaData, instructions: InsnList) {
        val newInstructions = mutableListOf<AbstractInsnNode>()
        newInstructions.addAll(
            listOf(
                // Launcher.INSTANCE
                FieldInsnNode(
                    Opcodes.GETSTATIC,
                    Constants.launcherInternalName,
                    "INSTANCE",
                    Constants.launcherDesc
                ),

                // new LauncherTask.Builder(SampleRunnable())
                TypeInsnNode(Opcodes.NEW, Constants.launchTaskBuilderInternalName),
                InsnNode(Opcodes.DUP),
                TypeInsnNode(Opcodes.NEW, taskData.classInternalName),
                InsnNode(Opcodes.DUP),
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    taskData.classInternalName,
                    "<init>",
                    "()V",
                    false
                ),
                TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Runnable"),
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    Constants.launchTaskBuilderInternalName,
                    "<init>",
                    "(Ljava/lang/Runnable;)V",
                    false
                ),

                // LauncherTask.Builder.name()
                LdcInsnNode(taskData.name),
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    Constants.launchTaskBuilderInternalName,
                    "name",
                    Constants.launchTaskNameDependOnMethodDesc,
                    false
                ),

                // LauncherTask.Builder.taskType()
                LdcInsnNode(taskData.taskType),
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    Constants.launchTaskBuilderInternalName,
                    "taskType",
                    Constants.launchTaskTaskTypeMethodDesc,
                    false
                ),

                // LauncherTask.Builder.transactionName()
                LdcInsnNode(taskData.transactionName),
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    Constants.launchTaskBuilderInternalName,
                    "transactionName",
                    Constants.launchTaskTransactionNameMethodDesc,
                    false
                ),

                // LauncherTask.Builder.order()
                LdcInsnNode(taskData.order),
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    Constants.launchTaskBuilderInternalName,
                    "order",
                    Constants.launchTaskOrderMethodDesc,
                    false
                ),
            )
        )

        // LauncherTask.Builder.dependOn(Launcher.requireTask("second"))
        taskData.dependOn.forEach {
            newInstructions.addAll(
                listOf(
                    FieldInsnNode(
                        Opcodes.GETSTATIC,
                        Constants.launcherInternalName,
                        "INSTANCE",
                        Constants.launcherDesc
                    ),
                    LdcInsnNode(it),
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        Constants.launcherInternalName,
                        "requireTask",
                        Constants.launcherRequireTaskMethodDesc,
                        false
                    ),
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        Constants.launchTaskBuilderInternalName,
                        "dependOn",
                        Constants.launchTaskBuilderDependOnMethodDesc,
                        false
                    )
                )
            )
        }

        // LauncherTask.Builder.targetProcess()
        taskData.targetProcess.forEach {
            newInstructions.addAll(
                listOf(
                    LdcInsnNode(it),
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        Constants.launchTaskBuilderInternalName,
                        "targetProcess",
                        Constants.launchTaskTargetProcessMethodDesc,
                        false
                    ),
                )
            )
        }

        newInstructions.addAll(
            listOf(
                // LauncherTask.Builder.build()
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    Constants.launchTaskBuilderInternalName,
                    "build",
                    Constants.launchTaskBuilderBuildMethodDesc,
                    false
                ),

                // Launcher.addTask()
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    Constants.launcherInternalName,
                    "addTask",
                    Constants.launcherAddTaskMethodDesc,
                    false
                )
            )
        )

        newInstructions.reversed().forEach {
            instructions.insert(it)
        }
    }

    fun checkAndMergeTasks(tasks: MutableSet<LaunchTaskMetaData>): MutableMap<LaunchTaskMetaData, Int> {
        val ret = mutableMapOf<LaunchTaskMetaData, Int>()
        val totalTasks = mutableSetOf<LaunchTaskMetaData>()
        tasks.forEach {
            val pathTasks = mutableSetOf<LaunchTaskMetaData>()
            check(it, pathTasks, totalTasks)
        }
        // 根据依赖找入度
        while (ret.size != totalTasks.size) {
            totalTasks.subtract(ret.keys).forEach {
                if (ret[it] == null) {
                    val dependOnTasks = it.dependOn.map {
                        taskMetaDatas[it]
                            ?: throw IllegalArgumentException("Could not found target task!")
                    }
                    if (dependOnTasks.isNullOrEmpty()) {
                        ret[it] = 0
                    } else if (ret.keys.containsAll(dependOnTasks)) {
                        ret[it] = dependOnTasks.map { ret[it]!! }.max()!! + 1
                    }
                }
            }
        }
        return ret
    }

    private fun check(
        task: LaunchTaskMetaData,
        pathTask: MutableSet<LaunchTaskMetaData>,
        totalTasks: MutableSet<LaunchTaskMetaData>
    ) {
        if (pathTask.contains(task)) throw IllegalArgumentException("Find ring in task dependencies!")
        totalTasks.add(task)
        task.dependOn.map {
            taskMetaDatas[it] ?: throw IllegalArgumentException("Could not found target task!")
        }.forEach {
            pathTask.add(task)
            check(it, pathTask, totalTasks)
            pathTask.remove(task)
        }
    }


    data class LaunchTaskMetaData(
        val classInternalName: String,

        val transactionName: String,
        val name: String,
        val targetProcess: List<String> = emptyList(),
        val taskType: String,
        val order: Int,
        val dependOn: List<String> = emptyList(),
    )
}