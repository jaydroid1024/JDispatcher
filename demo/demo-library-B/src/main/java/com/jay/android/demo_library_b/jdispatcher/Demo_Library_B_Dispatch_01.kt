package com.jay.android.demo_library_b.jdispatcher

import android.app.Application
import com.jay.android.dispatcher.annotation.Dispatch
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.dispatch.DispatchTemplate

/**
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Dispatch(
    name = "D_B_01",
    priority = 10,
    dependencies = ["D_A_02"],
    description = "组件B的01个Dispatch change"
)
class Demo_Library_B_Dispatch_01 : DispatchTemplate() {
    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        super.onCreate(app, dispatchItem)
    }
}