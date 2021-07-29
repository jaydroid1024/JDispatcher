package com.jay.android.dispatcher.common

import java.util.*

/**
 * 缓存所有 Dispatch 的仓库
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
object Warehouse {

    val dispatchOriginalMap: HashMap<String?, DispatchItem?>? by lazy {
        hashMapOf()
    }
    val dispatchSortedList: ArrayList<DispatchItem> by lazy {
        arrayListOf()
    }


    fun isDispatchListEmpty(): Boolean = dispatchSortedList.isEmpty()


    fun clear() {
        dispatchOriginalMap?.clear()
        dispatchSortedList.clear()
    }

}