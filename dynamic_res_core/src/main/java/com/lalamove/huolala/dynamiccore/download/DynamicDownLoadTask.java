package com.lalamove.huolala.dynamiccore.download;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: DynamicDownLoadTask
 * @author: kezongze
 * @date: 3/2/22
 * @description: 下载接口实现类，使用huolala下载器sdk
 * @history:
 */
public class DynamicDownLoadTask implements Runnable {
    private static final String TAG = DynamicDownLoadTask.class.getSimpleName();
    private String mLink;
    private String mDirectory;
    private String mFileName;
    private DynamicDefaultDownloader.OnHttpGetFileListener mListener;
    int connContentLength = 0;


    public DynamicDownLoadTask(String link, String directory, String fileName, DynamicDefaultDownloader.OnHttpGetFileListener listener) {
        mLink = link;
        mDirectory = directory;
        mFileName = fileName;
        mListener = listener;
    }


    private void onError() {
        if (mListener != null) {
            mListener.onDownloaded(null);
        }
    }

    private void onSuccess(File file) {
        if (mListener != null) {
            mListener.onDownloaded(file);
        }
    }

    private void onUpdate(int total, int percentage) {
        if (mListener != null) {
            mListener.onDownloading(total, percentage);
        }
    }

    /**
     * 创建文件
     *
     * @param directory 文件路径
     * @param fileName  文件名
     * @return File文件
     */
    private File buildFile(String directory, String fileName) {
        boolean isSucceed = false;

        File path = new File(directory);
        isSucceed = path.exists();
        if (!isSucceed) {
            isSucceed = path.mkdirs();
        }

        if (!isSucceed) {
            return null;
        }

        File file = new File(path, fileName);
        if (file.exists()) {
            isSucceed = file.delete();
        }

        return isSucceed ? file : null;
    }


    @Override
    public void run() {
        Object[] objs = link2HttpGetConnectionAndInputStream(mLink, 1);
        if (objs == null || objs.length == 0) {
            Log.e(TAG, "base: ERR: " + "ConnectionAndInputStream err for " + mLink);
            onError();
            return;
        }
        //获取网络传输相关的对象
        HttpURLConnection conn = (HttpURLConnection) objs[0];
        InputStream is = (InputStream) objs[1];

        //获取文件
        File file = null;
        file = buildFile(mDirectory, mFileName);

        if (file == null) {
            Log.e(TAG, "base: ERR: " + "file init err for " + mDirectory + File.separator + mDirectory);
            onError();
            return;
        }

        FileOutputStream fos = null;
        try {
            //写入流
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE_IN_BYTE * 20];
            int readLength = 0;
            int hasReadLength = 0;
            connContentLength = conn.getContentLength();

            while ((readLength = is.read(buffer)) != -1) {
                fos.write(buffer, 0, readLength);
                hasReadLength = hasReadLength + readLength;
                onUpdate(connContentLength, calculateDownloadPercentage(hasReadLength, connContentLength));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "base: ERR: " + "read http or save file err");
            file = null;
        } finally {
            try {
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        onSuccess(file);
    }

    /**
     * 默认的连接超时时间
     */
    public static final int DEFAULT_CONNECT_TIMEOUT_IN_MS = 30000;
    /**
     * 默认的读取超时时间
     */
    public static final int DEFAULT_READ_TIMEOUT_IN_MS = 30000;
    /**
     * 缓存字节大小
     */
    public static final int BUFFER_SIZE_IN_BYTE = 1024;

    /**
     * 当前下载进度
     *
     * @param finished  完成的大小
     * @param totalSize 当前的大小
     * @return
     */
    private int calculateDownloadPercentage(int finished, int totalSize) {
        double result = (double) finished / totalSize * 100;
        if (result > 100) {
            result = 100;
        }
        return (int) result;
    }

    /**
     * 根据Url连接网络
     *
     * @param link           Url地址
     * @param maxNoOfRetries 尝试的次数
     * @return 返回connection，和 InputStream
     */
    private Object[] link2HttpGetConnectionAndInputStream(final String link, final int maxNoOfRetries) {

        if (link == null || link.isEmpty()) {
            Log.e(TAG, "base: ERR: " + "link is null or empty");
            return new Object[]{null, null};
        }

        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url == null) {
            Log.e(TAG, "base: ERR: " + "malformed URL for " + link);
            return new Object[]{null, null};
        }

        HttpURLConnection conn = null;
        InputStream is = null;

        boolean hasException = false;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // will make httpGetFile not work, reason unknown
            // conn.setDoOutput(true);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");

            int countOfRetries = 0;

            while (is == null && countOfRetries < maxNoOfRetries) {
                if (countOfRetries >= 1) {
                    Log.i(TAG, "base: RETRY (" + (countOfRetries + 1) + "): " + link);
                }

                try {
                    is = new BufferedInputStream(conn.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "base: ERR: " + "get input stream error for " + link);
                }
                countOfRetries++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "base: ERR: " + "setup (not connect) connection error");

            hasException = true;
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return hasException ? null : new Object[]{conn, is};
    }
}

