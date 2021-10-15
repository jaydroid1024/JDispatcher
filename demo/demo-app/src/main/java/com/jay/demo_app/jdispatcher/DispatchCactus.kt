package com.jay.demo_app.jdispatcher

import android.app.Application
import com.jay.android.dispatcher.annotation.Dimension
import com.jay.android.dispatcher.annotation.Dispatch
import com.jay.android.dispatcher.annotation.Priority
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.dispatch.DispatchTemplate

/**
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Dispatch(
    priority = Priority.LOW_DEFAULT,
    dimension = Dimension.PROCESS_ALL or Dimension.THREAD_UI or Dimension.BUILD_ALL or Dimension.AUTOMATIC,
)
class DispatchCactus : DispatchTemplate() {
    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        super.onCreate(app, dispatchItem)

    }
}