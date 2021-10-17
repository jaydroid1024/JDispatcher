package com.jay.android.dispatcher.register.transform.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.register.launch.DispatcherExtension
import com.jay.android.dispatcher.register.utils.PluginConst
import com.jay.android.dispatcher.register.utils.PluginUtils
import org.gradle.api.Project
import java.io.IOException

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/14/21
 */
open class BaseTransform(val project: Project) : Transform() {

    var dispatcherExtension: DispatcherExtension? = null

    /**
     * Transform 的名字，也是代表该 Transform 的 Task 的名字 transformClassesWithJDispatcherForDebug
     *
     * @return
     */
    override fun getName(): String {
        return PluginConst.PROJECT
    }

    override fun isIncremental(): Boolean {
        return PluginUtils.getDispatcherExtension(project).buildIncremental
    }

    /**
     * Transform 的输入类型，过滤字节码
     *
     * @return
     */
    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * Transform 的作用域，扫描整个项目
     * @return
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(invocation: TransformInvocation) {
        beforeTransform()
        handleTransform(invocation)
        afterTransform()
    }

    open fun afterTransform() {
        Logger.debug("afterTransform...")
        Logger.debug("dispatcherExtension" + dispatcherExtension.toString())
    }

    open fun beforeTransform() {
        dispatcherExtension = PluginUtils.getDispatcherExtension(project)
        dispatcherExtension?.let { setLogger(it) }
        Logger.debug("beforeTransform...")

    }

    open fun handleTransform(invocation: TransformInvocation) {
        //子类实现
    }

    private fun setLogger(extension: DispatcherExtension) {
        Logger.showLog(extension.buildDebug)
        Logger.showStackTrace(extension.buildDebug)
    }


}