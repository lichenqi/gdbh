package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PhoneUtils;
import com.guodongbaohe.app.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*注册界面*/
public class RegisterActivity extends BigBaseActivity {
    String invitedcode;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.ed_phone)
    EditText ed_phone;
    @BindView(R.id.ed_code)
    EditText ed_code;
    @BindView(R.id.tv_get_code)
    TextView tv_get_code;
    @BindView(R.id.ed_password)
    EditText ed_password;
    @BindView(R.id.register)
    TextView register;
    private TimeCount time = new TimeCount(60000, 1000);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);
        ButterKnife.bind(this);
        invitedcode = getIntent().getStringExtra("invitedcode");
        setGetCodeBg();
    }

    String phone, code, password;

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_code:
                phone = ed_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入手机号");
                    return;
                }
                if (!PhoneUtils.isPhone(phone)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入正确的手机号");
                    return;
                }
                getCodeData(phone);
                break;
            case R.id.register:
                phone = ed_phone.getText().toString().trim();
                code = ed_code.getText().toString().trim();
                password = ed_password.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入手机号");
                    return;
                }
                if (!PhoneUtils.isPhone(phone)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入正确的手机号");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入您获取的验证码");
                    return;
                }
                if (!code.equals(words)) {
                    ToastUtils.showToast(getApplicationContext(), "您输入的短息验证码和该手机号收到的验证码不一致,请重新输入");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showToast(getApplicationContext(), "您输入登录密码");
                    return;
                }
                registerData(phone, code, password);
                break;
        }
    }

    private void registerData(String phone, String code, String password) {
        loadingDialog = DialogUtil.createLoadingDialog(RegisterActivity.this, "注册中...");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        map.put("checking", password);
        map.put("words", code);
        map.put("invite_code", invitedcode);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.REGISTER + "?" + mapParam)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .addHeader("APPKEY", Constant.APPKEY)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        DialogUtil.closeDialog(loadingDialog, RegisterActivity.this);
                        Log.i("注册", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog, RegisterActivity.this);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    Dialog loadingDialog;
    String words;

    private void getCodeData(String phone) {
        loadingDialog = DialogUtil.createLoadingDialog(RegisterActivity.this, "正在获取验证码...");
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.GETCODE + phone)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .addHeader("APPKEY", Constant.APPKEY)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        DialogUtil.closeDialog(loadingDialog, RegisterActivity.this);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String aReturn = jsonObject.getString("return");
                            if (aReturn.equals("0")) {
                                ToastUtils.showToast(getApplicationContext(), "短息验证码已发送至您的手机");
                                words = jsonObject.getString("words");/*验证码*/
                                time.start();
                            } else {
                                ToastUtils.showToast(getApplicationContext(), jsonObject.getString("result"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog, RegisterActivity.this);
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
            tv_get_code.setClickable(false);
            tv_get_code.setText("重新获取" + millisUntilFinished / 1000 + "s");
            tv_get_code.setBackgroundResource(R.drawable.gray_invite_code);
            tv_get_code.setTextColor(0xff939393);
        }

        @Override
        public void onFinish() {
            tv_get_code.setClickable(true);
            tv_get_code.setText("获取验证码");
            tv_get_code.setBackgroundResource(R.drawable.yanzma);
            tv_get_code.setTextColor(0xffffffff);
        }
    }


    @Override
    protected void onDestroy() {
        if (time != null) {
            time.cancel();
        }
        DialogUtil.closeDialog(loadingDialog, RegisterActivity.this);
        super.onDestroy();
    }


    private void setGetCodeBg() {
        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 11) {
                    tv_get_code.setBackgroundResource(R.drawable.yanzma);
                    tv_get_code.setTextColor(0xffffffff);
                } else {
                    tv_get_code.setBackgroundResource(R.drawable.gray_invite_code);
                    tv_get_code.setTextColor(0xff939393);
                }
            }
        });
    }
}
