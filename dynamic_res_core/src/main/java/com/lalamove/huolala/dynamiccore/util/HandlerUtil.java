package com.lalamove.huolala.dynamiccore.util;

import android.os.Handler;
import android.os.Looper;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: HandlerUtil
 * @author: huangyuchen
 * @date: 3/5/22
 * @description:
 * @history:
 */
public class HandlerUtil {

    private HandlerUtil() {
    }

    private static Handler sHandler;

    public static void runOnUiThread(Runnable action) {
        if (!isInMainThread()) {
            if (sHandler == null) {
                sHandler = new Handler(Looper.getMainLooper());
            }
            sHandler.post(action);
        } else {
            action.run();
        }
    }

    public static boolean isInMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
