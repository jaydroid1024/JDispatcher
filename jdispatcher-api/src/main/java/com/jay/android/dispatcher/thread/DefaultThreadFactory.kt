package com.jay.android.dispatcher.thread

import com.jay.android.dispatcher.common.Logger
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 线程池工厂类
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
class DefaultThreadFactory : ThreadFactory {
    private val threadNumber = AtomicInteger(1)
    private val group: ThreadGroup
    private val namePrefix: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = "JDispatcher task pool No." + poolNumber.getAndIncrement() + ", thread No."
    }

    override fun newThread(runnable: Runnable): Thread {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        Logger.info("Thread production, name is [$threadName]")
        val thread = Thread(group, runnable, threadName, 0)
        //设为非后台线程
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        //优先级为normal
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }


        // 捕获多线程处理中的异常
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, ex ->
            Logger.info(
                "Running task appeared exception! Thread [" + thread.name + "], because [" + ex.message + "]"
            )
        }
        return thread
    }

    companion object {
        private val poolNumber = AtomicInteger(1)
    }

}