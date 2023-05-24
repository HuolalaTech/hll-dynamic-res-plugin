package com.lalamove.huolala.dynamiccore.util;

import android.util.Log;

import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DebugLogUtil
 * @author: huangyuchen
 * @date: 2022/5/29
 * @description:
 * @history:
 */
public class DebugLogUtil {

    private DebugLogUtil() {
    }

    private static final String TAG = "dynamic_res";

    public static void d(String msg) {
        if (isDebugLog()) {
            Log.d(TAG, msg);
        }
    }

    private static boolean isDebugLog() {
        DynamicResManager manager = DynamicResManager.getInstance();
        if (manager.getConfig() == null) {
            return false;
        }
        return manager.getConfig().isDebugLog();
    }
}
