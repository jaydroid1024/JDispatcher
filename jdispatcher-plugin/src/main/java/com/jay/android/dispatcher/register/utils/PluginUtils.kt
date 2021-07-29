package com.jay.android.dispatcher.register.utils

import com.android.build.api.transform.TransformInvocation
import com.jay.android.dispatcher.register.launch.DispatcherExtension
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/12/21
 */
object PluginUtils {

    val timeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }
    val logInfo: LinkedHashMap<String, Any> by lazy {
        LinkedHashMap()
    }


    /**
     * 是否是 application 模块
     *
     * @param project
     * @return
     */
    fun isApp(project: Project): Boolean {
        return project.plugins.hasPlugin("com.android.application")
    }

    /**
     * DispatcherExtension
     *
     * @param project
     * @return
     */
    fun getDispatcherExtension(project: Project): DispatcherExtension {
        return project.extensions.getByType(DispatcherExtension::class.java)
    }

    /**
     * writeLogToFile
     *
     * @param fileParent
     * @param transformInvocation
     * @param log
     */
    fun writeLogToFile(fileParent: File, transformInvocation: TransformInvocation, log: String) {
        val dispatcherLogDir = File(fileParent, PluginConst.PROJECT)
        if (dispatcherLogDir.exists()) {
            dispatcherLogDir.delete()
        }
        dispatcherLogDir.mkdirs()
        val variantName = transformInvocation.context.variantName
        val logFileName =
            "${PluginConst.PROJECT.toLowerCase(Locale.CHINA) + "_" + variantName}.json"
        FileUtils.writeStringToFile(File(dispatcherLogDir, logFileName), log)
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
    fun time(desc: String?, runnable: Runnable): Long {
        val time = time(runnable)
        com.jay.android.dispatcher.common.Logger.debug(
            String.format(
                "%s 耗时: %s ms\n\n",
                desc,
                time
            )
        )
        return time
    }

    /**
     * 统计执行时间
     */
    fun timeStr(desc: String?, runnable: Runnable): String {
        val msg = String.format("%s 耗时: %s ms\n\n", desc, time(runnable))
        com.jay.android.dispatcher.common.Logger.debug(msg)
        return msg
    }

}