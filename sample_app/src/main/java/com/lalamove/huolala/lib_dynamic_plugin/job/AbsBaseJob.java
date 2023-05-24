package com.lalamove.huolala.lib_dynamic_plugin.job;

import android.content.Context;

import androidx.annotation.NonNull;

public interface AbsBaseJob {

    /**
     * 初始化
     *
     * @param context
     */
    void init(Context context);

    /**
     * 获取任务名
     *
     * @return
     */
    @NonNull
    String getJobName();
}
