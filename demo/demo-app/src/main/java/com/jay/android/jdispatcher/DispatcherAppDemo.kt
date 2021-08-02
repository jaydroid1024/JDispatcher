package com.jay.android.jdispatcher

import android.app.Application
import android.content.res.Configuration
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
        super.onCreate(app, dispatchItem)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}