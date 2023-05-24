package com.lalamove.huolala.dynamiccore.unzip;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IUnzipStrategy
 * @author: huangyuchen
 * @date: 3/8/22
 * @description: 解压策略接口
 * @history:
 */
public interface IUnzipStrategy {
    /**
     * 是否可以处理该文件
     *
     * @param zipFile
     * @return
     */
    boolean canHandle(File zipFile);

    /**
     * 执行解压操作
     *
     * @param zipPath 压缩文件路径
     * @param dstPath 待解压路径
     * @return
     * @throws IOException
     */
    List<File> unzip(String zipPath, String dstPath) throws IOException;
}
