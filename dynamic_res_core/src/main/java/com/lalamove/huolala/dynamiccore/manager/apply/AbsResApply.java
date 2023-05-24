package com.lalamove.huolala.dynamiccore.manager.apply;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;
import com.lalamove.huolala.dynamiccore.listener.DefaultLoadResListener;
import com.lalamove.huolala.dynamiccore.listener.ILoadResListener;
import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;
import com.lalamove.huolala.dynamiccore.util.ReportUtil;
import com.lalamove.huolala.dynamicres.R;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: AbsResApply
 * @author: huangyuchen
 * @date: 3/14/22
 * @description: 动态资源使用抽闲类
 * @history:
 */
public abstract class AbsResApply<V extends View, T> {


    protected ILoadResListener apply(V v, DynamicPkgInfo pkg, T defaultObj) {
        if (v == null || pkg == null) {
            reportFail(pkg, new DynamicResException(DynamicConst.Error.PARAM_CHECK, " apply check param null "));
            return null;
        }
        //从缓存获取成功，直接返回
        if (loadResFromCache(v, pkg, defaultObj)) {
            return null;
        }
        //如果存在默认资源，先加载
        if (defaultObj != null) {
            setDefaultRes(v, pkg, defaultObj);
        }
        DynamicResManager manager = DynamicResManager.getInstance();
        Context c = v.getContext();
        //获取lifecycler对象
        LifecycleOwner owner = null;
        if (c instanceof LifecycleOwner) {
            owner = (LifecycleOwner) c;
        }
        //尝试清除失败状态
        manager.clearFailState(pkg);
        //为该View设置一个资源id为tag，防止该view出现在列表中，导致加载错误
        v.setTag(R.id.dynamic_core_tag, pkg.getId());
        ILoadResListener listener = new DefaultLoadResListener() {
            @Override
            public void onSucceed(LoadResInfo info) {
                handleLoadResult(v, pkg, info);
            }

            @Override
            public void onError(DynamicResException e) {
                super.onError(e);
                reportFail(pkg, e);
            }
        };

        manager.load(pkg, listener, owner);
        return listener;
    }

    /**
     * 处理加载成功的结果
     *
     * @param v
     * @param pkg
     * @param info
     */
    protected void handleLoadResult(final V v, DynamicPkgInfo pkg, LoadResInfo info) {
        if (v == null || info == null || pkg == null) {
            reportFail(pkg, new DynamicResException(DynamicConst.Error.APPLY, " handleLoadResult param null "));
            return;
        }

        Object tag = v.getTag(R.id.dynamic_core_tag);
        v.setTag(R.id.dynamic_core_tag, null);
        //tag不匹配(可能是因为view在listview或者Recycler中)，直接返回
        if (!(tag instanceof String) || !TextUtils.equals((String) tag, pkg.getId())) {
            reportFail(pkg, new DynamicResException(DynamicConst.Error.APPLY, " handleLoadResult view tag error "));
            return;
        }

        Context c = v.getContext();
        //如果是LifecycleOwner对象，则只在DESTROYED状态前调用成功方法
        if (c instanceof LifecycleOwner) {
            LifecycleOwner life = (LifecycleOwner) c;
            if (life.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED) {
                callLoadSucceed(v, pkg, info);
            }
            reportFail(pkg, new DynamicResException(DynamicConst.Error.APPLY, " activity destroy "));
            return;
        }
        //如果是Activity对象,则我们只在它未被销毁时调用onLoadSucceed方法
        if (c instanceof Activity) {
            Activity a = (Activity) c;
            if (!a.isFinishing() && !a.isDestroyed()) {
                callLoadSucceed(v, pkg, info);
            }
            reportFail(pkg, new DynamicResException(DynamicConst.Error.APPLY, " activity destroy "));
            return;
        }

        callLoadSucceed(v, pkg, info);
    }

    private void callLoadSucceed(final V v, DynamicPkgInfo pkg, LoadResInfo info) {
        try {
            onLoadSucceed(v, pkg, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onLoadSucceed(V v, DynamicPkgInfo pkg, LoadResInfo info);

    protected boolean loadResFromCache(V v, DynamicPkgInfo pkg, T defaultObj) {
        return false;
    }

    protected void setDefaultRes(V v, DynamicPkgInfo pkg, T defaultObj) {

    }

    protected void reportSucceed(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return;
        }
        ReportUtil.monitorSummaryApplySucceed(DynamicResManager.getInstance().getConfig().getMonitor(), pkg);
    }

    protected void reportFail(DynamicPkgInfo pkg, DynamicResException ex) {
        if (pkg == null || ex == null) {
            return;
        }
        ReportUtil.monitorSummaryApplyFail(DynamicResManager.getInstance().getConfig().getMonitor(), pkg, ex);
    }

}
