package com.lalamove.huolala.dynamicbase.so;

import com.lalamove.huolala.dynamicbase.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: StaticLoadSoManager
 * @author: huangyuchen
 * @date: 2022/5/29
 * @description:
 * @history:
 */
public class StaticLoadSoManager implements ILoadSoManager {

    private boolean mLoadSucceed = false;

    @Override
    public boolean isSoReady(String libName) {
        return mLoadSucceed;
    }

    @Override
    public void loadSo(String libName, ILoadSoListener listener) {
        try {
            List<String> list = getLibList(libName);
            if (list == null || list.isEmpty()) {
                dispatchError(listener, new IllegalArgumentException(" can not find libName "));
                return;
            }
            for (String str : list) {
                System.loadLibrary(str);
            }
            dispatchSucceed(listener, "");
            mLoadSucceed = true;
        } catch (Exception e) {
            dispatchError(listener, e);
        }
    }

    private void dispatchError(ILoadSoListener listener, Throwable t) {
        if (listener != null) {
            listener.onError(t);
        }
    }

    private void dispatchSucceed(ILoadSoListener listener, String path) {
        if (listener != null) {
            listener.onSucceed(path);
        }
    }

    protected List<String> getLibList(String libName) {
        List<String> list = new ArrayList<>();
        if (!TextUtil.isEmpty(libName)) {
            list.add(libName);
        }
        return list;
    }
}
