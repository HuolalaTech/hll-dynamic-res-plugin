package com.lalamove.huolala.dynamiccore.local;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: ILocalProvider
 * @author: huangyuchen
 * @date: 4/17/22
 * @description:
 * @history:
 */
public interface ILocalProvider {
    ILocalRes getLocalRes();

    ILocalState getLocalState();
}
