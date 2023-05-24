package com.lalamove.huolala.dynamiccore.download;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IDownLoaderProvider
 * @author: huangyuchen
 * @date: 4/19/22
 * @description:
 * @history:
 */
public interface IDownLoaderProvider {
    /**
     * 获取下载器
     * @return 下载器
     */
    IDownloader getDownloader();
}
