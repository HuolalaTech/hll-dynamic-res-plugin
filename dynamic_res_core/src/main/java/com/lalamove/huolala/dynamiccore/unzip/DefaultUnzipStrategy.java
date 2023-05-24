package com.lalamove.huolala.dynamiccore.unzip;

import com.lalamove.huolala.dynamicbase.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultZipStrategy
 * @author: huangyuchen
 * @date: 3/8/22
 * @description:
 * @history:
 */
public class DefaultUnzipStrategy implements IUnzipStrategy {
    @Override
    public boolean canHandle(File zipFile) {
        if (zipFile == null || !zipFile.exists() || !zipFile.isFile()) {
            return false;
        }
        return zipFile.getAbsolutePath().endsWith(".zip");
    }

    @Override
    public List<File> unzip(String zipPath, String dstPath) throws IOException {
        return ZipUtil.unzipFile(new File(zipPath), new File(dstPath));
    }
}
