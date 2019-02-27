package com.guodongbaohe.app.util;

import android.os.Build;

public class PhoneVersionUtil {
    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    // 获取手机型号
    public static String getMode() {
        return Build.MODEL;
    }
}
