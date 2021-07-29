package com.jay.android.dispatcher.register.transform.filter

class ExcludeClassNameFilter : ClassNameFilter {

    override fun filter(className: String): Boolean {
        if (className.isEmpty()) {
            return false
        }
        blackList.forEach {
            if (className.startsWith(it)) {
                return true
            }
        }
        return false
    }

    companion object {
        val blackList = mutableListOf<String>().apply {
            add("kotlin")
            add("androidx")
            add("android")
            add("module-info")
            add("com/google/")
        }
    }
}