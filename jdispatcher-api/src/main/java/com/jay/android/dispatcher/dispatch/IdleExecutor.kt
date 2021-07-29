package com.jay.android.dispatcher.dispatch

import android.os.Looper
import android.os.MessageQueue
import java.util.*

/**
 * 空闲任务执行器
 * @author jaydroid
 * @version 1.0
 * @date 7/23/21
 */
class IdleExecutor {
    private val queue: Queue<() -> Unit> = LinkedList()
    private val idleHandler = MessageQueue.IdleHandler {
        if (queue.size > 0) {
            val task = queue.poll()
            task?.invoke()
        }
        !queue.isEmpty()
    }

    fun addTask(task: () -> Unit): IdleExecutor {
        queue.add(task)
        return this
    }

    fun start() {
        Looper.myQueue().addIdleHandler(idleHandler)
    }

}