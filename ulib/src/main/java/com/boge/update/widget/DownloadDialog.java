package com.boge.update.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boge.update.R;
import com.boge.update.UpdateWrapper;
import com.boge.update.base.BsDialog;
import com.boge.update.common.RadiusEnum;
import com.boge.update.common.BaseConfig;
import com.boge.update.net.DownLoadService;
import com.boge.update.net.DownLoadTask;
import com.boge.update.utils.FileUtils;
import com.boge.update.utils.ScreenUtils;
import com.boge.update.utils.ToastUtils;
import com.boge.update.utils.UpdateLog;

import java.io.File;

/**
 * @Author ibshen@aliyun.com
 */

public class DownloadDialog implements IDownload {

    private final static String TAG = DownloadDialog.class.getSimpleName();
    private final static int LOADING = 1000;
    private final static int DONE = 1001;
    private final static int ERROR = 1002;
    private Context mContext;
    private BsDialog bsDialog;
    private TextView titleTv,negtive,positive;
    private View line1,line2;
    private ProgressBar mProgressbar;
    private DownLoadService mDownLoadService;
    private String mUrl;
    private NotificationManager mManager;
    private Notification.Builder mBuilder;
    private Notification mNotification;
    private boolean userCancel = false;
    private int mNotificationIcon = R.drawable.ic_launcher;
    private UpdateWrapper.UpdateCallback mCallback;
    private Class<?> mClazz;
    private int mNotifyId;
    private int currentProgress;
    private boolean hasBindingService = false;
    private int lastProgress = 0;
    private View rootView;
    private RadiusEnum mRadius = RadiusEnum.UPDATE_RADIUS_10;
    private DownlaodCallback mDownlaodCallback;
    protected DownloadDialog(){
    }

