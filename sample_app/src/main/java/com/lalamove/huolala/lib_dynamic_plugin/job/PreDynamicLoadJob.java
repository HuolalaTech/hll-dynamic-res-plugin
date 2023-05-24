package com.lalamove.huolala.lib_dynamic_plugin.job;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lalamove.huolala.dynamiccore.manager.DynamicResManager;
import com.lalamove.huolala.lib_dynamic_plugin.DynamicResConst;
import com.lalamove.huolala.lib_dynamic_plugin.utils.IoThreadPool;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: PreDynamicLoadJob
 * @author: huangyuchen
 * @date: 2022/5/30
 * @description:预加载动态资源
 * @history:
 */
public class PreDynamicLoadJob implements AbsBaseJob {

    @Override
    public void init(Context context) {
        IoThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
            if (DynamicResManager.getInstance().getLoadSoManager() != null) {
                DynamicResManager.getInstance().getLoadSoManager().loadSo(DynamicResConst.DEMO_SO, null);
            }
        });
    }

    @NonNull
    @Override
    public String getJobName() {
        return PreDynamicLoadJob.class.getSimpleName();
    }
}
