package com.jay.android.dispatcher.dispatch

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.jay.android.dispatcher.common.CommonConst
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.common.IDispatch

/**
 * 分发 Application 生命周期的模板抽象类
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
abstract class DispatchTemplate : IDispatch {

    open val TAG: String = this.javaClass.simpleName

    open var app: Application? = null

    open var context: Context? = null

    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        Log.d(TAG, "${TAG}#onCreate, dispatchItem: $dispatchItem")
        Log.d(CommonConst.TAG, "${TAG}#onCreate, dispatchItem: $dispatchItem")
        this.app = app
    }

    override fun onPreCreate(context: Context, dispatchItem: DispatchItem) {
        Log.d(TAG, "${TAG}#onCreate, dispatchItem: $dispatchItem")
        Log.d(CommonConst.TAG, "${TAG}#onCreate, dispatchItem: $dispatchItem")
        this.context = context
    }

    override fun onTerminate() {
        Log.d(TAG, "${TAG}#onTerminate")
        Log.d(CommonConst.TAG, "${TAG}#onTerminate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "${TAG}#onConfigurationChanged,newConfig=$newConfig")
        Log.d(CommonConst.TAG, "${TAG}#onConfigurationChanged,newConfig=$newConfig")
    }

    override fun onLowMemory() {
        Log.d(TAG, "${TAG}#onLowMemory")
        Log.d(CommonConst.TAG, "${TAG}#onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        Log.d(TAG, "${TAG}#onTrimMemory,level=$level")
        Log.d(CommonConst.TAG, "${TAG}#onTrimMemory,level=$level")
    }

}