    public void start(){
        Intent mIntent = new Intent(mContext, DownLoadService.class);
        mContext.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    protected void showView(final int progress){
        bsDialog = new BsDialog(mContext, R.layout.download_dialog_default) {
            @Override
            public void onBindViewHolder(DialogViewHolder holder) {
                rootView = holder.getConvertView();
                userCancel = false;
                titleTv = rootView.findViewById(R.id.title);
                mProgressbar = rootView.findViewById(R.id.progressbar);
                negtive = rootView.findViewById(R.id.negtive);
                positive = rootView.findViewById(R.id.positive);
                line1 = rootView.findViewById(R.id.download_line1);
                line2 = rootView.findViewById(R.id.download_line2);
                if(mRadius.getType() != 10){
                    rootView.setBackground(mContext.getResources().getDrawable(ScreenUtils.getDrawableId(mRadius.getType())));
                }
                titleTv.setText(mContext.getString(R.string.update_lib_file_downloading));
                mProgressbar.setProgress(progress);
                negtive.setText(mContext.getString(R.string.update_lib_cancel));
                positive.setText(mContext.getString(R.string.update_lib_background_download_tv));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                statusCallback(1,mContext.getString(R.string.update_lib_background_download),true);
                            }
                        });
                        dismiss();
                    }
                });
                negtive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                statusCallback(2,mContext.getString(R.string.update_lib_download_cancel),true);
                            }
                        });
                        userCancel = true;
                        cancel();
                    }
                });
            }
        }.backgroundLight(0.5)
                .setCancelAble(true)
                .setCanceledOnTouchOutside(false);
        if(!BaseConfig.BACKGROUND_UPDATE){
           show();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownLoadService.DownLoadBinder binder = (DownLoadService.DownLoadBinder) service;
            mDownLoadService = binder.getService();
            mDownLoadService.registerProgressListener(mProgressListener);
            mDownLoadService.startDownLoad(mUrl);
            currentProgress = 0;
            lastProgress = 0;
            notifyProgress(0);
            hasBindingService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownLoadService.cancel();
            mDownLoadService = null;
            hasBindingService = false;
        }
    };

    private DownLoadTask.ProgressListener mProgressListener = new DownLoadTask.ProgressListener() {
        @Override
        public void done() {
            mHandler.sendEmptyMessage(DONE);
        }

        @Override
        public void update(long bytesRead, long contentLength) {
            currentProgress = (int) (bytesRead * 100 / contentLength);
            if (currentProgress < 1) {
                currentProgress = 1;
            }
            //UpdateLog.d(TAG, "" + bytesRead + "," + contentLength + ";current=" + currentProgress);
            Message message = mHandler.obtainMessage();
            message.what = LOADING;
            message.arg1 = currentProgress;
            Bundle bundle = new Bundle();
            bundle.putLong("bytesRead", bytesRead);
            bundle.putLong("contentLength", contentLength);
            message.setData(bundle);
            message.sendToTarget();
        }

        @Override
        public void onError() {
            mHandler.sendEmptyMessage(ERROR);
        }
    };

    @Override
    public void show(){
        if(bsDialog!=null){
            bsDialog.showDialog();
            return;
        }
        UpdateLog.e(TAG,mContext.getString(R.string.update_not_initialized_yet));
    }

    public static class Builder{
        DownloadDialog downloadDialog = new DownloadDialog();
        public Builder(Context context,String url,boolean backgroundDownload){
            downloadDialog.mContext = context;
            downloadDialog.mUrl = url;
            BaseConfig.BACKGROUND_UPDATE = backgroundDownload;
        }

        public Builder progress(int progress){
            downloadDialog.currentProgress = progress;
            return this;
        }

        public Builder notificationIcon(int notificationIcon){
            downloadDialog.mNotificationIcon = notificationIcon;
            return this;
        }

        public Builder notifyId(int notifyId){
            downloadDialog.mNotifyId = notifyId;
            return this;
        }
        public Builder downloadCallback(DownlaodCallback downlaodCallback) {
            downloadDialog.mDownlaodCallback = downlaodCallback;
            return this;
        }

        public Builder setClass(Class<?> clazz){
            downloadDialog.mClazz = clazz;
            return this;
        }

        public Builder radius(RadiusEnum radius) {
            downloadDialog.mRadius = radius;
            return this;
        }

        public Builder radius(BsDialog bsDialog) {
            downloadDialog.bsDialog = bsDialog;
            return this;
        }

        public DownloadDialog build(){
            downloadDialog.showView(downloadDialog.currentProgress);
            return downloadDialog;
        }
    }

    @Override
    public void dismiss(){
        if(bsDialog!=null){
            bsDialog.dismiss();
        }
        UpdateLog.e(TAG,mContext.getString(R.string.update_not_initialized_yet));
    }

    @Override
    public void cancel() {
        if(mManager!=null){
            mManager.cancel(mNotifyId);
        }
        if(mConnection!=null && hasBindingService){
            mDownLoadService.cancel();
            mContext.unbindService(mConnection);
            mConnection = null;
        }
        if(bsDialog!=null){
            bsDialog.dismiss();
            return;
        }
        UpdateLog.e(TAG,mContext.getString(R.string.update_download_did_not_start));
    }

    public void backgroundDownload() {
        dismiss();
    }

    public DownloadDialog show(@StyleRes int style){
        if(bsDialog!=null){
            bsDialog.showDialog(style);
        }
        return this;
    }

    public DownloadDialog setDialogStyle(@DrawableRes int style){
        if(rootView!=null){
            rootView.setBackgroundResource(style);
        }
        return this;
    }

    public DownloadDialog setTitle(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&titleTv!=null){
            titleTv.setText(var1);
        }
        return this;
    }

    public DownloadDialog setProgress(int var1){
        if(mProgressbar!=null){
            mProgressbar.setProgress(var1);
        }
        return this;
    }

    public DownloadDialog setTitleColor(@ColorRes int var1){
        if(titleTv!=null){
            titleTv.setTextColor(mContext.getResources().getColor(var1));
        }
        return this;
    }

    public DownloadDialog setLineColor(@ColorRes int var1){
        if(line1!=null){
            line1.setBackgroundColor(mContext.getResources().getColor(var1));
        }
        if(line2!=null){
            line2.setBackgroundColor(mContext.getResources().getColor(var1));
        }
        return this;
    }
    public DownloadDialog setCancelColor(@ColorRes int var1){
        if(negtive!=null){
            negtive.setTextColor(mContext.getResources().getColor(var1));
        }
        return this;
    }

    public DownloadDialog setConfirmColor(@ColorRes int var1){
        if(positive!=null){
            positive.setTextColor(mContext.getResources().getColor(var1));
        }
        return this;
    }

    public DownloadDialog setProgressStyle(Drawable var1){
        if(mProgressbar!=null){
            mProgressbar.setProgressDrawable(var1);
        }
        return this;
    }

    public DownloadDialog setCancelText(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&negtive!=null){
            negtive.setText(var1);
        }
        return this;
    }

    public DownloadDialog setConfirmText(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&positive!=null){
            positive.setText(var1);
        }
        return this;
    }

    private void showNotification(final Context context, String title, String content) {
        if(mManager==null){
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(mBuilder==null) {
            mBuilder = new Notification.Builder(context);
        }
        if(!TextUtils.isEmpty(title)){
            mBuilder.setContentTitle(title);
        }
        if(!TextUtils.isEmpty(content)){
            mBuilder.setContentText(content);
        }
        mBuilder.setSmallIcon(mNotificationIcon);
        //设置通知正文
        if(mClazz!=null){
            mBuilder.setContentIntent(PendingIntent.getActivity(context,1010,new Intent(context,mClazz),PendingIntent.FLAG_ONE_SHOT));
        }
        //点击是否消失
        mBuilder.setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //在8.1后id重复,抛异常
            mNotifyId = mNotifyId+(int) (Math.random() * 10000 + 10000);
        }
        mBuilder.setProgress(100,0,false);
        mNotification = mBuilder.build();
        mManager.notify(mNotifyId,mNotification);
    }

    private void notifyProgress(int progress){
        if(mManager==null || mBuilder==null || mNotification==null) {
            showNotification1(mContext,"正在下载","内容");
            return;
        }
        mBuilder.setProgress(100,progress,false);
        mManager.notify(mNotifyId,mNotification);
        if(progress==100){
            mManager.cancel(mNotifyId);
        }
    }

    private void showNotification1(Context context, String title, String content) {
        if(mManager==null){
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(mNotifyId+"", title, importance);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(false);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (mManager != null) {
                mManager.createNotificationChannel(notificationChannel);
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1");
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setProgress(100,0,false);
        mBuilder.setNumber(1);
        Notification notify = mBuilder.build();

        //id 1000-2000
        final int notifyId = (int) (Math.random() * 1000 + 1000);
        if (mManager != null) {
           mManager.notify(notifyId, notify);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    if(mProgressbar!=null&&lastProgress!=msg.arg1){
                        mProgressbar.setProgress(msg.arg1);
                        notifyProgress(msg.arg1);
                    }
                    lastProgress = msg.arg1;
                    Bundle bundle = msg.getData();
                    long bytesRead = bundle.getLong("bytesRead");
                    long contentLength = bundle.getLong("contentLength");
                    if (mContext != null)
                        titleTv.setText(String.format(mContext.getString(R.string.update_lib_file_download_format),
                                Formatter.formatFileSize(mContext.getApplicationContext(), bytesRead),
                                Formatter.formatFileSize(mContext.getApplicationContext(), contentLength)));
                    break;
                case DONE:
                    bsDialog.dismiss();
                    if (mContext != null && !userCancel) {
                        statusCallback(3,mContext.getString(R.string.update_lib_download_finish),true);
                        mContext.startActivity(FileUtils.openApkFile(mContext, new File(FileUtils.getApkFilePath(mContext, mUrl))));
                    }
                    break;
                case ERROR:
                    if (mContext != null)
                        statusCallback(4,mContext.getString(R.string.update_lib_download_failed),false);
                        cancel();
                    break;
            }
        }
    };

    public void statusCallback(int code, String msg, boolean debug){
        if(mDownlaodCallback != null) {
            mDownlaodCallback.callback(code,msg);
        }else {
            ToastUtils.show(mContext, msg);
        }
        if(debug){
            UpdateLog.d(TAG,msg+" code:"+code);
        }else{
            UpdateLog.e(TAG,msg+" code:"+code);
        }
    }

}
