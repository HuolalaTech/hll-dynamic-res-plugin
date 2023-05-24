package com.lalamove.huolala.dynamiccore.local;

import android.content.Context;

import com.lalamove.huolala.dynamiccore.bean.LocalResStateInfo;
import com.lalamove.huolala.dynamiccore.db.LocalStateDao;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultLocalState
 * @author: huangyuchen
 * @date: 3/9/22
 * @description:
 * @history:
 */
public class DefaultLocalState implements ILocalState {

    private LocalStateDao mDao;

    public DefaultLocalState(Context c) {
        mDao = new LocalStateDao(c);
    }

    @Override
    public LocalResStateInfo getState(String resId) {
        return mDao.findByKey(resId);
    }

    @Override
    public void setState(String resId, LocalResStateInfo info) {
        mDao.replace(info);
    }

    @Override
    public void deleteState(String resId) {
        mDao.delete(resId);
    }
}
