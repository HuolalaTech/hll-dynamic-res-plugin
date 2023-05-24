package com.lalamove.huolala.dynamicplugin.asm;

import com.lalamove.huolala.dynamicplugin.DynamicParam;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: SystemLoadClassVisitor
 * @author: huangyuchen
 * @date: 4/7/22
 * @description: 可能System.loadLibrary方法的类访问器
 * @history:
 */
public class SystemLoadClassVisitor extends ClassVisitor {

    private String mClassName;
    private final DynamicParam mParam;

    public SystemLoadClassVisitor(ClassVisitor classVisitor, DynamicParam param) {
        super(Opcodes.ASM7, classVisitor);
        mParam = param;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        mClassName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new SystemLoadMethodVisitor(mv, mClassName, mParam);

    }
}
