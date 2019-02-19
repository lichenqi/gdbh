package com.guodongbaohe.app.popupwindow_util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;


public class MyWindowManager {
    /**
     * 悬浮窗View的实例
     */
    private static FloatWindowView mWindow;


    /**
     * 悬浮窗View的参数
     */
    private static WindowManager.LayoutParams mWindowParams;

    /**
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createWindow(Context context) {

        @SuppressLint("WrongConstant") WindowManager windowManager = (WindowManager)
                context.getApplicationContext().getSystemService("window");
        if (mWindow == null) {
            mWindow = new FloatWindowView(context);
            if (mWindowParams == null) {
                mWindowParams = new WindowManager.LayoutParams();
                mWindowParams.x = 0;
                mWindowParams.y = 0;
                mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowParams.format = PixelFormat.RGBA_8888;
                mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            }
            mWindow.setFitsSystemWindows(true);
            mWindow.getWindowToken();

            windowManager.addView(mWindow, mWindowParams);
        }
    }
}