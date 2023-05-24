package com.lalamove.huolala.dynamiccore.manager;

import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.DynamicResType;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicSoInfo;
import com.lalamove.huolala.dynamicbase.so.ILoadSoListener;
import com.lalamove.huolala.dynamicbase.so.ILoadSoManager;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;
import com.lalamove.huolala.dynamiccore.listener.ILoadResListener;
import com.lalamove.huolala.dynamiccore.listener.LoadResDispatch;
import com.lalamove.huolala.dynamiccore.manager.apply.FrameAnimApply;
import com.lalamove.huolala.dynamiccore.manager.apply.TypefaceResApply;
import com.lalamove.huolala.dynamiccore.manager.soload.AbstractSoLoader;
import com.lalamove.huolala.dynamiccore.manager.soload.RelinkerSoLoader;
import com.lalamove.huolala.dynamiccore.state.InitState;
import com.lalamove.huolala.dynamiccore.state.base.DefaultStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.IState;
import com.lalamove.huolala.dynamiccore.state.base.ResCtx;
import com.lalamove.huolala.dynamiccore.state.base.State;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicResManager
 * @author: huangyuchen
 * @date: 3/5/22
 * @description:
 * @history:
 */
public class DynamicResManager {
    /**
     * 键为资源ID，值为状态管理器
     */
    private Map<String, IStateMachine> mMachines;
    /**
     * 配置对象
     */
    private DynamicConfig mConfig;
    /**
     * 字体缓存
     */
    private Map<String, TypefaceResApply> mTypefaceCache;

    private AbstractSoLoader mSoLoader;


    private DynamicResManager() {
        mMachines = new ConcurrentHashMap<>();
        mTypefaceCache = new ConcurrentHashMap<>();
        mSoLoader = new RelinkerSoLoader();
    }

    public static DynamicResManager getInstance() {
        return Holder.sInstance;
    }

    public void init(DynamicConfig config) {
        if (mConfig == null) {
            mConfig = config;
        }
        //设置外部传入的so loader
        if (mConfig != null && mConfig.getSoLoader() != null) {
            mSoLoader = mConfig.getSoLoader();
        }
    }

    public void load(DynamicPkgInfo pkg, ILoadResListener listener) {
        load(pkg, listener, null, true);
    }

    public void load(DynamicPkgInfo pkg, ILoadResListener listener, LifecycleOwner owner) {
        load(pkg, listener, owner, true);
    }

    /**
     * 加载动态资源
     *
     * @param pkg            资源信息
     * @param listener       回调监听器
     * @param owner          生命周期管理
     * @param callbackInMain 回调是否强制在主线程中
     */
    public void load(DynamicPkgInfo pkg, ILoadResListener listener, LifecycleOwner owner, boolean callbackInMain) {
        if (pkg == null) {
            if (listener != null) {
                listener.onError(new DynamicResException(DynamicConst.Error.PARAM_CHECK, " pkg is null "));
            }
            return;
        }
        IStateMachine machine;
        //如果该资源没有对应的状态管理器，则创建
        if (!mMachines.containsKey(pkg.getId())) {
            ResCtx ctx = new ResCtx(pkg);
            machine = new DefaultStateMachine(ctx, mConfig, new LoadResDispatch(callbackInMain));
            //设置当前状态为初始状态
            machine.setCurrentState(new InitState());
            //将他放到缓存中
            mMachines.put(pkg.getId(), machine);
        } else {
            //否则，直接从缓存中获取已创建的machine
            machine = mMachines.get(pkg.getId());
        }
        //开始状态管理器执行
        machine.start(listener, owner);
    }

    /**
     * 移除监听器
     *
     * @param pkg
     * @param listener
     */
    public void removeListener(DynamicPkgInfo pkg, ILoadResListener listener) {
        if (pkg == null || listener == null) {
            return;
        }
        IStateMachine machine = mMachines.get(pkg.getId());
        if (machine == null) {
            return;
        }
        machine.getDispatch().removeListener(listener);
    }

    /**
     * 设置动态字体
     *
     * @param tv
     * @param pkg
     */
    public void setTypeface(TextView tv, DynamicPkgInfo pkg) {
        if (tv == null || pkg == null) {
            return;
        }
        //如果资源信息不是字体，直接返回
        if (pkg.getType() != DynamicResType.TYPEFACE) {
            return;
        }
        TypefaceResApply apply = mTypefaceCache.get(pkg.getId());
        if (apply == null) {
            apply = new TypefaceResApply();
            mTypefaceCache.put(pkg.getId(), apply);
        }
        apply.setTypeface(tv, pkg);
    }

    /**
     * 创建帧动画动态资源
     *
     * @param iv
     * @return
     */
    public FrameAnimApply createFrameAnim(ImageView iv) {
        return FrameAnimApply.createImageSrc(iv);
    }

    void loadSo(DynamicSoInfo info, ILoadSoListener listener) {
        mSoLoader.loadSo(info, listener);
    }

    boolean ioSoReady(DynamicSoInfo info) {
        if (info == null) {
            return false;
        }
        DynamicPkgInfo pkg = info.getPkgInfo(Build.SUPPORTED_ABIS);
        if (pkg == null) {
            return false;
        }

        return mSoLoader.isSoLoad(pkg);
    }

    public ILoadSoManager getLoadSoManager() {
        return (mConfig == null) ? (null) : (mConfig.getLoadSoManager());
    }


    void proxySystemLoadSo(String libName) {
        Context c = (mConfig != null) ? (mConfig.getAppContext()) : null;
        mSoLoader.proxySystemSoLoad(c, libName);
    }

    /**
     * 清除失败状态
     *
     * @param pkg
     */
    public void clearFailState(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return;
        }
        IStateMachine machine = mMachines.get(pkg.getId());
        if (machine == null) {
            return;
        }
        IState state = machine.getCurrentState();
        if (state == null) {
            return;
        }
        if (state.getState() == State.ERROR) {
            machine.getResContext().clear();
            machine.setCurrentState(new InitState());
        }
    }

    /**
     * 清除成功状态
     *
     * @param pkg
     */
    public void clearSucceedState(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return;
        }
        IStateMachine machine = mMachines.get(pkg.getId());
        if (machine == null) {
            return;
        }
        IState state = machine.getCurrentState();
        if (state == null) {
            return;
        }
        if (state.getState() == State.SUCCEED) {
            machine.getResContext().clear();
            machine.setCurrentState(new InitState());
        }
    }

    /**
     * 资源是否就绪
     *
     * @param pkg
     * @return
     */
    public boolean isResReady(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return false;
        }
        IStateMachine machine = mMachines.get(pkg.getId());
        if (machine == null) {
            return false;
        }
        IState state = machine.getCurrentState();
        if (state == null) {
            return false;
        }
        return state.getState() == State.SUCCEED;
    }

    /**
     * 获取资源存储信息，为空说明不存在或未加载成功
     *
     * @param pkg
     * @return
     */
    public LoadResInfo getResInfo(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return null;
        }
        IStateMachine machine = mMachines.get(pkg.getId());
        if (machine == null) {
            return null;
        }
        return machine.getResContext().getmSucceedInfo();
    }

    public DynamicConfig getConfig() {
        return mConfig;
    }

    static class Holder {
        private Holder() {
        }

        static DynamicResManager sInstance = new DynamicResManager();
    }

}
