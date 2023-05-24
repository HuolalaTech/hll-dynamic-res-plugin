package com.lalamove.huolala.dynamiccore.manager.soload;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.SoType;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicSoInfo;
import com.lalamove.huolala.dynamicbase.so.ILoadSoListener;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;
import com.lalamove.huolala.dynamiccore.listener.DefaultLoadResListener;
import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;
import com.lalamove.huolala.dynamiccore.util.DebugLogUtil;
import com.lalamove.huolala.dynamiccore.util.DexUtil;
import com.lalamove.huolala.dynamiccore.util.ReportUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ISoLoad
 * @author: huangyuchen
 * @date: 4/17/22
 * @description:
 * @history:
 */
public abstract class AbstractSoLoader {
    /**
     * 等待加载的so库列表，形如libxxx
     */
    protected List<String> mSoWaitList;
    /**
     * 本及支持的abi列表
     */
    protected List<String> mLibSupportSoList;
    /**
     * 已加载完成的so缓存,key 资源id，value 资源文件路径
     */
    private final Map<String, String> mSoLoadMap;

    protected AbstractSoLoader() {
        this.mSoWaitList = new ArrayList<>();
        this.mLibSupportSoList = SoType.getSupportAbis();
        this.mSoLoadMap = new ConcurrentHashMap<>();
    }

    public void loadSo(DynamicSoInfo soInfo, ILoadSoListener listener) {
        //so资源实体类为空，直接返回
        if (soInfo == null) {
            DebugLogUtil.d(" loadSo fail soInfo null ");
            dispatchSoFail(listener, new DynamicResException(DynamicConst.Error.PARAM_CHECK, " soInfo is null "));
            return;
        }
        //根据本机abi，查找对应的资源实体类(因为我们的动态资源打包的so，不同abi会有不同的包)
        DynamicPkgInfo pkg = soInfo.getPkgInfo(Build.SUPPORTED_ABIS);
        //未找到直接返回
        if (pkg == null) {
            DynamicResException ex = new DynamicResException(DynamicConst.Error.PARAM_CHECK, " can not find pkg for abi ");
            dispatchSoFail(listener, ex);
            reportFail(soInfo.getFirstPkgInfo(), ex);
            return;
        }
        //尝试从缓存中查找当前so，如果找到，直接返回
        if (isLoadAndDispatchSo(pkg, listener)) {
            return;
        }
        DynamicResManager manager = DynamicResManager.getInstance();
        manager.clearFailState(pkg);
        //加载so库
        manager.load(pkg, new DefaultLoadResListener() {
            @Override
            public void onSucceed(LoadResInfo info) {
                super.onSucceed(info);
                handleLoadSoSucceed(pkg, info, listener);
            }

            @Override
            public void onError(DynamicResException e) {
                super.onError(e);
                dispatchSoFail(listener, e);
                reportFail(pkg, e);
            }
        });
    }

    /**
     * 根据资源id，从缓存中查找资源文件路径
     *
     * @param pkg
     * @return
     */
    private String getSoPath(DynamicPkgInfo pkg) {
        if (pkg == null) {
            return "";
        }
        return mSoLoadMap.get(pkg.getId());
    }

    /**
     * 判断资源是否加载成功，并将成功结果传递给监听器
     *
     * @param pkg
     * @param listener
     * @return
     */
    private boolean isLoadAndDispatchSo(DynamicPkgInfo pkg, ILoadSoListener listener) {
        //从缓存中查找资源路径
        String soPath = getSoPath(pkg);
        //如果找到路径，说明我们已经加载过该so资源了
        if (!TextUtils.isEmpty(soPath)) {
            Context c = DynamicResManager.getInstance().getConfig().getAppContext();
            //我们尝试加载等待列表中的so库
            loadAllFromWaitList(c);
            //回调成功给监听器
            dispatchSoSucceed(listener, soPath);
            return true;
        }
        return false;
    }

    /**
     * 分发so失败结果
     *
     * @param listener
     * @param t
     */
    private void dispatchSoFail(ILoadSoListener listener, Throwable t) {
        if (listener != null && t != null) {
            listener.onError(t);
        }
    }

    /**
     * 分发so加载成功结果
     *
     * @param listener
     * @param path
     */
    private void dispatchSoSucceed(ILoadSoListener listener, String path) {
        if (listener != null && !TextUtils.isEmpty(path)) {
            listener.onSucceed(path);
        }
    }

    protected void reportSucceed(DynamicPkgInfo pkg) {
        ReportUtil.monitorSummarySoSucceed(DynamicResManager.getInstance().getConfig().getMonitor(), pkg);
    }

