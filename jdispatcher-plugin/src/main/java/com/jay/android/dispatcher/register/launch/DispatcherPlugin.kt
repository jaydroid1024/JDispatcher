package com.jay.android.dispatcher.register.launch

import com.android.build.gradle.AppExtension
import com.jay.android.dispatcher.register.transform.transform.DispatcherTransform
 import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.register.utils.PluginConst
import com.jay.android.dispatcher.register.utils.PluginUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 插件编译器所做工作：
 * 一，IDispatch 的分发流程
 * 1. 扫描到所有 APT 生成的 JDispatcher$$Group_hash.java 文件
 * 2. 反射获取收集到的 Map<String, DispatchItem> atlas)
 * 3. 通过 atlas 集合收集到的 DispatchItem 实现对 IDispatch 对象的反射实例化
 * 4. 按照 DispatchItem 的排序规则完成排序操作
 * 5. 将排好序的 IDispatch 集合通过字节码插桩到 JDispatcher 中，运行时执行对所有 IDispatch 的分发操作
 *
 * 二，Application 生命周期方法的自动注册流程
 * 1. 通过调用方配置的 Application 全类名扫描到该类
 * 2. JDispatcher 调用字节码注入到 onTerminate()
 * 3. JDispatcher 调用字节码注入到 onConfigurationChanged(newConfig: Configuration)
 * 4. JDispatcher 调用字节码注入到 onLowMemory()
 * 5. JDispatcher 调用字节码注入到 onTrimMemory(level: Int)
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/12/21
 */
open class DispatcherPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        Logger.debug("=====>apply project: $project<=====")
        if (PluginUtils.isApp(project)) {
            //注册 Extension
            project.extensions.create(
                PluginConst.DISPATCHER_EXTENSION_NAME,
                DispatcherExtension::class.java
            )

            //注册 Transform
            val android = project.extensions.getByType(AppExtension::class.java)
            val transformImpl = DispatcherTransform(project)
            android.registerTransform(transformImpl)

            project.afterEvaluate {
                val e: DispatcherExtension =
                    project.extensions.getByName(PluginConst.DISPATCHER_EXTENSION_NAME) as DispatcherExtension
                Logger.info(e.appCanonicalName)
            }

        }
    }
}