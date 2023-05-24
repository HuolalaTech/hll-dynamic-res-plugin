package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.ResCtx;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.util.PathUtil;
import com.lalamove.huolala.dynamiccore.util.VerifyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: VerifyResState
 * @author: huangyuchen
 * @date: 3/5/22
 * @description: 校验资源包信息状态
 * @history:
 */
public class VerifyResState extends AbsState {

    @Override
    protected void processInner(IStateMachine machine) throws Exception {
        ResCtx resCtx = machine.getResContext();
        DynamicPkgInfo pkg = resCtx.getmPkg();
        String path = mOrgStatePath;
        //校验资源包信息
        VerifyUtil.verifyRes(path, pkg);
        //如果该资源为压缩包，进入解压缩状态
        if (machine.getConfig().getUnzipStrategy().canHandle(new File(path))) {
            resCtx.setmStatePath(path);
            machine.setCurrentState(new UnzipState());
            machine.process();
        } else {
            //否则，直接成功
            handleVerifySucceed(machine);
        }
    }

    /**
     * 成功时的处理
     */
    private void handleVerifySucceed(IStateMachine machine) {
        ResCtx resCtx = machine.getResContext();
        DynamicPkgInfo pkg = resCtx.getmPkg();
        String path = mOrgStatePath;
        //将文件从下载目录拷贝到我们的目录下
        String newPath = PathUtil.getRootPath(machine.getConfig().getAppContext()) + File.separator + pkg.getName();
        boolean copyRes = FileUtil.copyFile(path, newPath);
        //拷贝成功，删除原文件
        if (copyRes) {
            FileUtil.deleteFileOrDir(path, false);
            path = newPath;
        }
        //保存本地资源信息
        saveResInfo(machine, DynamicConst.VerifyType.FILE, path);
        //调转到成状态
        List<File> files = new ArrayList<>();
        files.add(new File(path));
        gotoSucceedState(machine, path, DynamicConst.VerifyType.FILE, files);
    }

    @Override
    protected void handleException(IStateMachine machine, Exception e) {
        super.handleException(machine, e);
        //校验失败，删除下载文件
        FileUtil.deleteFileOrDir(mOrgStatePath, true);
        //删除本地资源信息
        deleteResInfo(machine, machine.getResContext().getmPkg().getId());
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.VERIFY_RES;
    }

    @Override
    public State getInitState() {
        return State.VERIFY_RES;
    }
}
