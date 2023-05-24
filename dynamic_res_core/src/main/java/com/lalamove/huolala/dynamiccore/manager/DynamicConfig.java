package com.lalamove.huolala.dynamiccore.manager;

import android.content.Context;

import com.lalamove.huolala.dynamicbase.so.ILoadSoManager;
import com.lalamove.huolala.dynamiccore.download.DynamicDefaultDownloader;
import com.lalamove.huolala.dynamiccore.download.IDownLoaderProvider;
import com.lalamove.huolala.dynamiccore.download.IDownloader;
import com.lalamove.huolala.dynamiccore.local.DefaultLocalProvider;
import com.lalamove.huolala.dynamiccore.local.ILocalProvider;
import com.lalamove.huolala.dynamiccore.local.ILocalRes;
import com.lalamove.huolala.dynamiccore.local.ILocalState;
import com.lalamove.huolala.dynamiccore.manager.soload.AbstractSoLoader;
import com.lalamove.huolala.dynamiccore.report.ILogger;
import com.lalamove.huolala.dynamiccore.report.IMonitor;
import com.lalamove.huolala.dynamiccore.unzip.DefaultUnzipStrategy;
import com.lalamove.huolala.dynamiccore.unzip.IUnzipStrategy;

import java.util.concurrent.Executor;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicConfig
 * @author: huangyuchen
 * @date: 3/5/22
 * @description: 资源加载配置类
 * @history:
 */
public class DynamicConfig {
    /**
     * 本地资源信息存储接口
     */
    private ILocalRes mLocalRes;
    /**
     * 本地资源状态存储接口
     */
    private ILocalState mLocalState;
    /**
     * 本地资源存储提供者
     */
    private ILocalProvider mLocalProvider;
    /**
     * 使用线程池
     */
    private Executor mExec;
    /**
     * 下载器接口
     */
    private IDownLoaderProvider mDownloaderProvider;
    private IDownloader mDownloader;
    private ILogger mLogger;
    private IMonitor mMonitor;
    /**
     * 解压策略接口
     */
    private IUnzipStrategy mUnzipStrategy;
    /**
     * so文件加载器
     */
    private ILoadSoManager mLoadSoManager;
    /**
     * so 加载器
     */
    private AbstractSoLoader mSoLoader;
    /**
     * Context对象
     */
    private Context mAppContext;
    /**
     * 打印本地调试日志
     */
    private boolean mDebugLog;

    private DynamicConfig() {

    }

    public synchronized ILocalRes getLocalRes() {
        if (mLocalRes == null) {
            mLocalRes = mLocalProvider.getLocalRes();
        }
        return mLocalRes;
    }

    public synchronized ILocalState getLocalState() {
        if (mLocalState == null) {
            mLocalState = mLocalProvider.getLocalState();
        }
        return mLocalState;
    }

    public Executor getExec() {
        return mExec;
    }

    public synchronized IDownloader getDownloader() {
        if (mDownloader == null) {
            mDownloader = mDownloaderProvider.getDownloader();
        }
        //接入方没有配置自己的下载器，默认给一个简单版本的
        if (mDownloader == null) {
            mDownloader = new DynamicDefaultDownloader(mAppContext, getExec());
        }
        return mDownloader;
    }

    public ILogger getLogger() {
        return mLogger;
    }

    public IMonitor getMonitor() {
        return mMonitor;
    }

    public IUnzipStrategy getUnzipStrategy() {
        return mUnzipStrategy;
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public ILoadSoManager getLoadSoManager() {
        return mLoadSoManager;
    }

    public boolean isDebugLog() {
        return mDebugLog;
    }

    public AbstractSoLoader getSoLoader(){
        return mSoLoader;
    }

    /**
     * 构建器类
     */
    public static class Builder {

        private Executor mExec;
        private IDownLoaderProvider mDownloadProvider;
        private IMonitor mMonitor;
        private ILogger mLogger;
        private IUnzipStrategy mUnzipStrategy;
        private final Context mAppContext;
        private ILocalProvider mLocalProvider;
        private ILoadSoManager mLoadSoManager;
        private AbstractSoLoader mSoLoader;
        private boolean mDebugLog;

        public static Builder with(Context c) {
            return new Builder(c);
        }

        private Builder(Context c) {
            mAppContext = c.getApplicationContext();
        }

        public Builder localProvider(ILocalProvider provider) {
            this.mLocalProvider = provider;
            return this;
        }

        public Builder executor(Executor exec) {
            this.mExec = exec;
            return this;
        }

        public Builder downlader(IDownLoaderProvider loader) {
            this.mDownloadProvider = loader;
            return this;
        }

        public Builder loggger(ILogger logger) {
            this.mLogger = logger;
            return this;
        }

        public Builder debugLog(boolean debugLog) {
            this.mDebugLog = debugLog;
            return this;
        }

        public Builder monitor(IMonitor monitor) {
            this.mMonitor = monitor;
            return this;
        }

        public Builder unzipStrategy(IUnzipStrategy unzip) {
            this.mUnzipStrategy = unzip;
            return this;
        }

        public Builder loadSoManager(ILoadSoManager manager) {
            this.mLoadSoManager = manager;
            return this;
        }

        public Builder soLoader(AbstractSoLoader soLoader) {
            this.mSoLoader = soLoader;
            return this;
        }

        public DynamicConfig build() {
            if (mExec == null) {
                throw new IllegalArgumentException("  mExec null ");
            }
            if (mDownloadProvider == null) {
                throw new IllegalArgumentException(" mDownloader null ");
            }
            if (mLogger == null) {
                throw new IllegalArgumentException(" mLogger null ");
            }
            if (mMonitor == null) {
                throw new IllegalArgumentException(" mMonitor null ");
            }
            if (mLoadSoManager == null) {
                throw new IllegalArgumentException(" mLoadSoManager null ");
            }
            buildNullConfig();
            return createConfig();
        }

        private DynamicConfig createConfig() {
            DynamicConfig config = new DynamicConfig();
            config.mExec = mExec;
            config.mDownloaderProvider = mDownloadProvider;
            config.mLocalProvider = mLocalProvider;
            config.mUnzipStrategy = mUnzipStrategy;
            config.mLogger = mLogger;
            config.mMonitor = mMonitor;
            config.mAppContext = mAppContext;
            config.mLoadSoManager = mLoadSoManager;
            config.mDebugLog = mDebugLog;
            config.mSoLoader = mSoLoader;
            return config;
        }

        /**
         * 创建默认的构建对象
         */
        private void buildNullConfig() {
            if (mLocalProvider == null) {
                mLocalProvider = new DefaultLocalProvider(mAppContext);
            }
            if (mUnzipStrategy == null) {
                mUnzipStrategy = new DefaultUnzipStrategy();
            }
        }
    }

}
