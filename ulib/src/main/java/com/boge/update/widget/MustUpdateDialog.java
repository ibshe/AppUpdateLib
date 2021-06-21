package com.boge.update.widget;

import android.content.Context;

import com.boge.update.R;
import com.boge.update.common.RadiusEnum;

/**
 * @Author ibshen@aliyun.com
 */

public class MustUpdateDialog extends AbstractUpdateDialog {

    public MustUpdateDialog(Context context, String title, String negivteTx, String positiveTx, RadiusEnum radius,DownlaodCallback downlaodCallback) {
        super(context, title, negivteTx, positiveTx,R.layout.must_update_dialog,true,radius,downlaodCallback);
        onCreate();
    }

}
