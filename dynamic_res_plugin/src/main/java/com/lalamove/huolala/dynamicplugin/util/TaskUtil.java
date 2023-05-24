package com.lalamove.huolala.dynamicplugin.util;

import com.lalamove.huolala.dynamicplugin.DynamicParam;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: TaskUtil
 * @author: huangyuchen
 * @date: 4/20/22
 * @description:
 * @history:
 */
public class TaskUtil {

    private TaskUtil() {
    }

    public static Task getTaskForProject(Project project, String... names) {
        if (project == null || names == null || names.length == 0) {
            return null;
        }
        Task task = null;
        TaskContainer container = project.getTasks();
        for (String name : names) {
            try {
                task = container.getByName(name);
                return task;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return task;
    }

    public static Task getSoFirstTask(Project project, DynamicParam param) {
        return getTaskForProject(project, param.getSoFirstTask());
    }

    public static Task getSoSecondTask(Project project, DynamicParam param) {
        return getTaskForProject(project, param.getSoSecondTask());
    }

}
