package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.IState;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.bean.LocalResStateInfo;
import com.lalamove.huolala.dynamiccore.local.ILocalState;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: InitState
 * @author: huangyuchen
 * @date: 3/9/22
 * @description: 初始状态
 * @history:
 */
public class InitState extends AbsState {
    @Override
    protected State getInitState() {
        return State.INIT;
    }

    @Override
    protected void processInner(IStateMachine machine) throws Exception {
        getReportParam(machine).start();
        ILocalState localState = machine.getConfig().getLocalState();
        DynamicPkgInfo pkg = machine.getResContext().getmPkg();
        //在本次查找是否有要恢复的信息
        LocalResStateInfo stateInfo = localState.getState(pkg.getId());
        //根据该信息，计算需要恢复的状态
        IState state = getStateFromLocal(machine, stateInfo);
        //如果没有要恢复的状态，那么我们直接进入查找版本号状态
        if (state == null) {
            state = new CheckVersionState();
        } else {
            //否则，保存恢复状态到上报数据结构中
            getReportParam(machine).resume(state.getState());
        }
        machine.setCurrentState(state);
        machine.process();
    }

    /**
     * 根据本地待恢复信息，选择合适的状态
     */
    private IState getStateFromLocal(IStateMachine machine, LocalResStateInfo info) {
        if (info == null) {
            return null;
        }
        int state = info.getState();
        String id = machine.getResContext().getmPkg().getId();
        //如果保存的状态为初始化或成功，失败，则状态有错误，删除记录
        if (state == State.INIT.id || state == State.ERROR.id
                || state == State.SUCCEED.id) {
            machine.getConfig().getLocalState().deleteState(id);
            return null;
        }

        IState findState = null;
        //根据本地保存状态，创建待恢复的状态
        if (state == State.CHECK_VERSION.id) {
            findState = new CheckVersionState();
        } else if (state == State.START_DOWNLOAD.id
                || state == State.DOWNLOADING.id) {
            findState = new DownloadState();
        } else if (state == State.VERIFY_RES.id) {
            findState = new VerifyResState();
        } else if (state == State.UNZIP.id) {
            findState = new UnzipState();
        } else if (state == State.VERIFY_ZIP.id) {
            findState = new VerifyZipState();
        }
        //如果找到了待恢复状态，则我们设置StatePath
        if (findState != null) {
            machine.getResContext().setmStatePath(info.getStatePath());
        }

        return findState;
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.INIT;
    }
}
