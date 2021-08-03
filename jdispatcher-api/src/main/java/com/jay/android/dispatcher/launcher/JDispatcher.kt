package com.jay.android.dispatcher.launcher

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Trace
import androidx.annotation.NonNull
import com.jay.android.dispatcher.annotation.Dimension
import com.jay.android.dispatcher.common.*
import com.jay.android.dispatcher.dispatch.DispatchHelper
import com.jay.android.dispatcher.sort.DispatchListSortHelper
import com.jay.android.dispatcher.utils.ApiConst
import com.jay.android.dispatcher.utils.ApiUtils
import com.jay.android.dispatcher.utils.ClassUtils
import com.jay.android.dispatcher.utils.VersionHelper
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * JDispatcher facade
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
class JDispatcher private constructor() {

    private var hasInit = false

    private var debuggable = false

    private var registerByPlugin = false

    var context: Context? = null

    private var dispatchHelper: DispatchHelper? = null

    //排好序的列表通过字节码注入到这里
    private val dispatchSortedJsonListFromPlugin: ArrayList<String> by lazy {
        arrayListOf()
    }

    private val dispatchExtraParam: HashMap<String, HashMap<String, String>> by lazy {
        hashMapOf()
    }

    /**
     * 通过ASM字节码注入的方法入口
     * dispatchSortedJsonListFromPlugin.add("")
     * dispatchSortedJsonListFromPlugin.add("")
     */
    fun registerDispatchSortedList() {

    }

    /**
     * 初始化
     *
     * @param context
     */
    fun init(@NonNull context: Context): JDispatcher {
        if (!hasInit) {
            hasInit = true
            // 加载排好序的 Dispatch （编译时|运行时）
            loadSortedDispatch(context)
            // 准备分发
            dispatch(context)
        }
        return this
    }

    /**
     * 加载排好序的 Dispatch （编译时|运行时）
     *
     * @param context
     */
    private fun loadSortedDispatch(context: Context) {
        //获取通过ASM字节码注入的排好序的列表
        registerDispatchSortedList()
        //编译时扫描+排序
        if (dispatchSortedJsonListFromPlugin.size > 0) {
            Logger.debug("编译时扫描+排序")
            registerByPlugin = true
            Logger.debug("从插件获取的排好序的原始Json列表：$dispatchSortedJsonListFromPlugin")
            loadSortedDispatchFromPluginJsonList(dispatchSortedJsonListFromPlugin)
        } else {
            //运行时扫描+排序
            Logger.debug("运行时扫描+排序")
            loadSortedDispatchFromRuntime(context)
        }
        Logger.debug("最终排好序的 dispatchSortedList：" + Warehouse.dispatchSortedList.toString())
    }


