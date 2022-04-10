package com.skiiyis.study.launcher

abstract class LaunchTask : Runnable {

    private var beDependedList: MutableList<LaunchTask>? = null

    abstract fun name(): String
    abstract fun taskType(): String
    abstract fun targetProcess(): List<String>
    abstract fun transactionName(): String
    abstract fun dependOn(): List<LaunchTask>?

    fun beDepended(): List<LaunchTask>? {
        return beDependedList
    }

    open fun order(): Int {
        return Int.MIN_VALUE
    }
    // A <- B
    // C <- B

    class Builder(val runnable: Runnable) {
        private var name: String? = null
        private var taskType: String? = null
        private var process: List<String>? = null
        private var transactionName: String? = null
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

        fun transactionName(name: String): Builder {
            this.transactionName = name
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
            checkNotNull(transactionName)
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

                override fun transactionName(): String {
                    return transactionName!!
                }

                override fun run() {
                    LauncherHooks.beforeTaskRunHook?.invoke(this)
                    runnable.run()
                    LauncherHooks.afterTaskRunHook?.invoke(this)
                }

                override fun dependOn(): List<LaunchTask> {
                    return dependOnTasks
                }

                override fun order(): Int {
                    return order
                }
            }
            dependOnTasks.forEach {
                if (it.beDependedList == null) {
                    it.beDependedList = mutableListOf()
                }
                it.beDependedList?.add(ret)
            }
            return ret
        }
    }
}