package com.jay.android

import androidx.multidex.MultiDexApplication
import com.jay.android.dispatcher.launcher.JDispatcher
import com.jay.android.jdispatcher.BuildConfig
import java.util.*


/**
 * @author jaydroid
 * @version 1.0
 * @date 6/2/21
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        //为分发类指定自定义参数，用于三方key的统一收口配置
        val dispatchExtraParam = HashMap<String, HashMap<String, String>>()
        dispatchExtraParam["com.jay.android.jdispatcher.DispatcherAppDemo"] =
            if (BuildConfig.DEBUG) hashMapOf(
                Pair("key1", "value1_debug"),
                Pair("key2", "value2_debug")
            )
            else hashMapOf(
                Pair("key1", "value1_release"),
                Pair("key2", "value2_release")
            )

        //自动分发
        JDispatcher.instance
            .withDebugAble(true)//调试模式：打印更多日志，实时刷新等
            .withDispatchExtraParam(dispatchExtraParam)//分发参数
            .init(this)

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