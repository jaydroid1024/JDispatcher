package com.jay.android.dispatcher.dispatch

import android.app.Application
import android.content.res.Configuration
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

    abstract override fun onCreate(app: Application, dispatchItem: DispatchItem)

    override fun onTerminate() {
        // todo
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // todo
    }

    override fun onLowMemory() {
        // todo
    }

    override fun onTrimMemory(level: Int) {
        // todo
    }

}