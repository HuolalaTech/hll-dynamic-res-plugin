package com.lalamove.huolala.dynamiccore.bean;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: LoadResInfo
 * @author: huangyuchen
 * @date: 3/11/22
 * @description:
 * @history:
 */
public class LoadResInfo {
    /**
     * 资源最终路径
     */
    public final String path;
    /**
     * 资源类型，文件或者文件夹
     */
    public final int type;
    /**
     * 该资源包中所有有效文件列表
     */
    public final List<File> files;

    public LoadResInfo(String path, int type, List<File> files) {
        this.path = path;
        this.type = type;
        this.files = files;
    }

    @Override
    public String toString() {
        return "LoadResInfo{" +
                "path='" + path + '\'' +
                ", type=" + type +
                ", files=" + files +
                '}';
    }
}
