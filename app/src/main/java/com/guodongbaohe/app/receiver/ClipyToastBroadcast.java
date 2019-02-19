package com.guodongbaohe.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ClipyToastBroadcast extends BroadcastReceiver {
    private static final String Action_Name = "com.intel.unit.Clipy";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Action_Name.equals(action)) {
            String content = intent.getStringExtra("count");
            Log.i("李晨奇剪切板", content);
        }
    }
}
