package com.jay.android.demo_library_a.jdispatcher;

import android.app.Application;

import com.jay.android.dispatcher.annotation.Dimension;
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
@Dispatch(
    name = "D_A_04",
    priority = Priority.HIGH.HIGH_III,
    description = "组件B的04个Dispatch")
public class Demo_Library_A_Dispatch_04 extends DispatchTemplate {
  @Override
  public void onCreate(@NotNull Application app, @NotNull DispatchItem dispatchItem) {
    super.onCreate(app, dispatchItem);
  }
}
