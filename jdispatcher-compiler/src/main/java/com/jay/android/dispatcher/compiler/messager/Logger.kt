package com.jay.android.dispatcher.compiler.messager

import com.jay.android.dispatcher.compiler.utils.AptConst
import com.jay.android.dispatcher.compiler.utils.AptUtils
import javax.tools.Diagnostic
import javax.tools.Diagnostic.Kind.*

/**
 * Simplify the message print.
 * @author jaydroid
 * @version 1.0
 * @date 6/14/21
 */
object Logger {

    var isDebug: Boolean = true

    @JvmStatic
    fun printlnLog(info: String) {
        println(info)
    }

    @JvmStatic
    fun info(info: String) {
        if (isDebug) {
            log(NOTE, info)
        }
    }

    @JvmStatic
    fun warn(warn: String) {
        log(WARNING, warn)
    }

    @JvmStatic
    fun mandatoryWarn(mandatoryWarn: String) {
        log(MANDATORY_WARNING, mandatoryWarn)
    }

    @JvmStatic
    fun error(error: String) {
        log(ERROR, error)
    }

    @JvmStatic
    fun error(error: Throwable) {
        log(ERROR, "${error.message} \n ${formatStackTrace(error.stackTrace)}")
    }

    /**
     * Prints a message of the specified kind.
     *
     * @param kind the kind of message
     * @param message  the message, or an empty string if none
     */
    private fun log(kind: Diagnostic.Kind, message: String) {
        if (!AptUtils.AptContext.isInitFinish) {
            throw IllegalStateException("You should call AptContext#init() to init Messager first")
        }
        AptUtils.AptContext.msg.printMessage(
            kind, "${AptConst.TAG} ${prefixMsg(kind)}${message} \r\n"
        )
    }
}


private fun prefixMsg(kind: Diagnostic.Kind): String {
    return when (kind) {
        ERROR -> "An exception is encountered,"
        else -> ""
    }
}


private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String {
    val sb = StringBuilder()
    for (element in stackTrace) {
        sb.append("    at ").append(element.toString())
        sb.append("\r\n")
    }
    return sb.toString()
}





