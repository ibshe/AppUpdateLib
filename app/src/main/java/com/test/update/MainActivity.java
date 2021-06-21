package com.test.update;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.boge.update.DownloadWrapper;
import com.boge.update.UpdateWrapper;
import com.boge.update.common.RadiusEnum;
import com.boge.update.common.BaseConfig;
import com.boge.update.entity.VersionModel;
import com.boge.update.utils.ScreenUtils;
import com.boge.update.widget.AbstractUpdateDialog;
import com.boge.update.widget.DialogViewHolder;
import com.boge.update.widget.DownlaodCallback;
import com.boge.update.widget.DownloadDialog;

import org.json.JSONException;

/**
 * 1、更新内容带#自动换行
 * 2、设置DownlaodCallback回调后，toast不展示；如需展示默认的toast，不设置DownlaodCallback即可
 * @Author ibshen@aliyun.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String mJsonUrl = "http://103.45.138.168/apps/app-update.json";
    private String mDownloadUrl = "http://103.45.138.168/apps/music_pj.apk";
    private VersionModel mModel;
    private String mJson = "{\n" +
            "  'versionCode':24,\n" +
            "  'versionName':'1.2.4',\n" +
            "  'content':'1、增加XXX功能#2、修复已知bug',\n" +
            "  'minSupport':4,\n" +
            "  'url':'http://103.45.138.168/apps/music_pj.apk',\n" +
            "  'updateTitle':'发现新版本',\n" +
            "  'mustUpdate':false,\n" +
            "  'date':'2021-06-01 09:02:10'\n" +
            "}";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.update_local_json).setOnClickListener(this);
        findViewById(R.id.must_update).setOnClickListener(this);
        findViewById(R.id.custom_update).setOnClickListener(this);
        findViewById(R.id.silence_download).setOnClickListener(this);
        findViewById(R.id.start_download).setOnClickListener(this);
        findViewById(R.id.custom_download_progress).setOnClickListener(this);
        findViewById(R.id.custom_download).setOnClickListener(this);
        mModel = parse(mJson);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.update:
                new UpdateWrapper.Builder(this,mJsonUrl)
                        .title("测试更新")//更新dialog标题
                        .negtiveText("取消")//更新dialog取消按钮
                        .radius(RadiusEnum.UPDATE_RADIUS_10)//更新和下载圆角弧度
                        .positiveText("立即升级")//更新dialog确定按钮
                        .checkEveryday(false)//默认false 立即下载,true 每天最多检查一次。如今日已检查，则不再检查
                        .showNetworkErrorToast(true)//无网络提示
                        .showNoUpdateToast(true)//无更新提示
                        .isPost(false)//检查更新请求协议是否为POST，默认GET
                        .isMustUpdate(false)//是否强制更新
                        .backgroundDownload(false)//是否后台下载
                        .model(null)//非本地实体，不传默认为null
                        .downloadCallback(new DownlaodCallback() {//下载状态回调
                            @Override
                            public void callback(int code, String message) {
                                //code 1、后台下载；2、取消下载；3、下载完成；4、下载失败；
                                //Code 1. Background download; 2. Cancel the download; 3. Download completed; 4. Download failed;
                                Log.i(TAG,message);
                            }
                        })
                        .updateCallback(new UpdateWrapper.UpdateCallback() {//获取远端信息回调
                            @Override
                            public void res(VersionModel model, boolean hasNewVersion) {
                                Log.i(TAG,model.toString());
                            }
                        })
                        .build()
                        .start();
                break;
            case R.id.update_local_json:
                new UpdateWrapper.Builder(this,null)
                        .model(mModel)//本地更新传实体，本地实体url优先级 > url
                        .radius(RadiusEnum.UPDATE_RADIUS_10)
                        .checkEveryday(false)
                        .downloadCallback(null)//是否回调下载状态，默认null且使用系统toast提示
                        .build()
                        .start();
                break;
            case R.id.must_update:
                new UpdateWrapper.Builder(this,mJsonUrl)
                        .isMustUpdate(true)
                        .radius(RadiusEnum.UPDATE_RADIUS_10)
                        .build()
                        .start();
                break;
            case R.id.custom_update:
                new UpdateWrapper.Builder(this,mJsonUrl)
                        .customDialog(new CustomDialog(this,"张三","李四","王五",R.layout.custom_update_dialog))
                        .checkEveryday(false)
                        .radius(RadiusEnum.UPDATE_RADIUS_30)
                        .build()
                        .start();
                break;
            case R.id.silence_download:
                new DownloadWrapper(this,mDownloadUrl,true,RadiusEnum.UPDATE_RADIUS_30)
                        .start();
                break;
            case R.id.start_download:
                new DownloadWrapper(this,mDownloadUrl,false,RadiusEnum.UPDATE_RADIUS_10)
                        .start();
                break;
            case R.id.custom_download:
                new DownloadDialog.Builder(this,mDownloadUrl,false)
                        .radius(RadiusEnum.UPDATE_RADIUS_10)
                        .build().start();
                break;
            case R.id.custom_download_progress:
                new DownloadDialog.Builder(this,mDownloadUrl,false)
                        .downloadCallback(new DownlaodCallback() {
                            @Override
                            public void callback(int code, String message) {
                            }
                        })
                        .progress(85).build();
                break;
        }
    }

    class CustomDialog extends AbstractUpdateDialog {
        public CustomDialog(Context context, final String title, final String negivteTx, final String positiveTx, int layoutId) {
            super(context, title, negivteTx, positiveTx, layoutId, false, RadiusEnum.UPDATE_RADIUS_10, new DownlaodCallback() {
                @Override
                public void callback(int code, String message) {
                    Log.i(TAG,message);
                }
            });
            this.customOnCreate(new BindingCallback() {
                @Override
                public void bindingVh(DialogViewHolder holder) {
                    View view = holder.getConvertView();
                    view.setBackgroundResource(ScreenUtils.getDrawableId(RadiusEnum.UPDATE_RADIUS_30.getType()));
                    titleTv = view.findViewById(R.id.title);
                    contentTv = view.findViewById(R.id.message);
                    negtive = view.findViewById(R.id.negtive);
                    positive = view.findViewById(R.id.positive);
                    titleTv.setText(title);
                    contentTv.setText(BaseConfig.UPDATE_CONTENT);
                    negtive.setText(negivteTx);
                    positive.setText(positiveTx);
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
            });
        }
    }

    private VersionModel parse(String json){
        try {
            return new VersionModel().parse(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
