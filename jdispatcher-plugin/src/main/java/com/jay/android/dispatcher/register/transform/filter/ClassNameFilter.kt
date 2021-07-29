package com.jay.android.dispatcher.register.transform.filter

interface ClassNameFilter {
    fun filter(className: String): Boolean
}