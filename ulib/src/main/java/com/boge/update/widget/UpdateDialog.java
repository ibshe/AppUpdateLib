package com.boge.update.widget;

import android.content.Context;

import com.boge.update.R;
import com.boge.update.common.RadiusEnum;

/**
 * @Author ibshen@aliyun.com
 */

public class UpdateDialog extends AbstractUpdateDialog {

    public UpdateDialog(Context context, String title, String negivteTx, String positiveTx,RadiusEnum radius,DownlaodCallback downlaodCallback) {
        super(context, title, negivteTx, positiveTx,R.layout.update_dialog_default,false,radius,downlaodCallback);
        onCreate();
    }

}
