package com.jay.android.dispatcher.dispatch

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Trace
import com.jay.android.dispatcher.annotation.Dimension
import com.jay.android.dispatcher.common.CommonConst.METHOD_ON_CONFIGURATION_CHANGED
import com.jay.android.dispatcher.common.CommonConst.METHOD_ON_CREATE
import com.jay.android.dispatcher.common.CommonConst.METHOD_ON_LOW_MEMORY
import com.jay.android.dispatcher.common.CommonConst.METHOD_ON_TERMINATE
import com.jay.android.dispatcher.common.CommonConst.METHOD_ON_TRIM_MEMORY
import com.jay.android.dispatcher.common.CommonConst.METHOD_PROVIDER_ON_CREATE
import com.jay.android.dispatcher.common.CommonUtils
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.common.Warehouse
import com.jay.android.dispatcher.launcher.JDispatcher
import com.jay.android.dispatcher.thread.DefaultPoolExecutor
import com.jay.android.dispatcher.utils.ApiUtils
import com.jay.android.dispatcher.utils.ApiUtils.isMainProcess
import java.util.*

/**
 * Dispatch 分发帮助类
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
class DispatchHelper(private val sortedDispatchList: List<DispatchItem>?) {

    /**
     * 是否忽略该初始化
     * 忽略优先级：是否手动 > 是否debug > 是否空闲线程 > 是否主进程
     */
    private fun isIgnoreDispatch(dispatchItem: DispatchItem, dispatchMethod: String): Boolean {
        //只忽略 onCreate 方法的调用时机，
        if (dispatchMethod != METHOD_ON_CREATE && dispatchMethod != METHOD_PROVIDER_ON_CREATE) {
            return false
        }
        //手动延迟分发时忽略掉
        if (Dimension.isManual(dispatchItem.dimension)) {
            Logger.debug(
                "isManual, name: " + dispatchItem.name
                        + ", dimension: " + dispatchItem.dimension
                        + ", dimension str: " + Dimension.getDimensionStr(dispatchItem.dimension)
            )
            return true
        }
        //需要debug维度下分发的在release构建时忽略掉
        if (Dimension.isBuildDebug(dispatchItem.dimension) && !JDispatcher.instance.debuggable()) {
            Logger.debug(
                "isBuildDebug, name: " + dispatchItem.name
                        + ", dimension: " + dispatchItem.dimension
                        + ", dimension str: " + Dimension.getDimensionStr(dispatchItem.dimension)
            )
            return true
        }
        // ✔️ ❌     主进程，子进程
        // 所有进程   ✔️     ✔️
        // 主进程，   ✔️     ❌
        // 非主进程   ❌     ✔️
        //需要在主进程维度下分发的在其它进程运行时忽略掉, 需要在非主进程维度分发的在所有进程运行时忽略掉
        if ((Dimension.isProcessMain(dispatchItem.dimension) && !isMainProcess()) ||
            Dimension.isProcessOther(dispatchItem.dimension) && isMainProcess()
        ) {
            Logger.debug(
                "isProcessMain, name: " + dispatchItem.name
                        + ", dimension: " + dispatchItem.dimension
                        + ", dimension str: " + Dimension.getDimensionStr(dispatchItem.dimension)
            )
            return true
        }
        return false
    }


    private fun dispatchThreadWork(
        dispatchItem: DispatchItem,
        application: Application?
    ) {
        DefaultPoolExecutor.instance.execute {
            if (application != null) {
                invokeDispatchOnCreate(dispatchItem, application)
            }
        }
    }


    private fun dispatchThreadUiIdl(
        dispatchItem: DispatchItem,
        application: Application
    ) {
        IdleExecutor().addTask {
            invokeDispatchOnCreate(dispatchItem, application)
        }.start()
    }


    fun onProviderCreate(context: Context) {
        if (Warehouse.isDispatchListEmpty()) return
        dispatch(METHOD_PROVIDER_ON_CREATE) { dispatchItem ->
            if (Dimension.isProvider(dispatchItem.dimension)) {
                dispatchProviderOnCreate(dispatchItem, context)
            }
        }
    }

    private fun dispatchProviderOnCreate(dispatchItem: DispatchItem, context: Context) {
        //进程信息
        dispatchItem.processName = ApiUtils.getProcessName() ?: ""
        dispatchItem.time = CommonUtils.time("${dispatchItem.name}#$METHOD_PROVIDER_ON_CREATE  ") {
            dispatchItem.instance?.onPreCreate(context, dispatchItem)
        }
    }

    fun onCreate(
        application: Application,
        dispatchExtraParam: HashMap<String, HashMap<String, String>>
    ) {
        if (Warehouse.isDispatchListEmpty()) return
        dispatch(METHOD_ON_CREATE) { dispatchItem ->
            if (!Dimension.isProvider(dispatchItem.dimension)) {
                dispatchOnCreate(dispatchExtraParam, dispatchItem, application)
            }
        }
    }

    fun dispatchOnCreate(
        dispatchExtraParam: HashMap<String, HashMap<String, String>>,
        dispatchItem: DispatchItem,
        application: Application
    ) {
        //追加额外参数信息
        if (dispatchExtraParam.containsKey(dispatchItem.name)) {
            dispatchItem.extraParam = dispatchExtraParam[dispatchItem.name]
        }
        //进程信息
        dispatchItem.processName = ApiUtils.getProcessName()

        //分发 onCreate
        val time = CommonUtils.time("${dispatchItem.name}#$METHOD_ON_CREATE  ") {
            Trace.beginSection("${dispatchItem.name}#$METHOD_ON_CREATE  ")
            //需要在主线程空闲维度分发的直接过滤掉不参与排序
            when {
                //todo 目前是单独执行并没有处理UI/IDLE/Work线程交叉依赖的情况
                Dimension.isThreadUiIdle(dispatchItem.dimension) -> {
                    dispatchThreadUiIdl(dispatchItem, application)
                }
                Dimension.isThreadWork(dispatchItem.dimension) -> {
                    dispatchThreadWork(dispatchItem, application)
                }
                else -> {
                    invokeDispatchOnCreate(dispatchItem, application)
                }
            }
            Trace.endSection()
        }
        dispatchItem.time = time
    }

    @Synchronized
    private fun invokeDispatchOnCreate(
        dispatchItem: DispatchItem,
        application: Application
    ) {
        dispatchItem.instance?.onCreate(application, dispatchItem)
    }

    fun onTerminate() {
        dispatch(METHOD_ON_TERMINATE) { dispatchItem ->
            CommonUtils.time("${dispatchItem.name}#$METHOD_ON_TERMINATE ") {
                dispatchItem.instance?.onTerminate()
            }
        }
    }

    fun onConfigurationChanged(newConfig: Configuration?) {
        dispatch(METHOD_ON_CONFIGURATION_CHANGED) { dispatchItem ->
            CommonUtils.time("${dispatchItem.name}#$METHOD_ON_CONFIGURATION_CHANGED  ") {
                newConfig?.let { dispatchItem.instance?.onConfigurationChanged(it) }
            }
        }
    }

    fun onLowMemory() {
        dispatch(METHOD_ON_LOW_MEMORY) { dispatchItem ->
            CommonUtils.time("${dispatchItem.name}#$METHOD_ON_LOW_MEMORY  ") {
                dispatchItem.instance?.onLowMemory()
            }
        }
    }

    fun onTrimMemory(level: Int) {
        dispatch(METHOD_ON_TRIM_MEMORY) { dispatchItem ->
            CommonUtils.time("${dispatchItem.name}#$METHOD_ON_TRIM_MEMORY  ") {
                dispatchItem.instance?.onTrimMemory(level)
            }
        }
    }


    /**
     * 遍历列表执行分发流程
     *
     * @param action
     */
    private inline fun dispatch(
        dispatchMethod: String,
        action: (dispatchItem: DispatchItem) -> Unit
    ) {
        if (Warehouse.isDispatchListEmpty()) return
        for (dispatchItem in sortedDispatchList!!) {
            //添加忽略规则去除不符合分发维度的
            if (isIgnoreDispatch(dispatchItem, dispatchMethod)) {
                continue
            }
            //按照优先级和依赖项的分发操作
            action(dispatchItem)
        }
    }

}