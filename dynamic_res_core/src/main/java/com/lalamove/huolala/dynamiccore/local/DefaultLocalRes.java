package com.lalamove.huolala.dynamiccore.local;

import android.content.Context;

import com.lalamove.huolala.dynamiccore.bean.LocalResInfo;
import com.lalamove.huolala.dynamiccore.db.LocalResDao;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicResVersion
 * @author: huangyuchen
 * @date: 3/4/22
 * @description:
 * @history:
 */
public class DefaultLocalRes implements ILocalRes {

    private final LocalResDao mDao;

    public DefaultLocalRes(Context c) {
        mDao = new LocalResDao(c);
    }

    @Override
    public LocalResInfo getInfo(String resId) {
        return mDao.findByKey(resId);
    }

    @Override
    public void setInfo(String resId, LocalResInfo info) {
        mDao.replace(info);
    }

    @Override
    public void delete(String resId) {
        mDao.delete(resId);
    }
}
