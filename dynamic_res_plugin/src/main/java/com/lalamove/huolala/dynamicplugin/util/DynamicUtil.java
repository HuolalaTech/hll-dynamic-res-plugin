package com.lalamove.huolala.dynamicplugin.util;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.DynamicResType;
import com.lalamove.huolala.dynamicbase.bean.AbsResInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamicbase.util.Md5Util;
import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamicbase.util.ZipUtil;
import com.lalamove.huolala.dynamicplugin.DynamicParam;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicUtil
 * @author: huangyuchen
 * @date: 4/19/22
 * @description:
 * @history:
 */
public class DynamicUtil {

    private DynamicUtil() {
    }

    public static List<ZipInfo> zipFolder(File inputDir, File outDir) {
        List<ZipInfo> list = new ArrayList<>();
        //如果输入为空，或者不存在，或者不为文件夹，直接返回
        if (inputDir == null || !inputDir.exists() || !inputDir.isDirectory()) {
            return list;
        }
        //如果输出为空，或者不存在，或者不为文件夹，直接返回
        if (outDir == null || !outDir.exists() || !outDir.isDirectory()) {
            return list;
        }
        File[] inputDirs = inputDir.listFiles();
        if (inputDirs == null || inputDirs.length == 0) {
            return list;
        }
        for (File input : inputDirs) {
            if (!input.isDirectory()) {
                continue;
            }
            String name = input.getName() + ".zip";
            File out = new File(outDir + File.separator + name);
            FileUtil.createOrExistsFile(out);
            try {
                ZipUtil.toZip(input.getAbsolutePath(), new FileOutputStream(out), true);
                list.add(new ZipInfo(input, out, TextUtil.removeFileSuffix(input.getName())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void createPkgData(DynamicParam param, List<DynamicPkgInfo> pkgs, File file, DynamicResType type) {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        if (pkgs == null) {
            return;
        }
        String name = file.getName();
        String id = TextUtil.removeFileSuffix(file.getName());
        long length = file.length();
        String md5 = Md5Util.getFileMD5(file, false);
        String url = param.uploadDynamicPkg(id, file.getAbsolutePath());
        DynamicPkgInfo pkg = new DynamicPkgInfo(id, name, type, -1, url, -9999, length, md5);

        pkgs.add(pkg);
    }

    public static void createPkgData(DynamicParam param, List<DynamicPkgInfo> pkgs, List<ZipInfo> zips, DynamicResType type) {
        if (zips == null || zips.size() == 0) {
            return;
        }
        if (pkgs == null) {
            return;
        }

        for (ZipInfo zip : zips) {
            String name = zip.zipFile.getName();
            String id = zip.id;
            long length = zip.zipFile.length();
            String md5 = Md5Util.getFileMD5(zip.zipFile, false);
            String url = param.uploadDynamicPkg(id, zip.zipFile.getAbsolutePath());
            DynamicPkgInfo pkg = new DynamicPkgInfo(id, name, type, -1, url, -9999, length, md5);
            createSubResData(pkg, zip.orgFile);
            pkgs.add(pkg);
        }
    }

    private static void createSubResData(AbsResInfo resInfo, File input) {
        if (resInfo.getVerifyType() == DynamicConst.VerifyType.FILE) {
            return;
        }
        if (input.isDirectory()) {
            File[] files = input.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            DynamicPkgInfo.FolderInfo folderInfo = new DynamicPkgInfo.FolderInfo(input.getName());
            resInfo.putSubResInfo(folderInfo);
            for (File file : files) {
                createSubResData(folderInfo, file);
            }
        } else if (input.isFile()) {
            String name = input.getName();
            long length = input.length();
            String md5 = Md5Util.getFileMD5(input, false);
            DynamicPkgInfo.FileInfo fileInfo = new DynamicPkgInfo.FileInfo(name, md5, length);
            resInfo.putSubResInfo(fileInfo);
        }
    }

    public static class ZipInfo {
        public final File orgFile;
        public final File zipFile;
        public final String id;

        public ZipInfo(File orgFile, File zipFile, String id) {
            this.orgFile = orgFile;
            this.zipFile = zipFile;
            this.id = id;
        }
    }

}
