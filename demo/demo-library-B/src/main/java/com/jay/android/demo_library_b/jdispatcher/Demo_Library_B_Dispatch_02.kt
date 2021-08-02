package com.jay.android.demo_library_b.jdispatcher

import android.app.Application
import android.util.Log
import com.jay.android.dispatcher.annotation.Dispatch
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.dispatch.DispatchTemplate

/**
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Dispatch(
    name = "D_B_02",
    priority = 20,
    dependencies = ["D_B_01"],
    description = "组件B的02个Dispatch"
)
class Demo_Library_B_Dispatch_02 : DispatchTemplate() {
    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
//        super.onCreate(app, dispatchItem)
    }
}