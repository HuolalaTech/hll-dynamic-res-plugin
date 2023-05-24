package com.lalamove.huolala.dynamiccore.download;

import androidx.annotation.NonNull;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IDownload
 * @author: huangyuchen
 * @date: 3/1/22
 * @description:
 * @history:
 */
public interface IDownloader {
    /**
     * 下载器接口
     *
     * @param url      下载的url
     * @param fileName 下载文件名称（如果实际文件不同，强制重命名）
     * @param listener 下载监听器
     */
    void download(@NonNull String url, @NonNull String fileName, IDownloadListener listener);
}
