package com.lalamove.huolala.dynamiccore.state.base;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IState
 * @author: huangyuchen
 * @date: 3/3/22
 * @description: 状态接口
 * @history:
 */
public interface IState {
    /**
     * 执行当前状态
     *
     * @param machine 状态机
     */
    void process(IStateMachine machine);

    /**
     * 获取当前状态
     *
     * @return 状态
     */
    State getState();
}
