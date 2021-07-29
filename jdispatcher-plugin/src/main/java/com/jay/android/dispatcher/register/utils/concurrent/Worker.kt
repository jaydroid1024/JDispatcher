package com.jay.android.dispatcher.register.utils.concurrent

import java.io.IOException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.function.Consumer

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
open class Worker internal constructor(private var executor: ExecutorService) {

    private val futures = LinkedList<Future<*>>()

    fun execute(runnable: Runnable) {
        futures.add(executor.submit(runnable))
    }

    fun <T> submit(callable: Callable<T>?): Future<T> {
        val future = executor.submit(callable)
        futures.add(future)
        return future
    }

    @Throws(IOException::class)
    fun await() {
        var future: Future<*>
        while (futures.pollFirst().also { future = it } != null) {
            try {
                future.get()
            } catch (e: ExecutionException) {
                when (e.cause) {
                    is IOException -> {
                        throw (e.cause as IOException?)!!
                    }
                    is RuntimeException -> {
                        throw (e.cause as RuntimeException?)!!
                    }
                    is Error -> {
                        throw (e.cause as Error?)!!
                    }
                    else -> throw RuntimeException(e.cause)
                }
            } catch (e: InterruptedException) {
                when (e.cause) {
                    is IOException -> {
                        throw (e.cause as IOException?)!!
                    }
                    is RuntimeException -> {
                        throw (e.cause as RuntimeException?)!!
                    }
                    is Error -> {
                        throw (e.cause as Error?)!!
                    }
                    else -> throw RuntimeException(e.cause)
                }
            }
        }
    }

    @Throws(IOException::class)
    fun <I> submitAndAwait(taskList: Collection<I>, consumer: Consumer<I>) {
        taskList.stream().map { f: I ->
            Runnable {
                consumer.accept(f)
            }
        }.forEach { runnable: Runnable ->
            execute(runnable)
        }

        await()
    }
}