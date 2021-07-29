package com.jay.android.demo_library_a.jdispatcher;

import android.app.Application;
import android.util.Log;

import com.jay.android.dispatcher.annotation.Dimension;
import com.jay.android.dispatcher.annotation.Dispatch;
import com.jay.android.dispatcher.common.DispatchItem;
import com.jay.android.dispatcher.dispatch.DispatchTemplate;

import org.jetbrains.annotations.NotNull;

/**
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Dispatch(name = "D_A_03",
        priority = 2,
        dimension = Dimension.DIMENSION_DEFAULT_THREAD_UI_IDLE,
        description = "组件B的03个Dispatch")
public class Demo_Library_A_Dispatch_03 extends DispatchTemplate {
    @Override
    public void onCreate(@NotNull Application app, @NotNull DispatchItem dispatchItem) {
        Log.d("Jay", "Demo_Library_A_Dispatch_03#onCreate" + dispatchItem);
        Log.d("Jay", "Demo_Library_A_Dispatch_03,currentThread: " + Thread.currentThread().getName());

    }

}
