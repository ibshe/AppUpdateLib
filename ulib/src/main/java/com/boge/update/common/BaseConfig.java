package com.boge.update.common;

import com.boge.update.R;
/**
 * @Author ibshen@aliyun.com
 */
public class BaseConfig {
    public static final boolean DEBUG = Boolean.parseBoolean("true");
    public static String JSON_URL = "";
    public static String DOWNLOAD_URL = "";
    public static String UPDATE_CONTENT = "";
    public static String UPDATE_TITLE = "";
    public static String UPDATE_NEGITIVE = "";
    public static String UPDATE_POSITIVE = "";
    public static boolean BACKGROUND_UPDATE = false;
    public static boolean SHOW_CANCEL_TOAST = true;
    public static int NOTIFY_ID = 666;
    public static int NOTIFICATION_ICON = R.drawable.ic_launcher;

    public static void resetConfig(){
        JSON_URL = "";
        DOWNLOAD_URL = "";
        BACKGROUND_UPDATE = false;
        NOTIFY_ID = 666;
        NOTIFICATION_ICON = R.drawable.ic_launcher;
    }
}
