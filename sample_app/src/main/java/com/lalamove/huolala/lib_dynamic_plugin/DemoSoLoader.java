package com.lalamove.huolala.lib_dynamic_plugin;

import android.content.Context;
import android.util.Log;

import com.getkeepsafe.relinker.ReLinker;
import com.lalamove.huolala.dynamiccore.manager.soload.AbstractSoLoader;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: RelinkerSoLoad
 * @author: huangyuchen
 * @date: 4/17/22
 * @description:
 * @history:
 */
public class DemoSoLoader extends AbstractSoLoader {


    @Override
    protected void realSoLoad(Context c, String libName) {
        try {
            Log.e("DemoSoLoader",libName);
            //使用Relinker库加载so，解决动态加载时，liba依赖libB时，无法正确加载问题
            ReLinker.recursively().loadLibrary(c, libName);
            //记载成功，从等待队列中移除该库
            removeFormWaitList(libName);
        } catch (Throwable t) {
            //加载失败，将该库加入等待队列
            addToWaitList(libName);
        }
    }
}
