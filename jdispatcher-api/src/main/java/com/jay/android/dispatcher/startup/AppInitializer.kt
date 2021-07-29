/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jay.android.dispatcher.startup

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.tracing.Trace
import java.util.*

/**
 * An [AppInitializer] can be used to initialize all discovered [ComponentInitializer]s.
 * <br></br>
 * The discovery mechanism is via `<meta-data>` entries in the merged `AndroidManifest.xml`.
</meta-data> */
class AppInitializer private constructor(context: Context) {


    private val mInitialized: MutableMap<Class<*>, Any?>
    private val mDiscovered: MutableSet<Class<out Initializer<*>?>>
    val mContext: Context = context.applicationContext

    /**
     * Creates an instance of [AppInitializer]
     *
     * @param context The application context
     */
    init {
        mDiscovered = HashSet()
        mInitialized = HashMap()
    }

    /**
     * Initializes a [Initializer] class type.
     *
     * @param component The [Class] of [Initializer] to initialize.
     * @param <T>       The instance type being initialized
     * @return The initialized instance
    </T> */
    fun <T> initializeComponent(component: Class<out Initializer<T?>?>): T {
        return doInitialize(component, HashSet())!!
    }

    /**
     * Returns `true` if the [Initializer] was eagerly initialized..
     *
     * @param component The [Initializer] class to check
     * @return `true` if the [Initializer] was eagerly initialized.
     */
    fun isEagerlyInitialized(component: Class<out Initializer<*>?>): Boolean {
        // If discoverAndInitialize() was never called, then nothing was eagerly initialized.
        return mDiscovered.contains(component)
    }

    fun <T> doInitialize(
        component: Class<out Initializer<*>?>,
        initializing: MutableSet<Class<*>?>
    ): T? {

        synchronized(sLock) {
            val isTracingEnabled = Trace.isEnabled()
            return try {
                if (isTracingEnabled) {
                    // Use the simpleName here because section names would get too big otherwise.
                    Trace.beginSection(component.simpleName)
                }

                if (initializing.contains(component)) {
                    val message = String.format(
                        "Cannot initialize %s. Cycle detected.", component.name
                    )
                    throw IllegalStateException(message)
                }


                val result: Any?
                //还没初始化过
                if (!mInitialized.containsKey(component)) {
                    //正在初始化
                    initializing.add(component)
                    try {
                        //获取实例
                        val instance: Any? = component.getDeclaredConstructor().newInstance()
                        val initializer = instance as Initializer<*>?
                        val dependencies = initializer!!.dependencies()
                        //递归获取依赖项
                        if (dependencies.isNotEmpty()) {
                            for (clazz in dependencies) {
                                if (!mInitialized.containsKey(clazz)) {
                                    //递归
                                    doInitialize<Any>(clazz, initializing)
                                }
                            }
                        }
                        if (StartupLogger.DEBUG) {
                            StartupLogger.i(String.format("Initializing %s", component.name))
                        }
                        //依赖项先初始化
                        result = initializer.create(mContext)
                        if (StartupLogger.DEBUG) {
                            StartupLogger.i(String.format("Initialized %s", component.name))
                        }
                        initializing.remove(component)
                        mInitialized[component] = result
                    } catch (throwable: Throwable) {
                        throw StartupException(throwable)
                    }
                } else {
                    result = mInitialized[component]
                }

                result as? T
            } finally {
                Trace.endSection()
            }
        }
    }

    fun discoverAndInitialize() {
        try {
            Trace.beginSection(SECTION_NAME)
            val componentName =
                ComponentName(mContext.packageName, InitializationProvider::class.java.name)
            val providerInfo = mContext.packageManager
                .getProviderInfo(componentName, PackageManager.GET_META_DATA)

            val metadata = providerInfo.metaData
            val startup = "androidx_startup"
            if (metadata != null) {
                val initializing: MutableSet<Class<*>?> = HashSet()
                //获取所有 InitializationProvider 标签包裹的 Initializer 全类名信息
                val keys = metadata.keySet()
                for (key in keys) {
                    // 全类名信息对应的key 必须是 androidx_startup
                    val value = metadata.getString(key, null)
                    if (startup == value) {
                        val clazz = Class.forName(key)
                        //确定此 {@code Class} 对象表示的类或接口是否与指定的 {@code Class} 参数表示的类或接口相同，
                        // 或者是其超类或超接口。如果是，则返回 {@code true}；
                        if (Initializer::class.java.isAssignableFrom(clazz)) {
                            //反射获取 Initializer 实例
                            val component = clazz as? Class<out Initializer<*>?>
                            //缓存所有 Initializer 到 mDiscovered
                            if (component != null) {
                                mDiscovered.add(component)
                            }
                            if (StartupLogger.DEBUG) {
                                StartupLogger.i(String.format("Discovered %s", key))
                            }
                            //实例化每个 Initializer
                            if (component != null) {
                                doInitialize<Any>(component, initializing)
                            }
                        }
                    }
                }
            }
        } catch (exception: PackageManager.NameNotFoundException) {
            throw StartupException(exception)
        } catch (exception: ClassNotFoundException) {
            throw StartupException(exception)
        } finally {
            Trace.endSection()
        }
    }


    companion object {
        // Tracing
        private const val SECTION_NAME = "Startup"

        /**
         * Guards app initialization.
         */
        private val sLock = Any()

        /**
         * The [AppInitializer] instance.
         */
        @Volatile
        private var sInstance: AppInitializer? = null

        /**
         * @param context The Application [Context]
         * @return The instance of [AppInitializer] after initialization.
         */
        @JvmStatic
        fun getInstance(context: Context): AppInitializer {
            if (sInstance == null) {
                synchronized(sLock) {
                    if (sInstance == null) {
                        sInstance = AppInitializer(context)
                    }
                }
            }
            return sInstance!!
        }

    }


}