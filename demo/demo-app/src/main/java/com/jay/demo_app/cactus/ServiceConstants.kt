package com.jay.demo_app.cactus

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.gyf.cactus.Cactus
import com.gyf.cactus.callback.CactusCallback
import com.gyf.cactus.ext.cactus
import com.jay.android.jdispatcher.R
import com.jay.demo_app.MainActivity


/**
 *
 * @Description
 * @date 2020/10/13 9:52 PM
 * @author BryceCui
 * @Version 1.0
 */
object ServiceConstants : CactusCallback {

    const val TAG = "ServiceConstants"

    fun setCactus(context: Context) {
        //可选，设置通知栏点击事件
//        val pendingIntent =
//            PendingIntent.getActivity(context, 0, Intent().apply {
//                setClass(context, MainActivity::class.java)
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }, PendingIntent.FLAG_UPDATE_CURRENT)
        //可选，注册广播监听器
        context.registerReceiver(MainReceiver(), IntentFilter().apply {
            addAction(Cactus.CACTUS_WORK)
            addAction(Cactus.CACTUS_STOP)
            addAction(Cactus.CACTUS_BACKGROUND)
            addAction(Cactus.CACTUS_FOREGROUND)
        })

        context.cactus {
            //可选，设置通知栏点击事件
//            setPendingIntent(pendingIntent)
            //可选，设置音乐
            setMusicId(com.gyf.cactus.R.raw.cactus)
            //可选，是否是debug模式
//            isDebug(true)
            //可选，退到后台是否可以播放音乐
            setBackgroundMusicEnabled(true)
            //可选，设置奔溃可以重启，google原生rom android 10以下可以正常重启
            setCrashRestartUIEnabled(true)
            setChannelId("com.aoao.flashman.lite")
            setChannelName("嗷嗷骑士极速版")
            setSmallIcon(R.mipmap.ic_launcher)
            setTitle("嗷嗷骑士极速版")
            setContent("正在运行中")
            //可选，运行时回调
            addCallback(this@ServiceConstants)
            //可选，切后台切换回调
            addBackgroundCallback {
                val back = if (it) "退到后台啦" else "跑到前台啦"

                Log.d(TAG, back)
            }
        }
    }

    /**
     * do something
     * @param times Int 连接次数
     */
    override fun doWork(times: Int) {
        Log.d(TAG, "doWork: times=$times")

    }

    /**
     * 停止时调用
     */
    override fun onStop() {
        Log.d(TAG, "onStop")
    }
}