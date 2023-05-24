package com.lalamove.huolala.lib_dynamic_plugin;

import android.app.Application;

import com.lalamove.huolala.lib_dynamic_plugin.job.DynamicInitJob;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DemoApplication
 * @author: kezongze
 * @date: 2022/11/28
 * @description: TODO
 * @history:
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new DynamicInitJob().init(this);
    }
}
