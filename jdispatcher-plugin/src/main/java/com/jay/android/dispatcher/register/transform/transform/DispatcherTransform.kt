package com.jay.android.dispatcher.register.transform.transform

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.jay.android.dispatcher.annotation.Dimension
import com.jay.android.dispatcher.common.*
import com.jay.android.dispatcher.register.asm.ASMHelper
import com.jay.android.dispatcher.register.asm.ApplicationClassVisitor
import com.jay.android.dispatcher.register.asm.JDispatcherClassVisitor
import com.jay.android.dispatcher.register.transform.callback.TransformScanCallBack
import com.jay.android.dispatcher.register.transform.filter.ApplicationClassNameFilter
import com.jay.android.dispatcher.register.transform.filter.DispatchClassNameFilter
import com.jay.android.dispatcher.register.transform.filter.JDispatcherClassNameFilter
import com.jay.android.dispatcher.register.transform.scan.TransformScanHelper
import com.jay.android.dispatcher.register.utils.ClassUtils
import com.jay.android.dispatcher.register.utils.PluginConst
import com.jay.android.dispatcher.register.utils.PluginUtils
import com.jay.android.dispatcher.register.utils.PluginUtils.getDispatcherExtension
import com.jay.android.dispatcher.register.utils.PluginUtils.logInfo
import com.jay.android.dispatcher.register.utils.PluginUtils.time
import com.jay.android.dispatcher.register.utils.PluginUtils.timeFormat
import com.jay.android.dispatcher.sort.DispatchListSortHelper
import groovy.lang.GroovyClassLoader
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import kotlin.collections.HashSet

/**
 * DispatcherTransform
 * @author jaydroid
 * @version 1.0
 * @date 6/28/21
 */
class DispatcherTransform(project: Project) : BaseTransform(project) {

    // IDispatchGroup classes
    val dispatchGroupClassNameFilter: DispatchClassNameFilter by lazy {
        DispatchClassNameFilter()
    }
    val dispatchClassSet: HashSet<String> by lazy { HashSet() }

    //Application class
    val applicationClassNameFilter: ApplicationClassNameFilter by lazy {
        ApplicationClassNameFilter(getDispatcherExtension(project).appCanonicalName)
    }
    var applicationClassName: String? = null
    var applicationClassFile: File? = null

    //JDispatcher class
    val jDispatcherClassNameFilter: JDispatcherClassNameFilter by lazy {
        JDispatcherClassNameFilter()
    }
    var jDispatcherClassName: String? = null
    var jDispatcherClassFile: File? = null

    var groovyClassLoader: GroovyClassLoader? = null

    override fun beforeTransform() {
        super.beforeTransform()
        // ?????? Variant ?????????????????? transform ???????????? beforeTransform ??????????????? ClassLoader??????????????? Variant ????????????????????????????????????
        groovyClassLoader = GroovyClassLoader()

        val android = project.extensions.getByType(AppExtension::class.java)
        val androidBootClasspath = android.bootClasspath[0].toString()
        groovyClassLoader?.addClasspath(androidBootClasspath)
    }

