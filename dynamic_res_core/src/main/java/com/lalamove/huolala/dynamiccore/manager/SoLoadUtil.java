package com.lalamove.huolala.dynamiccore.manager;


import android.util.Log;

import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamiccore.util.DebugLogUtil;

import java.io.File;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: SoLoadUtil
 * @author: huangyuchen
 * @date: 4/17/22
 * @description: so加载工具类 插桩使用。包名类名改动需要插件进行同步
 * @history:
 */
public class SoLoadUtil {
    private SoLoadUtil() {
    }

    public static void loadLibrary(String libName) {
        DebugLogUtil.d(" SoLoadUtil.loadLibrary " + libName + "  " + Log.getStackTraceString(new Throwable()));
        DynamicResManager.getInstance().proxySystemLoadSo(libName);
    }

    public static void load(String fileName) {
        if (TextUtil.isEmpty(fileName) || !fileName.endsWith(".so")) {
            return;
        }
        int index = fileName.lastIndexOf(File.separator);
        String libName = "";
        if (index >= 0) {
            libName = fileName.substring(index + 1);
        }
        index = fileName.indexOf("lib");
        if (index < 0) {
            return;
        }
        libName = libName.replaceFirst("lib", "");
        libName = libName.replace(".so", "");
        DynamicResManager.getInstance().proxySystemLoadSo(libName);
    }

}
