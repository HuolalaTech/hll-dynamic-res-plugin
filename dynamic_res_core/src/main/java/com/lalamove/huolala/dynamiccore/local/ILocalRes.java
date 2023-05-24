package com.lalamove.huolala.dynamiccore.local;

import com.lalamove.huolala.dynamiccore.bean.LocalResInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IResourceVersion
 * @author: huangyuchen
 * @date: 3/1/22
 * @description: 本次资源信息存储接口
 * @history:
 */
public interface ILocalRes {
    /**
     * 根据资源id，获取本地资源信息
     *
     * @param resId
     * @return
     */
    LocalResInfo getInfo(String resId);

    /**
     * 存储本地资源信息
     *
     * @param resId
     * @param info
     */
    void setInfo(String resId, LocalResInfo info);

    /**
     * 删除本地资源信息
     *
     * @param resId
     */
    void delete(String resId);
}
