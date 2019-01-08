package com.guodongbaohe.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;


public class FloatWindowService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyWindowManager.createWindow(XinShouJiaoChengActivity.getInstance());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
