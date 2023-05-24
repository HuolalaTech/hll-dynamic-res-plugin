package com.lalamove.huolala.dynamicplugin.task;

import com.lalamove.huolala.dynamicplugin.DynamicParam;

import org.gradle.api.Project;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ITask
 * @author: huangyuchen
 * @date: 4/15/22
 * @description:
 * @history:
 */
public interface ITask {
    void process(Project project, DynamicParam param);
}
