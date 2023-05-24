package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.ResCtx;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.download.IDownloadListener;
import com.lalamove.huolala.dynamiccore.listener.LoadResDispatch;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DownloadState
 * @author: huangyuchen
 * @date: 3/3/22
 * @description: 下载资源状态
 * @history:
 */
public class DownloadState extends AbsState {

    @Override
    protected void processInner(IStateMachine machine) {
        ResCtx rexCtx = machine.getResContext();
        DynamicPkgInfo pkg = rexCtx.getmPkg();

        LoadResDispatch dispatch = machine.getDispatch();
        getReportParam(machine).startDownload();
        //调用下载接口，开启下载
        machine.getConfig().getDownloader().download(pkg.getUrl(), pkg.getName(), new IDownloadListener() {
            @Override
            public void onSucceed(String path) {
                getReportParam(machine).endDownload();
                //下载成功，设置路径
                machine.getResContext().setmStatePath(path);
                //设置当前状态为校验资源
                machine.setCurrentState(new VerifyResState());
                //由于下载器的执行线程不确定，我们强制后续状态机操作执行在我们自己的线程池中
                //防止占用下载器线程
                machine.processInExec();
            }

            @Override
            public void onFail(Exception e) {
                //下载失败，跳转到失败状态
                getReportParam(machine).endDownload();
                gotoErrorState(machine, getErrorCode(), e);
            }

            @Override
            public void onProcess(int process) {
                //设置当前状态为下载中
                if (mState != State.DOWNLOADING) {
                    mState = State.DOWNLOADING;
                    dispatch.dispatchStateChange(mState);
                    updateResState(machine, mState.id, "");
                }
                dispatch.dispatchDownloading(process);
            }

            @Override
            public void onBroken(boolean b) {
                if (b) {
                    getReportParam(machine).downloadResume();
                }
            }
        });
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.DOWNLOAD;
    }

    @Override
    public State getInitState() {
        return State.START_DOWNLOAD;
    }

}
