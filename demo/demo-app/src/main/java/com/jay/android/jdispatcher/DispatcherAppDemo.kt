package com.jay.android.jdispatcher;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.jay.android.dispatcher.annotation.Dispatch;
import com.jay.android.dispatcher.annotation.Priority;
import com.jay.android.dispatcher.common.DispatchItem;
import com.jay.android.dispatcher.dispatch.DispatchTemplate;

import org.jetbrains.annotations.NotNull;

/**
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Dispatch(priority = Priority.LOW_DEFAULT, description = "DispatcherAppDemo")
public class DispatcherAppDemo extends DispatchTemplate {

    @Override
    public void onCreate(@NotNull Application app, @NotNull DispatchItem dispatchItem) {
        Log.d("Jay", "DispatcherAppDemo#onCreate" + dispatchItem);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        Log.d("Jay", "DispatcherAppDemo#onConfigurationChanged" + newConfig);
    }

    @Override
    public void onLowMemory() {
        Log.d("Jay", "DispatcherAppDemo#onLowMemory");

    }

    @Override
    public void onTerminate() {
        Log.d("Jay", "DispatcherAppDemo#onTerminate");

    }

    @Override
    public void onTrimMemory(int level) {
        Log.d("Jay", "DispatcherAppDemo#onTrimMemory" + level);

    }
}
