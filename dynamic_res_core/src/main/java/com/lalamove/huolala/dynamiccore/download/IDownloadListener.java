package com.lalamove.huolala.dynamiccore.download;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IDownloadListener
 * @author: huangyuchen
 * @date: 3/1/22
 * @description: 下载回调
 * @history:
 */
public interface IDownloadListener {
    /**
     * 下载成功回调
     *
     * @param path 文件路径
     */
    void onSucceed(String path);

    /**
     * 下载失败回调
     *
     * @param e
     */
    void onFail(Exception e);

    /**
     * 下载中回调
     *
     * @param process
     */
    void onProcess(int process);

    /**
     * 本次下载为断点续传回调
     *
     * @param b
     */
    void onBroken(boolean b);
}
