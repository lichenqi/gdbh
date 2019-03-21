package com.guodongbaohe.app.base_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.app_status.AppStatus;
import com.guodongbaohe.app.app_status.AppStatusManager;
import com.guodongbaohe.app.common_constant.AppManager;
import com.guodongbaohe.app.receiver.NetBroadcastReceiver;
import com.guodongbaohe.app.util.NetUtil;


public class BigBaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetEvevt {
    public static NetBroadcastReceiver.NetEvevt evevt;
    /*网络类型*/
    private int netMobile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this); //添加到栈中
        evevt = this;
        inspectNet();
        if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYVLE) {
            //跳到MainActivity,让MainActivity也finish掉
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this); //从栈中移除
    }

    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(BigBaseActivity.this);
        return isNetConnect();
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;

        }
        return false;
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        this.netMobile = netMobile;
        isNetConnect();
    }

}
