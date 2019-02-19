package com.guodongbaohe.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.util.NetUtil;


/**
 * Created by Administrator on 2018/6/25.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    public NetEvevt evevt = BigBaseActivity.evevt;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            // 接口回调传过去状态的类型
            if ("null".equals(String.valueOf(netWorkState)) || "0".equals(String.valueOf(netWorkState))) {
                return;
            }
            evevt.onNetChange(netWorkState);
        }
    }

    // 自定义接口
    public interface NetEvevt {
        void onNetChange(int netMobile);
    }
}
