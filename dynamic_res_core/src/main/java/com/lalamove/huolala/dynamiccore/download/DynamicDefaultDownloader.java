package com.lalamove.huolala.dynamiccore.download;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicDefaultDownloader
 * @author: huangyuchen
 * @date: 3/2/22
 * @description: 下载接口实现类，使用huolala下载器sdk
 * @history:
 */
public class DynamicDefaultDownloader implements IDownloader {

    public interface OnHttpGetFileListener {
        void onDownloaded(File file);

        void onDownloading(int total, int percentage);
    }

    private Context mContext;
    private Executor mExecutor;

    public DynamicDefaultDownloader(Context c, Executor exec) {
        this.mContext = c.getApplicationContext();
        this.mExecutor = exec;
    }

    @Override
    public void download(@NonNull String url, @NonNull String fileName, IDownloadListener listener) {
        mExecutor.execute(new DynamicDownLoadTask(url, mContext.getFilesDir().getAbsolutePath(), fileName, new OnHttpGetFileListener() {
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
