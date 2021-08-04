package com.jay.android.dispatcher.dispatch

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.jay.android.dispatcher.common.CommonConst
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.common.IDispatch
import com.jay.android.dispatcher.utils.ApiUtils

/**
 * 分发 Application 生命周期的模板抽象类
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
abstract class DispatchTemplate : IDispatch {

    open val TAG: String = this.javaClass.simpleName

    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        //线程信息
        dispatchItem.thread = ApiUtils.getThreadName()
        Log.d(CommonConst.TAG, "${TAG}#onCreate, dispatchItem: $dispatchItem")
        DispatchTemplate.app = app
    }

    override fun onPreCreate(context: Context, dispatchItem: DispatchItem) {
        //线程信息
        dispatchItem.thread = ApiUtils.getThreadName()
        Log.d(CommonConst.TAG, "${TAG}#onCreate, dispatchItem: $dispatchItem")
        DispatchTemplate.context = context
    }

    override fun onTerminate() {
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
    }

    override fun onLowMemory() {
    }

    override fun onTrimMemory(level: Int) {
    }

    companion object {

        var app: Application? = null

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

    }

}