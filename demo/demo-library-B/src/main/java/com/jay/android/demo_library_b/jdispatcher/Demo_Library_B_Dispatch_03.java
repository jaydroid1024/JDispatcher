package com.jay.android.demo_library_b.jdispatcher;

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
@Dispatch(name = "D_B_03",
        priority = 30,
        dependencies = {"D_B_02", "D_B_01"},
        dimension = Dimension.DIMENSION_DEFAULT_MANUAL,
        description = "组件B的03个Dispatch")
public class Demo_Library_B_Dispatch_03 extends DispatchTemplate {
    @Override
    public void onCreate(@NotNull Application app, @NotNull DispatchItem dispatchItem) {
        Log.d("Jay", "DemoLibrary_B_Dispatch_03#onCreate" + dispatchItem);
    }

}
