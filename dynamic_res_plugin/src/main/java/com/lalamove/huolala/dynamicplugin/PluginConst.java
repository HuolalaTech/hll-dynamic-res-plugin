package com.lalamove.huolala.dynamicplugin;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: LoadConst
 * @author: huangyuchen
 * @date: 4/11/22
 * @description:
 * @history:
 */
public interface PluginConst {
    String SYSTEM_CLASS = "java/lang/System";
    String LOAD_LIBRARY_METHOD = "loadLibrary";
    String LOAD_METHOD = "load";
    String LOAD_LIBRARY_DESC = "(Ljava/lang/String;)V";

    String PLUGIN_NAME = "DynamicPlugin";

    String DEBUG_ALL_TEST = "debug_all_test";

    String CLASS_SO_LOAD_UTIL= "com/lalamove/huolala/dynamiccore/manager/SoLoadUtil";

    interface Task {
        String DELETE_SO = "deleteAndCopySo";

        String ZIP_RES = "zipRes";

    }

}
