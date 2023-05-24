package com.lalamove.huolala.dynamicbase.bean;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.DynamicResType;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicPackageInfo
 * @author: huangyuchen
 * @date: 3/1/22
 * @description: 动态资源包
 * @history:
 */
public class DynamicPkgInfo extends AbsResInfo {
    /**
     * 唯一标识符号，每个资源必须唯一
     */
    private final String mId;
    /**
     * 网络下载地址
     */
    private final String mUrl;
    /**
     * 版本号
     */
    private final int mVersion;
    /**
     * 资源类型
     */
    private final DynamicResType mType;
    /**
     * 资源子类型，不用时可以设置为-1
     */
    private final int mSubType;

    public DynamicPkgInfo(String id, String name, DynamicResType type, int subType, String url, int version, long length, String md5, AbsResInfo... infos) {
        super(name, md5, length, infos);
        this.mId = id;
        this.mUrl = url;
        this.mVersion = version;
        this.mType = type;
        this.mSubType = subType;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getVersion() {
        return mVersion;
    }

    public String getId() {
        return mId;
    }

    @Override
    public @DynamicConst.VerifyType
    int getVerifyType() {
        return DynamicConst.VerifyType.RES_PKG;
    }

    public DynamicResType getType() {
        return mType;
    }

    public int getSubType() {
        return mSubType;
    }

    /**
     * 单个文件类型
     */
    public static class FileInfo extends AbsResInfo {

        public FileInfo(String name, String md5, long length) {
            super(name, md5, length);
        }

        @Override
        public @DynamicConst.VerifyType
        int getVerifyType() {
            return DynamicConst.VerifyType.FILE;
        }
    }

    /**
     * 文件夹类型
     */
    public static class FolderInfo extends AbsResInfo {

        public FolderInfo(String name, AbsResInfo... infoArray) {
            super(name, "", -1, infoArray);
        }

        @Override
        public @DynamicConst.VerifyType
        int getVerifyType() {
            return DynamicConst.VerifyType.FOLDER;
        }
    }
}
