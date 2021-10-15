package com.jay.android.dispatcher.startup

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.RestrictTo
import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.launcher.JDispatcher

/**
 * The [ContentProvider] which discovers [Initializer]s in an application and
 * initializes them before [Application.onCreate].
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class InitializationProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        Logger.debug("InitializationProvider-onCreate, context:$context")
        if (context != null) {
            JDispatcher.instance.init(context!!)
        } else {
            throw RuntimeException("Context cannot be null")
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        throw IllegalStateException("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        throw IllegalStateException("Not allowed.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw IllegalStateException("Not allowed.")
    }

    override fun delete(
        uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): Int {
        throw IllegalStateException("Not allowed.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw IllegalStateException("Not allowed.")
    }
}