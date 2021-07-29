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
@Dispatch(name = "D_A_02",
        priority = 20,
        dimension = Dimension.DIMENSION_DEFAULT_THREAD_WORK,
        description = "组件B的02个Dispatch")
public class Demo_Library_A_Dispatch_02 extends DispatchTemplate {

    @Override
    public void onCreate(@NotNull Application app, @NotNull DispatchItem dispatchItem) {
        Log.d("Jay", "Demo_Library_A_Dispatch_02#onCreate" + dispatchItem);
        Log.d("Jay", "Demo_Library_A_Dispatch_02,currentThread: " + Thread.currentThread().getName());


    }
}
