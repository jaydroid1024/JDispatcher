package com.jay.android

import android.app.Application
import android.util.Log
import com.jay.android.dispatcher.launcher.JDispatcher
import com.jay.android.jdispatcher.BuildConfig

/**
 * @author jaydroid
 * @version 1.0
 * @date 6/2/21
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //自动分发
        JDispatcher.instance
            .withDebugAble(true)
            .init(this)

    }



// region 如果在 app build.gradle 中配置了
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