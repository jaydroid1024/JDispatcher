package com.jay.demo_app.jdispatcher

import android.app.Application
import android.content.res.Configuration
import android.os.Looper
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
    dimension = Dimension.PROCESS_ALL or Dimension.THREAD_UI or Dimension.BUILD_ALL or Dimension.AUTOMATIC,
    description = "DispatcherAppDemo"
)
class DispatcherAppDemo : DispatchTemplate() {

    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        super.onCreate(app, dispatchItem)
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "DispatcherAppDemo. postDelayed")
        }, 1000)

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