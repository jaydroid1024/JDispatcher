package com.jay.android

import android.content.Context
import android.util.Log
import androidx.startup.Initializer

/**
 * @author jaydroid
 * @version 1.0
 * @date 6/2/21
 */
class LitePalInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        context.applicationContext.applicationInfo
        Log.d("Jay", "LitePalInitializer. create")
    }

    //ependencies()方法表示，当前的LitePalInitializer是否还依赖于其他的Initializer，
    // 如果有的话，就在这里进行配置，App Startup会保证先初始化依赖的Initializer，
    // 然后才会初始化当前的LitePalInitializer。
    override fun dependencies(): List<Class<out Initializer<*>>> {
        Log.d("Jay", "LitePalInitializer. dependencies")
        return listOf(OtherInitializer::class.java)
    }

}
