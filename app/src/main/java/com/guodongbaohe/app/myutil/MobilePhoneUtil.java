package com.guodongbaohe.app.myutil;

import android.content.Context;
import android.content.res.Resources;

public class MobilePhoneUtil {

    //获取顶部statusBar高度
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        int height = resources.getDimensionPixelSize( resourceId );
        return height;
    }

}
