package com.lalamove.huolala.dynamicplugin.task;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamicplugin.DynamicParam;
import com.lalamove.huolala.dynamicplugin.PluginConst;
import com.lalamove.huolala.dynamicplugin.asm.SystemLoadClassVisitor;
import com.lalamove.huolala.dynamicplugin.util.Log;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: TransformTask
 * @author: huangyuchen
 * @date: 4/15/22
 * @description: 执行替换System.loadlibrary、System.load操作
 * @history:
 */
public class TransformTask extends Transform implements ITask {

    private DynamicParam mParams;

    @Override
    public String getName() {
        return PluginConst.PLUGIN_NAME;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void process(Project project, DynamicParam param) {
        mParams = param;
        AppExtension app = project.getExtensions().getByType(AppExtension.class);
        app.registerTransform(this);
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Log.debug(mParams, " TransformTask start ");
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider output = transformInvocation.getOutputProvider();
        if (output != null) {
            output.deleteAll();
        }
        for (TransformInput input : inputs) {
            Collection<DirectoryInput> dirs = input.getDirectoryInputs();
            for (DirectoryInput dirInput : dirs) {
                handleDirInput(dirInput, output);
            }
            Collection<JarInput> jars = input.getJarInputs();
            for (JarInput jarInput : jars) {
                handleJarInput(jarInput, output);
            }
        }
    }

    private void handleDirInput(DirectoryInput dirInput, TransformOutputProvider provider) {
        if (!dirInput.getFile().isDirectory()) {
            return;
        }
        FileUtils.getAllFiles(dirInput.getFile()).forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                FileOutputStream fos = null;
                try {

                    if (checkSystemLoadClass(file.getAbsolutePath())) {
                        ClassReader classReader = new ClassReader(IOUtils.toByteArray(new FileInputStream(file)));
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                        ClassVisitor cv = new SystemLoadClassVisitor(classWriter, mParams);
                        classReader.accept(cv, ClassReader.EXPAND_FRAMES);
                        byte[] code = classWriter.toByteArray();
                        fos = new FileOutputStream(
                                file.getParentFile().getAbsolutePath() + File.separator + file.getName());
                        fos.write(code);
                        fos.close();
                    }
                } catch (Exception e) {
                    IOUtils.closeQuietly(fos);
                }
            }
        });


        try {
            // 固定写法 把输出给下一个任务
            File dest = provider.getContentLocation(dirInput.getName(),
                    dirInput.getContentTypes(), dirInput.getScopes(),
                    Format.DIRECTORY);
            FileUtils.copyDirectory(dirInput.getFile(), dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleJarInput(JarInput jarInput, TransformOutputProvider provider) {
        if (!jarInput.getFile().getAbsolutePath().endsWith(".jar")) {
            return;
        }

        String jarName = jarInput.getName();
        String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4);
        }
        File tmpFile = new File(jarInput.getFile().getParent() + File.separator + "classes_temp.jar");
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        JarOutputStream jarOutputStream = null;
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarInput.getFile());
            Enumeration<JarEntry> enumeration = jarFile.entries();
            jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile));
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                jarOutputStream.putNextEntry(zipEntry);
                if (checkSystemLoadClass(entryName)) {
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream));
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                    ClassVisitor cv = new SystemLoadClassVisitor(classWriter, mParams);
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES);
                    byte[] code = classWriter.toByteArray();
                    jarOutputStream.write(code);
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(jarOutputStream);
            IOUtils.closeQuietly(jarFile);
        }

        try {
            File dest = provider.getContentLocation(jarName + md5Name,
                    jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
            FileUtils.copyFile(tmpFile, dest);
            tmpFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkSystemLoadClass(String name) {
        if (TextUtil.isEmpty(name)) {
            return false;
        }
        String rawName = name;
        int lastIndex = name.lastIndexOf('/');
        if (lastIndex != -1) {
            name = name.substring(lastIndex + 1, name.length());
        }
        boolean res = (name.endsWith(".class") &&
                !name.startsWith("R$") &&
                !name.startsWith("BR$") &&
                !TextUtil.equals(name, "BB.class") &&
                !TextUtil.equals(name, "BuildConfig.class"));
        if (!res) {
            return false;
        }

        name = rawName;
        name = TextUtil.replaceClassName(name);
        if (mParams.getScanLoadLibraryPkgs().contains(PluginConst.DEBUG_ALL_TEST)) {
            return true;
        }
        //遍历待替换列表（为包名或类名）
        for (String clsPkg : mParams.getScanLoadLibraryPkgs()) {
            //如果该class名称需要替换
            if (name.contains(clsPkg)) {
                boolean isInIgnore = false;
                //遍历忽略替换列表(如待替换包名为com.lalamove.huolala，则忽略则可设置为com.lalamove.huolala.demo)
                for (String ignoreCls : mParams.getIgnoreLoadLibraryPkgs()) {
                    //该class名称被忽略，直接跳出循环
                    if (name.contains(ignoreCls)) {
                        isInIgnore = true;
                        break;
                    }
                }
                //如果class名称需要替换，且没有被忽略，则返回true
                if (!isInIgnore) {
                    return true;
                }
            }
        }
        return false;
    }
}
