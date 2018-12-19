package com.guodongbaohe.app.util;

import android.content.Context;
import android.content.pm.PackageInfo;

public class WChatApp {
    //判断是否安装微信
    public static boolean isInstallWeChat(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.tencent.mm", 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
}
