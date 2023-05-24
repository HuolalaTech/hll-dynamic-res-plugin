package com.lalamove.huolala.dynamicplugin.asm;

import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamicplugin.DynamicParam;
import com.lalamove.huolala.dynamicplugin.PluginConst;
import com.lalamove.huolala.dynamicplugin.util.Log;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: SystemLoadMethodVisitor
 * @author: huangyuchen
 * @date: 4/11/22
 * @description:
 * @history:
 */
public class SystemLoadMethodVisitor extends MethodVisitor {

    private final String mClsName;
    private final DynamicParam mParam;

    public SystemLoadMethodVisitor(MethodVisitor methodVisitor, String clsName, DynamicParam param) {
        super(Opcodes.ASM7, methodVisitor);
        mClsName = clsName;
        mParam = param;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

        if (mParam.isReplaceLoadLibrary() && TextUtil.equals(owner, PluginConst.SYSTEM_CLASS) &&
                TextUtil.equals(name, PluginConst.LOAD_LIBRARY_METHOD) &&
                TextUtil.equals(descriptor, PluginConst.LOAD_LIBRARY_DESC)) {
            Log.debug(mParam, "System.loadLibrary replace " + mClsName);
            owner = PluginConst.CLASS_SO_LOAD_UTIL;
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, descriptor, false);

            return;
        }

        if (mParam.isReplaceLoad() && TextUtil.equals(owner, PluginConst.SYSTEM_CLASS) &&
                TextUtil.equals(name, PluginConst.LOAD_METHOD) &&
                TextUtil.equals(descriptor, PluginConst.LOAD_LIBRARY_DESC)) {
            Log.debug(mParam, "System.load replace " + mClsName);
            owner = PluginConst.CLASS_SO_LOAD_UTIL;
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, descriptor, false);
            return;
        }

        mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
