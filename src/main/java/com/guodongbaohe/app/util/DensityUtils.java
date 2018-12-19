package com.guodongbaohe.app.util;

import android.content.Context;

public class DensityUtils {

    public static int dip2px(Context contex, int dp) {
        float density = contex.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

}
