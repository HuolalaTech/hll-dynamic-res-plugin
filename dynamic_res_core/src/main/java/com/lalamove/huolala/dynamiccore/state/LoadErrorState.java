package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.DynamicResException;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.util.ReportUtil;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: LoadErrorState
 * @author: huangyuchen
 * @date: 3/5/22
 * @description: 加载失败状态
 * @history:
 */
public class LoadErrorState extends AbsState {

    @Override
    protected boolean needProcessWithException() {
        return false;
    }

    @Override
    protected void processInner(IStateMachine machine) {
        //发生错误时，我们上报错误信息
        reportError(machine, machine.getResContext().getmException());
        machine.getDispatch().dispatchError(machine.getResContext().getmException());
    }

    private void reportError(IStateMachine machine, DynamicResException e) {
        getReportParam(machine).error(e);
        ReportUtil.monitorSummaryRes(machine.getConfig().getMonitor(), getReportParam(machine));
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.NONE;
    }

    @Override
    protected State getInitState() {
        return State.ERROR;
    }
}
