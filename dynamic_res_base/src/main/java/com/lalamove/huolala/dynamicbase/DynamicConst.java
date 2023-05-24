package com.lalamove.huolala.dynamicbase;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicConst
 * @author: huangyuchen
 * @date: 3/2/22
 * @description:
 * @history:
 */
public interface DynamicConst {


    /**
     * 文件路径或前缀，后缀名
     */
    interface Path {
        String ROOT = "dynamic_res";
        String UNZIP = "unzip_";
    }

    /**
     * 校验类型
     */
    @IntDef({VerifyType.FILE, VerifyType.FOLDER, VerifyType.RES_PKG})
    @Retention(RetentionPolicy.SOURCE)
    @interface VerifyType {
        /**
         * 文件类型
         */
        int FILE = 1;
        /**
         * 文件夹类型
         */
        int FOLDER = 2;
        /**
         * 资源包类型
         */
        int RES_PKG = 3;
    }

    /**
     * 错误类型
     */
    @IntDef({Error.PARAM_CHECK, Error.NONE, Error.INIT, Error.CHECK_VERSION, Error.DOWNLOAD, Error.VERIFY_RES, Error.UNZIP, Error.VERIFY_ZIP, Error.APPLY})
    @Retention(RetentionPolicy.SOURCE)
    @interface Error {
        /**
         * 参数检查失败，未进入加载流程
         */
        int PARAM_CHECK = -1;
        int NONE = 0;
        /**
         * 初始化（状态恢复）失败
         */
        int INIT = 1;
        /**
         * 检查版本号失败
         */
        int CHECK_VERSION = 2;
        /**
         * 下载失败
         */
        int DOWNLOAD = 3;
        /**
         * 资源包校验失败
         */
        int VERIFY_RES = 4;
        /**
         * 解压缩失败
         */
        int UNZIP = 5;
        /**
         * 校验压缩包失败
         */
        int VERIFY_ZIP = 6;
        /**
         * 加载完成，但是动态资源应用过程中失败
         */
        int APPLY = 7;
    }
}
