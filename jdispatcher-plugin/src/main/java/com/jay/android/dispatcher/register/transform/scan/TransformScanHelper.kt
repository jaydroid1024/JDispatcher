package com.jay.android.dispatcher.register.transform.scan

import com.android.build.api.transform.*
import com.google.common.io.Files
import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.register.launch.DispatcherExtension
import com.jay.android.dispatcher.register.transform.callback.DeleteCallBack
import com.jay.android.dispatcher.register.transform.callback.TransformScanCallBack
import com.jay.android.dispatcher.register.transform.filter.ClassNameFilter
import com.jay.android.dispatcher.register.transform.filter.ExcludeClassNameFilter
import com.jay.android.dispatcher.register.utils.*
import groovy.lang.GroovyClassLoader
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool

class TransformScanHelper(
    invocation: TransformInvocation?,
    private val project: Project,
    private val groovyClassLoader: GroovyClassLoader?,
    callBack: TransformScanCallBack
) {
    private var scanCallBack: TransformScanCallBack? = callBack
    private var context: Context? = null
    private var inputs: Collection<TransformInput>? = null
    private var outputProvider: TransformOutputProvider? = null
    private var isIncremental = false
    private var deleteCallBack: DeleteCallBack? = null
    private var simpleScan = false
    private var extension: DispatcherExtension? = null
    private var excludeClassNameFilter: ClassNameFilter? = null
    private var executor: ExecutorService
    private val tasks: MutableList<Callable<Void>> = ArrayList()
    private val destFiles = mutableListOf<File>()

    init {
        context = invocation?.context
        extension = PluginUtils.getDispatcherExtension(project)
        inputs = invocation?.inputs
        outputProvider = invocation?.outputProvider
        isIncremental = invocation?.isIncremental ?: false
        executor = ForkJoinPool.commonPool()
    }

    fun openSimpleScan() {
        simpleScan = true
    }

    fun setDeleteCallBack(deleteCallBack: DeleteCallBack?) {
        this.deleteCallBack = deleteCallBack
    }

    fun startTransform() {
        try {
            Logger.debug("startTransform isIncremental: $isIncremental")
            Logger.debug("startTransform outputProvider: $outputProvider")

            //非增量编译，清空输出目录
            if (!isIncremental) {
                try {
                    outputProvider?.deleteAll()
                } catch (e: IOException) {
                    Logger.error(e.localizedMessage.toString())
                }
            }

            inputs?.forEach { input ->
                //扫描 Jar 文件
                scanJarInput(input)
                //扫描 aar 文件
                scanDirectoryInput(input)
            }

            //异步执行扫描任务，加快编译速度
            Logger.debug("startTransform Tasks.size: ${tasks.size}")

            //todo 线程并发数量太少，替换线程池
            executor.invokeAll(tasks)

            destFiles.forEach {
                it.filterTest("temp")?.forEach { file ->
                    file.deleteAll()
                }
            }

            scanCallBack?.finishScan()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 扫描 aar 文件
     *
     * @param input
     */
    private fun scanDirectoryInput(input: TransformInput) {
        for (directoryInput in input.directoryInputs) {
            groovyClassLoader?.addClasspath(directoryInput.file.absolutePath)
            val dest = outputProvider!!.getContentLocation(
                directoryInput.name, directoryInput.contentTypes,
                directoryInput.scopes, Format.DIRECTORY
            )
            destFiles.add(dest)
            val map = directoryInput.changedFiles
            val dir = directoryInput.file
            if (isIncremental) {
                for ((file, status) in map) {
                    val destFilePath =
                        file.absolutePath.replace(dir.absolutePath, dest.absolutePath)
                    val destFile = File(destFilePath)
                    when (status) {
                        Status.ADDED, Status.CHANGED -> {
                            val callable = Callable<Void> {
                                try {
                                    FileUtils.touch(destFile)
                                } catch (ignored: Exception) {
                                    Files.createParentDirs(destFile)
                                }
                                modifySingleFile(dir, file, destFile)
                                null
                            }
                            tasks.add(callable)
                        }
                        Status.REMOVED -> deleteDirectory(destFile, dest)
                        else -> {
                        }
                    }
                }
            } else {
                changeFile(dir, dest)
            }
        }
    }

    /**
     * 扫描 Jar 文件
     *
     * @param input TransformInput
     */
    private fun scanJarInput(input: TransformInput) {
        for (jarInput in input.jarInputs) {
            groovyClassLoader?.addClasspath(jarInput.file.absolutePath)
            val status = jarInput.status
            var destName = jarInput.file.name
            //重名名输出文件,因为可能同名,会覆盖
            val hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
            if (destName.endsWith(".jar")) {
                destName = destName.substring(0, destName.length - 4)
            }
            //获得输出文件
            val dest = outputProvider!!.getContentLocation(
                destName + "_" + hexName,
                jarInput.contentTypes,
                jarInput.scopes, Format.JAR
            )

            //如果是增量编译，根据文件状态分别处理
            if (isIncremental) {
                when (status) {
                    //当前文件不需要处理，甚至复制操作都不用
                    Status.NOTCHANGED -> {
//                        Logger.debug("NOTCHANGED, jarInput.absolutePath: ${jarInput.file.absolutePath}")
                    }
                    //如果状态是ADD的情况下，则扫描修改这个jar文件
                    Status.ADDED -> foreachJar(dest, jarInput)
                    //如果是CHANGE状态，则先扫描新久两个jar，比较获取删除的文件，然后重复ADD操作。
                    Status.CHANGED -> diffJar(dest, jarInput)
                    //如果状态是删除则先扫描jar之后把output 中的文件删除
                    Status.REMOVED -> {
                        try {
                            deleteScan(dest)
                            if (dest.exists()) {
                                FileUtils.forceDelete(dest)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    else -> {

                    }
                }
            } else {
                //不是增量编译，全局扫描
                foreachJar(dest, jarInput)
            }
        }
    }


    private fun deleteDirectory(destFile: File, dest: File) {
        try {
            if (destFile.isDirectory) {
                for (classFile in com.android.utils.FileUtils.getAllFiles(destFile)) {
                    deleteSingle(classFile, dest)
                }
            } else {
                deleteSingle(destFile, dest)
            }
        } catch (ignored: Exception) {
        }

        try {
            if (destFile.exists()) {
                FileUtils.forceDelete(destFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteSingle(classFile: File, dest: File) {
        try {
            if (classFile.name.endsWith(".class")) {
                val absolutePath = classFile.absolutePath.replace(
                    dest.absolutePath +
                            File.separator, ""
                )
                val className = ClassUtils.path2Classname(absolutePath)
                val bytes = IOUtils.toByteArray(FileInputStream(classFile))
                deleteCallBack?.delete(className, bytes)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun modifySingleFile(dir: File, file: File, dest: File) {
        try {
            val absolutePath = file.absolutePath.replace(dir.absolutePath + File.separator, "")
            val className = ClassUtils.path2Classname(absolutePath)
            if (absolutePath.endsWith(".class")) {
                var modifiedBytes: ByteArray?
                val bytes = IOUtils.toByteArray(FileInputStream(file))
                modifiedBytes = if (!simpleScan) {
                    process(className, bytes, dest)
                } else {
                    process(className, null, dest)
                }
                if (modifiedBytes == null) {
                    modifiedBytes = bytes
                }
                ClassUtils.saveFile(dest, modifiedBytes)
            } else {
                if (!file.isDirectory) {
                    FileUtils.copyFile(file, dest)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun process(className: String, classBytes: ByteArray?, dest: File): ByteArray? {
        try {
            if (excludeClassNameFilter == null) {
                excludeClassNameFilter = ExcludeClassNameFilter()
            }
            if (excludeClassNameFilter?.filter(className) == false) {
                return scanCallBack?.processScan(className, classBytes, dest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun changeFile(dir: File, dest: File) {
        if (dir.isDirectory) {
            FileUtils.copyDirectory(dir, dest)
            for (classFile in com.android.utils.FileUtils.getAllFiles(dir)) {
                if (classFile.name.endsWith(".class")) {
                    val task = Callable<Void> {
                        val absolutePath =
                            classFile.absolutePath.replace(dir.absolutePath + File.separator, "")
                        val className = ClassUtils.path2Classname(absolutePath)
                        if (!simpleScan) {
                            val bytes = IOUtils.toByteArray(FileInputStream(classFile))
                            val modifiedBytes = process(className, bytes, dest)
                            modifiedBytes?.let {
                                saveClassFile(it, dest, absolutePath)
                            }
                        } else {
                            process(className, null, dest)
                        }
                        null
                    }
                    tasks.add(task)
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun saveClassFile(modifiedBytes: ByteArray, dest: File, absolutePath: String) {
        val tempDir = File(dest, "/temp")
        val tempFile = File(tempDir, absolutePath)
        tempFile.mkdirs()
        val modified = ClassUtils.saveFile(tempFile, modifiedBytes)
        //key为相对路径
        val target = File(dest, absolutePath)
        if (target.exists()) {
            target.delete()
        }
        FileUtils.copyFile(modified, target)
        tempFile.delete()
    }

    /**
     *
     *
     * @param dest
     * @param jarInput
     */
    private fun foreachJar(dest: File, jarInput: JarInput) {
        val task = Callable<Void> {
            try {
                if (!simpleScan) {
                    val modifiedJar =
                        JarUtils.modifyJarFile(dest, jarInput.file, context?.temporaryDir, this)
                    FileUtils.copyFile(modifiedJar, dest)
                } else {
                    val jarFile = jarInput.file
                    val classNames = JarUtils.scanJarFile(jarFile)
                    for (className in classNames) {
                        process(className, null, dest)
                    }
                    FileUtils.copyFile(jarFile, dest)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }
        tasks.add(task)
    }

    private fun diffJar(dest: File, jarInput: JarInput) {
        try {
            val oldJarFileName = JarUtils.scanJarFile(dest)
            val newJarFileName = JarUtils.scanJarFile(jarInput.file)
            val diff = SetDiff(oldJarFileName, newJarFileName)
            val removeList = diff.removedList
            if (removeList.size > 0) {
                JarUtils.deleteJarScan(dest, removeList, deleteCallBack)
            }
            foreachJar(dest, jarInput)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteScan(dest: File) {
        try {
            JarUtils.deleteJarScan(dest, deleteCallBack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}