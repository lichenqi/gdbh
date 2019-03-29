package com.guodongbaohe.app.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/*连续点击弹土司显示问题*/
public class ToastUtils {

    private static Toast toast, centerToast;

    /*显示在屏幕下方*/
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    /*显示下屏幕中间*/
    public static void showCenterToast(Context context, String content) {
        if (centerToast == null) {
            centerToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            centerToast.setText(content);
        }
        centerToast.setGravity(Gravity.CENTER, 0, 0);
        centerToast.show();
    }
}
