package com.jay.android.dispatcher.thread

import com.jay.android.dispatcher.common.Logger
import java.util.concurrent.*

/**
 * 线程池
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
class DefaultPoolExecutor private constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    threadFactory,
    RejectedExecutionHandler { r, executor ->
        Logger.error("Task rejected, too many task!")
    }) {

    override fun afterExecute(runnable: Runnable?, throwable: Throwable?) {
        var t: Throwable? = throwable
        super.afterExecute(runnable, throwable)
        if (t == null && runnable is Future<*>) {
            try {
                (runnable as Future<*>).get()
            } catch (ce: CancellationException) {
                t = ce
            } catch (ee: ExecutionException) {
                t = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt() // ignore/reset
            }
        }
        if (t != null) {
            Logger.warning(
                "Running task appeared exception! Thread ["
                        + Thread.currentThread().name + "], because ["
                        + t.message + "]\n"
                        + t.stackTrace
            )
        }
    }

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val INIT_THREAD_COUNT = CPU_COUNT + 1
        private val MAX_THREAD_COUNT = INIT_THREAD_COUNT
        private const val SURPLUS_THREAD_LIFE = 30L

        @JvmStatic
        val instance: DefaultPoolExecutor by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DefaultPoolExecutor(
                INIT_THREAD_COUNT,
                MAX_THREAD_COUNT,
                SURPLUS_THREAD_LIFE,
                TimeUnit.SECONDS,
                ArrayBlockingQueue(64),
                DefaultThreadFactory()
            )
        }
    }
}