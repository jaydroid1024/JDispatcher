package com.jay.android.dispatcher.register.utils.concurrent

import java.util.concurrent.*

/**
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
object Schedulers {

    //Java虚拟机可用的最大处理器数
    private val cpuCount = Runtime.getRuntime().availableProcessors()

    //IO密集型:是指系统大部分时间在跟I/O交互，例如文件读写操作等，
    // 在这个过程中线程不会占用 CPU，所以在这个时间范围内，可以尽可能的由其他线程来使用 CPU，
    // 因而可以多配置一些线程，通常是 CPU 核数的两倍。
    private val IO: ExecutorService = ThreadPoolExecutor(
        0,
        cpuCount * 3,
        30L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue()
    )

    //CPU密集型：是指系统中对 CPU 使用率较高，例如数字运算、赋值、分配内存、内存拷贝、循环、查找、排序等，
    // 这些处理都需要 CPU 来完成，因而可以少配置一些线程，一般只需要 CPU 核数的线程就可以了。
    private val CPU = Executors.newWorkStealingPool(cpuCount)


    fun IO_Worker(): Worker {
        return Worker(IO)
    }

    fun CPU_Worker(): Worker {
        return Worker(CPU)
    }

}