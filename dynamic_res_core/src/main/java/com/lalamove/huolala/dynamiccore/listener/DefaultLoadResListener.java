package com.lalamove.huolala.dynamiccore.listener;

import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultLoadResListener
 * @author: huangyuchen
 * @date: 3/13/22
 * @description:
 * @history:
 */
public abstract class DefaultLoadResListener implements ILoadResListener {
    @Override
    public void onSucceed(LoadResInfo info) {

    }

    @Override
    public void onError(DynamicResException e) {

    }

    @Override
    public void onDownloading(int progress) {

    }

    @Override
    public void onStateChange(State state) {

    }
}
