package com.lalamove.huolala.dynamiccore.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.FileUtil;

import java.io.File;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: PathUtil
 * @author: huangyuchen
 * @date: 4/24/22
 * @description:
 * @history:
 */
public class PathUtil {

    private PathUtil() {
    }

    /**
     * 获取解压缩文件路径
     *
     * @param c Context
     * @return 路径
     */
    public static String getUnzipPath(Context c, DynamicPkgInfo pkg) {
        if (pkg == null) {
            return "";
        }
        String root = getRootPath(c);
        if (TextUtils.isEmpty(root)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(root);
        sb.append(File.separator).append(DynamicConst.Path.UNZIP).append(pkg.getId());
        return sb.toString();
    }

    /**
     * 获取动态资源管理系统根目录
     *
     * @param c Context
     * @return 路径
     */
    public static String getRootPath(Context c) {
        String path = getInternalAppDataPath(c) + File.separator + DynamicConst.Path.ROOT;
        FileUtil.createOrExistsDir(new File(path));
        return path;
    }

    private static String getInternalAppDataPath(Context c) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return c.getApplicationInfo().dataDir;
        }
        return c.getDataDir().getAbsolutePath();
    }
}
