package com.jay.android.dispatcher.common

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.*

/**
 * 分发 Application 生命周期的抽象接口
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
interface IDispatch {

    /**
     * 在 [Application.onCreate] 中调用
     *
     * 在应用程序启动时调用，在创建任何 activity、service、receiver 对象（不包括 content providers）之前。
     * 在实现此方法时如果指定的线程维度类型为 [Dimension.THREAD_UI] 切记不可做过多的耗时任务，
     * 因为在此函数中花费的时间直接影响启动进程中第一个 activity、service、receiver 的性能。
     */

    fun onCreate(app: Application, dispatchItem: DispatchItem)

    /**
     * 在 [Application.onCreate] 之前调用
     *
     * @param context
     * @param dispatchItem
     */
    fun onPreCreate(context: Context, dispatchItem: DispatchItem)

    /**
     * 在 [Application.onTerminate] 中调用
     *
     * 在杀死应用进程时调用，此方法用于模拟过程环境。它永远不会在生产 Android 设备上被调用。
     */
    fun onTerminate()

    /**
     * 在 [Application.onConfigurationChanged] 中调用
     *
     * 在组件运行时设备配置更改时由系统调用。
     * 与 Activity组件不同，其他组件{Activity,Service,ContentProvider,Application}在配置更改时永远不会重新启动，
     * 它们必须始终处理更改的结果，例如通过重新检索资源。
     * 在调用此函数时，资源对象将被更新以返回与新配置匹配的资源值，
     */
    fun onConfigurationChanged(newConfig: Configuration)

    /**
     * 在 [Application.onLowMemory] 中调用
     *
     * 当整个系统内存不足时调用此方法，并且活跃的进程应减少其内存使用量。
     * 虽然没有定义调用这个方法的确切点，但通常会在所有后台进程都被杀死时发生。
     * 也就是说，在杀死我们希望避免杀死的托管服务和前台 UI 的进程之前。
     * 您应该实现此方法以释放您可能持有的任何缓存或其他不必要的资源。
     * 从该方法返回后，系统将为您执行GC。
     * 最好从 {@link ComponentCallbacks2} 实现 {@link ComponentCallbacks2onTrimMemory} 以根据不同级别的内存需求逐步卸载资源。
     * 该 API 可用于 API 级别 14 及更高级别，因此您应该仅使用此 {@link onLowMemory} 方法作为旧版本的后备，
     * 可以将其视为 {@link ComponentCallbacks2onTrimMemory} 与 {@link ComponentCallbacks2TRIM_MEMORY_COMPLETE} 级别.<p>
     */
    fun onLowMemory()

    /**
     * 在 [Application.onTrimMemory] 中调用
     * 如果您不根据此回调定义的内存级别来修剪您的资源，则系统更有可能在您的进程缓存在最近最少使用 (LRU) 列表中时杀死您的进程，
     * 从而要求您的应用程序重新启动并在用户返回时恢复所有状态。
     * level 值为您提供有关内存可用性的不同类型的线索：
     * 当您的应用程序运行时：
     * TRIM_MEMORY_RUNNING_MODERATE
     * 设备开始运行内存不足。您的应用程序正在运行且不可杀死。
     * TRIM_MEMORY_RUNNING_LOW
     * 该设备在内存上运行得低得多。您的应用正在运行且不可杀死，但请释放未使用的资源以提高系统性能（这会直接影响您的应用性能）。
     * TRIM_MEMORY_RUNNING_CRITICAL
     * 设备运行的内存极低。您的应用程序尚未被视为可杀死进程，但如果应用程序不释放资源，系统将开始杀死后台进程，因此您应该立即释放非关键资源以防止性能下降。
     *
     * 当您的应用的可见性发生变化时：
     * TRIM_MEMORY_UI_HIDDEN
     *  您的应用程序的 UI 不再可见，因此这是释放仅由您的 UI 使用的大量资源的好时机。
     *
     * 当您的应用进程驻留在后台 LRU 列表中时：
     * TRIM_MEMORY_BACKGROUND
     * 系统内存不足，您的进程接近 LRU 列表的开头。虽然你的app进程被杀的风险不高，但系统可能已经在杀LRU列表中的进程，所以你应该释放容易恢复的资源，这样你的进程会留在列表中并在用户返回到您的应用程序。
     * TRIM_MEMORY_MODERATE
     * 系统内存不足，您的进程接近 LRU 列表的中间。如果系统的内存进一步受限，您的进程就有可能被终止。
     * TRIM_MEMORY_COMPLETE
     * 系统内存不足，如果系统现在没有恢复内存，您的进程将是第一个被杀死的进程。您应该完全释放对恢复应用程序状态不重要的所有内容。
     *
     * 要支持低于 14 的 API 级别，您可以使用onLowMemory()方法作为与 onTrimMemory() 方法中 TRIM_MEMORY_COMPLETE 级别大致等效的回退。
     * 注意：当系统开始杀死 LRU 列表中的进程时，虽然它主要是自下而上工作的，
     * 但它确实会考虑哪些进程消耗更多内存，因此如果被杀死，将提供更多的内存收益。
     * 因此，总体而言，您在 LRU 列表中消耗的内存越少，您保留在列表中并能够快速恢复的机会就越大。
     */
    fun onTrimMemory(level: Int)

}
