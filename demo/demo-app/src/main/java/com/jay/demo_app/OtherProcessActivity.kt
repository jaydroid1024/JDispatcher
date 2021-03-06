package com.jay.demo_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jay.android.dispatcher.utils.ApiUtils
import com.jay.android.jdispatcher.R

class OtherProcessActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_process)
        val text = findViewById<TextView>(R.id.tv_info)
        text.text = "进程名：${ApiUtils.getProcessName()}"
    }
}