    protected void reportFail(DynamicPkgInfo pkg, DynamicResException ex) {
        ReportUtil.monitorSummarySoFail(DynamicResManager.getInstance().getConfig().getMonitor(), pkg, ex);
    }

    /**
     * 判断so库是否加载完成
     *
     * @param pkg
     * @return
     */
    public boolean isSoLoad(DynamicPkgInfo pkg) {
        String abiPath = getSoPath(pkg);
        return !TextUtils.isEmpty(abiPath);
    }

    /**
     * 缓存so库加载结果
     *
     * @param pkg
     * @param abiPath
     */
    private void setSoLoad(DynamicPkgInfo pkg, String abiPath) {
        if (pkg == null || TextUtils.isEmpty(abiPath)) {
            return;
        }
        mSoLoadMap.put(pkg.getId(), abiPath);
    }

    /**
     * 处理so动态资源加载成功结果
     *
     * @param pkg
     * @param info
     * @param listener
     */
    private void handleLoadSoSucceed(DynamicPkgInfo pkg, LoadResInfo info, ILoadSoListener listener) {
        //这里首先需要考虑缓存，因为有可能其他地方已经调用过加载so库的方法，并返回了加载结果
        //如果缓存中有的话，我们就可以直接使用，无需再次加载了
        if (isLoadAndDispatchSo(pkg, listener)) {
            return;
        }
        //判断资源包中是否存在文件
        if (info == null || info.files == null || info.files.size() == 0) {
            DynamicResException ex = new DynamicResException(DynamicConst.Error.APPLY, " handleLoadSoSucceed file is empty ");
            dispatchSoFail(listener, ex);
            reportFail(pkg, ex);
            return;
        }
        //如果该资源包不包含so文件,直接返回
        File soFile = info.files.get(0);
        if (!soFile.isFile() || !soFile.getName().endsWith(".so")) {
            DynamicResException ex = new DynamicResException(DynamicConst.Error.APPLY, "handleLoadSoSucceed no so file ");
            dispatchSoFail(listener, ex);
            reportFail(pkg, ex);
            return;
        }
        //如果找不到本机需要的abi，则直接返回
        File soAbi = soFile.getParentFile();
        if (soAbi == null || !mLibSupportSoList.contains(soAbi.getName())) {
            DynamicResException ex = new DynamicResException(DynamicConst.Error.APPLY, "handleLoadSoSucceed so abi error ");
            dispatchSoFail(listener, ex);
            reportFail(pkg, ex);
            return;
        }
        Context c = DynamicResManager.getInstance().getConfig().getAppContext();
        try {
            //将so加载到系统DePathList中的数组前面
            DexUtil.installDexAndSo(c.getClassLoader(), soAbi);
            //尝试加载等待队列中的所有so库
            loadAllFromWaitList(c);
            //缓存该so路径
            setSoLoad(pkg, soAbi.getAbsolutePath());
            //分发成功结果
            dispatchSoSucceed(listener, soAbi.getAbsolutePath());
            reportSucceed(pkg);
        } catch (Throwable t) {
            DynamicResException ex = new DynamicResException(DynamicConst.Error.APPLY, t);
            dispatchSoFail(listener, ex);
            reportFail(pkg, ex);
        }
    }

    /**
     * 替代系统的System.loadLibrary方法调用
     *
     * @param c
     * @param libName
     */
    public final void proxySystemSoLoad(Context c, String libName) {
        //如果so库名称为空，直接返回
        if (TextUtils.isEmpty(libName)) {
            return;
        }
        //context为空，说明我们的动态管理系统可能没准备好，我们将该库名称放入待加载列表
        if (c == null) {
            addToWaitList(libName);
            return;
        }
        //真正加载so库的方法
        realSoLoad(c, libName);
    }

    /**
     * 将so库放入待加载列表
     *
     * @param libName
     */
    protected synchronized void addToWaitList(String libName) {
        if (!mSoWaitList.contains(libName)) {
            mSoWaitList.add(libName);
        }
    }

    /**
     * 从待加载列表中移除so库
     *
     * @param libName
     */
    protected synchronized void removeFormWaitList(String libName) {
        if (mSoWaitList.contains(libName)) {
            mSoWaitList.remove(libName);
        }
    }

    /**
     * 尝试加载所有待加载的so库
     *
     * @param c Context
     */
    protected synchronized void loadAllFromWaitList(Context c) {
        if (mSoWaitList == null || mSoWaitList.isEmpty()) {
            return;
        }
        //遍历待加载列表，尝试加载所有so库
        List<String> list = new ArrayList<>(mSoWaitList);
        for (String name : list) {
            realSoLoad(c, name);
        }
    }

    protected abstract void realSoLoad(Context c, String libName);


}