    override fun handleTransform(invocation: TransformInvocation) {
        logInfo["compile_date"] = timeFormat.format(Date())
        logInfo["transform_total_time"] = time("transform ") {
            try {
                logInfo["scan_time"] = time("scan ") {
                    //?????? jar&aar ?????????????????????
                    scan(invocation)
                }
                logInfo["handle_time"] = time("handle ") {
                    //???????????????
                    handle()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.error(e.message.toString())
            } finally {
                groovyClassLoader?.clearCache()
                groovyClassLoader?.close()
            }
        }

        //????????????????????????
        PluginUtils.writeLogToFile(
            project.buildDir,
            invocation,
            CommonUtils.PRETTY_GSON.toJson(logInfo)
        )
    }


    /**
     * ???????????????
     */
    private fun handle() {

        //?????? JDispatcher
        if (jDispatcherClassName.isNullOrEmpty() || jDispatcherClassFile?.exists() == false) {
            throw  IllegalStateException("???????????? ${PluginConst.CLASS_OF_JDISPATCHER}")
        }

        modifyJarClass(jDispatcherClassFile)

        //?????? Application
        if (applicationClassName.isNullOrEmpty() || applicationClassFile?.exists() == false) {
            Logger.warning("????????????appCanonicalName ")
            return
        }

        if (applicationClassFile?.isDirectory == true) {
            modifyDirectoryClass(applicationClassFile, applicationClassName)
        } else {
            modifyJarClass(applicationClassFile)
        }
    }

    fun modifyDirectoryClass(destDir: File?, className: String?) {
        if (destDir == null || className.isNullOrEmpty()) {
            return
        }
        val entryName = ClassUtils.classname2path(className, true)
        val classFile = File(destDir, entryName)
        FileInputStream(classFile).use { fis ->
            val bytes = modifyDirectoryClass(fis, className) ?: return
            FileOutputStream(classFile).use { fos ->
                fos.write(bytes)
            }
        }
    }

    fun modifyDirectoryClass(inputStream: InputStream, className: String): ByteArray? {
        if (className == applicationClassName) {
            //  Application
            return ASMHelper.modifyClass(inputStream) {
                ApplicationClassVisitor(it)
            }
        }
        return null
    }

    /**
     * modify JarClass
     * @param inputJarFile
     */
    private fun modifyJarClass(inputJarFile: File?) {
        Logger.debug("modifyJarClass???inputJarFile: $inputJarFile ")
        if (inputJarFile == null || !inputJarFile.exists() || !inputJarFile.name.endsWith(".jar")) {
            return
        }
        val tempJarFile = File(inputJarFile.parent, inputJarFile.name + ".temp")
        if (tempJarFile.exists()) {
            tempJarFile.delete()
        }
        JarFile(inputJarFile).use { jarFile ->
            JarOutputStream(FileOutputStream(tempJarFile)).use { jarOutputStream ->
                val enumeration = jarFile.entries()
                while (enumeration.hasMoreElements()) {
                    val jarEntry = enumeration.nextElement()
                    val entryName = jarEntry.name
                    jarOutputStream.putNextEntry(ZipEntry(entryName))
                    jarFile.getInputStream(jarEntry).use { jarEntryInputStream ->
                        //???????????????????????????????????? null ??????????????????
                        var bytes = modifyJarClass(jarEntryInputStream, entryName)
                        if (bytes == null) {
                            bytes = IOUtils.toByteArray(jarEntryInputStream)
                        }
                        jarOutputStream.write(bytes!!)
                        jarOutputStream.closeEntry()
                    }
                }
            }
        }
        inputJarFile.delete()
        tempJarFile.renameTo(inputJarFile)
    }


    fun modifyJarClass(inputStream: InputStream, entryName: String): ByteArray? {
        val checkClassName: String = ClassUtils.path2Classname(entryName)
        // com.jay.android.dispatcher.launcher.JDispatcher
        if (checkClassName == PluginConst.CLASS_OF_JDISPATCHER) {
            //load dispatch group class
            loadDispatchFromGroup()
            var dispatchSortedList: ArrayList<DispatchItem>? = null
            logInfo["sort_time"] = time("sort ") {
                //todo ??????????????????
                dispatchSortedList = DispatchListSortHelper.sortDispatchFromMap()
            }
            if (dispatchSortedList.isNullOrEmpty()) {
                return null
            }
            //????????????????????????????????????
            logSortedDispatchInfo(dispatchSortedList)

            return ASMHelper.modifyClass(inputStream) { classWriter ->
                JDispatcherClassVisitor(classWriter, dispatchSortedList!!)
            }

        } else if (checkClassName == applicationClassName) {
            // Application
            return ASMHelper.modifyClass(inputStream) { classWriter ->
                ApplicationClassVisitor(classWriter)
            }
        }
        return null
    }

    private fun logSortedDispatchInfo(dispatchSortedList: ArrayList<DispatchItem>?) {
        val dispatchSubInfoList = arrayListOf<Map<String, String>>()

        dispatchSortedList?.forEach {
            val dispatchSubInfo = linkedMapOf<String, String>()
            dispatchSubInfo["name"] = it.name
            dispatchSubInfo["priority"] = it.priority.toString()
            val dimensionText =
                Dimension.getProcess(it.dimension) +
                        " | " + Dimension.getThread(it.dimension) +
                        " | " + Dimension.getBuild(it.dimension) +
                        " | " + Dimension.getManual(it.dimension)
            dispatchSubInfo["dimension"] = dimensionText
            dispatchSubInfo["dependencies"] = it.dependencies.toList().toString()
            dispatchSubInfoList.add(dispatchSubInfo)
        }

        Logger.debug("dispatch_sorted_sub_info: $dispatchSubInfoList")

        logInfo["dispatch_sorted_sub_info"] = dispatchSubInfoList
        logInfo["dispatch_sorted_list"] = dispatchSortedList!!
    }


    private fun loadDispatchFromGroup() {
        //????????????????????????
        Warehouse.clear()
        dispatchClassSet.forEach { className ->
            val dispatchClazz = loadClass(className)?.newInstance()
            if (dispatchClazz is IDispatchGroup) {
                dispatchClazz.loadInto(Warehouse.dispatchOriginalMap)
            }
        }
    }

    /**
     * ????????????????????? Class
     * @param name
     * @return Class
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    fun loadClass(name: String): Class<*>? {
        return groovyClassLoader?.loadClass(name)
    }

    /**
     * ??????jar aar ?????????????????????
     * @param invocation
     */
    private fun scan(invocation: TransformInvocation) {
        //????????????????????????????????????????????????????????????????????????
        val transformScanHelper =
            TransformScanHelper(invocation, project, groovyClassLoader, transformScanCallBack)
        //todo ???????????????????????????
        transformScanHelper.openSimpleScan()
        transformScanHelper.startTransform()
    }

    val transformScanCallBack = object : TransformScanCallBack {
        override fun processScan(
            className: String,
            classBytes: ByteArray?,
            dest: File
        ): ByteArray? {
            val checkClassName: String = ClassUtils.path2Classname(className)
            if (dispatchGroupClassNameFilter.filter(checkClassName)) {
                dispatchClassSet.add(checkClassName)
                Logger.debug("Transform: dispatchGroupClassNameFilter, className = $className, checkClassName = $checkClassName")
                Logger.debug("Transform: dispatchGroupClassNameFilter, dest = ${dest.absolutePath}")
            }

            if (applicationClassNameFilter.filter(checkClassName)) {
                applicationClassName = checkClassName
                applicationClassFile = dest
                Logger.debug("Transform: applicationClassNameFilter, className = $className, checkClassName = $checkClassName")
                Logger.debug("Transform: applicationClassNameFilter, dest = ${dest.absolutePath}")
            }

            if (jDispatcherClassNameFilter.filter(checkClassName)) {
                jDispatcherClassName = checkClassName
                jDispatcherClassFile = dest
                Logger.debug("Transform: jDispatcherClassNameFilter, className = $className, checkClassName = $checkClassName")
                Logger.debug("Transform: jDispatcherClassNameFilter, dest = ${dest.absolutePath}")
            }
            return null
        }
    }


}