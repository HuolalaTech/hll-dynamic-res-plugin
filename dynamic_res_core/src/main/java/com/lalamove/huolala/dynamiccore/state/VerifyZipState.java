package com.lalamove.huolala.dynamiccore.state;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.util.VerifyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: VerifyZipState
 * @author: huangyuchen
 * @date: 3/9/22
 * @description: 校验解压文件状态
 * @history:
 */
public class VerifyZipState extends AbsState {

    @Override
    protected void processInner(IStateMachine machine) throws Exception {
        String path = mOrgStatePath;
        DynamicPkgInfo pkg = machine.getResContext().getmPkg();
        List<File> outFiles = new ArrayList<>();
        //校验解压目录
        VerifyUtil.verifyZipFolderRes(path, pkg, outFiles);
        //校验成功则保存资源信息到本地
        saveResInfo(machine, DynamicConst.VerifyType.FOLDER, path);
        //跳转成功状态
        gotoSucceedState(machine, path, DynamicConst.VerifyType.FOLDER, outFiles);
    }

    @Override
    protected void handleException(IStateMachine machine, Exception e) {
        super.handleException(machine, e);
        //校验失败，删除解压的目录
        FileUtil.deleteFileOrDir(mOrgStatePath, true);
        //删除本地资源信息
        deleteResInfo(machine, machine.getResContext().getmPkg().getId());
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.VERIFY_ZIP;
    }

    @Override
    protected State getInitState() {
        return State.VERIFY_ZIP;
    }
}
