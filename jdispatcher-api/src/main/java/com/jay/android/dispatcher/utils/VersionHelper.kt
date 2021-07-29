package com.jay.android.dispatcher.utils

import android.content.Context
import android.text.TextUtils


/**
 * Android 版本帮助类
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
object VersionHelper {

    private const val LAST_VERSION_NAME = "LAST_VERSION_NAME"
    private const val LAST_VERSION_CODE = "LAST_VERSION_CODE"
    private var NEW_VERSION_NAME: String = ""
    private var NEW_VERSION_CODE = 0L

    fun isNewVersion(context: Context): Boolean {
        val packageInfo = ApiUtils.getPackageInfo(context)
        return if (null != packageInfo) {
            val versionName = packageInfo.versionName
            val versionCode =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    packageInfo.versionCode.toLong()
                }
            if (versionName != ApiUtils.getString(LAST_VERSION_NAME) ||
                versionCode != ApiUtils.getLong(LAST_VERSION_CODE)
            ) {
                NEW_VERSION_NAME = versionName
                NEW_VERSION_CODE = versionCode
                true
            } else {
                false
            }
        } else {
            true
        }
    }

    fun updateVersion() {
        if (!TextUtils.isEmpty(NEW_VERSION_NAME) && NEW_VERSION_CODE != 0L) {
            ApiUtils.putString(LAST_VERSION_NAME, NEW_VERSION_NAME)
            ApiUtils.putLong(LAST_VERSION_CODE, NEW_VERSION_CODE)
        }
    }


}