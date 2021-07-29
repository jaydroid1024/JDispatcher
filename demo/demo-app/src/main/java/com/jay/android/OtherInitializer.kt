package com.jay.android

import android.content.Context
import android.util.Log
import androidx.startup.Initializer

/**
 * @author jaydroid
 * @version 1.0
 * @date 6/2/21
 */
class OtherInitializer : Initializer<String> {

    override fun create(context: Context): String {
        Log.d("Jay", "OtherInitializer. create")

        return "create"
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        Log.d("Jay", "OtherInitializer. dependencies")
        return emptyList()
    }

}
