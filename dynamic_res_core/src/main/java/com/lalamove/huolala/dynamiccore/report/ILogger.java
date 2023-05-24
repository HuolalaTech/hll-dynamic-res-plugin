package com.lalamove.huolala.dynamiccore.report;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ILogger
 * @author: huangyuchen
 * @date: 3/22/22
 * @description: 输出log信息接口
 * @history:
 */
public interface ILogger {

    /**
     * 调试日志
     *
     * @param tag     tag
     * @param content 内容
     */
    void d(String tag, String content);

    /**
     * 错误日志
     *
     * @param tag     tag
     * @param content 内容
     */
    void e(String tag, String content);

    /**
     * 错误日志
     *
     * @param tag tag
     * @param t   错误
     */
    void e(String tag, Throwable t);
}
