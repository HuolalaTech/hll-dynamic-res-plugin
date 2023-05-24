package com.lalamove.huolala.dynamicbase.so;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: EmptyLoadSoManager
 * @author: huangyuchen
 * @date: 2022/5/30
 * @description:
 * @history:
 */
public class EmptyLoadSoManager implements ILoadSoManager {
    @Override
    public boolean isSoReady(String libName) {
        return true;
    }

    @Override
    public void loadSo(String libName, ILoadSoListener listener) {
        if (listener != null) {
            listener.onSucceed("");
        }
    }
}
