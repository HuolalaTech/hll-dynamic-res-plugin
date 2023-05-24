package com.lalamove.huolala.dynamiccore.local;

import com.lalamove.huolala.dynamiccore.bean.LocalResStateInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ILocalState
 * @author: huangyuchen
 * @date: 3/9/22
 * @description: 资源状态接口
 * @history:
 */
public interface ILocalState {
    /**
     * 获取当前资源状态
     *
     * @param resId
     * @return
     */
    LocalResStateInfo getState(String resId);

    /**
     * 存储当前资源状态
     *
     * @param resId
     * @param info
     */
    void setState(String resId, LocalResStateInfo info);

    /**
     * 删除当前资源状态
     *
     * @param resId
     */
    void deleteState(String resId);
}


