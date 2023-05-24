package com.lalamove.huolala.dynamicbase.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: CloseUtil
 * @author: huangyuchen
 * @date: 3/5/22
 * @description:
 * @history:
 */
public class CloseUtil {

    private CloseUtil() {

    }

    public static void close(Closeable... closables) {
        if (closables == null || closables.length == 0) {
            return;
        }
        for (Closeable c : closables) {
            if (c == null) {
                continue;
            }
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
