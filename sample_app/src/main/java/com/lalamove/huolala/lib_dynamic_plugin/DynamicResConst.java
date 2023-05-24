package com.lalamove.huolala.lib_dynamic_plugin;

import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicSoInfo;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicResConst
 * @author: huangyuchen
 * @date: 3/17/22
 * @description: 动态资源常量类
 * @history:
 */
public class DynamicResConst {

    public static final String DEMO_SO = "demo_so";

    /**
     * 字体动态资源
     */
    public static class TypeFace {

        public static final DynamicPkgInfo JETBRAINSMONO_BOLDITALIC = new DynamicPkgInfo (
                "JetBrainsMono-BoldItalic",
                "JetBrainsMono-BoldItalic.ttf",
                com.lalamove.huolala.dynamicbase.DynamicResType.TYPEFACE,
                -1,
                "http://url",
                -9999,
                280528,
                "e6481b49712e18ccd0e8147ca71f2f19" );

    }

    public static class FrameAnim {
        public static final DynamicPkgInfo ANIM_CAR = new DynamicPkgInfo(
                "anim_car",
                "anim_car.zip",
                com.lalamove.huolala.dynamicbase.DynamicResType.FRAME_ANIM,
                -1,
                "http://url",
                -9999,
                31034,
                "842f3e85b44a92ce9bdacbd4fc9fa7ec",
                new DynamicPkgInfo.FolderInfo("anim_car",
                        new DynamicPkgInfo.FileInfo("client_car6.webp", "de9588538f9d26af6ac25cb1fd878697", 3912),
                        new DynamicPkgInfo.FileInfo("client_car8.webp", "d504502d62c3da3f5dcb4701d489e016", 3856),
                        new DynamicPkgInfo.FileInfo("client_car3.webp", "8a9f00c600ba4b0b29a5bd187cc7b102", 3738),
                        new DynamicPkgInfo.FileInfo("client_car5.webp", "bb7a17300afd4a78c7fee6f887cfd398", 3714),
                        new DynamicPkgInfo.FileInfo("client_car2.webp", "0d5f4c6b87bfa365616cb9f821ac642b", 3680),
                        new DynamicPkgInfo.FileInfo("client_car1.webp", "64d082df5b38b449c4bdb19657228539", 3650),
                        new DynamicPkgInfo.FileInfo("client_car4.webp", "dd80cc41f3a7eae3107149e9c7ea3859", 3644),
                        new DynamicPkgInfo.FileInfo("client_car7.webp", "db30e224fb18fcd97b85f68ee2e1cddc", 3642)));
    }

    public static class So {
        private static final DynamicPkgInfo DEMOSO_ARM64_V8A_SO = new DynamicPkgInfo(
                "demoSo_arm64-v8a_so",
                "demoSo_arm64-v8a_so.zip",
                com.lalamove.huolala.dynamicbase.DynamicResType.SO,
                -1,
                "http://url",
                -9999,
                70478,
                "e1256fac558dd9090eedbe8955a1ba82",
                new DynamicPkgInfo.FolderInfo("demoSo_arm64-v8a_so",
                        new DynamicPkgInfo.FolderInfo("arm64-v8a",
                                new DynamicPkgInfo.FileInfo("libdynamiclib.so", "692eec8b3e015b6da8629afc55f82719", 210936))));

        public static final DynamicSoInfo DEMOSO_SO = new DynamicSoInfo(
                new DynamicSoInfo.DynamicAbiInfo(
                        com.lalamove.huolala.dynamicbase.SoType.ARM64_V8A, DEMOSO_ARM64_V8A_SO));
    }


}
