package com.lalamove.huolala.dynamiccore.bean;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ResumeResStateInfo
 * @author: huangyuchen
 * @date: 3/8/22
 * @description: 资源加载状态数据库存储类，用于加载资源异常时的恢复
 * @history:
 */

public class LocalResStateInfo {
    /**
     * 资源id
     */
    private final String key;
    /**
     * 当前资源状态
     */
    private final int state;
    /**
     * 当前状态对应的路径
     */
    private final String statePath;
    /**
     * 额外数据
     */
    private final String extra;

    public LocalResStateInfo(String key, int state, String statePath, String extra) {
        this.key = key;
        this.state = state;
        this.statePath = statePath;
        this.extra = extra;
    }

    public LocalResStateInfo(String key, int state, String statePath) {
        this(key, state, statePath, "");
    }

    public String getKey() {
        return key;
    }

    public int getState() {
        return state;
    }

    public String getStatePath() {
        return statePath;
    }

    public String getExtra() {
        return extra;
    }
}
