package com.lalamove.huolala.dynamicbase.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: Md5Util
 * @author: huangyuchen
 * @date: 3/9/22
 * @description: 获取文件Md5码
 * @history:
 */
public class Md5Util {

    private Md5Util() {

    }

    private static final char[] HEX_DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getFileMD5(final String filePath, boolean isUpperCase) {
        if (isSpaceString(filePath)) {
            return "";
        }
        return getFileMD5(new File(filePath), isUpperCase);
    }

    public static String getFileMD5(final File file, boolean isUpperCase) {
        if (file == null) {
            return "";
        }
        DigestInputStream dis = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) {
                    break;
                }
            }
            md = dis.getMessageDigest();
            return bytes2HexString(md.digest(), isUpperCase);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(dis);
            CloseUtil.close(fis);
        }
        return "";
    }

    private static String bytes2HexString(final byte[] bytes, boolean isUpperCase) {
        if (bytes == null) {
            return "";
        }
        char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
        int len = bytes.length;
        if (len <= 0) {
            return "";
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static boolean isSpaceString(String filePath) {
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
}
