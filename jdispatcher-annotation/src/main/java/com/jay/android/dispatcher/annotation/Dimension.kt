package com.jay.android.dispatcher.annotation

/**
 * 分发类型维度类型
 * @author jaydroid
 * @version 1.0
 * @date 7/7/21
 */
object Dimension {

    /*
     * 用于标识分发类的进程类型
     */

    /**
     * 所有进程
     * 1
     */
    const val PROCESS_ALL = 0x01

    /**
     * 主进程
     * 2
     */
    const val PROCESS_MAIN = PROCESS_ALL.shl(1)

    /**
     * 非主进程
     * 4
     */
    const val PROCESS_OTHER = PROCESS_MAIN.shl(1)


    /*
     * 用于标识分发类的线程类型
     */

    /**
     * UI线程
     * 8
     */
    const val THREAD_UI = PROCESS_OTHER.shl(1)

    /**
     * UI线程-空闲线程
     * 16
     */
    const val THREAD_UI_IDLE = THREAD_UI.shl(1)

    /**
     * 工作线程
     * 32
     */
    const val THREAD_WORK = THREAD_UI_IDLE.shl(1)


    /*
     * 用于标识分发类的构建类型
     */

    /**
     * 调试模式
     * 64
     */
    const val BUILD_DEBUG = THREAD_WORK.shl(1)

    /**
     * 调试模式+发布模式
     * 128
     */
    const val BUILD_ALL = BUILD_DEBUG.shl(1)


    /*
     * 用于标识分发类的初始化时机类型
     */

    /**
     * Application 自动初始化
     * 256
     */
    const val AUTOMATIC = BUILD_ALL.shl(1)

    /**
     * 手动初始化
     * 512
     */
    const val MANUAL = AUTOMATIC.shl(1)

    /**
     * ContentProvider 超前初始化
     * 1024
     */
    const val PRELOAD = MANUAL.shl(1)


    /*
     * 常用的组合维度
     */

    /**
     * 主进程+UI线程+构建所有+自动初始化
     */
    const val DIMENSION_DEFAULT =
        PROCESS_MAIN or THREAD_UI or BUILD_ALL or AUTOMATIC

    /**
     * 主进程+工作线程+构建所有+自动初始化
     */
    const val DIMENSION_DEFAULT_THREAD_WORK =
        PROCESS_MAIN or THREAD_WORK or BUILD_ALL or AUTOMATIC

    /**
     * 主进程+空闲线程+构建所有+自动初始化
     */
    const val DIMENSION_DEFAULT_THREAD_UI_IDLE =
        PROCESS_MAIN or THREAD_UI_IDLE or BUILD_ALL or AUTOMATIC

    /**
     * 主进程+UI线程+调试模式+自动初始化
     */
    const val DIMENSION_DEFAULT_BUILD_DEBUG =
        PROCESS_MAIN or THREAD_UI or BUILD_DEBUG or AUTOMATIC

    /**
     * 主进程+UI线程+调试模式+手动初始化
     */
    const val DIMENSION_DEFAULT_MANUAL =
        PROCESS_MAIN or THREAD_UI or BUILD_ALL or MANUAL


    fun isPreload(dimension: Int): Boolean {
        return dimension and PRELOAD == PRELOAD
    }

    fun isManual(dimension: Int): Boolean {
        return dimension and MANUAL == MANUAL
    }

    fun isAutomatic(dimension: Int): Boolean {
        return dimension and AUTOMATIC == AUTOMATIC
    }

    fun getManual(dimension: Int): String {
        return when {
            isManual(dimension) -> {
                "手动初始化"
            }
            isAutomatic(dimension) -> {
                "自动初始化"
            }
            isPreload(dimension) -> {
                "超前初始化"
            }
            else -> {
                ""
            }
        }
    }

    fun isBuildDebug(dimension: Int): Boolean {
        return dimension and BUILD_DEBUG == BUILD_DEBUG
    }

    fun isBuildAll(dimension: Int): Boolean {
        return dimension and BUILD_ALL == BUILD_ALL
    }

    fun getBuild(dimension: Int): String {
        return when {
            isBuildDebug(dimension) -> {
                "构建调试 "
            }
            isBuildAll(dimension) -> {
                "构建所有"
            }
            else -> {
                ""
            }
        }
    }

    fun isThreadUiIdle(dimension: Int): Boolean {
        return dimension and THREAD_UI_IDLE == THREAD_UI_IDLE
    }

    fun isThreadUi(dimension: Int): Boolean {
        return dimension and THREAD_UI == THREAD_UI
    }

    fun isThreadWork(dimension: Int): Boolean {
        return dimension and THREAD_WORK == THREAD_WORK
    }

    fun getThread(dimension: Int): String {
        return when {
            isThreadUi(dimension) -> {
                "UI线程"
            }
            isThreadWork(dimension) -> {
                "工作线程"
            }
            isThreadUiIdle(dimension) -> {
                "空闲"
            }
            else -> {
                ""
            }
        }
    }


    fun isProcessMain(dimension: Int): Boolean {
        return dimension and PROCESS_MAIN == PROCESS_MAIN
    }

    fun isProcessAll(dimension: Int): Boolean {
        return dimension and PROCESS_ALL == PROCESS_ALL
    }

    fun isProcessOther(dimension: Int): Boolean {
        return dimension and PROCESS_OTHER == PROCESS_OTHER
    }

    fun getProcess(dimension: Int): String {
        return when {
            isProcessMain(dimension) -> {
                "主进程"
            }
            isProcessOther(dimension) -> {
                "非主进程"
            }
            isProcessAll(dimension) -> {
                "所有进程"
            }
            else -> {
                ""
            }
        }
    }

    fun getDimensionStr(dimension: Int): String {
        return getProcess(dimension) +
                " | " + getThread(dimension) +
                " | " + getBuild(dimension) +
                " | " + getManual(dimension)
    }


}