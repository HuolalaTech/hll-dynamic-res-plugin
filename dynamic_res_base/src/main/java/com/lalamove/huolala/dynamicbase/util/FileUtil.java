package com.lalamove.huolala.dynamicbase.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: FileUtil
 * @author: huangyuchen
 * @date: 3/8/22
 * @description:
 * @history:
 */
public class FileUtil {

    private FileUtil() {

    }

    /**
     * 拷贝文件
     *
     * @param oldPathFile
     * @param newPathFile
     * @return
     */
    public static boolean copyFile(String oldPathFile, String newPathFile) {
        if (TextUtil.equals(oldPathFile, newPathFile)) {
            return false;
        }
        FileOutputStream fs = null;
        InputStream inStream = null;
        try {
            int byteRead = 0;
            File oldFile = new File(oldPathFile);
            if (oldFile.exists()) {
                inStream = new FileInputStream(oldPathFile);
                File n = new File(newPathFile);
                if (!n.exists()) {
                    n.createNewFile();
                }
                fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                fs.flush();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(fs);
            CloseUtil.close(inStream);
        }
        return false;
    }


    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 强制重命名文件(如果新文件名称存在，则删除它)
     *
     * @param file
     * @param newName
     * @return
     */
    public static RenameResult forceRename(final File file, final String newName) {
        //文件为空，直接返回
        if (file == null || !file.exists()) {
            return new RenameResult(false, null);
        }
        //新文件名称为空或者全部空格
        if (TextUtil.isEmpty(newName) || isSpaceString(newName)) {
            return new RenameResult(false, null);
        }
        //新老文件名称相同，无需重命名
        if (newName.equals(file.getName())) {
            return new RenameResult(true, null);
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        //如果新文件存在，则直接删除
        if (newFile.exists()) {
            newFile.delete();
        }
        //重命名
        boolean res = file.renameTo(newFile);
        newFile = res ? newFile : (null);
        return new RenameResult(res, newFile);
    }

    public static boolean isSpaceString(String filePath) {
        if (TextUtil.isEmpty(filePath)) {
            return true;
        }
        for (int i = 0, len = filePath.length(); i < len; ++i) {
            if (!Character.isWhitespace(filePath.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean deleteFileOrDir(String path, boolean deleteSelf) {
        if (TextUtil.isEmpty(path)) {
            return false;
        }
        return deleteFileOrDir(new File(path), deleteSelf);
    }

    /**
     * 递归删除目录或者文件
     *
     * @param rootFile
     * @param deleteSelf
     * @return
     */
    public static boolean deleteFileOrDir(File rootFile, boolean deleteSelf) {
        //如果路径为空，直接返回
        if (rootFile == null) {
            return false;
        }
        //路径不存在，无需删除
        if (!rootFile.exists()) {
            return true;
        }
        //文件类型无需删除
        if (rootFile.isFile()) {
            return rootFile.delete();
        }
        if (!rootFile.isDirectory()) {
            return false;
        }
        //文件夹类型，列表所有子文件
        File[] files = rootFile.listFiles();
        //子文件为空，返回
        if (files == null) {
            return false;
        }
        //遍历所有子文件
        for (File file : files) {
            //文件类型，执行删除操作
            if (file.isFile()) {
                if (!file.delete()) {
                    return false;
                }
            } else if (file.isDirectory()) {
                //文件夹类型，递归调用本方法
                if (!deleteFileOrDir(file, true)) {
                    return false;
                }
            }
        }

        //如果需要删除根目录自身，则删除
        if (deleteSelf) {
            return rootFile.delete();
        }
        return true;
    }

    public static class RenameResult {
        public final boolean res;
        public final File file;

        public RenameResult(boolean res, File file) {
            this.res = res;
            this.file = file;
        }
    }

}
