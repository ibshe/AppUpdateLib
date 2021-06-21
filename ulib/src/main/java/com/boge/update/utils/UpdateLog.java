package com.boge.update.utils;

import android.util.Log;

import com.boge.update.common.BaseConfig;
/**
 * @Author ibshen@aliyun.com
 */
public class UpdateLog {

    public static void v(String tag, String msg) {
        if (BaseConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BaseConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BaseConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BaseConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BaseConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

}
