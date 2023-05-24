package com.lalamove.huolala.dynamiccore.state.base;

import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.report.DynamicReportParam;
import com.lalamove.huolala.dynamiccore.state.LoadErrorState;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.bean.LoadResInfo;
import com.lalamove.huolala.dynamiccore.bean.LocalResInfo;
import com.lalamove.huolala.dynamiccore.bean.LocalResStateInfo;
import com.lalamove.huolala.dynamiccore.state.LoadSucceedState;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: AbsState
 * @author: huangyuchen
 * @date: 3/5/22
 * @description:
 * @history:
 */
public abstract class AbsState implements IState {
    /**
     * 当前状态
     */
    protected volatile State mState;
    /**
     * 进入该状态时的statePath,表示该状态下，需要处理的目录。为空表示无需处理
     */
    protected String mOrgStatePath;

    protected AbsState() {
        mState = getInitState();
    }

    @Override
    public final void process(IStateMachine machine) {
        //分发状态改变事件
        machine.getDispatch().dispatchStateChange(mState);
        //将当前执行状态，更新到上报结构中
        getReportParam(machine).addState(mState);
        //获取StatePath并保存下来
        mOrgStatePath = machine.getResContext().getmStatePath();
        //更新当前状态到数据库，便于恢复
        updateResState(machine, mState.id, mOrgStatePath);
        try {
            processInner(machine);
        } catch (Exception e) {
            handleException(machine, e);
            //如果需要，则跳转到异常状态
            if (needProcessWithException()) {
                gotoErrorState(machine, getErrorCode(), e);
            }
        }
    }

    @Override
    public final State getState() {
        return mState;
    }

    protected abstract State getInitState();

    protected abstract void processInner(IStateMachine machine) throws Exception;

    protected abstract int getErrorCode();

    protected boolean needProcessWithException() {
        return true;
    }

    protected void handleException(IStateMachine machine, Exception e) {
    }

    /**
     * 跳转到错误状态
     *
     * @param machine   状态机
     * @param errorCode 错误码
     * @param msg       错误信息
     */
    protected void gotoErrorState(IStateMachine machine, int errorCode, String msg) {
        DynamicResException exception = new DynamicResException(errorCode, msg);
        gotoErrorState(machine, exception);
    }

    /**
     * 跳转到错误状态
     *
     * @param machine   状态机
     * @param errorCode 错误码
     * @param e         错误
     */
    protected void gotoErrorState(IStateMachine machine, int errorCode, Exception e) {
        DynamicResException exception;
        if (e instanceof DynamicResException) {
            exception = (DynamicResException) e;
        } else {
            exception = new DynamicResException(errorCode, e);
        }
        gotoErrorState(machine, exception);
    }

    /**
     * 跳转到错误处理状态
     *
     * @param machine   状态机
     * @param exception 异常
     */
    protected void gotoErrorState(IStateMachine machine, DynamicResException exception) {
        //将异常设置到全局RexCtx环境中
        machine.getResContext().setmException(exception);
        //设置当前状态为错误
        machine.setCurrentState(new LoadErrorState());
        //继续执行该状态
        machine.process();
    }

    /**
     * 跳转到成功状态
     *
     * @param machine 状态机
     * @param path    文件路径
     * @param type
     * @param files
     */
    protected void gotoSucceedState(IStateMachine machine, String path, int type, List<File> files) {
        if (TextUtils.isEmpty(path) || machine.getResContext() == null) {
            return;
        }
        //将加载完成信息设置到全局环境ResCtx
        machine.getResContext().setmSucceedInfo(new LoadResInfo(path, type, files));
        //设置当前状态为加载成功
        machine.setCurrentState(new LoadSucceedState());
        machine.process();
    }

    /**
     * 保存本地资源信息到数据库
     *
     * @param machine    状态机
     * @param verifyType 文件类型
     * @param path       文件路径
     */
    protected void saveResInfo(IStateMachine machine, int verifyType, String path) {
        DynamicPkgInfo pkg = machine.getResContext().getmPkg();
        LocalResInfo info = new LocalResInfo(pkg.getId(), pkg.getVersion(), path, verifyType);
        machine.getConfig().getLocalRes().setInfo(pkg.getId(), info);
    }

    /**
     * 从数据库删除本地资源信息
     *
     * @param machine 状态机
     * @param resId   资源id
     */
    protected void deleteResInfo(IStateMachine machine, String resId) {
        machine.getConfig().getLocalRes().delete(resId);
    }

    /**
     * 更新当前状态到数据库，为了方便异常状态恢复
     *
     * @param machine 状态机
     * @param stateId 状态id
     * @param path    文件路径
     */
    protected void updateResState(IStateMachine machine, int stateId, String path) {
        String id = machine.getResContext().getmPkg().getId();
        //开始状态或者检查版本状态，无需记录，直接返回
        if (stateId == State.INIT.id || stateId == State.CHECK_VERSION.id) {
            return;
        }
        //更新状态为成功或失败，说明我们本次加载已经完成，删除保存的中间状态数据
        if (stateId == State.SUCCEED.id
                || stateId == State.ERROR.id) {
            machine.getConfig().getLocalState().deleteState(id);
            return;
        }
        path = (path == null) ? ("") : (path);
        LocalResStateInfo info = new LocalResStateInfo(id, stateId, path);
        machine.getConfig().getLocalState().setState(id, info);
    }

    protected void logD(IStateMachine machine, String tag, String content) {
        machine.getConfig().getLogger().d(tag, content);
    }

    protected void logE(IStateMachine machine, String tag, String content) {
        machine.getConfig().getLogger().e(tag, content);
    }

    protected void logE(IStateMachine machine, String tag, Throwable t) {
        if (t instanceof DynamicResException) {
            DynamicResException dex = (DynamicResException) t;
            logE(machine, tag, dex.toString());
        } else {
            machine.getConfig().getLogger().e(tag, t);
        }
    }


    protected DynamicReportParam getReportParam(IStateMachine machine) {
        return machine.getResContext().getmReportParam();
    }

}
