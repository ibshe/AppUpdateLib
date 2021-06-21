package com.boge.update;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.boge.update.common.RadiusEnum;
import com.boge.update.common.BaseConfig;
import com.boge.update.entity.VersionModel;
import com.boge.update.net.CheckUpdateTask;
import com.boge.update.utils.NetWorkUtils;
import com.boge.update.utils.PackageUtils;
import com.boge.update.utils.PublicFunctionUtils;
import com.boge.update.utils.ToastUtils;
import com.boge.update.utils.UpdateDateUtils;
import com.boge.update.widget.AbstractUpdateDialog;
import com.boge.update.widget.DownlaodCallback;
import com.boge.update.widget.IDownload;
import com.boge.update.widget.MustUpdateDialog;
import com.boge.update.widget.UpdateDialog;

import java.util.Map;

/**
 * @Author ibshen@aliyun.com
 */

public class UpdateWrapper {

    private AbstractUpdateDialog mUpdateDialog;
    private boolean hasBuild = false;
    private IDownload mDownloadDialog;
    private Context mContext;
    private String mUrl = "";
    //true 每天最多检查一次，false 立即更新
    private boolean checkEveryDay = false;
    //网络不可用是否toast
    private boolean isShowErrorToast = true;
    private boolean isPost = false;
    //不同页面控制是否toast
    private boolean isShowNoUpdateToast = true;
    private boolean isBackgoundDownload = false;
    private Map<String, String> mPostParams;
    private int mNotificationIcon = R.drawable.ic_launcher;
    private UpdateCallback mCallback;
    private Class<?> mClazz = null;
    private int mNotifyId = 0;
    private String mTitle ="";
    private String mNegtive = "";
    private String mPositive = "";
    private boolean mMustUpdate = false;
    private RadiusEnum mRadius;
    private VersionModel mModel;
    private DownlaodCallback mDownlaodCallback;
    public UpdateWrapper(){
        resetSettings();
    }

    public void start(){
        if(!hasBuild){
            throw new RuntimeException("The build method was not called");
        }
        if (!NetWorkUtils.getNetworkStatus(mContext)) {
            if (isShowErrorToast) {
                ToastUtils.show(mContext, R.string.update_lib_network_not_available);
            }
            return;
        }
        if (TextUtils.isEmpty(mUrl)||"".equals(mUrl.trim())) {
            if(mModel == null){
                throw new RuntimeException("URL and entity cannot be empty at the same time");
            }
        }
        BaseConfig.NOTIFICATION_ICON = mNotificationIcon;
        if(mNotifyId !=0 )
            BaseConfig.NOTIFY_ID = mNotifyId;
            BaseConfig.BACKGROUND_UPDATE = isBackgoundDownload;
        if(checkEveryDay && !intervalDate()){
            return;
        }
        //model
        if(mModel != null && mModel.getVersionCode() != 0){
            BaseConfig.DOWNLOAD_URL = mModel.getUrl();
            checkUpdate(mModel);
            return;
        }
        BaseConfig.JSON_URL = mUrl;
        new CheckUpdateTask(mContext, mUrl, isPost, mPostParams,callBack).start();
    }

    public static class Builder{
        UpdateWrapper updateWrapper = new UpdateWrapper();
        public Builder(Context context,String url) {
            updateWrapper.mContext = context;
            updateWrapper.mUrl = url;
            updateWrapper.hasBuild = true;
        }

        public Builder model(VersionModel model){
            updateWrapper.mModel = model;
            return this;
        }
        public Builder title(String title){
            updateWrapper.mTitle = title;
            return this;
        }
        public Builder negtiveText(String negtive){
            updateWrapper.mNegtive = negtive;
            return this;
        }
        public Builder positiveText(String positive){
            updateWrapper.mPositive = positive;
            return this;
        }

        public Builder notificationIcon(int notificationIcon) {
            updateWrapper.mNotificationIcon = notificationIcon;
            return this;
        }

        public Builder setNotifyId(int notifyId) {
            updateWrapper.mNotifyId = notifyId;
            return this;
        }

        public Builder radius(RadiusEnum radius) {
            updateWrapper.mRadius = radius;
            return this;
        }

        public Builder updateCallback(UpdateCallback callback) {
            updateWrapper.mCallback = callback;
            return this;
        }

        public Builder setClazz(Class<?> clazz) {
            updateWrapper.mClazz = clazz;
            return this;
        }

        public Builder showNetworkErrorToast(boolean isShowNetworkErrorToast) {
            updateWrapper.isShowErrorToast = isShowNetworkErrorToast;
            return this;
        }

        public Builder showNoUpdateToast(boolean showNoUpdateToast) {
            updateWrapper.isShowNoUpdateToast = showNoUpdateToast;
            return this;
        }

        public Builder backgroundDownload(boolean isShowBackgroundDownload) {
            updateWrapper.isBackgoundDownload = isShowBackgroundDownload;
            return this;
        }

