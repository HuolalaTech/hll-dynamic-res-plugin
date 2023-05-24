package com.lalamove.huolala.dynamiccore.state.base;

import androidx.lifecycle.LifecycleOwner;

import com.lalamove.huolala.dynamiccore.listener.ILoadResListener;
import com.lalamove.huolala.dynamiccore.listener.LoadResDispatch;
import com.lalamove.huolala.dynamiccore.manager.DynamicConfig;
import com.lalamove.huolala.dynamiccore.util.HandlerUtil;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultStateMachine
 * @author: huangyuchen
 * @date: 3/3/22
 * @description:
 * @history:
 */
public class DefaultStateMachine implements IStateMachine {
    /**
     * 当前状态信息
     */
    private IState mCurrentState;
    /**
     * 全局环境Context
     */
    private final ResCtx mResCtx;
    /**
     * 监听器分发
     */
    private final LoadResDispatch mDispatch;
    /**
     * 资源管理全局配置信息
     */
    private final DynamicConfig mConfig;
    /**
     * 执行当前状态的Runnable
     */
    private final Runnable mProcessRun;
    /**
     * 该状态机是否已经执行过了
     */
    private boolean mHasStart;

    public DefaultStateMachine(ResCtx ctx, DynamicConfig config, LoadResDispatch dispatch) {
        mResCtx = ctx;
        mConfig = config;
        this.mDispatch = dispatch;
        mHasStart = false;
        mProcessRun = () -> {
            if (mCurrentState != null) {
                mCurrentState.process(DefaultStateMachine.this);
            }
        };
    }

    @Override
    public synchronized void start(ILoadResListener listener, LifecycleOwner owner) {
        //判断当前状态，配置信息，和全局环境
        if (mCurrentState == null || mConfig == null || mResCtx == null) {
            return;
        }
        State state = mCurrentState.getState();
        if (state == State.SUCCEED) {
            //当前状态为成功，直接回调成功方法
            listener.onSucceed(mResCtx.getmSucceedInfo());
        } else if (state == State.ERROR) {
            //当前状态为失败，直接回调失败方法
            listener.onError(mResCtx.getmException());
        } else if (!mHasStart && state == State.INIT) {
            //该状态机未启动过，则将监听器加入Dispatch
            mDispatch.addListener(listener, owner);
            //开始执行状态机器
            process();
            //设置启动标志
            mHasStart = true;
        } else {
            //其他情况，加入监听器
            mDispatch.addListener(listener, owner);
        }
    }

    @Override
    public synchronized IState getCurrentState() {
        return mCurrentState;
    }

    @Override
    public synchronized void setCurrentState(IState state) {
        if (state == null) {
            return;
        }
        if (mCurrentState != state) {
            mCurrentState = state;
            //当前状态被设置为初始状态，说明我们需要重新开始，因此清除start标志
            if (state.getState() == State.INIT) {
                mHasStart = false;
            }
        }
    }

    @Override
    public synchronized void process() {
        //如果我们在主线程中，使用线程池执行状态机
        if (HandlerUtil.isInMainThread()) {
            processInExec();
        } else {
            //其他线程中，直接执行
            mProcessRun.run();
        }
    }

    @Override
    public synchronized void processInExec() {
        getConfig().getExec().execute(mProcessRun);
    }

    @Override
    public ResCtx getResContext() {
        return mResCtx;
    }

    @Override
    public LoadResDispatch getDispatch() {
        return mDispatch;
    }

    @Override
    public DynamicConfig getConfig() {
        return mConfig;
    }

}
