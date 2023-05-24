package com.lalamove.huolala.dynamiccore;

import com.lalamove.huolala.dynamicbase.DynamicConst;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicResException
 * @author: huangyuchen
 * @date: 3/1/22
 * @description:
 * @history:
 */
public class DynamicResException extends Exception {

    public final int errorCode;

    public DynamicResException(@DynamicConst.Error int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public DynamicResException(@DynamicConst.Error int errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return " DynamicResException errorCode " + errorCode + "  msg " + getMessage();
    }
}