    /**
     * 从插件中获取到的排好序的json列表转化为 Dispatch 列表
     * 根据全类名反射实例化每一个 Dispatch 实例
     * todo (将反射获取 Dispatch 实例的操作提前到编译阶段)
     *
     * @param dispatchSortedJsonList
     */
    private fun loadSortedDispatchFromPluginJsonList(dispatchSortedJsonList: ArrayList<String>) {

        dispatchSortedJsonList.forEach { jsonItem ->
            val dispatchItem: DispatchItem? = try {
                CommonUtils.fromJson(jsonItem, DispatchItem::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.error(e.message.toString())
                null
            }

            dispatchItem?.let { dispatch ->
                val dispatchItemClazz = try {
                    Class.forName(dispatch.className).getConstructor().newInstance()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Logger.error(e.message.toString())
                }
                if (dispatchItemClazz is IDispatch) {
                    dispatch.instance = dispatchItemClazz
                    Warehouse.dispatchSortedList.add(dispatch)
                }
            }
        }
    }


    /**
     * 运行时扫描+排序
     * 分发组类信息进行版本本地缓存
     *
     * @param context
     */
    private fun loadSortedDispatchFromRuntime(context: Context) {
        val dispatchGroupClassList: Set<String>
        // 在调试模式或全新安装的情况下扫描获取
        if (debuggable() || VersionHelper.isNewVersion(context)) {
            Logger.info("在调试模式或全新安装的情况下扫描获取")
            //获取项目中通过 APT 生成的所有分发类组的 ClassName
            dispatchGroupClassList = ClassUtils.getFileNameByPackageName(
                context,
                CommonConst.PACKAGE_OF_GENERATE_FILE //com.jay.android.dispatcher.generate.dispatch
            )
            //分发组类信息本地缓存
            if (dispatchGroupClassList.isNotEmpty()) {
                ApiUtils.putStringSet(JDISPATCHER_SP_KEY_LIST, dispatchGroupClassList)
            }
            VersionHelper.updateVersion()
        } else {
            //正式版本中从缓存获取 dispatch group className，避免重复扫描
            Logger.info("正式版本中从缓存获取 dispatch group className")
            dispatchGroupClassList = HashSet(ApiUtils.getStringSet(JDISPATCHER_SP_KEY_LIST))
        }
        Logger.info("运行时获取的 dispatchGroupClassList：$dispatchGroupClassList")
        // 从分发类组中获取所有分发类 缓存到 dispatchOriginalMap
        DispatchListSortHelper.loadDispatchFromGroup(dispatchGroupClassList)
        // 从 dispatchOriginalMap 进行最终的排序，缓存到 dispatchSortedList
        val sortedDispatchList = DispatchListSortHelper.sortDispatchFromMap()
        sortedDispatchList.forEach { dispatchItem ->
            dispatchItem.let { dispatch ->
                val dispatchItemClazz = try {
                    Class.forName(dispatch.className).getConstructor().newInstance()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Logger.error(e.message.toString())
                }
                if (dispatchItemClazz is IDispatch) {
                    dispatch.instance = dispatchItemClazz
                    Warehouse.dispatchSortedList.add(dispatch)
                }
            }
        }
    }


    fun withDispatchExtraParam(dispatchExtraParam: HashMap<String, HashMap<String, String>>): JDispatcher {
        this.dispatchExtraParam.putAll(dispatchExtraParam)
        return this
    }

    fun withDispatchExtraParam(name: String, extraParam: HashMap<String, String>): JDispatcher {
        this.dispatchExtraParam[name] = extraParam
        return this
    }

    fun withDebugAble(isDebugAble: Boolean): JDispatcher {
        debuggable = isDebugAble
        Logger.showLog(debuggable)
        Logger.showStackTrace(debuggable)
        Logger.setTag(ApiConst.TAG)
        return this
    }


    /**
     * 手动分发
     * @param key Dispatch 唯一标识
     */
    fun manualDispatch(key: String) {
        if (!hasInit || application == null) {
            throw RuntimeException("JDispatcher.init(context) first!")
        }
        if (key.isEmpty()) {
            Logger.error("Manual dispatch key is empty")
            return
        }
        Warehouse.dispatchSortedList.forEach { dispatchItem ->
            //过滤出需要手动分发的 Dispatch
            if (key == dispatchItem.name && Dimension.isManual(dispatchItem.dimension)) {
                dispatchHelper?.dispatchOnCreate(dispatchExtraParam, dispatchItem, application!!)
            }
        }
    }

    /**
     * 分发入口
     */
    private fun dispatch(context: Context) {
        this.context = context
        if (!hasInit) {
            throw RuntimeException("JDispatcher.init(context) first!")
        }
        if (dispatchHelper == null) {
            dispatchHelper = DispatchHelper(Warehouse.dispatchSortedList)
        }
        // onCreate 方法与其他App回调方法不同，需要在这里手动调用
        onProviderCreate(context)
    }

    fun destroy() {
        Warehouse.clear()
        hasInit = false
        dispatchHelper = null
    }

    fun debuggable(): Boolean {
        return debuggable
    }

    fun onCreate(@NonNull application: Application) {
        JDispatcher.application = application
        //init 在 InitializationProvider 已预先经初始化,这里考虑多进程的情况
        if (!hasInit || dispatchHelper == null) {
            init(application)
        }
        val totalOnCreateTime = CommonUtils.timeStr("总的 onCreate ") {
            // todo Trace 事件打点使用说明 https://developer.android.com/topic/performance/tracing/custom-events?hl=zh-cn
            // Android 4.3（API 级别 18）及更高版本中， 您可以在代码中使用 Trace 类来定义
            // 随后会出现在 Perfetto 和 Systrace 报告中的自定义事件
            // 务必将每次对 beginSection() 的调用与一次对 endSection() 的调用正确匹配。
            // 不能在一个线程上调用 beginSection()，而在另一个线程上结束它；您必须在同一个线程上调用这两个方法。
            Trace.beginSection("总的 onCreate ")
            dispatchHelper?.onCreate(application, dispatchExtraParam)
            Trace.endSection()
        }
        logInfo(totalOnCreateTime)
    }


    fun onProviderCreate(@NonNull context: Context) {
        val totalProviderOnCreateTime = CommonUtils.timeStr("总的 onProviderCreate ") {
            dispatchHelper?.onProviderCreate(context)
        }
    }

    private fun logInfo(totalOnCreateTime: String) {
        logInfo["total_on_create_time"] = totalOnCreateTime
        val dispatchSubInfoList = arrayListOf<Map<String, String>>()

        Warehouse.dispatchSortedList.forEach {

            val dispatchSubInfo = linkedMapOf<String, String>()
            dispatchSubInfo["name"] = it.name
            dispatchSubInfo["priority"] = it.priority.toString()
            val dimensionText =
                Dimension.getProcess(it.dimension) +
                        " | " + Dimension.getThread(it.dimension) +
                        " | " + Dimension.getBuild(it.dimension) +
                        " | " + Dimension.getManual(it.dimension)
            dispatchSubInfo["dimension"] = dimensionText
            dispatchSubInfo["time"] = it.time.toString()
            dispatchSubInfo["dependencies"] = it.dependencies.toList().toString()
            dispatchSubInfoList.add(dispatchSubInfo)
        }
        logInfo["dispatch_sorted_sub_info"] = dispatchSubInfoList
    }

    fun onTerminate() {
        CommonUtils.timeStr("总的 onTerminate ") { dispatchHelper?.onTerminate() }
    }

    fun onConfigurationChanged(newConfig: Configuration?) {
        CommonUtils.timeStr("总的 onConfigurationChanged ") {
            dispatchHelper?.onConfigurationChanged(newConfig)
        }
    }

    fun onLowMemory() {
        CommonUtils.timeStr("总的 onLowMemory ") { dispatchHelper?.onLowMemory() }

    }

    fun onTrimMemory(level: Int) {
        CommonUtils.timeStr("总的 onTrimMemory ") { dispatchHelper?.onTrimMemory(level) }
    }


    companion object {

        val logInfo: ConcurrentHashMap<String, Any> by lazy {
            ConcurrentHashMap()
        }

        private const val JDISPATCHER_SP_KEY_LIST = "JDISPATCHER_SP_KEY_LIST"

        var application: Application? = null


        @JvmStatic
        val instance: JDispatcher by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JDispatcher() }
    }

}