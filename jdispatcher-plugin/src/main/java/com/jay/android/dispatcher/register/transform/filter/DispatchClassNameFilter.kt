package com.jay.android.dispatcher.register.transform.filter

import com.jay.android.dispatcher.register.utils.PluginConst

class DispatchClassNameFilter : ClassNameFilter {

    override fun filter(className: String): Boolean {
        if (className.isEmpty()) {
            return false
        }
        whiteList.forEach {
            if (className.startsWith(it)) {
                return true
            }
        }
        return false
    }

    companion object {
        val whiteList = mutableListOf<String>().apply {
            add(PluginConst.CLASS_OF_GENERATE_ALL_PREFIX)
        }
    }
}