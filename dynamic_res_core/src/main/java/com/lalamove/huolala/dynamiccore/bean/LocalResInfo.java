package com.lalamove.huolala.dynamiccore.bean;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicVersionInfo
 * @author: huangyuchen
 * @date: 3/4/22
 * @description: 本地资源包数据库存储类
 * @history:
 */
public class LocalResInfo {
    /**
     * 资源id
     */
    private final String key;
    /**
     * 资源版本号
     */
    private final int version;
    /**
     * 资源路径
     */
    private final String path;
    /**
     * 资源校验类型，文件或文件夹
     */
    private final int verifyType;
    /**
     * 额外数据
     */
    private final String extra;

    public LocalResInfo(String key, int version, String path, int verifyType, String extra) {
        this.key = key;
        this.version = version;
        this.path = path;
        this.verifyType = verifyType;
        this.extra = extra;
    }

    public LocalResInfo(String key, int version, String path, int verifyType) {
        this(key, version, path, verifyType, "");
    }

    public String getKey() {
        return key;
    }

    public int getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public int getVerifyType() {
        return verifyType;
    }

    public String getExtra() {
        return extra;
    }
}
