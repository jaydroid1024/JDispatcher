package com.jay.android.dispatcher.register.transform.filter

import com.jay.android.dispatcher.register.utils.PluginConst

class JDispatcherClassNameFilter : ClassNameFilter {

    override fun filter(className: String): Boolean {
        if (className.isEmpty()) {
            return false
        }
        whiteList.forEach {
            if (className.contentEquals(it)) {
                return true
            }
        }
        return false
    }

    companion object {
        val whiteList = mutableListOf<String>().apply {
            add(PluginConst.CLASS_OF_JDISPATCHER)
        }
    }
}