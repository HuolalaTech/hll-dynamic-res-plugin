package com.lalamove.huolala.dynamiccore.util;

import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.AbsResInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.Md5Util;
import com.lalamove.huolala.dynamiccore.DynamicResException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: VerifyUtil
 * @author: huangyuchen
 * @date: 3/5/22
 * @description:
 * @history:
 */
public class VerifyUtil {

    private VerifyUtil() {
    }

    /**
     * 校验资源
     *
     * @param path 文件路径
     * @param info 动态资源
     * @throws DynamicResException
     */
    public static void verifyRes(String path, DynamicPkgInfo info) throws DynamicResException {
        if (TextUtils.isEmpty(path)) {
            throw new DynamicResException(DynamicConst.Error.VERIFY_ZIP, " verifyRes file empty ");
        }
        verifyFileInner(new File(path), info);
    }

    /**
     * 校验解压后的zip文件夹
     *
     * @param path 文件路径
     * @param pkg  动态资源
     * @param outFiles 输出路径
     * @throws DynamicResException 异常
     */
    public static void verifyZipFolderRes(String path, DynamicPkgInfo pkg, List<File> outFiles) throws DynamicResException {
        if (TextUtils.isEmpty(path)) {
            throw new DynamicResException(DynamicConst.Error.VERIFY_ZIP, " verifyZipFolderRes file empty ");
        }
        verifyFolderInner(new File(path), pkg, new HashMap<>(), outFiles);
    }

    /**
     * 校验文件夹
     *
     * @param folder 文件夹路径
     * @param info 动态资源
     * @param parentMap
     * @param outFiles
     * @throws DynamicResException 异常
     */
    private static void verifyFolderInner(File folder, AbsResInfo info, Map<String, AbsResInfo> parentMap, List<File> outFiles) throws DynamicResException {
        if (info.getVerifyType() == DynamicConst.VerifyType.FILE) {
            throw new DynamicResException(DynamicConst.Error.UNZIP, " verifyFolderInner调用错误,type = " + info.getVerifyType());
        }

        if (folder == null || info == null) {
            throw new DynamicResException(DynamicConst.Error.UNZIP, " 校验目录或者资源信息为空 ");
        }
        if (!folder.exists() || !folder.isDirectory()) {
            throw new DynamicResException(DynamicConst.Error.UNZIP, " 校验目录不存在 ");
        }

        Map<String, AbsResInfo> map = info.getMap();
        if (map.size() == 0) {
            //如果是资源包类型，则不允许没有子文件或文件夹
            if (info.getVerifyType() == DynamicConst.VerifyType.RES_PKG) {
                throw new DynamicResException(DynamicConst.Error.UNZIP, " 资源包内容为空 ");
            }
            //文件夹类型，没有待校验子类型，代表校验通过，直接从父类型移除
            parentMap.remove(info.getName());
            return;
        }
        //或者子文件
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            //遍历所有子文件
            for (File file : files) {
                //如果资源中不包含该文件名称，则返回
                String fileName = file.getName();
                if (!map.containsKey(fileName)) {
                    continue;
                }

                AbsResInfo childInfo = map.get(fileName);
                if (file.isFile()) {
                    //文件类型，校验文件
                    verifyFileInner(file, childInfo);
                    //校验成功，从父文件夹中移除该子文件
                    map.remove(fileName);
                    outFiles.add(file);
                } else if (file.isDirectory()) {
                    //文件夹类型，递归调用，校验该文件夹
                    verifyFolderInner(file, childInfo, map, outFiles);
                    map.remove(fileName);
                }
            }
        }

        //如果资源中所有子文件或问价夹校验通过，从父资源中移除该资源
        if (map.size() == 0) {
            parentMap.remove(info.getName());
        } else {
            throw new DynamicResException(DynamicConst.Error.UNZIP, " 校验失败 ");
        }
    }

    /**
     * 校验单个文件
     *
     * @param file 文件
     * @param info 资源信息
     * @throws DynamicResException
     */
    private static void verifyFileInner(File file, AbsResInfo info) throws DynamicResException {
        if (file == null || info == null) {
            throw new DynamicResException(DynamicConst.Error.VERIFY_ZIP, " 校验文件或者资源信息为空 ");
        }
        if (!file.exists() || !file.isFile()) {
            throw new DynamicResException(DynamicConst.Error.VERIFY_ZIP, " 校验文件不存在 ");
        }
        if (info.getVerifyType() == DynamicConst.VerifyType.FOLDER) {
            throw new DynamicResException(DynamicConst.Error.VERIFY_ZIP, " verifyFileInner error type = " + info.getVerifyType());
        }
        //校验长度 md5码和名称
        String md5 = Md5Util.getFileMD5(file, false);
        boolean lengthSame = file.length() == info.getLength();
        boolean md5Same = TextUtils.equals(info.getMd5(), md5);
        boolean nameSame = TextUtils.equals(file.getName(), info.getName());
        DebugLogUtil.d(" verifyFileInner md5 " + md5 + " length " + file.length() + " name " + file.getAbsolutePath());
        //资源包类型，不校验名称
        if (info.getVerifyType() == DynamicConst.VerifyType.RES_PKG) {
            nameSame = true;
        }
        if (!lengthSame || !md5Same || !nameSame) {
            throw new DynamicResException(DynamicConst.Error.VERIFY_ZIP, " 文件校验失败");
        }
    }


}
