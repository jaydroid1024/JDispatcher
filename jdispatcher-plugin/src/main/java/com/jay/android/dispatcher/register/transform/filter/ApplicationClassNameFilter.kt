package com.jay.android.dispatcher.register.transform.filter

class ApplicationClassNameFilter(private val appCanonicalName: String?) : ClassNameFilter {

    override fun filter(className: String): Boolean {
        if (className.isEmpty() || appCanonicalName.isNullOrEmpty()) {
            return false
        }
        if (className.contentEquals(appCanonicalName)) {
            return true
        }
        return false
    }
}