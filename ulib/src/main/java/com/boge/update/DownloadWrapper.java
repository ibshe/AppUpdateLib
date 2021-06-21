package com.boge.update;

import android.content.Context;
import android.text.TextUtils;

import com.boge.update.common.RadiusEnum;
import com.boge.update.common.BaseConfig;
import com.boge.update.widget.DownloadDialog;
import com.boge.update.widget.IDownload;

/**
 * @Author ibshen@aliyun.com
 */

public class DownloadWrapper implements IDownload {

    private Context mContext;
    private IDownload mDialog;
    private String cancelTx,ConfirmTx;
    private Class<?> mClazz = null;
    private boolean backgroundDownload = false;
    private RadiusEnum mRadius;

    //custom download dialog
    public DownloadWrapper(Context context,IDownload dialog,String url,boolean isBackgroundDownload,RadiusEnum radius){
        this(context,url,dialog,0,0,null,isBackgroundDownload,radius);
    }

    public DownloadWrapper(Context context,String url,boolean isBackgroundDownload,RadiusEnum radius){
        this(context,url,null,0,0,null,isBackgroundDownload,radius);
    }

    public DownloadWrapper(Context context,String url,IDownload dialog,int notificationIcon,int notifyId,Class<?> clazz,boolean isBackgroundDownload,RadiusEnum radius){
        this.mContext =context;
        resetSettings();
        this.mRadius = radius;
        if(!TextUtils.isEmpty(url)){
            BaseConfig.DOWNLOAD_URL = url;
        }
        if(BaseConfig.NOTIFICATION_ICON != notificationIcon && 0 == notificationIcon){
            BaseConfig.NOTIFICATION_ICON = notificationIcon;
        }
        if(BaseConfig.NOTIFY_ID != notifyId && 0 != notifyId){
            BaseConfig.NOTIFY_ID = notifyId;
        }
        if(clazz!=null){
            this.mClazz = clazz;
        }
        if(backgroundDownload != isBackgroundDownload){
            BaseConfig.BACKGROUND_UPDATE = isBackgroundDownload;
        }
        if(dialog==null){
            initDialog();
        }else {
            this.mDialog = dialog;
        }
    }

    private void initDialog(){
        this.mDialog = new DownloadDialog.Builder(mContext,BaseConfig.DOWNLOAD_URL,BaseConfig.BACKGROUND_UPDATE).progress(0).notificationIcon(BaseConfig.NOTIFICATION_ICON)
                .notifyId(BaseConfig.NOTIFY_ID).setClass(mClazz).build();
    }

    @Override
    public void show() {
        mDialog.show();
    }

    @Override
    public void start() {
        mDialog.start();
    }

    @Override
    public void dismiss() {
        mDialog.dismiss();
    }

    @Override
    public void cancel() {
        mDialog.cancel();
    }

    private void resetSettings(){
        BaseConfig.resetConfig();
        mRadius = RadiusEnum.UPDATE_RADIUS_10;
        backgroundDownload = false;
    }
}
