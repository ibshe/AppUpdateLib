package com.boge.update.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.boge.update.R;
import com.boge.update.base.BsDialog;
import com.boge.update.common.RadiusEnum;
import com.boge.update.common.BaseConfig;
import com.boge.update.utils.ScreenUtils;
import com.boge.update.utils.UpdateLog;

/**
 * @Author ibshen@aliyun.com
 */

public abstract class AbstractUpdateDialog implements IUpdate{

    private String TAG = AbstractUpdateDialog.class.getSimpleName();
    protected BsDialog bsDialog;
    protected TextView titleTv,contentTv,negtive,positive;
    protected Context mContext;
    protected String mTitle,mNegivteTx,mPositiveTx;
    protected BindingCallback bindingCallback;
    protected boolean mMustUpdate = false;
    protected int layoutId;
    protected RadiusEnum mRadius = RadiusEnum.UPDATE_RADIUS_10;
    protected DownlaodCallback mDownlaodCallback;

    public AbstractUpdateDialog(Context context, final String title, final String negivteTx, final String positiveTx, int layoutId, boolean mustUpdate, RadiusEnum radius,DownlaodCallback downlaodCallback){
        this.mContext = context;
        this.mTitle = title;
        this.mNegivteTx = negivteTx;
        this.mPositiveTx = positiveTx;
        this.layoutId = layoutId;
        this.mMustUpdate = mustUpdate;
        this.mRadius = radius;
        this.mDownlaodCallback = downlaodCallback;
    }

    @Override
    public AbstractUpdateDialog onCreate(){
        if(mMustUpdate){
            return onCreateMust();
        }
        bsDialog = new BsDialog(mContext, layoutId) {
            @Override
            public void onBindViewHolder(DialogViewHolder holder) {
                View view = holder.getConvertView();
                titleTv = view.findViewById(com.boge.update.R.id.title);
                contentTv = view.findViewById(com.boge.update.R.id.message);
                negtive = view.findViewById(com.boge.update.R.id.negtive);
                positive = view.findViewById(com.boge.update.R.id.positive);
                titleTv.setText(TextUtils.isEmpty(mTitle)?(TextUtils.isEmpty(BaseConfig.UPDATE_TITLE)?mContext.getString(R.string.update_lib_dialog_title):BaseConfig.UPDATE_TITLE):mTitle);
                contentTv.setText(BaseConfig.UPDATE_CONTENT);
                negtive.setText(TextUtils.isEmpty(mNegivteTx)?(TextUtils.isEmpty(BaseConfig.UPDATE_NEGITIVE)?mContext.getString(R.string.update_no_thanks):BaseConfig.UPDATE_NEGITIVE):mNegivteTx);
                positive.setText(TextUtils.isEmpty(mPositiveTx)?(TextUtils.isEmpty(BaseConfig.UPDATE_POSITIVE)?mContext.getString(R.string.update_sure):BaseConfig.UPDATE_POSITIVE):mPositiveTx);
                if(mRadius.getType() != 10){
                    view.setBackgroundResource(ScreenUtils.getDrawableId(mRadius.getType()));
                }
                negtive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        start();
                    }
                });
            }
        }.backgroundLight(0.5)
                .setCancelAble(true)
                .setCanceledOnTouchOutside(true);
        return this;
    }

    public AbstractUpdateDialog onCreateMust(){
        bsDialog = new BsDialog(mContext, layoutId) {
            @Override
            public void onBindViewHolder(DialogViewHolder holder) {
                View view = holder.getConvertView();
                titleTv = view.findViewById(com.boge.update.R.id.title);
                contentTv = view.findViewById(com.boge.update.R.id.message);
                positive = view.findViewById(com.boge.update.R.id.positive);
                titleTv.setText(TextUtils.isEmpty(mTitle)?(TextUtils.isEmpty(BaseConfig.UPDATE_TITLE)?mContext.getString(R.string.update_lib_dialog_title):BaseConfig.UPDATE_TITLE):mTitle);
                contentTv.setText(BaseConfig.UPDATE_CONTENT);
                if(mRadius.getType() != 10){
                    view.setBackground(mContext.getResources().getDrawable(ScreenUtils.getDrawableId(mRadius.getType())));
                }
                positive.setText(TextUtils.isEmpty(mPositiveTx)?(TextUtils.isEmpty(BaseConfig.UPDATE_POSITIVE)?mContext.getString(R.string.update_lib_update):BaseConfig.UPDATE_POSITIVE):mPositiveTx);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        start();
                    }
                });
            }
        }.backgroundLight(0.5)
                .setCancelAble(true)
                .setCanceledOnTouchOutside(false);
        return this;
    }


    protected AbstractUpdateDialog customOnCreate(final BindingCallback bindingCallback){
        this.bindingCallback = bindingCallback;
        bsDialog = new BsDialog(mContext, layoutId) {
            @Override
            public void onBindViewHolder(DialogViewHolder holder) {
                bindingCallback.bindingVh(holder);
            }
        }.backgroundLight(0.5)
                .setCancelAble(true)
                .setCanceledOnTouchOutside(true);
        return this;
    }

    public BsDialog getDialog(){
        return bsDialog;
    }

    public AbstractUpdateDialog setCancelTextColor(int var1){
        if(negtive!=null){
            negtive.setTextColor(var1);
        }
        return this;
    }

    public AbstractUpdateDialog setConfirmTextColor(int var1){
        if(positive!=null){
            positive.setTextColor(var1);
        }
        return this;
    }

    public AbstractUpdateDialog setTitle(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&titleTv!=null){
            titleTv.setText(var1);
        }
        return this;
    }

    public AbstractUpdateDialog setContent(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&contentTv!=null){
            contentTv.setText(var1);
        }
        return this;
    }

    public AbstractUpdateDialog setCancelText(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&negtive!=null){
            negtive.setText(var1);
        }
        return this;
    }

    public AbstractUpdateDialog setConfirmText(String var1){
        if(TextUtils.isEmpty(var1)){
            return this;
        }
        if(bsDialog!=null&&positive!=null){
            positive.setText(var1);
        }
        return this;
    }
    @Override
    public void show(){
        if(bsDialog!=null){
            bsDialog.dismiss();
            bsDialog.showDialog();
            return;
        }
        UpdateLog.e(TAG,mContext.getString(R.string.update_not_initialized_yet));
    }
    @Override
    public void cancel() {
        if(bsDialog!=null){
            bsDialog.dismiss();
            return;
        }
        UpdateLog.e(TAG,mContext.getString(R.string.update_not_initialized_yet));
    }

    public void start() {
        if(mMustUpdate){
            new DownloadDialog.Builder(mContext,BaseConfig.DOWNLOAD_URL,true).progress(0).radius(mRadius).downloadCallback(mDownlaodCallback).build().start();
        }else {
            new DownloadDialog.Builder(mContext, BaseConfig.DOWNLOAD_URL, BaseConfig.BACKGROUND_UPDATE).progress(0).radius(mRadius).downloadCallback(mDownlaodCallback).build().start();
        }
        cancel();
    }

    public interface BindingCallback{
        void bindingVh(DialogViewHolder holder);
    }
}
