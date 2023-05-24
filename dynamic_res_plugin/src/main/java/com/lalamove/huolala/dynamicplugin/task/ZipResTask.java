package com.lalamove.huolala.dynamicplugin.task;

import com.lalamove.huolala.dynamicbase.DynamicResType;
import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamicplugin.DynamicParam;
import com.lalamove.huolala.dynamicplugin.PluginConst;
import com.lalamove.huolala.dynamicplugin.util.DynamicUtil;
import com.lalamove.huolala.dynamicplugin.util.Log;
import com.lalamove.huolala.dynamicplugin.util.TaskUtil;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ZipResTask
 * @author: huangyuchen
 * @date: 4/16/22
 * @description:
 * @history:
 */
public class ZipResTask implements ITask {

    @Override
    public void process(Project project, DynamicParam param) {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                Task beforeTask = getBeforeTask(project, param);
                Task soSecondTask = TaskUtil.getSoSecondTask(project, param);
                Task zipResTask = project.getTasks().create(PluginConst.Task.ZIP_RES);
                zipResTask.doLast(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        doInTask(project, param);
                    }
                });
                soSecondTask.dependsOn(zipResTask);
                zipResTask.dependsOn(beforeTask);
            }
        });
    }

    private Task getBeforeTask(Project project, DynamicParam param) {
        return TaskUtil.getTaskForProject(project, PluginConst.Task.DELETE_SO, param.getSoFirstTask());
    }

    private void doInTask(Project project, DynamicParam param) {
        Log.debug(param, "ZipResTask start ");
        //首先，清空output目录下内容
        FileUtil.deleteFileOrDir(param.getOutputPath(), false);
        //如果需要处理普通资源,则依次处理
        if (param.isZipRes()) {
            //处理字体
            handleTypeface(param);
            //处理帧动画
            handleFrameAnim(param);
            //处理压缩包
            handleZipPath(param);
            //处理自定义单个文件
            handleSingle(param);
        }
        //处理so文件资源
        if (param.isZipSo()) {
            handleZipSo(param);
        }
        //尝试将数据写入文件
        writePkgsToFile(param);
    }

    private void writePkgsToFile(DynamicParam param) {
        param.getFileCreate().createFile(param.getPkgs(), param);
    }

    private void handleTypeface(DynamicParam param) {
        handleSingleFileReal(param, param.getInputTypeface(), "typeface", DynamicResType.TYPEFACE);
    }

    private void handleFrameAnim(DynamicParam param) {
        handleZipReal(param, param.getInputFrameAnim(), "frame_anim", DynamicResType.FRAME_ANIM);
    }

    private void handleZipSo(DynamicParam param) {
        handleZipReal(param, param.getInputSo(), "so", DynamicResType.SO);
    }

    private void handleZipPath(DynamicParam param) {
        handleZipReal(param, param.getInputZip(), "zip", DynamicResType.ZIP);
    }

    private void handleSingle(DynamicParam param) {
        handleSingleFileReal(param, param.getInputSingle(), "single", DynamicResType.SINGLE);
    }


    private void handleSingleFileReal(DynamicParam param, String input, String subDir, DynamicResType type) {
        File dirFile = new File(input);
        //输入不是文件夹，返回
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        //输入文件夹下没有找到文件，返回
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        //创建output/subDir文件夹
        File outDir = new File(param.getOutputPath() + File.separator + subDir);
        FileUtil.createOrExistsDir(outDir);
        //遍历所有子文件
        for (File file : files) {
            if (!file.isFile() || file.getName().startsWith(".")) {
                continue;
            }
            //拷贝子文件
            String outFile = outDir + File.separator + file.getName();
            FileUtil.copyFile(file.getAbsolutePath(), outFile);
            DynamicUtil.createPkgData(param, param.getPkgs(), new File(outFile), type);
        }
    }


    private void handleZipReal(DynamicParam param, String input, String subDir, DynamicResType type) {
        //在输出目录下，创建本项目子文件夹
        File outDir = new File(param.getOutputPath() + File.separator + subDir);
        FileUtil.createOrExistsDir(outDir);
        List<DynamicUtil.ZipInfo> zips = DynamicUtil.zipFolder(new File(input), outDir);
        DynamicUtil.createPkgData(param, param.getPkgs(), zips, type);
    }
}
