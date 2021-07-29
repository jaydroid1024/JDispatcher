package com.jay.android.dispatcher.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/12/21
 */
object CommonUtils {

    val PRETTY_GSON: Gson by lazy { GsonBuilder().setPrettyPrinting().create() }

    val GSON: Gson by lazy { Gson() }

    fun prettyToJson(any: Any): String? {
        return PRETTY_GSON.toJson(any)
    }

    fun toJson(any: Any): String? {
        return GSON.toJson(any)
    }

    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(json: String?, classOfT: Class<T>?): T {
        return GSON.fromJson(json, classOfT)
    }

    /**
     * 统计执行时间
     */
    fun time(runnable: Runnable): Long {
        val startTime = System.currentTimeMillis()
        runnable.run()
        return System.currentTimeMillis() - startTime
    }

    /**
     * 统计执行时间
     */
    @JvmStatic
    fun time(desc: String?, runnable: Runnable): Long {
        val time = time(runnable)
        Logger.debug(String.format("%s 耗时: %s ms\n\n", desc, time))
        return time
    }

    /**
     * 统计执行时间
     */
    @JvmStatic
    fun timeStr(desc: String?, runnable: Runnable): String {
        val msg = String.format("%s 耗时: %s ms\n\n", desc, time(runnable))
        Logger.debug(msg)
        return msg
    }

}