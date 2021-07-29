package com.jay.android.dispatcher.sort

import com.jay.android.dispatcher.common.*
import java.util.*

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/15/21
 */
object DispatchListSortHelper {

    /**
     * 从分发类组中获取所有分发类
     *
     * @param routerMap
     * @return
     */
    fun loadDispatchFromGroup(routerMap: Set<String>): HashMap<String?, DispatchItem?>? {
        for (className in routerMap) {
            if (className.startsWith(CommonConst.CLASS_OF_GENERATE_ALL_PREFIX)) {
                val dispatchClazz = try {
                    Class.forName(className).getConstructor().newInstance()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Logger.error(e.message.toString())
                }
                if (dispatchClazz is IDispatchGroup) {
                    dispatchClazz.loadInto(Warehouse.dispatchOriginalMap)
                }
            }
        }
        return Warehouse.dispatchOriginalMap
    }

    /**
     * 对收集到的原始数据进行排序
     * todo 目前只对优先级和依赖项两种排序规则做了交叉排序的逻辑，
     * todo 对于是否制定进程、是否手动延迟分发、是否异步分发等维度的交叉混合分发的情况暂时需要调用方人为校验
     * @return
     */
    fun sortDispatchFromMap(): ArrayList<DispatchItem> {
        val dispatchMap = Warehouse.dispatchOriginalMap
        val dispatchItemList: ArrayList<DispatchItem> = arrayListOf()
        if (dispatchMap.isNullOrEmpty()) {
            return dispatchItemList
        }
        for (dispatchItem in dispatchMap.values) {
            //根据依赖项重新计算优先级
            calculateDispatchPriority(dispatchItem)
        }

        dispatchMap.values.forEach {
            if (it != null) {
                dispatchItemList.add(it)
            }
        }
        //按照优先级排序
        dispatchItemList.sortByDescending { it.priority }
        return dispatchItemList
    }


    private fun calculateDispatchPriority(dispatchItem: DispatchItem?) {
        if (dispatchItem == null) {
            return
        }
        val dependencies = dispatchItem.dependencies
        //递归结束的条件
        if (dependencies.isNullOrEmpty()) {
            return
        }
        for (dependency in dependencies) {
            //依赖了自己
            if (dependency == dispatchItem.name) {
                throw  IllegalArgumentException("DispatchItem 依赖了自己：" + dispatchItem.name)
            }
            val dependDispatchItem = findDispatchItemByName(dependency) ?: continue
            //检查循环依赖
            checkDispatchCircularDependency(
                dispatchItem,
                dependDispatchItem,
                ArrayList<DispatchItem>()
            )
            //递归计算依赖的依赖的优先级
            calculateDispatchPriority(dependDispatchItem)

            //todo 本身和依赖维度校验问题
            //校验进程依赖问题，不支持本身和依赖一个是主进程一个是非主进程的情况
            //校验线程依赖问题，不支持本身和依赖前者是UI线程，后者是工作线程或空闲线程
            //校验构建依赖问题，不支持本身和依赖前者是全部构建，后者是调试构建的情况
            //校验手动依赖问题，不支持本身和依赖前者是自动，后者是手动的情况

            //校准并重置优先级
            if (dependDispatchItem.priority <= dispatchItem.priority) {
                dependDispatchItem.priority = dependDispatchItem.priority + dispatchItem.priority
            }
        }

    }

    private fun checkDispatchCircularDependency(
        dispatchItem: DispatchItem,
        dependDispatchItem: DispatchItem?,
        transitiveDependencyList: ArrayList<DispatchItem>?
    ) {
        if (dependDispatchItem == null) {
            return
        }
        //依赖的依赖
        val transitiveDependencies = dependDispatchItem.dependencies

        if (transitiveDependencies.isNullOrEmpty()) {
            return
        }
        for (dependency in transitiveDependencies) {
            //依赖的依赖依赖了自己
            if (dependency == dependDispatchItem.name) {
                throw  RuntimeException("DispatchItem 依赖的依赖依赖了自己：" + dispatchItem.name)
            }
            //循环依赖了
            if (dependency == dispatchItem.name) {
                val exception = if (transitiveDependencyList.isNullOrEmpty()) {
                    dispatchItem.name + " 产生循环依赖了,"
                } else {
                    var message = dispatchItem.name + " 产生传递循环依赖了，transitiveDependencyList："
                    transitiveDependencyList.forEach {
                        message += it.name + " "
                    }
                    message
                }
                throw RuntimeException(exception)
            } else {
                transitiveDependencyList?.add(dependDispatchItem)
            }
            val transitiveDependDispatchItem = findDispatchItemByName(dependency) ?: continue
            //继续递归检查
            checkDispatchCircularDependency(
                dispatchItem,
                transitiveDependDispatchItem,
                transitiveDependencyList
            )

        }
    }

    private fun findDispatchItemByName(dependency: String?): DispatchItem? {
        if (dependency.isNullOrEmpty()) {
            return null
        }
        val dispatchMap = Warehouse.dispatchOriginalMap
        if (dispatchMap?.containsKey(dependency) == true) {
            return dispatchMap[dependency]
        } else {
            throw IllegalArgumentException("DispatchItem 依赖项不存在：$dependency")
        }
    }

}