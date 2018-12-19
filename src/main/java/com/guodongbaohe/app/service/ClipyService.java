package com.guodongbaohe.app.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ClipyService extends Service {
    private int count = 0;
    private final static String DIVIDE_RESULT = "com.intel.unit.Clipy";
    private boolean stop_state = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop_state) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(DIVIDE_RESULT);
                    String message = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                    intent.putExtra("count", message);
                    if (message != null || message != "") {
                        sendBroadcast(intent);
                    }
                    clipboardManager.setText("");
                    count++;
                    if (count == 100000) {
                        stop_state = true;
                    }
                }
//                ClipyService.this.stopSelf();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stop_state) {
            this.stopSelf();
        } else {
            this.startService(new Intent(this, ClipyService.class));
        }
    }

}
