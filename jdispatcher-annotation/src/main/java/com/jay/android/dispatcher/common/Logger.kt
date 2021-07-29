package com.jay.android.dispatcher.common

/**
 * JDispatcher 日志打印工具
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
object Logger {

    private var isShowLog = true

    private var TAG = CommonConst.TAG

    private var isShowStackTrace = false

    @JvmStatic
    fun showLog(showLog: Boolean) {
        isShowLog = showLog
    }

    @JvmStatic
    fun showStackTrace(showStackTrace: Boolean) {
        isShowStackTrace = showStackTrace
    }

    fun setTag(tag: String) {
        TAG = tag
    }

    @JvmStatic
    fun debug(message: String) {
        if (isShowLog) {
            printLog(message)
        }
    }

    @JvmStatic
    fun info(message: String) {
        if (isShowLog) {
            printLog(message)
        }
    }

    @JvmStatic
    fun warning(message: String) {
        if (isShowLog) {
            printLog(message)
        }
    }


    @JvmStatic
    fun error(message: String) {
        printLog(message)
    }

    @JvmStatic
    fun error(message: String?, e: Throwable?) {
        printLog(message + e?.message)
    }

    private fun getExtInfo(): String {
        val stackTraceElement = Thread.currentThread().stackTrace[3]
        val separator = " & "
        val sb = StringBuilder("[")
        if (isShowStackTrace) {
            val threadName = Thread.currentThread().name
            val fileName = stackTraceElement.fileName
            val className = stackTraceElement.className
            val methodName = stackTraceElement.methodName
            val threadID = Thread.currentThread().id
            val lineNumber = stackTraceElement.lineNumber
            sb.append("ThreadId=").append(threadID).append(separator)
            sb.append("ThreadName=").append(threadName).append(separator)
            sb.append("FileName=").append(fileName).append(separator)
            sb.append("ClassName=").append(className).append(separator)
            sb.append("MethodName=").append(methodName).append(separator)
            sb.append("LineNumber=").append(lineNumber)
        }
        sb.append(" ] ")
        return sb.toString()
    }


     fun printLog(message: String) {
         println(TAG + message)
     }

}