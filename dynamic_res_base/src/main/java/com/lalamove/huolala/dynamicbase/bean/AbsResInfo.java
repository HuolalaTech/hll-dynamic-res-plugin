package com.lalamove.huolala.dynamicbase.bean;

import com.lalamove.huolala.dynamicbase.DynamicConst;

import java.util.HashMap;
import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: AbsResInfo
 * @author: huangyuchen
 * @date: 3/12/22
 * @description: 资源抽象实体类
 * @history:
 */
public abstract class AbsResInfo {
    /**
     * 资源名称
     */
    protected String mName;
    /**
     * 资源md5
     */
    protected String mMd5;
    /**
     * 资源长度
     */
    protected long mLength;
    /**
     * 该资源包含的子文件或文件夹
     */
    protected Map<String, AbsResInfo> mMap;

    protected AbsResInfo(String name, String md5, long length, AbsResInfo... infos) {
        this.mName = name;
        this.mMd5 = md5;
        this.mLength = length;
        //该资源不为单个文件类型，则加入子文件
        if (getVerifyType() != DynamicConst.VerifyType.FILE) {
            mMap = new HashMap<>();
            if (infos != null) {
                for (AbsResInfo info : infos) {
                    mMap.put(info.getName(), info);
                }
            }
        }
    }

    /**
     * 获取校验类型
     *
     * @return 校验类型
     */
    public abstract @DynamicConst.VerifyType
    int getVerifyType();

    public String getName() {
        return mName;
    }

    public String getMd5() {
        return mMd5;
    }

    public long getLength() {
        return mLength;
    }

    public Map<String, AbsResInfo> getMap() {
        return mMap;
    }

    public void putSubResInfo(AbsResInfo... infoArray) {
        if (getVerifyType() != DynamicConst.VerifyType.FILE) {
            if (mMap == null) {
                mMap = new HashMap<>();
            }
            if (infoArray != null) {
                for (AbsResInfo info : infoArray) {
                    mMap.put(info.getName(), info);
                }
            }
        }
    }

}
