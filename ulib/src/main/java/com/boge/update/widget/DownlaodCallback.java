package com.boge.update.widget;

public interface DownlaodCallback {
    //code 1、后台下载；2、取消下载；3、下载完成；4、下载失败；
    //Code 1. Background download; 2. Cancel the download; 3. Download completed; 4. Download failed;
    void callback(int code,String message);
}
