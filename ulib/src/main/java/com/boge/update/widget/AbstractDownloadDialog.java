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
import com.boge.update.common.BaseConfig;
import com.boge.update.net.DownLoadService;
import com.boge.update.net.DownLoadTask;
import com.boge.update.utils.FileUtils;
import com.boge.update.utils.ToastUtils;

import java.io.File;

/**
 * @Author ibshen@aliyun.com
 */
public abstract class AbstractDownloadDialog implements IDownload {

    private final static String TAG = AbstractDownloadDialog.class.getSimpleName();
    private final static int LOADING = 1000;
    private final static int DONE = 1001;
    private final static int ERROR = 1002;
    private Context mContext;
    private BsDialog bsDialog;
    private TextView titleTv,negtive,positive;
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

    private AbstractDownloadDialog(){
    }

    protected abstract int inflateLayout();

    public void start(){
        Intent mIntent = new Intent(mContext, DownLoadService.class);
        mContext.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void showView(final int progress){
        bsDialog = new BsDialog(mContext, R.layout.download_dialog_default) {
            @Override
            public void onBindViewHolder(DialogViewHolder holder) {
                View view = holder.getConvertView();
                userCancel = false;
                titleTv = view.findViewById(R.id.title);
                mProgressbar = view.findViewById(R.id.progressbar);
                negtive = view.findViewById(R.id.negtive);
                positive = view.findViewById(R.id.positive);
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
                                ToastUtils.show(mContext,R.string.update_lib_background_download);
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
                                ToastUtils.show(mContext,R.string.update_lib_download_cancel);
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
        ToastUtils.show(mContext,R.string.update_not_initialized_yet);
    }

    public static class Builder{
        AbstractDownloadDialog downloadDialog;
        {
            try {
                downloadDialog = AbstractDownloadDialog.class.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

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

        public Builder setClass(Class<?> clazz){
            downloadDialog.mClazz = clazz;
            return this;
        }

        public AbstractDownloadDialog build(){
            downloadDialog.showView(downloadDialog.currentProgress);
            return downloadDialog;
        }
    }

    @Override
    public void dismiss(){
        if(bsDialog!=null){
            bsDialog.dismiss();
        }
        ToastUtils.show(mContext,R.string.update_not_initialized_yet);
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
        ToastUtils.show(mContext,R.string.update_download_did_not_start);
    }

    public void backgroundDownload() {
        dismiss();
    }

    public AbstractDownloadDialog show(@StyleRes int style){
        if(bsDialog!=null){
            bsDialog.showDialog(style);
        }
        return this;
    }

    public AbstractDownloadDialog setTitle(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&titleTv!=null){
            titleTv.setText(var1);
        }
        return this;
    }

    public AbstractDownloadDialog setProgress(int var1){
        if(mProgressbar!=null){
            mProgressbar.setProgress(var1);
        }
        return this;
    }

    public AbstractDownloadDialog setCancelTextColor(int var1){
        if(negtive!=null){
            negtive.setTextColor(var1);
        }
        return this;
    }

    public AbstractDownloadDialog setConfirmTextColor(int var1){
        if(positive!=null){
            positive.setTextColor(var1);
        }
        return this;
    }

    public AbstractDownloadDialog setProgressStyle(Drawable var1){
        if(mProgressbar!=null){
            mProgressbar.setProgressDrawable(var1);
        }
        return this;
    }

    public AbstractDownloadDialog setCancelText(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&negtive!=null){
            negtive.setText(var1);
        }
        return this;
    }

    public AbstractDownloadDialog setConfirmText(String var1){
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
           // showNotification(mContext,"正在下载","内容");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        mBuilder.setSmallIcon(mNotificationIcon);
        mBuilder.setTicker(title+"===");
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setProgress(100,0,false);
        mBuilder.setNumber(1);
        final Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notify.defaults = Notification.DEFAULT_SOUND; // 调用系统自带声音

        //随机id 1000-2000
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
                        mContext.startActivity(FileUtils.openApkFile(mContext, new File(FileUtils.getApkFilePath(mContext, mUrl))));
                        ToastUtils.show(mContext, R.string.update_lib_download_finish);
                    }
                    break;
                case ERROR:
                    if (mContext != null)
                        ToastUtils.show(mContext, R.string.update_lib_download_failed);
                        cancel();
                    break;
            }
        }
    };
}
