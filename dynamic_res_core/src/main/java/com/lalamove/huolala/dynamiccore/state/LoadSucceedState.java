package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.util.ReportUtil;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: LoadSucceedState
 * @author: huangyuchen
 * @date: 3/5/22
 * @description: 加载成功状态
 * @history:
 */
public class LoadSucceedState extends AbsState {

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.NONE;
    }

    @Override
    protected boolean needProcessWithException() {
        return false;
    }

    @Override
    public void processInner(IStateMachine machine) {
        reportSucceed(machine);
        machine.getDispatch().dispatchSucceed(machine.getResContext().getmSucceedInfo());
    }

    private void reportSucceed(IStateMachine machine) {
        getReportParam(machine).succeed();
        ReportUtil.monitorSummaryRes(machine.getConfig().getMonitor(), getReportParam(machine));
    }


    @Override
    protected State getInitState() {
        return State.SUCCEED;
    }
}
