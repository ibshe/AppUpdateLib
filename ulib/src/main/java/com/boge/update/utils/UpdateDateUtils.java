package com.boge.update.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author ibshen@aliyun.com
 */
public class UpdateDateUtils {

    public static String getCurDayStr(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }
}
