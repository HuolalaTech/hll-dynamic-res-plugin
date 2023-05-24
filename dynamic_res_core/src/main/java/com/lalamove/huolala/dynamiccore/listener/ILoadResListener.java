package com.lalamove.huolala.dynamiccore.listener;

import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;
import com.lalamove.huolala.dynamiccore.state.base.State;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ILoadResListener
 * @author: huangyuchen
 * @date: 3/3/22
 * @description: 资源加载回调接口
 * @history:
 */
public interface ILoadResListener {
    /**
     * 资源加载成功
     *
     * @param info
     */
    void onSucceed(LoadResInfo info);

    /**
     * 资源加载失败
     *
     * @param e
     */
    void onError(DynamicResException e);

    /**
     * 资源正在加载中
     *
     * @param progress
     */
    void onDownloading(int progress);

    /**
     * 资源加载状态改变
     *
     * @param state
     */
    void onStateChange(State state);
}
