package com.lalamove.huolala.dynamiccore.listener;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.util.HandlerUtil;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: LoadResDispatch
 * @author: huangyuchen
 * @date: 3/5/22
 * @description: 分发资源加载结果
 * @history:
 */
public class LoadResDispatch {
    private List<ILoadResListener> mListeners;
    private boolean mDispatchInMainThread;

    public LoadResDispatch(boolean dispatchInMain) {
        mListeners = new ArrayList<>();
        mDispatchInMainThread = dispatchInMain;
    }

    public synchronized void addListener(ILoadResListener listener) {
        addListener(listener, null);
    }

    public synchronized void addListener(ILoadResListener listener, @Nullable LifecycleOwner owner) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
            //如果传入了LifecycleOwner，则我们在生命周期结束时，自动移除监听器，防止内存泄漏
            if (owner != null) {
                owner.getLifecycle().addObserver(new LifecycleObserver() {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    void onDestroy() {
                        removeListener(listener);
                    }
                });
            }
        }
    }

    public synchronized void removeListener(ILoadResListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public synchronized void clearAllListener() {
        mListeners.clear();
    }

    public void dispatchSucceed(LoadResInfo info) {
        exce(new Runnable() {
            @Override
            public void run() {
                dispatchSucceedInner(info);
            }
        });
    }

    public void dispatchError(DynamicResException e) {
        exce(new Runnable() {
            @Override
            public void run() {
                dispatchErrorInner(e);
            }
        });
    }

    public void dispatchStateChange(State state) {
        exce(new Runnable() {
            @Override
            public void run() {
                dispatchStateChangeInner(state);
            }
        });
    }

    public void dispatchDownloading(int progress) {
        exce(new Runnable() {
            @Override
            public void run() {
                dispatchDownloadingInner(progress);
            }
        });
    }

    /**
     * 如果mDispatchInMainThread为true，则将run内容转发到主线程中
     *
     * @param run
     */
    private void exce(Runnable run) {
        if (mDispatchInMainThread) {
            HandlerUtil.runOnUiThread(run);
        } else {
            run.run();
        }
    }

    private synchronized void dispatchSucceedInner(LoadResInfo info) {
        for (ILoadResListener listener : mListeners) {
            listener.onSucceed(info);
        }
        mListeners.clear();
    }

    private synchronized void dispatchErrorInner(DynamicResException e) {
        for (ILoadResListener listener : mListeners) {
            listener.onError(e);
        }
        mListeners.clear();
    }

    private synchronized void dispatchStateChangeInner(State state) {
        for (ILoadResListener listener : mListeners) {
            listener.onStateChange(state);
        }
    }

    private synchronized void dispatchDownloadingInner(int progress) {
        for (ILoadResListener listener : mListeners) {
            listener.onDownloading(progress);
        }
    }
}
