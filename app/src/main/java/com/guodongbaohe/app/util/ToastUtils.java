package com.guodongbaohe.app.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast toast;

    /*连续点击弹土司显示问题*/
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}