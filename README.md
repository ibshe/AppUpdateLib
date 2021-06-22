# AppUpdateLib


    Appupdatelib is a simple application update framework similar to IOS style. It can be used to customize 
    update and download dialog styles, as well as customized dialog or custom toast
[中文README](https://gitee.com/zkzyjs/AppUpdateLib/blob/master/README.md) 

[博客](https://blog.csdn.net/m0_37824232/article/details/118102122) 

### renderings
    
![](http://103.45.138.168/apps/Screenshot1.jpg) 

## Usage

### 1. checkout out MagicIndicator, which contains source code and demo
### 2. import module magicindicator and add dependency:
    
```Java
implementation project(':ulib')
```
#### or

```Java
repositories {
    ...
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    ...
    implementation 'com.github.ibshe:AppUpdateLib:1.0.6' // for support lib
}
```


### 3. Use in your activiti or fragment
* One line of code for application update

```Java
new UpdateWrapper.Builder(this,mJsonUrl).build().start();
```

* main parameter

```Java
new UpdateWrapper.Builder(this,mJsonUrl)
                        .title("测试更新")//更新dialog标题
                        .negtiveText("取消")//更新dialog取消按钮
                        .radius(RadiusEnum.UPDATE_RADIUS_10)//更新和下载dialog圆角弧度同时生效
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
```

* Silent Download

```Java
new DownloadWrapper(this,mDownloadUrl,true,RadiusEnum.UPDATE_RADIUS_30).start();
```

* Download the installation package directly
```Java
new DownloadDialog.Builder(this,mDownloadUrl,false)
                        .build().start();
```

* Custom update dialog style

```Java
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
```

Use custom update dialog:

```Java
new UpdateWrapper.Builder(this,mJsonUrl)
                        .customDialog(new CustomDialog(this,"发现新版本","取消","升级",R.layout.custom_update_dialog))
                        .checkEveryday(false)
                        .radius(RadiusEnum.UPDATE_RADIUS_30)
                        .build()
                        .start();
```

* Required JSON examples
```Html
{
  "versionCode":24,
  "versionName":"1.7.8",
  "content":"增加报表导出功能",
  "minSupport":4,	
  "url":"http://103.45.138.168/apps/music_pj.apk",
  "updateTitle":"发现新版本",
  "mustUpdate":false,
  "date":"2021-06-01 09:02:10"
}
```


### Thank you for the tool class provided by chongheng.wang and caik

### Friends, more parameters please see demo, welcome to ask questions or participate in open source, contact me ibshen@aliyun.com