        public Builder downloadCallback(DownlaodCallback downlaodCallback) {
            updateWrapper.mDownlaodCallback = downlaodCallback;
            return this;
        }
        //检查更新请求协议是否为POST，默认get
        public Builder isPost(boolean isPost) {
            updateWrapper.isPost = isPost;
            return this;
        }

        public Builder isMustUpdate(boolean mustUpdate) {
            updateWrapper.mMustUpdate = mustUpdate;
            return this;
        }

        public Builder postParams(Map<String, String> postParams) {
            updateWrapper.mPostParams = postParams;
            return this;
        }

        public Builder checkEveryday(boolean isCheck) {
            updateWrapper.checkEveryDay = isCheck;
            return this;
        }
        public Builder customDialog(AbstractUpdateDialog updateDialog) {
            updateWrapper.mUpdateDialog = updateDialog;
            return this;
        }
        public UpdateWrapper build() {
            return updateWrapper;
        }
    }

    private boolean intervalDate() {
        String lastCheckUpdateTime = PublicFunctionUtils.getLastCheckDate(mContext);
        if (!UpdateDateUtils.getCurDayStr().equals(lastCheckUpdateTime)) {
            return true;
        }
        return false;
    }

    private void checkUpdate(VersionModel model){
        boolean hasNewVersion = PackageUtils.getVersionCode(mContext) < model.getVersionCode();
        if (mCallback != null) {
            mCallback.res(model, hasNewVersion);
        }
        PublicFunctionUtils.setLastCheckTime(mContext, System.currentTimeMillis(),UpdateDateUtils.getCurDayStr());
        if(hasNewVersion){
            if(!TextUtils.isEmpty(model.getContent())){
                if(model.getContent().contains("#")){
                    model.setContent(model.getContent().replace("#","\r\n"));
                }
            }
            BaseConfig.UPDATE_CONTENT = model.getContent();
            BaseConfig.UPDATE_TITLE = model.getUpdateTitle();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mUpdateDialog == null){
                        if(mMustUpdate){
                            new MustUpdateDialog(mContext, mTitle, mNegtive, mPositive,mRadius,mDownlaodCallback).show();
                            return;
                        }
                        new UpdateDialog(mContext, mTitle, mNegtive, mPositive,mRadius,mDownlaodCallback).show();
                        return;
                    }
                    mUpdateDialog.setContent(BaseConfig.UPDATE_CONTENT);
                    mUpdateDialog.show();
                }
            });
            return;
        }
        if(isShowNoUpdateToast){
            ToastUtils.show(mContext,mContext.getString(R.string.update_lib_default_toast));
        }
    }

    private CheckUpdateTask.Callback callBack = new CheckUpdateTask.Callback() {
        @Override
        public void callBack(final VersionModel model, final boolean hasNewVersion) {
            if (model == null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!hasNewVersion&&isShowNoUpdateToast){
                            ToastUtils.show(mContext,mContext.getString(R.string.update_lib_default_toast));
                        }
                    }
                });
                return;
            }
            //记录本次更新时间
            PublicFunctionUtils.setLastCheckTime(mContext, System.currentTimeMillis(),UpdateDateUtils.getCurDayStr());
            if (mCallback != null) {
                mCallback.res(model, hasNewVersion);
            }
            if(hasNewVersion){
                //If you use Notepad to edit a JSON document and do not choose the correct encoding type, you may report the following error: java.net.MalformedURLException: no protocol  --by sjibo
                BaseConfig.DOWNLOAD_URL = model.getUrl().replaceAll("\\ufeff","");
                if(!TextUtils.isEmpty(model.getContent())){
                    if(model.getContent().contains("#")){
                        model.setContent(model.getContent().replace("#","\r\n"));
                    }
                }
                BaseConfig.UPDATE_CONTENT = model.getContent();
                BaseConfig.UPDATE_TITLE = model.getUpdateTitle();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mUpdateDialog == null){
                                if(mMustUpdate){
                                    new MustUpdateDialog(mContext, mTitle, mNegtive, mPositive,mRadius,mDownlaodCallback).show();
                                    return;
                                }
                                new UpdateDialog(mContext, mTitle, mNegtive, mPositive,mRadius,mDownlaodCallback).show();
                                return;
                            }
                            mUpdateDialog.setContent(BaseConfig.UPDATE_CONTENT);
                            mUpdateDialog.show();
                        }
                    });
            }
        }
    };

    private void resetSettings(){
        BaseConfig.resetConfig();
        hasBuild = false;
        checkEveryDay = false;
        isShowNoUpdateToast = true;
        isBackgoundDownload = false;
        isShowErrorToast = true;
        mRadius = RadiusEnum.UPDATE_RADIUS_10;
        mMustUpdate = false;
    }

    public interface UpdateCallback{
        void res(VersionModel model, boolean hasNewVersion);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static void main(String[] args) {
        String dd = "1、新增XXX功能@2、修复已知bug";
        System.out.println(dd.replace("@","\t\n"));
        System.out.println(dd.replace("@","\r\n"));
    }
}
