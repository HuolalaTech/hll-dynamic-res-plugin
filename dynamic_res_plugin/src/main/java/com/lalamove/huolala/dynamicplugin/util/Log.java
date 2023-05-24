package com.lalamove.huolala.dynamicplugin.util;

import com.lalamove.huolala.dynamicplugin.DynamicParam;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: Log
 * @author: huangyuchen
 * @date: 4/14/22
 * @description:
 * @history:
 */
public class Log {

    private Log() {
    }

    private static final String TAG = "dynamic_plugin";

    public static void d(String tag, String msg) {
        System.out.println(tag + " : " + msg);
    }

    public static void debug(DynamicParam param, String msg) {
        if (param == null) {
            return;
        }
        if (param.isDebugLog()) {
            d(TAG, msg);
        }
    }
}
