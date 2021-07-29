package com.jay.android.dispatcher.common;

import java.util.Map;

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/8/21
 */
public interface IDispatchGroup {

    void loadInto(Map<String, DispatchItem> atlas);

}
