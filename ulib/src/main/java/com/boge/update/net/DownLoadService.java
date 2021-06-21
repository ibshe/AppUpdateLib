package com.boge.update.net;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.boge.update.utils.FileUtils;

import java.io.File;
/**
 * @Author ibshen@aliyun.com
 */
public class DownLoadService extends Service {
    private String filePath;
    //是否后台下载
    private boolean isBackground = false;
    private DownLoadTask mDownLoadTask;
    //进度回调
    private DownLoadTask.ProgressListener mProgressListener;

    public void startDownLoad(String url) {
        filePath = FileUtils.getApkFilePath(getApplicationContext(), url);
        mDownLoadTask = new DownLoadTask(filePath, url, new DownLoadTask.ProgressListener() {
            @Override
            public void done() {
                if (isBackground) {
                    startActivity(FileUtils.openApkFile(getApplicationContext(), new File(filePath)));
                } else {
                    if (mProgressListener != null) {
                        mProgressListener.done();
                    }
                }
            }
            @Override
            public void update(long bytesRead, long contentLength) {
                if (isBackground) {
                    int currentProgress = (int) (bytesRead * 100 / contentLength);
                    if (currentProgress < 1) {
                        currentProgress = 1;
                    }
                    return;
                }
                if (mProgressListener != null) {
                    mProgressListener.update(bytesRead, contentLength);
                }
            }

            @Override
            public void onError() {
                if (mProgressListener != null) {
                    mProgressListener.onError();
                }
            }
        });
        mDownLoadTask.taskFlag(false);
        mDownLoadTask.start();
    }

    public void setBackground(boolean background) {
        isBackground = background;
    }
    private final DownLoadBinder mDownLoadBinder = new DownLoadBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownLoadBinder;
    }

    public void cancel() {
        if (mDownLoadTask != null) {
            mDownLoadTask.taskFlag(true);
            mDownLoadTask = null;
        }
    }

    public class DownLoadBinder extends Binder {
        public DownLoadService getService() {
            return DownLoadService.this;
        }
    }

    public void registerProgressListener(DownLoadTask.ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

}
