package com.lalamove.huolala.dynamiccore.state.base;

import androidx.lifecycle.LifecycleOwner;

import com.lalamove.huolala.dynamiccore.listener.ILoadResListener;
import com.lalamove.huolala.dynamiccore.listener.LoadResDispatch;
import com.lalamove.huolala.dynamiccore.manager.DynamicConfig;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IStateMachine
 * @author: huangyuchen
 * @date: 3/3/22
 * @description: 状态机接口
 * @history:
 */
public interface IStateMachine {
    /**
     * 设置当前状态
     *
     * @param state 状态
     */
    void setCurrentState(IState state);

    /**
     * 获取当前状态
     *
     * @return 状态
     */
    IState getCurrentState();

    /**
     * 状态机继续执行
     */
    void process();

    /**
     * 状态机强制线程池中执行
     */
    void processInExec();

    /**
     * 启动状态机
     *
     * @param listener 资源加载监听
     * @param owner    生命周期舰艇
     */
    void start(ILoadResListener listener, LifecycleOwner owner);

    /**
     * 获取全局环境Context
     *
     * @return 状态机全局Context
     */
    ResCtx getResContext();

    /**
     * 获取状态分发器Dispatch
     *
     * @return 状态分发器Dispatch
     */
    LoadResDispatch getDispatch();

    /**
     * 获取全局配置信息
     *
     * @return 全局配置信息
     */
    DynamicConfig getConfig();
}
