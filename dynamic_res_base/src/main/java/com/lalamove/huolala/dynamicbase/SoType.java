package com.lalamove.huolala.dynamicbase;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: SoType
 * @author: huangyuchen
 * @date: 4/17/22
 * @description:
 * @history:
 */
public enum SoType {
    //第5代、第6代的ARM处理器，早期的手机用的比较多。
    ARMEABI("armeabi"),
    //第7代及以上的 ARM 处理器。
    ARMEABI_V7A("armeabi-v7a"),
    //第8代、64位ARM处理器。
    ARM64_V8A("arm64-v8a"),
    //平板、模拟器用得比较多。
    X86("x86"),
    X86_64("x86_64");

    public final String name;

    SoType(String name) {
        this.name = name;
    }

    public static List<String> getSupportAbis() {
        List<String> list = new ArrayList<>();
        list.add(ARM64_V8A.name);
        list.add(ARMEABI_V7A.name);
        list.add(ARMEABI.name);
        list.add(X86.name);
        list.add(X86_64.name);
        return list;
    }
}
