package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ContainsEmojiEditText;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePhoneActivity extends BaseActivity {
    ImageView iv_back;
    @BindView(R.id.submit_btn)
    TextView submit_btn;
    @BindView(R.id.yzm_code)
    ContainsEmojiEditText yzm_code;
    @BindView(R.id.get_code)
    TextView get_code;
    @BindView(R.id.old_phone)
    ContainsEmojiEditText old_phone;
    String member_id;
    private TimeCount time = new TimeCount(60000, 1000);

    @Override
    public int getContainerView() {
        return R.layout.change_phone;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        String phoneNum = getIntent().getStringExtra("phoneNum");
        old_phone.setEnabled(false);
        old_phone.setFocusable(false);
        yzm_code.setEnabled(true);
        yzm_code.setFocusable(true);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        if (!TextUtils.isEmpty(phoneNum)) {
            old_phone.setText(phoneNum);
        }
        setMiddleTitle("修改手机号");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.get_code, R.id.submit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_code:
                if (!TextUtils.isEmpty(old_phone.getText().toString())) {
                    getCodeData(old_phone.getText().toString());
                    Log.i("旧手机号", old_phone.getText().toString());
                }
                break;
            case R.id.submit_btn:
                if (!TextUtils.isEmpty(yzm_code.getText().toString())) {
                    registerData(old_phone.getText().toString(), yzm_code.getText().toString());
                } else {
                    ToastUtils.showToast(ChangePhoneActivity.this, "请输入验证码");
                }
                hintKeyBoard();
                break;
        }
    }

    Dialog loadingDialog;

    private void getCodeData(String phone) {
        loadingDialog = DialogUtil.createLoadingDialog(ChangePhoneActivity.this, "正在获取验证码...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("phone", phone);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.GETCODE + "?" + mapParam)
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
                        Log.i("验证码", response.toString());
                        DialogUtil.closeDialog(loadingDialog);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int aReturn = jsonObject.getInt("status");
                            if (aReturn >= 0) {
                                ToastUtils.showToast(getApplicationContext(), "短息验证码已发送至您的手机");
                                time.start();
                            } else {
                                String result = jsonObject.getString("result");
                                if (TextUtils.isEmpty(result)) {
                                    ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                                } else {
                                    ToastUtils.showToast(getApplicationContext(), result);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            get_code.setClickable(false);
            get_code.setText("重新获取" + millisUntilFinished / 1000 + "s");
            get_code.setBackgroundResource(R.drawable.gray_invite_code);
            get_code.setTextColor(0xff939393);
        }

        @Override
        public void onFinish() {
            get_code.setClickable(true);
            get_code.setText("获取验证码");
            get_code.setBackgroundResource(R.drawable.yanzma);
            get_code.setTextColor(0xffffffff);
        }
    }

    @Override
    protected void onDestroy() {
        if (time != null) {
            time.cancel();
        }
        super.onDestroy();
    }

    private void registerData(String phone, String code) {
        loadingDialog = DialogUtil.createLoadingDialog(ChangePhoneActivity.this, "修改中...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("old_phone", phone);
        map.put("words", code);
        map.put("member_id", member_id);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.CHANGE_PHONE + "?" + param)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), member_id))
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        JSONObject jsonObject = null;
                        try {
                            DialogUtil.closeDialog(loadingDialog);
                            jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                Log.i("注册", response.toString());
                                Intent intent = new Intent(ChangePhoneActivity.this, SetNewPhoneActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtils.showToast(ChangePhoneActivity.this, jsonObject.getString("result"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive() && getCurrentFocus() != null) {
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
