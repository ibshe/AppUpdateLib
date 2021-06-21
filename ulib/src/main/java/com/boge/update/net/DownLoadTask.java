package com.boge.update.net;

import com.boge.update.utils.UpdateLog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * @Author ibshen@aliyun.com
 */
public class DownLoadTask extends Thread {

    private static final String TAG = DownLoadTask.class.getSimpleName();

    private String mFilePath;
    private String mDownLoadUrl;
    private ProgressListener mProgressListener;
    private InputStream in = null;
    private FileOutputStream fileOutputStream = null;
    private boolean interrupt_flag = false;

    public DownLoadTask(String filePath, String downLoadUrl, ProgressListener progressListener) {
        mDownLoadUrl = downLoadUrl;
        mFilePath = filePath;
        mProgressListener = progressListener;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mDownLoadUrl);
            if (mDownLoadUrl.startsWith("https://")) {
                TrustAllCertificates.install();
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Keep-Alive", "header");

            in = new BufferedInputStream(connection.getInputStream());
            int count = connection.getContentLength();
            if (count <= 0) {
                UpdateLog.e(TAG, "file length must > 0");
                return;
            }

            if (in == null) {
                UpdateLog.e(TAG, "InputStream not be null");
                return;
            }
            if(interrupt_flag){
                UpdateLog.e(TAG, "user cancel download");
                return;
            }
            writeToFile(count, mFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            if (mProgressListener != null) {
                mProgressListener.onError();
            }
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
            interrupt_flag = false;
        }
    }

    private void writeToFile(int count, String filePath) throws IOException {
        int len;
        byte[] buf = new byte[2048];
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        fileOutputStream = new FileOutputStream(file);
        int bytesRead = 0;
        while ((len = in.read(buf)) != -1) {
            if(interrupt_flag){
                break;
            }
            fileOutputStream.write(buf, 0, len);
            bytesRead += len;
            mProgressListener.update(bytesRead, count);
        }
        mProgressListener.done();
    }

    public interface ProgressListener {
        void done();

        void update(long bytesRead, long contentLength);

        void onError();
    }

    public void taskFlag(boolean flag){
        this.interrupt_flag = flag;
    }
}
