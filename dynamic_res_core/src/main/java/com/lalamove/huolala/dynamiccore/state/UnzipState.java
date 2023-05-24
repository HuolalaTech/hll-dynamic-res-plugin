package com.lalamove.huolala.dynamiccore.state;

import android.content.Context;
import android.text.TextUtils;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.util.FileUtil;
import com.lalamove.huolala.dynamiccore.state.base.AbsState;
import com.lalamove.huolala.dynamiccore.state.base.IStateMachine;
import com.lalamove.huolala.dynamiccore.state.base.State;
import com.lalamove.huolala.dynamiccore.unzip.IUnzipStrategy;
import com.lalamove.huolala.dynamiccore.util.PathUtil;

import java.io.File;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: UnzipState
 * @author: huangyuchen
 * @date: 3/8/22
 * @description: 解压缩状态
 * @history:
 */
public class UnzipState extends AbsState {

    private File mUnzipFile;

    @Override
    protected void processInner(IStateMachine machine) throws Exception {
        getReportParam(machine).startUnzip();
        String downloadPath = mOrgStatePath;
        Context context = machine.getConfig().getAppContext();
        IUnzipStrategy unzipStrategy = machine.getConfig().getUnzipStrategy();
        DynamicPkgInfo pkg = machine.getResContext().getmPkg();
        //获取解压缩后文件存放目录
        String unzipPath = PathUtil.getUnzipPath(context, pkg);
        //目录获取失败
        if (TextUtils.isEmpty(unzipPath)) {
            deleteAllFile();
            gotoErrorState(machine, DynamicConst.Error.UNZIP, "无法创建解压目录");
            return;
        }
        File fUnzip = new File(unzipPath);
        mUnzipFile = fUnzip;
        //如果解压目录不存在，则创建目录
        if (!fUnzip.exists()) {
            fUnzip.mkdir();
        } else {
            //该目录存在，删除目录下所有文件
            FileUtil.deleteFileOrDir(fUnzip, false);
        }
        //执行解压缩操作
        List<File> list = unzipStrategy.unzip(downloadPath, fUnzip.getAbsolutePath());
        //解压缩后，没有文件，则判断为失败
        if (list == null || list.size() == 0) {
            deleteAllFile();
            gotoErrorState(machine, DynamicConst.Error.UNZIP, "解压缩失败");
            return;
        }
        //解压缩成功，删除原来文件
        FileUtil.deleteFileOrDir(downloadPath, true);
        getReportParam(machine).endUnzip();
        //进入校验压缩包状态
        machine.getResContext().setmStatePath(fUnzip.getAbsolutePath());
        machine.setCurrentState(new VerifyZipState());
        machine.process();
    }

    @Override
    protected void handleException(IStateMachine machine, Exception e) {
        super.handleException(machine, e);
        getReportParam(machine).endUnzip();
        deleteAllFile();
    }

    private void deleteAllFile() {
        FileUtil.deleteFileOrDir(mOrgStatePath, true);
        FileUtil.deleteFileOrDir(mUnzipFile, true);
    }

    @Override
    protected State getInitState() {
        return State.UNZIP;
    }

    @Override
    protected int getErrorCode() {
        return DynamicConst.Error.UNZIP;
    }
}
