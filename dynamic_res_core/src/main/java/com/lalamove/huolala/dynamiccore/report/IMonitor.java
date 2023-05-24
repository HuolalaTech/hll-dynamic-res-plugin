package com.lalamove.huolala.dynamiccore.report;

import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: IMonitor
 * @author: huangyuchen
 * @date: 3/22/22
 * @description: 监控接口
 * @history:
 */
public interface IMonitor {

    /**
     * 埋点
     * @param event 事件名称
     * @param param 参数内容
     */
    void report(String event, Map<String, Object> param);

    /**
     * 监控计数
     *
     * @param name 事件名称
     * @param map 参数内容
     * @param extra 额外信息
     */
    void monitorCounter(String name, Map<String, String> map, String extra);

    /**
     * 监控分析
     *
     * @param name 事件名称
     * @param v 值
     * @param map 参数内容
     * @param extra 额外信息
     */
    void monitorSummary(String name, float v, Map<String, String> map, String extra);

}
