package com.lalamove.huolala.dynamiccore.util;

import android.util.ArrayMap;

import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.report.DynamicReportParam;
import com.lalamove.huolala.dynamiccore.report.IMonitor;
import com.lalamove.huolala.dynamiccore.DynamicResException;

import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ReportUtil
 * @author: huangyuchen
 * @date: 3/22/22
 * @description:
 * @history:
 */
public class ReportUtil {

    private ReportUtil() {
    }

    /**
     * 资源包加载过程上报event
     */
    private static final String DYNAMIC_RES_EVENT = "dynamic_res";
    /**
     * 资源包应用过程上报
     */
    private static final String DYNAMIC_RES_APPLY_EVENT = "dynamic_res_apply";
    /**
     * 动态so资源应用过程上报
     */
    private static final String DYNAMIC_SO_EVENT = "dynamic_so";


    public static void monitorSummaryRes(IMonitor monitor, DynamicReportParam param) {
        if (monitor == null || param == null) {
            return;
        }

        Map<String, String> map = new ArrayMap<>(16);
        map.put("pkgId", param.getPkgId());
        map.put("downloadTime", String.valueOf(param.getDownloadTime()));
        map.put("unzipTime", String.valueOf(param.getUnzipTime()));
        map.put("allTime", String.valueOf(param.getAllTime()));
        map.put("succeed", String.valueOf(param.isSucceed()));
        map.put("errorCode", String.valueOf(param.getErrorCode()));
        map.put("errorMsg", param.getErrorMsg());
        map.put("resumeState", String.valueOf(param.getResumeState()));
        map.put("downloadResume", String.valueOf(param.isDownloadResume()));
        map.put("stateList", String.valueOf(param.getStates()));
        int result = param.isSucceed() ? (1) : (0);
        String event = DYNAMIC_RES_EVENT;
        monitor.monitorSummary(event, result, map, DYNAMIC_RES_EVENT);
        DebugLogUtil.d("monitorSummaryRes：" + " event= " + event + " ,params = " + map);
    }

    public static void monitorSummaryApplySucceed(IMonitor monitor, DynamicPkgInfo pkg) {
        monitorSummarySucceed(monitor, DYNAMIC_RES_APPLY_EVENT, pkg);
    }

    public static void monitorSummaryApplyFail(IMonitor monitor, DynamicPkgInfo pkg, DynamicResException ex) {
        monitorSummaryFail(monitor, DYNAMIC_RES_APPLY_EVENT, pkg, ex);
    }

    public static void monitorSummarySoSucceed(IMonitor monitor, DynamicPkgInfo pkg) {
        monitorSummarySucceed(monitor, DYNAMIC_SO_EVENT, pkg);
    }

    public static void monitorSummarySoFail(IMonitor monitor, DynamicPkgInfo pkg, DynamicResException ex) {
        monitorSummaryFail(monitor, DYNAMIC_SO_EVENT, pkg, ex);
    }


    private static void monitorSummarySucceed(IMonitor monitor, String event, DynamicPkgInfo pkg) {
        if (monitor == null || pkg == null) {
            return;
        }
        Map<String, String> map = new ArrayMap<>(4);
        map.put("pkgId", pkg.getId());
        map.put("type", String.valueOf(pkg.getType()));
        map.put("succeed", String.valueOf(true));
        monitor.monitorSummary(event, 1, map, event);
        DebugLogUtil.d("monitorSummarySucceed：" + " event= " + event + " ,params = " + map);
    }

    private static void monitorSummaryFail(IMonitor monitor, String event, DynamicPkgInfo pkg, DynamicResException ex) {
        if (monitor == null || pkg == null || ex == null) {
            return;
        }
        Map<String, String> map = new ArrayMap<>(6);
        map.put("pkgId", pkg.getId());
        map.put("type", String.valueOf(pkg.getType()));
        map.put("succeed", String.valueOf(false));
        map.put("errorCode", String.valueOf(ex.errorCode));
        map.put("errorMsg", ex.getMessage());
        monitor.monitorSummary(event, 0, map, event);
        DebugLogUtil.d("monitorSummaryFail：" + " event= " + event + " ,params = " + map);
    }
}
