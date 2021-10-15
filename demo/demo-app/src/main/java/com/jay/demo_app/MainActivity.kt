package com.jay.demo_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jay.android.dispatcher.common.CommonUtils
import com.jay.android.dispatcher.launcher.JDispatcher
import com.jay.android.jdispatcher.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.tv_info)
        text.text = CommonUtils.prettyToJson(JDispatcher.logInfo)
    }

    fun onManualDispatch(view: View) {
        //手动调用分发
        JDispatcher.instance.manualDispatch("D_B_03")
    }

    fun goToOtherProcess(view: View) {
        startActivity(Intent(this, OtherProcessActivity::class.java))
    }

}