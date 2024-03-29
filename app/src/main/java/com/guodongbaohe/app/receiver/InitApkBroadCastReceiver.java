package com.guodongbaohe.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.guodongbaohe.app.util.IOUtils;

public class InitApkBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            IOUtils.rmoveFile("gdbh.apk");
        }

        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            IOUtils.rmoveFile("gdbh.apk");
            System.out.println("");
        }

        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            IOUtils.rmoveFile("gdbh.apk");
        }
    }
}

