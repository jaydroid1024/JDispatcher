package com.jay.android.dispatcher.common

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/9/21
 */
object CommonConst {
    const val PROJECT = "JDispatcher"
    const val SEPARATOR = "$$"
    const val DOT = "."
    const val TAG = "$PROJECT::Common >>>> >>>> "
    const val PACKAGE_OF_GENERATE_FILE = "com.jay.android.dispatcher.generate.dispatch"
    const val PACKAGE_OF_GENERATE_DOCS = "com.jay.android.dispatcher.docs"
    const val CLASS_OF_GENERATE_PREFIX: String = PROJECT + SEPARATOR + "Group_"
    const val CLASS_OF_GENERATE_ALL_PREFIX: String =
        PACKAGE_OF_GENERATE_FILE + DOT + CLASS_OF_GENERATE_PREFIX


    const val METHOD_ON_CREATE = "onCreate"
    const val METHOD_ON_TERMINATE = "onTerminate"
    const val METHOD_ON_CONFIGURATION_CHANGED = "onConfigurationChanged"
    const val METHOD_ON_LOW_MEMORY = "onLowMemory"
    const val METHOD_ON_TRIM_MEMORY = "onTrimMemory"

}