package com.lalamove.huolala.dynamicplugin.create;

import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicplugin.DynamicParam;

import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IDynamicFileCreate
 * @author: huangyuchen
 * @date: 4/19/22
 * @description:
 * @history:
 */
public interface IDynamicFileCreate {
    /**
     * 生成Java文件
     * @param pkgs 动态包数据
     * @param param 配置
     */
    void createFile(List<DynamicPkgInfo> pkgs, DynamicParam param);
}
