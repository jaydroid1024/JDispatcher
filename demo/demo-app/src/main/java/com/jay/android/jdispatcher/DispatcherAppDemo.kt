package com.jay.android.jdispatcher

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.jay.android.dispatcher.annotation.Dimension
import com.jay.android.dispatcher.annotation.Dispatch
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.dispatch.DispatchTemplate

/**
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Dispatch(
    priority = 17,
    dependencies = ["D_A_03"],
    dimension = Dimension.DIMENSION_DEFAULT,
    description = "DispatcherAppDemo"
)
class DispatcherAppDemo : DispatchTemplate() {
    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        Log.d(TAG, "DispatcherAppDemo#onCreate$dispatchItem")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "DispatcherAppDemo#onConfigurationChanged,newConfig=$newConfig")
    }

    override fun onLowMemory() {
        Log.d(TAG, "DispatcherAppDemo#onLowMemory")
    }

    override fun onTerminate() {
        Log.d(TAG, "DispatcherAppDemo#onTerminate")
    }

    override fun onTrimMemory(level: Int) {
        Log.d(TAG, "DispatcherAppDemo#onTrimMemory,level=$level")
    }
}