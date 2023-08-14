package com.lalamove.huolala.lib_dynamic_plugin.job;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lalamove.huolala.dynamicbase.so.ILoadSoManager;
import com.lalamove.huolala.dynamiccore.download.IDownLoaderProvider;
import com.lalamove.huolala.dynamiccore.download.IDownloader;
import com.lalamove.huolala.dynamiccore.manager.DynamicConfig;
import com.lalamove.huolala.dynamiccore.manager.DynamicLoadSoManager;
import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;
import com.lalamove.huolala.dynamiccore.report.ILogger;
import com.lalamove.huolala.dynamiccore.report.IMonitor;
import com.lalamove.huolala.lib_dynamic_plugin.BuildConfig;
import com.lalamove.huolala.lib_dynamic_plugin.DemoSoLoader;
import com.lalamove.huolala.lib_dynamic_plugin.download.DefaultDownloader;
import com.lalamove.huolala.lib_dynamic_plugin.DynamicResConst;
import com.lalamove.huolala.lib_dynamic_plugin.utils.CpuThreadPool;
import com.lalamove.huolala.lib_dynamic_plugin.utils.LogUtils;

import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicJob
 * @author: huangyuchen
 * @date: 3/17/22
 * @description: 动态资源初始化任务
 * @history:
 */
public class DynamicInitJob implements AbsBaseJob {

    private static final String TAG = "DynamicJob";

    @Override
    public void init(Context context) {
        DynamicConfig config = DynamicConfig.Builder.with(context)
                .executor(CpuThreadPool.getInstance().getThreadPoolExecutor())
                .loadSoManager(createLoadSoManager())
                .downloader(new IDownLoaderProvider() {
                    @Override
                    public IDownloader getDownloader() {
                        return new DefaultDownloader(context);
                    }
                })
                .soLoader(new DemoSoLoader())
                .debugLog(BuildConfig.DEBUG)
                .loggger(new ILogger() {
                    @Override
                    public void d(String tag, String content) {
                        LogUtils.i(tag, content);
                    }

                    @Override
                    public void e(String tag, String content) {
                        LogUtils.e(tag, content);
                    }

                    @Override
                    public void e(String tag, Throwable t) {
                        LogUtils.e(tag, t.getMessage());
                    }
                })
                .monitor(new IMonitor() {
                    @Override
                    public void report(String event, Map<String, Object> params) {
                        LogUtils.i(TAG, "   === > event = " + event + ",params  = " + params);
                    }

                    @Override
                    public void monitorCounter(String event, Map<String, String> params, String extra) {
                        LogUtils.i(TAG, "monitorCounter：" + " event= " + event + ",params = " + params + ",extra = " + extra);
                    }

                    @Override
                    public void monitorSummary(String event, float v, Map<String, String> params, String extra) {
                        LogUtils.i(TAG, "monitorSummary：" + " event= " + event + ",value = " + v + ",params = " + params + ",extra = " + extra);

                    }
                }).build();
        DynamicResManager.getInstance().init(config);
    }

    @NonNull
    @Override
    public String getJobName() {
        return DynamicInitJob.class.getSimpleName();
    }

    private ILoadSoManager createLoadSoManager() {
        DynamicLoadSoManager manager = new DynamicLoadSoManager();
        manager.addSoLib(DynamicResConst.DEMO_SO, DynamicResConst.So.DEMOSO_SO);
        return manager;
    }

}
