package com.lalamove.huolala.lib_dynamic_plugin.download;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lalamove.huolala.dynamiccore.download.IDownloadListener;
import com.lalamove.huolala.dynamiccore.download.IDownloader;
import com.lalamove.huolala.lib_dynamic_plugin.utils.IoThreadPool;

import java.io.File;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DefaultDownloader
 * @author: huangyuchen
 * @date: 3/2/22
 * @description: 下载接口实现类，使用huolala下载器sdk
 * @history:
 */
public class DefaultDownloader implements IDownloader {

    public interface OnHttpGetFileListener {
        void onDownloaded(File file);

        void onDownloading(int total, int percentage);
    }

    private Context context;

    public DefaultDownloader(Context c) {
        this.context = c.getApplicationContext();
    }

    @Override
    public void download(@NonNull String url, @NonNull String fileName, IDownloadListener listener) {
        IoThreadPool.execute(new DownLoadTask(url, context.getFilesDir().getAbsolutePath(), fileName, new OnHttpGetFileListener() {
            @Override
            public void onDownloaded(File file) {
                if (file == null || !file.exists()) {
                    //demo 工程 下载逻辑比较简陋 推荐使用其他成熟的下载组件
                    listener.onFail(new Exception("download error"));
                } else {
                    listener.onSucceed(file.getAbsolutePath());
                }
            }

            @Override
            public void onDownloading(int total, int percentage) {
                listener.onProcess(percentage);
            }
        }));
        //demo 工程 不支持断点续传
        listener.onBroken(false);
    }
}
