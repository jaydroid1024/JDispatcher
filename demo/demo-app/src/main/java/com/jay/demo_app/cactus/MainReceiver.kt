package com.jay.demo_app.cactus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gyf.cactus.Cactus

/**
 * 测试Cactus广播接受
 * @author geyifeng
 * @date 2019-08-30 10:30
 */
class MainReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.apply {
            when (this) {
                Cactus.CACTUS_WORK -> {
                    val time = intent.getIntExtra(Cactus.CACTUS_TIMES, 0)
                    Log.d(ServiceConstants.TAG, "CACTUS_WORK, time:$time")
                }
                Cactus.CACTUS_STOP -> {
                    Log.d(ServiceConstants.TAG, "CACTUS_WORK: $this")
                }
                Cactus.CACTUS_BACKGROUND -> {
                    Log.d(ServiceConstants.TAG, "CACTUS_BACKGROUND: $this")
                }
                Cactus.CACTUS_FOREGROUND -> {
                    Log.d(ServiceConstants.TAG, "CACTUS_FOREGROUND: $this")

                }
            }
        }
    }

}