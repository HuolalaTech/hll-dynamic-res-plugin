package com.lalamove.huolala.dynamicplugin;

import com.lalamove.huolala.dynamicplugin.task.DeleteAndCopySoTask;
import com.lalamove.huolala.dynamicplugin.task.ITask;
import com.lalamove.huolala.dynamicplugin.task.TransformTask;
import com.lalamove.huolala.dynamicplugin.task.ZipResTask;
import com.lalamove.huolala.dynamicplugin.util.Log;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;


/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: SystemLoadPlugin
 * @author: huangyuchen
 * @date: 4/7/22
 * @description:
 * @history:
 */
public class DynamicPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        DynamicParam params = new DynamicParam();
        params.parse(project.getRootProject().getExtensions().getExtraProperties());
        Log.debug(params, " DynamicPlugin start ");
        List<ITask> list = createTasks(params);
        execTasks(list, project, params);
    }

    private List<ITask> createTasks(DynamicParam param) {
        List<ITask> list = new ArrayList<>();
        Log.debug(param, " createTask replaceLoadLibrary " + param.isReplaceLoadLibrary()
                + " replaceLoad " + param.isReplaceLoad()
                + " deleteSo " + param.isDeleteSo() + " copySo " + param.isCopySo()
                + " zipRes " + param.isZipRes() + " zipSo " + param.isZipSo());
        if (param.isReplaceLoadLibrary() || param.isReplaceLoad()) {
            //执行替换System.loadlibrary操作||替换System.load操作
            list.add(new TransformTask());
            Log.debug(param, " add TransformTask ");
        }
        if (param.isDeleteSo() || param.isCopySo()) {
            //删除||拷贝so
            list.add(new DeleteAndCopySoTask());
            Log.debug(param, " add DeleteAndCopySoTask ");
        }
        if (param.isZipRes() || param.isZipSo()) {
            //对资源||so 进行压缩 生成配置文件
            list.add(new ZipResTask());
            Log.debug(param, " add ZipResTask ");
        }
        return list;
    }

    private void execTasks(List<ITask> list, Project project, DynamicParam param) {
        for (ITask task : list) {
            task.process(project, param);
        }
    }
}
