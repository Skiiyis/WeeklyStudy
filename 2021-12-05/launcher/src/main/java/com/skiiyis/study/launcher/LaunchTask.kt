package com.skiiyis.study.launcher

abstract class LaunchTask : Runnable {

    protected var beDepended: LaunchTask? = null

    abstract fun name(): String
    abstract fun taskType(): String
    abstract fun targetProcess(): List<String>
    abstract fun scene(): String
    abstract fun dependOn(): List<LaunchTask>

    fun beDepended(): LaunchTask? {
        return beDepended
    }

    open fun order(): Int {
        return Int.MIN_VALUE
    }


    class Builder(val runnable: Runnable) {
        private var name: String? = null
        private var taskType: String? = null
        private var process: List<String>? = null
        private var scene: String? = null
        private var order: Int = Int.MIN_VALUE
        private val dependOnTasks = mutableListOf<LaunchTask>()

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun taskType(taskType: String): Builder {
            this.taskType = taskType
            return this
        }

        fun targetProcess(process: List<String>): Builder {
            this.process = process
            return this
        }

        fun scene(scene: String): Builder {
            this.scene = scene
            return this
        }

        fun dependOn(launchTask: LaunchTask): Builder {
            this.dependOnTasks.add(launchTask)
            return this
        }

        fun order(order: Int): Builder {
            this.order = order
            return this
        }

        fun build(): LaunchTask {
            checkNotNull(name)
            checkNotNull(taskType)
            checkNotNull(process)
            checkNotNull(scene)
            val ret = object : LaunchTask() {
                override fun name(): String {
                    return name!!
                }

                override fun taskType(): String {
                    return taskType!!
                }

                override fun targetProcess(): List<String> {
                    return process!!
                }

                override fun scene(): String {
                    return scene!!
                }

                override fun run() {
                    runnable.run()
                }

                override fun dependOn(): List<LaunchTask> {
                    return dependOnTasks
                }

                override fun order(): Int {
                    return order
                }
            }
            dependOnTasks.forEach {
                it.beDepended = ret
            }
            return ret
        }
    }
}