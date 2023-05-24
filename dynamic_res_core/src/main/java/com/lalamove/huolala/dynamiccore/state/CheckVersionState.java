package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamiccore.local.ILocalRes;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.ResCtx;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.bean.LocalResInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: CheckVersionState
 * @author: huangyuchen
 * @date: 3/3/22
 * @description: 检查版本号状态
 * @history:
 */
public class CheckVersionState extends AbsState {

    @Override
    protected void processInner(IStateMachine machine) {
        ResCtx ctx = machine.getResContext();
        DynamicPkgInfo pkg = ctx.getmPkg();
        ILocalRes localRes = machine.getConfig().getLocalRes();
        //获取本地版本信息
        LocalResInfo info = localRes.getInfo(pkg.getId());
        //获取本地版本号
        int localVersion = (info != null) ? (info.getVersion()) : (-1);
        boolean same = pkg.getVersion() == localVersion;
        if (same) {
            //如果本地版本号相同，说明我们已经下载过该资源了，直接校验
            //我们将该路径设置到RexCtx中
            ctx.setmStatePath(info.getPath());
            //如果校验类型为文件，调整到资源包校验。否则跳转到压缩包校验
            if (info.getVerifyType() == DynamicConst.VerifyType.FILE) {
                machine.setCurrentState(new VerifyResState());
            } else {
                machine.setCurrentState(new VerifyZipState());
            }
        } else {
            //如果本地资源版本号不同，那么我们删除本地资源
            if (info != null) {
                FileUtil.deleteFileOrDir(info.getPath(), true);
            }
            //本地版本为空，或者版本不同，我们走下载流程
            machine.setCurrentState(new DownloadState());
        }
        machine.process();
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.CHECK_VERSION;
    }

    @Override
    protected State getInitState() {
        return State.CHECK_VERSION;
    }

}
