package com.lalamove.huolala.dynamicbase.so;


/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ILoadSoManager
 * @author: huangyuchen
 * @date: 2022/5/23
 * @description:
 * @history:
 */
public interface ILoadSoManager {
    boolean isSoReady(String libName);

    void loadSo(String libName, ILoadSoListener listener);
}
