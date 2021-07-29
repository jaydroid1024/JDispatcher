package com.jay.android.jdispatcher

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jay.android.dispatcher.common.CommonUtils
import com.jay.android.dispatcher.launcher.JDispatcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.tv_info)
        text.text = CommonUtils.prettyToJson(JDispatcher.logInfo)
    }

    fun onInfo(view: View) {
        //分发时携带的参数信息
        val pushDispatchExtraParam = hashMapOf<String, HashMap<String, String>>()
        val pushExtraParam = hashMapOf<String, String>()
        if (BuildConfig.DEBUG) {
            pushExtraParam["push_key"] = "111232323"
        } else {
            pushExtraParam["push_key"] = "33333333"
        }
        pushDispatchExtraParam["DemoLibrary_B_Dispatch_03"] = pushExtraParam

        //手动调用分发
        JDispatcher.instance
            .manualDispatch("DemoLibrary_B_Dispatch_03")
    }
}