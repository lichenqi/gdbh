package com.guodongbaohe.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginAndRegisterActivity extends BigBaseActivity {
    @BindView(R.id.wchat_login)
    TextView wchat_login;
    @BindView(R.id.phone_login)
    TextView phone_login;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    IWXAPI iwxapi;
    String start_guide_to_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginandregisteractivity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        /*登录状态至为false，其实相当于登出状态*/
        PreferUtils.putBoolean(getApplicationContext(), "isLogin", false);
        /*android强制登录开关*/
        start_guide_to_login = PreferUtils.getString(getApplicationContext(), "start_guide_to_login");
        /*微信登录*/
        iwxapi = WXAPIFactory.createWXAPI(this, Constant.WCHATAPPID, true);
        iwxapi.registerApp(Constant.WCHATAPPID);
        initBroadReceiver();
        if (!TextUtils.isEmpty(start_guide_to_login)) {
            if (start_guide_to_login.equals("yes")) {
                iv_back.setVisibility(View.GONE);
            } else {
                iv_back.setVisibility(View.VISIBLE);
            }
        } else {
            iv_back.setVisibility(View.VISIBLE);
        }
    }

    private void initBroadReceiver() {
        // 动态注册广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("wchatloginfinish");
        registerReceiver(refreshAddressList, intentFilter);
    }


    private BroadcastReceiver refreshAddressList = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("wchatloginfinish")) {
                finish();
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(refreshAddressList);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.wchat_login, R.id.phone_login, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wchat_login:
                wchatlogin();
                break;
            case R.id.phone_login:
                Intent intent = new Intent(getApplicationContext(), CheckPhoneActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /*微信登录*/
    private void wchatlogin() {
        if (!iwxapi.isWXAppInstalled()) {
            ToastUtils.showToast(getApplicationContext(), "请先安装微信APP");
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_xb_live_state";
            iwxapi.sendReq(req);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 100) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGINSUCCESS:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!TextUtils.isEmpty(start_guide_to_login)) {
                if (start_guide_to_login.equals("yes")) {
//                    finish();
//                    System.exit(0);
                } else {
                    finish();
                }
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
