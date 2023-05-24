package com.lalamove.huolala.client.nativelib;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicLib
 * @author: kezongze
 * @date: 2022/12/6
 * @description: 动态so测试
 * @history:
 */

public class DynamicLib {
    static {
        System.loadLibrary("dynamiclib");
    }

    public native String stringFromJNI();
}
