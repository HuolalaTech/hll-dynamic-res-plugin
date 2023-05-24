package com.lalamove.huolala.dynamicbase.so;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ISoLoadListener
 * @author: huangyuchen
 * @date: 4/18/22
 * @description:
 * @history:
 */
public interface ILoadSoListener {
    /**
     * so 加载成功
     * @param path so路径
     */
    void onSucceed(String path);

    /**
     * 加载失败
     * @param t 异常
     */
    void onError(Throwable t);
}
