package com.lalamove.huolala.dynamicplugin.task;

import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamicplugin.DynamicParam;
import com.lalamove.huolala.dynamicplugin.PluginConst;
import com.lalamove.huolala.dynamicplugin.util.Log;
import com.lalamove.huolala.dynamicplugin.util.TaskUtil;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DeleteCopySoTask
 * @author: huangyuchen
 * @date: 4/15/22
 * @description:
 * @history:
 */
public class DeleteAndCopySoTask implements ITask {

    private static final String TAG = "DeleteAndCopySoTask";

    @Override
    public void process(Project project, DynamicParam param) {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                //插入到任务中间merged和strip so task之间
                Task soFirstTask = TaskUtil.getSoFirstTask(project, param);
                Task soSecondTask = TaskUtil.getSoSecondTask(project, param);
                Task deleteTask = project.getTasks().create(PluginConst.Task.DELETE_SO);
                deleteTask.doLast(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        deleteAndCopySo(param);
                    }
                });
                soSecondTask.dependsOn(deleteTask);
                deleteTask.dependsOn(soFirstTask);
            }
        });
    }

    private void deleteAndCopySo(DynamicParam param) {
        Log.debug(param, " DeleteAndCopySoTask start ");
        //as编译输出的so路径
        String soDir = param.getSoTaskOutputPath();
        File soFile = new File(soDir);
        //如果as编译的so路径不存在，抛出异常
        if (!soFile.exists() || !soFile.isDirectory()) {
            throw new IllegalArgumentException(TAG + "  soDir invalid " + soDir);
        }
        //插件so文件输入目录
        String dstDir = param.getInputSo();
        File dstFile = new File(dstDir);
        //删除待拷贝文件夹中的内容
        if (dstFile.exists()) {
            FileUtil.deleteFileOrDir(dstFile, false);
        }
        scanSoAbiPath(param, soFile);
    }

    /**
     * 扫描as编译输出的so文件夹
     * @param param Plugin 配置
     * @param soCompileOutputDir as编译输出的so文件夹
     */
    private void scanSoAbiPath(DynamicParam param, File soCompileOutputDir) {
        //获取soPath下的所有路径(形如arm64-v8a,armeabi)
        File[] files = soCompileOutputDir.listFiles();
        for (File soAbiPath : files) {
            if (soAbiPath.isDirectory() && param.getScanSoAbis().contains(soAbiPath.getName())) {
                scanSoFile(param, soAbiPath);
            }
        }
    }

    /**
     * 扫描as编译输出的so文件夹的子目录形如arm64-v8a,armeabi
     * @param param Plugin 配置
     * @param soAbiPath arm64-v8a,armeabi路径
     */
    private void scanSoFile(DynamicParam param, File soAbiPath) {
        boolean isCopySo = param.isCopySo();
        boolean isDeleteSo = param.isDeleteSo();

        //获取abi路径下所有so文件，例如armeabi/a.so,armeabi/b.so
        File[] files = soAbiPath.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        int fileNumbers = files.length;
        int deleteNumbers = 0;

        for (File soFile : files) {
            String dirName = getSoDirName(soFile, soAbiPath.getName(), param);
            if (TextUtil.isEmpty(dirName)) {
                //dirName 被忽略 或者
                continue;
            }

            if (isCopySo) {
                String copySoDir = param.getInputSo() + File.separator + dirName;
                FileUtil.createOrExistsDir(new File(copySoDir));
                copySoDir = copySoDir + File.separator + soAbiPath.getName();
                FileUtil.createOrExistsDir(new File(copySoDir));
                String copySoFile = copySoDir + File.separator + soFile.getName();
                FileUtil.copyFile(soFile.getAbsolutePath(), copySoFile);
            }

            if (isDeleteSo) {
                FileUtil.deleteFileOrDir(soFile.getAbsolutePath(), true);
                deleteNumbers++;
            }
        }

        if (deleteNumbers == fileNumbers) {
            FileUtil.deleteFileOrDir(soAbiPath, true);
        }
    }

    private String getSoDirName(File soFile, String abi, DynamicParam param) {
        //如果不是so文件,跳过
        if ((!soFile.isFile())
                || (!soFile.getName().endsWith(".so"))) {
            return "";
        }
        String fileName = soFile.getName();
        //dynamic_scan_so_map 扫描列表有so文件，我们使用扫描列表，只扫描列表文件
        if (!param.isScanSoMapEmpty()) {
            String pkgName = param.getScanSoPkgName(fileName);
            if (!TextUtil.isEmpty(pkgName)) {
                return pkgName + "_" + abi + "_so";
            }
            return "";
        }
        List<String> ignoreSoFiles = param.getIgnoreSoFiles();
        //忽略列表中包含so文件，则返回true，代表跳过文件
        if (!ignoreSoFiles.isEmpty()) {
            if (!ignoreSoFiles.contains(fileName)) {
                return param.getInputSoPrefix() + "_" + abi + "_so";
            }
            return "";
        }
        //2个列表都为空，则不扫描任何so文件
        return "";
    }
}
