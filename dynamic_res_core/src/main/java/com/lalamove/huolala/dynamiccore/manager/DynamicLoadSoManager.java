package com.lalamove.huolala.dynamiccore.manager;

import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.bean.DynamicSoInfo;
import com.lalamove.huolala.dynamicbase.so.ILoadSoListener;
import com.lalamove.huolala.dynamicbase.so.ILoadSoManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultLoadSoManager
 * @author: huangyuchen
 * @date: 2022/5/23
 * @description:
 * @history:
 */
public class DynamicLoadSoManager implements ILoadSoManager {

    private final Map<String, DynamicSoInfo> mSoMap = new HashMap<>();

    @Override
    public boolean isSoReady(String libName) {
        DynamicSoInfo info = getSoInfoFromName(libName);
        return DynamicResManager.getInstance().ioSoReady(info);
    }

    @Override
    public void loadSo(String libName, ILoadSoListener listener) {
        DynamicSoInfo info = getSoInfoFromName(libName);
        DynamicResManager.getInstance().loadSo(info, listener);
    }

    public void addSoLib(String libName, DynamicSoInfo soInfo) {
        if (TextUtils.isEmpty(libName) || soInfo == null) {
            return;
        }
        if (mSoMap.containsKey(libName)) {
            return;
        }

        mSoMap.put(libName, soInfo);
    }

    private DynamicSoInfo getSoInfoFromName(String libName) {
        if (TextUtils.isEmpty(libName)) {
            return null;
        }
        return mSoMap.get(libName);
    }
}
