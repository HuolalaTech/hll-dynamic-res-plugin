package com.lalamove.huolala.dynamiccore.local;

import android.content.Context;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultLocalProvider
 * @author: huangyuchen
 * @date: 4/17/22
 * @description:
 * @history:
 */
public class DefaultLocalProvider implements ILocalProvider {
    private final Context mContext;

    public DefaultLocalProvider(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ILocalRes getLocalRes() {
        return new DefaultLocalRes(mContext);
    }

    @Override
    public ILocalState getLocalState() {
        return new DefaultLocalState(mContext);
    }
}
