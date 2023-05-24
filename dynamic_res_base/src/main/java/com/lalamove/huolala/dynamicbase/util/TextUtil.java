package com.lalamove.huolala.dynamicbase.util;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: TextUtil
 * @author: huangyuchen
 * @date: 4/11/22
 * @description:
 * @history:
 */
public class TextUtil {

    private TextUtil() {
    }

    public static String replaceClassName(String name) {
        if (isEmpty(name)) {
            return "";
        }
        return name.replace("/", ".");
    }

    public static String removeFileSuffix(String fileName) {
        if (TextUtil.isEmpty(fileName)) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index >= 0) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    public static String getValidJavaFieldName(String fileName) {
        if (TextUtil.isEmpty(fileName)) {
            return "";
        }
        String name = fileName;

        fileName = removeFileSuffix(fileName);
        String removeName = fileName;
        int index = fileName.indexOf(".");
        fileName = fileName.replaceAll("\\.", "_");
        fileName = fileName.replaceAll("-", "_");
        fileName = fileName.toUpperCase();
        return fileName;
    }


    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
