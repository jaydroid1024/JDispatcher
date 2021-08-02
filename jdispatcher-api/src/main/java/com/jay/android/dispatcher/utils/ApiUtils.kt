package com.jay.android.dispatcher.utils

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Process
import android.text.TextUtils
import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.launcher.JDispatcher

/**
 * Api Utils
 * @author jaydroid
 * @version 1.0
 * @date 7/16/21
 */
object ApiUtils {

    /**
     * 包名判断是否为主进程
     */
    @JvmStatic
    fun isMainProcess(): Boolean =
        TextUtils.equals(JDispatcher.instance.context?.packageName, getProcessName())

    /**
     * 获取进程全名
     */
    fun getProcessName(): String? {
        val am =
            JDispatcher.instance.context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                ?: return ""
        val runningApps = am.runningAppProcesses ?: return ""
        for (proInfo in runningApps) {
            if (proInfo.pid == Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName
                }
            }
        }
        return ""
    }


    fun getPackageInfo(context: Context): PackageInfo? {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_CONFIGURATIONS
            )
        } catch (ex: Exception) {
            Logger.error("Get package info error.");
        }
        return packageInfo
    }


    private val SP: SharedPreferences? by lazy {
        JDispatcher.instance.context?.getSharedPreferences(
            ApiConst.JDISPATCHER_SP_CACHE_KEY,
            Context.MODE_PRIVATE
        )
    }

    fun putString(key: String, value: String) {
        SP?.edit()?.putString(key, value)?.apply()
    }

    fun getString(key: String): String? {
        return SP?.getString(key, null)
    }

    fun putLong(key: String, value: Long) {
        SP?.edit()?.putLong(key, value)?.apply()
    }

    fun getLong(key: String): Long? {
        return SP?.getLong(key, -1)
    }

    fun putStringSet(key: String, value: Set<String>) {
        SP?.edit()?.putStringSet(key, value)?.apply()
    }

    fun getStringSet(key: String): MutableSet<String>? {
        return SP?.getStringSet(key, HashSet<String>())
    }
}