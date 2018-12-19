package com.guodongbaohe.app.cliputil;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by longtv, All rights reserved.
 * -----------------------------------------------------------------
 * File: ClipService.java
 * Author: Victor
 * Date: 2018/12/3 10:08
 * Description:
 * -----------------------------------------------------------------
 */
public class ClipService extends Service implements ClipboardUtil.OnPrimaryClipChangedListener {
    private String TAG = "ClipService";
    private ClipboardUtil mClipboard;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()......");
        mClipboard = ClipboardUtil.getInstance();
        mClipboard.addOnPrimaryClipChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClipboard.removeOnPrimaryClipChangedListener(this);
    }

    @Override
    public void onPrimaryClipChanged(ClipboardManager clipboardManager) {
        Log.e(TAG, "onPrimaryClipChanged-clipboardManager.getPrimaryClip() = " + clipboardManager.getPrimaryClip().toString());
        //此处以拷贝 Intent 为例进行处理
        ClipData data = clipboardManager.getPrimaryClip();
        String mimeType = mClipboard.getPrimaryClipMimeType();
        Log.e(TAG, "onPrimaryClipChanged-mimeType = " + mimeType);
        //一般来说，收到系统 onPrimaryClipChanged() 回调时，剪贴板一定不为空
        //但为了保险起见，在这边还是做了空指针判断
        if (data == null) {
            return;
        }
        if (ClipDescription.MIMETYPE_TEXT_PLAIN.equals(mimeType)) {
//            Log.e("李晨奇剪切板", "李晨奇剪切板onPrimaryClipChanged-mimeType2 = " + mimeType);
            DataObservable.getInstance().setData(clipboardManager.getPrimaryClip());
//            if (TextUtils.isEmpty(data.getItemAt(0).getText().toString())) {
//                return;
//            }
//            final String key_word = data.getItemAt(0).getText().toString();
//            List<ClipBean> allClipList = LitePal.findAll(ClipBean.class);
//            for (ClipBean bean : allClipList) {
//                if (bean.getTitle().equals(key_word)) {
//                    return;
//                }
//            }
//            Intent intent = new Intent(getApplicationContext(), TouMingSearchDialogActivity.class);
//            intent.putExtra("key_word", key_word);
//            startActivity(intent);
        }
    }
}
