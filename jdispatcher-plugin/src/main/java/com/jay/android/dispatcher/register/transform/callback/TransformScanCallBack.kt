package com.jay.android.dispatcher.register.transform.callback

import java.io.File

interface TransformScanCallBack {
    fun processScan(className: String, classBytes: ByteArray?, dest: File): ByteArray?
    fun finishScan() {}
}