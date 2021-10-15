package com.jay.android.demo_library_a

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.multidex.MultiDexApplication
import com.jay.android.dispatcher.common.Warehouse
import com.jay.android.dispatcher.launcher.JDispatcher
import java.util.*

/**
 * @author jaydroid
 * @version 1.0
 * @date 6/2/21
 */
class App : MultiDexApplication() {


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        val name = getProcessName()
        Log.d("JDispatcher", "------App onCreate in process: $name")
        Log.d("JDispatcher", "------dispatchSortedList size : ${Warehouse.dispatchSortedList.size}")


        //init 已经在 InitializationProvider 中提前初始化，到这里需要分发onCreate 或其他配置
        JDispatcher.instance
            .withDispatchExtraParam(getDispatchExtraParam())//分发参数
            .withDebugAble(true) //调试模式，打印更多日志
            .onCreate(this)//分发onCreate

    }

    private fun getDispatchExtraParam(): HashMap<String, HashMap<String, String>> {
        //为分发类指定自定义参数，用于三方key的统一收口配置
        val dispatchExtraParam = HashMap<String, HashMap<String, String>>()
        dispatchExtraParam["com.jay.android.jdispatcher.DispatcherStart"] =
            if (BuildConfig.DEBUG) hashMapOf(
                Pair("key1", "value1_debug"),
                Pair("key2", "value2_debug")
            )
            else hashMapOf(
                Pair("key1", "value1_release"),
                Pair("key2", "value2_release")
            )
        return dispatchExtraParam
    }

    // region如果在 app build.gradle 中配置了
// dispatcher {appCanonicalName = "com.jay.android.App"}
// 就不需要添加以下代码,dispatcher 插件会自动注入
//
//    override fun onTerminate() {
//        super.onTerminate()
//        JDispatcher.instance.onTerminate()
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        JDispatcher.instance.onConfigurationChanged(newConfig)
//    }
    //
//    override fun onLowMemory() {
//        super.onLowMemory()
//        JDispatcher.instance.onLowMemory()
//    }
//
//    override fun onTrimMemory(level: Int) {
//        super.onTrimMemory(level)
//        JDispatcher.instance.onTrimMemory(level)
//    }
//endregion


}