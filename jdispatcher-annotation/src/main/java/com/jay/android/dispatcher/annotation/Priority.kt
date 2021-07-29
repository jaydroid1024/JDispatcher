package com.jay.android.dispatcher.annotation

/**
 * Application 生命周期分发 优先级
 * 数字越大优先级越高，越早调用
 * @author wangxuejie
 * @version 1.0
 * @date 12/30/20
 */
object Priority {

    /**
     * 低优先级
     * 十进制: 16
     */
    const val LOW_DEFAULT = 0x10

    object LOW {
        //十进制: 32
        const val LOW_I = 0x20

        //十进制: 48
        const val LOW_II = 0x30

        //十进制: 64
        const val LOW_III = 0x40
    }

    /**
     * 中优先级
     * 十进制: 80
     */
    const val MEDIUM_DEFAULT = 0x50

    object MEDIUM {
        //十进制: 96
        const val MEDIUM_I = 0x60

        //十进制: 112
        const val MEDIUM_II = 0x70

        //十进制: 128
        const val MEDIUM_III = 0x80
    }

    /**
     * 高优先级
     * 十进制: 144
     */
    const val HIGH_DEFAULT = 0x90

    object HIGH {
        //十进制: 160
        const val HIGH_I = 0x100

        //十进制: 176
        const val HIGH_II = 0x110

        //十进制: 192
        const val HIGH_III = 0x120
    }
}