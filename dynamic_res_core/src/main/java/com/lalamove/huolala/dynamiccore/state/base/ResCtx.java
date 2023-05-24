package com.lalamove.huolala.dynamiccore.state.base;

import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.report.DynamicReportParam;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ResContext
 * @author: huangyuchen
 * @date: 3/2/22
 * @description: 状态机全局Context对象
 * @history:
 */
public class ResCtx {
    //要加载的资源信息
    private DynamicPkgInfo mPkg;
    //当前状态下，我们进行处理的目录。通常由前一个状态设置
    private String mStatePath;
    //最终加载资源
    private LoadResInfo mSucceedInfo;
    //存储发生的异常
    private DynamicResException mException;
    //上报信息参数
    private DynamicReportParam mReportParam;

    public ResCtx(DynamicPkgInfo pkg) {
        this.mPkg = pkg;
        mReportParam = new DynamicReportParam(pkg.getId());
    }

    public DynamicPkgInfo getmPkg() {
        return mPkg;
    }

    /**
     * 清除所有状态
     */
    public void clear() {
        this.mStatePath = null;
        this.mSucceedInfo = null;
        this.mException = null;
    }

    public String getmStatePath() {
        return mStatePath;
    }

    public void setmStatePath(String mStatePath) {
        this.mStatePath = mStatePath;
    }

    public DynamicResException getmException() {
        return mException;
    }

    public void setmException(DynamicResException mException) {
        this.mException = mException;
    }

    public LoadResInfo getmSucceedInfo() {
        return mSucceedInfo;
    }

    public void setmSucceedInfo(LoadResInfo mSucceedInfo) {
        this.mSucceedInfo = mSucceedInfo;
    }

    public DynamicReportParam getmReportParam() {
        return mReportParam;
    }
}
