package com.jay.android.dispatcher.register.utils

/**
 * JDispatcher 常量
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
object PluginConst {
    const val PROJECT = "JDispatcher"
    const val SEPARATOR = "$$"
    const val DOT = "."
    const val CLASS = "class"
    const val DISPATCHER_EXTENSION_NAME = "dispatcher"

    //JDispatcher::Plugin >>> >>>
    const val TAG = "$PROJECT::Plugin >>> >>> "
    const val CLASS_OF_JDISPATCHER = "com.jay.android.dispatcher.launcher.JDispatcher"
    const val METHOD_OF_JDISPATCHER_TO_INJECT = "registerDispatchSortedList"

    //com.jay.android.dispatcher.generate.dispatch.JDispatcher$$Group_
    const val CLASS_OF_GENERATE_PREFIX: String = PROJECT + SEPARATOR + "Group_"
    const val PACKAGE_OF_GENERATE_FILE = "com.jay.android.dispatcher.generate.dispatch"
    const val CLASS_OF_GENERATE_ALL_PREFIX: String =
        PACKAGE_OF_GENERATE_FILE + DOT + CLASS_OF_GENERATE_PREFIX


}