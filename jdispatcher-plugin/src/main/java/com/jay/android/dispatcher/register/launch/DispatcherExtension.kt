package com.jay.android.dispatcher.register.launch

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/12/21
 */
open class DispatcherExtension {

    // Application 的类全名，配置后注入字节码自动回调 JDispatcher 对应的 Application 生命周期方法
    var appCanonicalName: String = ""

    // 是否是增量编译，开启增量编译可以优化编译时间
    var buildIncremental: Boolean = false

    // 是否是调试模式，调试模式可打印更多的调试日志
    var buildDebug: Boolean = false

    override fun toString(): String {
        return "DispatcherExtension(appCanonicalName='$appCanonicalName', buildIncremental=$buildIncremental, buildDebug=$buildDebug)"
    }


}