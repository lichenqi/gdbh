package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*检验手机号界面*/
public class CheckPhoneActivity extends BigBaseActivity {
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    /*是否需要邀请码标识*/
    String online_switch_android;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        DialogUtil.closeDialog(loadingDialog, CheckPhoneActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkphoneactivity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        online_switch_android = PreferUtils.getString(getApplicationContext(), "online_switch_android");
    }

    @OnClick({R.id.login, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String phonenum = phone.getText().toString().trim();
                if (TextUtils.isEmpty(phonenum)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入手机号");
                    return;
                }
                if (phonenum.length() != 11) {
                    ToastUtils.showToast(getApplicationContext(), "请输入11位手机号");
                    return;
                }
                getData(phonenum);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    Dialog loadingDialog;
    Intent intent;

    private void getData(final String phonenum) {
        loadingDialog = DialogUtil.createLoadingDialog(CheckPhoneActivity.this, "正在登录...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("phone", phonenum);
        String mapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(mapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        Log.i("验证手机号参数", "时间戳" + timelineStr + "   " + Constant.BASE_URL + Constant.CHECKPHONE + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.CHECKPHONE + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        DialogUtil.closeDialog(loadingDialog, CheckPhoneActivity.this);
                        Log.i("返回值", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                /*代表老用户*/
                                if (online_switch_android.equals("yes")) {
                                    /*不要邀请码老用户*/
                                    intent = new Intent(getApplicationContext(), NoInviteOldUserLoginActivity.class);
                                    intent.putExtra("phone", phonenum);
                                    startActivity(intent);
                                } else {
                                    /*要邀请码老用户*/
                                    intent = new Intent(getApplicationContext(), CheckYanZmaActivity.class);
                                    intent.putExtra("phone", phonenum);
                                    startActivityForResult(intent, 100);
                                }
                            } else if (status == -1003) {
                                String special = jsonObject.getString("special");
                                if (TextUtils.isEmpty(special)) {
                                    ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                                } else {
                                    if (special.equals("phone")) {
                                        /*代表新用户*/
                                        if (online_switch_android.equals("yes")) {
                                            /*不要邀请码新用户*/
                                            intent = new Intent(getApplicationContext(), NoInviteCodeNewLoginActivity.class);
                                            intent.putExtra("phone", phonenum);
                                            startActivity(intent);
                                        } else {
                                            /*要邀请码新用户*/
                                            intent = new Intent(getApplicationContext(), InvitationCodeActivity.class);
                                            intent.putExtra("phone", phonenum);
                                            startActivityForResult(intent, 100);
                                        }
                                    } else {
                                        String result = jsonObject.getString("result");
                                        ToastUtils.showToast(getApplicationContext(), result);
                                    }
                                }
                            } else {
                                String result = jsonObject.getString("result");
                                ToastUtils.showToast(getApplicationContext(), result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog, CheckPhoneActivity.this);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 100) {
            setResult(100);
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

}
