package com.lalamove.huolala.dynamiccore.report;

import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.state.base.State;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicReportParam
 * @author: huangyuchen
 * @date: 3/22/22
 * @description:
 * @history:
 */
public class DynamicReportParam {

    /**
     * 资源包id
     */
    private final String mPkgId;
    /**
     * 下载时间
     */
    private long mDownloadTime;
    /**
     * 解压时间
     */
    private long mUnzipTime;
    /**
     * 总耗时
     */
    private long mAllTime;
    /**
     * 是否成功
     */
    private boolean mSucceed;
    /**
     * 失败code
     */
    private int mErrorCode;
    /**
     * 失败信息,初始值为空，防止上报系统不接收空值
     */
    private String mErrorMsg = "";
    /**
     * 有状态数值，代表之前发生异常，本次从该状态恢复。否则为-1
     */
    private int mResumeState = -1;
    /**
     * 执行过的指令序列
     */
    private final List<Integer> mStates;
    /**
     * 本地下载资源，是断点续传的
     */
    private boolean mDownloadResume;

    private long mStartTime;
    private long mDownloadStartTime;
    private long mUnzipStartTime;

    public DynamicReportParam(String pkgId) {
        this.mPkgId = pkgId;
        mStates = new ArrayList<>();
    }

    public String getPkgId() {
        return mPkgId;
    }

    public long getDownloadTime() {
        return mDownloadTime;
    }

    public long getUnzipTime() {
        return mUnzipTime;
    }

    public long getAllTime() {
        return mAllTime;
    }

    public boolean isSucceed() {
        return mSucceed;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public int getResumeState() {
        return mResumeState;
    }

    public List<Integer> getStates() {
        return mStates;
    }


    public boolean isDownloadResume() {
        return mDownloadResume;
    }

    public void start() {
        mStartTime = getCurrentTime();
    }

    public void startDownload() {
        mDownloadStartTime = getCurrentTime();
    }

    public void endDownload() {
        mDownloadTime = getCurrentTime() - mDownloadStartTime;
    }

    public void startUnzip() {
        mUnzipStartTime = getCurrentTime();
    }

    public void endUnzip() {
        mUnzipTime = getCurrentTime() - mUnzipStartTime;
    }

    public void resume(State state) {
        mResumeState = state.id;
    }

    public void addState(State state) {
        mStates.add(state.id);
    }

    public void succeed() {
        mSucceed = true;
        mAllTime = getCurrentTime() - mStartTime;
    }

    public void downloadResume() {
        mDownloadResume = true;
    }

    public void error(DynamicResException e) {
        mSucceed = false;
        mErrorCode = e.errorCode;
        mErrorMsg = e.toString();
        mAllTime = getCurrentTime() - mStartTime;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
