package com.boge.update.widget;

/**
 * @Author ibshen@aliyun.com
 */
public interface IDownload {
    void show();
    // Front desk display and start downloading
    void start();
    void dismiss();
    // cancel the download
    void cancel();
}
