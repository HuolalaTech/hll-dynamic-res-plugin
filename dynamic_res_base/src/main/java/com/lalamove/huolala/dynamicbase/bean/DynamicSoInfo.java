package com.lalamove.huolala.dynamicbase.bean;


import com.lalamove.huolala.dynamicbase.DynamicResType;
import com.lalamove.huolala.dynamicbase.SoType;

import java.util.HashMap;
import java.util.Map;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicSoInfo
 * @author: huangyuchen
 * @date: 4/18/22
 * @description: so动态资源包
 * @history:
 */
public class DynamicSoInfo {

    private Map<String, DynamicPkgInfo> mSoInfoArray;


    public DynamicSoInfo(DynamicAbiInfo... abiInfoArray) {
        mSoInfoArray = new HashMap<>();
        if (abiInfoArray != null) {
            for (DynamicAbiInfo info : abiInfoArray) {
                if (info.mPkgInfo.getType() == DynamicResType.SO) {
                    mSoInfoArray.put(info.mSoType.name, info.mPkgInfo);
                }
            }
        }
    }

    public DynamicPkgInfo getPkgInfo(String[] supportAbis) {
        if (supportAbis == null || supportAbis.length == 0) {
            return null;
        }

        for (String abi : supportAbis) {
            DynamicPkgInfo pkg = mSoInfoArray.get(abi);
            if (pkg != null) {
                return pkg;
            }
        }
        return null;
    }

    public DynamicPkgInfo getFirstPkgInfo() {
        for (DynamicPkgInfo pkg : mSoInfoArray.values()) {
            return pkg;
        }
        return null;
    }


    public static class DynamicAbiInfo {
        public final SoType mSoType;
        public final DynamicPkgInfo mPkgInfo;

        public DynamicAbiInfo(SoType mSoType, DynamicPkgInfo mPkgInfo) {
            this.mSoType = mSoType;
            this.mPkgInfo = mPkgInfo;
        }
    }
}
