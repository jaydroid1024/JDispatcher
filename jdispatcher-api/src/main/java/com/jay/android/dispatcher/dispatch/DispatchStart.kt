package com.jay.android.dispatcher.dispatch

import android.app.Application
import com.jay.android.dispatcher.common.DispatchItem

/**
 * 分发队列起止任务
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
open class DispatcherStart : DispatchTemplate() {

    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        super.onCreate(app, dispatchItem)

    }

}

open class DispatcherEnd : DispatchTemplate() {

    override fun onCreate(app: Application, dispatchItem: DispatchItem) {
        super.onCreate(app, dispatchItem)

    }

}