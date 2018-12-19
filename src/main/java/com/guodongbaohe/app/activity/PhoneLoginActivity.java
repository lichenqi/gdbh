package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.InviteBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EmjoyAndTeShuUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PhoneUtils;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*微信登录手机绑定界面*/
public class PhoneLoginActivity extends BigBaseActivity {
    String avatar, member_name, city, openid, province, gender, unionid;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.ed_phone)
    EditText ed_phone;
    @BindView(R.id.ed_yan)
    EditText ed_yan;
    @BindView(R.id.et_get_yan_code)
    TextView et_get_yan_code;
    @BindView(R.id.login)
    TextView login;
    private TimeCount time = new TimeCount(60000, 1000);
    @BindView(R.id.ll_yao_parent)
    LinearLayout ll_yao_parent;
    @BindView(R.id.ed_yaoqingma)
    EditText ed_yaoqingma;
    @BindView(R.id.iv_xieyi)
    ImageView iv_xieyi;
    @BindView(R.id.ll_xieyi)
    LinearLayout ll_xieyi;
    int num;
    private boolean isXieyi = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phoneloginactivity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        avatar = intent.getStringExtra("avatar");
        member_name = intent.getStringExtra("member_name");
        city = intent.getStringExtra("city");
        openid = intent.getStringExtra("openid");
        province = intent.getStringExtra("province");
        gender = intent.getStringExtra("gender");
        unionid = intent.getStringExtra("unionid");
        num = (int) ((Math.random() * 9 + 1) * 10000000);
        if (TextUtils.isEmpty(avatar)) {
            avatar = "";
        }
        if (TextUtils.isEmpty(member_name)) {
            member_name = "";
        }
        if (TextUtils.isEmpty(city)) {
            city = "";
        }
        if (TextUtils.isEmpty(province)) {
            province = "";
        }
        if (TextUtils.isEmpty(gender)) {
            gender = "";
        }
        setGetCodeBg();
        initPhoneChange();
    }

    private void initPhoneChange() {
        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*验证手机号是否注册过*/
                if (editable.toString().length() == 11) {
                    if (PhoneUtils.isPhone(editable.toString())) {
                        checkPhoneData(editable.toString());
                    }
                }
            }
        });
    }

    String status;

    private void checkPhoneData(String phone) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("phone", phone);
        String mapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(mapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
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
                        Log.i("返回值", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            status = jsonObject.getString("status");
                            if (status.equals("0")) /*代表老用户*/ {
                                ll_yao_parent.setVisibility(View.GONE);
                                InviteBean inviteBean = GsonUtil.GsonToBean(response.toString(), InviteBean.class);
                                yaoqingma = inviteBean.getResult().getInvite_code();
                            } else if (status.equals("-1003")) {/*代表新用户*/
                                ll_yao_parent.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    String phone, yanzhengma, yaoqingma;

    @OnClick({R.id.iv_back, R.id.et_get_yan_code, R.id.login, R.id.iv_xieyi, R.id.ll_xieyi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.et_get_yan_code:
                phone = ed_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入手机号");
                    return;
                }
                getCodeData(phone);
                break;
            case R.id.login:
                phone = ed_phone.getText().toString().trim();
                yanzhengma = ed_yan.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入手机号");
                    return;
                }
                if (ll_yao_parent.getVisibility() == View.VISIBLE) {
                    yaoqingma = ed_yaoqingma.getText().toString().trim();
                    if (TextUtils.isEmpty(yaoqingma)) {
                        ToastUtils.showToast(getApplicationContext(), "请输入邀请码或邀请人手机号");
                        return;
                    }
                }
                if (TextUtils.isEmpty(yanzhengma)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入您获取的验证码");
                    return;
                }
                if (isXieyi == false) {
                    ToastUtils.showToast(getApplicationContext(), "请先阅读用户协议并勾选");
                    return;
                }
                if (TextUtils.isEmpty(openid)) {
                    ToastUtils.showToast(getApplicationContext(), "微信平台异常，请使用手机号登录");
                    return;
                }
                registerData(phone, yanzhengma);
                break;
            case R.id.iv_xieyi:
                if (isXieyi) {
                    iv_xieyi.setImageResource(R.mipmap.buxianshiyjin);
                } else {
                    iv_xieyi.setImageResource(R.mipmap.xainshiyjin);
                }
                isXieyi = !isXieyi;
                break;
            case R.id.ll_xieyi:
                Intent intent = new Intent(getApplicationContext(), XinShouJiaoChengActivity.class);
                intent.putExtra("url", PreferUtils.getString(getApplicationContext(), "agreement"));
                startActivity(intent);
                break;
        }
    }

    private void registerData(String phone, String yanzhengma) {
        loadingDialog = DialogUtil.createLoadingDialog(PhoneLoginActivity.this, "正在登录");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("openid", openid);
        map.put("unionid", unionid);
        map.put("member_name", member_name);
        map.put("gender", gender);
        map.put("province", province);
        map.put("city", city);
        map.put("phone", phone);
        map.put("words", yanzhengma);
        map.put("invite_code", yaoqingma);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        Log.i("微信注册参数", param);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.WCHATREGISTER + "?" + param)
                .addParam("avatar", avatar)
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
                        DialogUtil.closeDialog(loadingDialog);
                        Log.i("微信注册结果", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                String member_id = jsonObject.getString("result");/*用户id*/
                                getConfigurationData(member_id);
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

    Dialog loadingDialog;

    private void getCodeData(String phone) {
        loadingDialog = DialogUtil.createLoadingDialog(PhoneLoginActivity.this, "正在获取验证码...");
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
            et_get_yan_code.setClickable(false);
            et_get_yan_code.setText("重新获取" + millisUntilFinished / 1000 + "s");
            et_get_yan_code.setBackgroundResource(R.drawable.gray_invite_code);
            et_get_yan_code.setTextColor(0xff939393);
        }

        @Override
        public void onFinish() {
            et_get_yan_code.setClickable(true);
            et_get_yan_code.setText("获取验证码");
            et_get_yan_code.setBackgroundResource(R.drawable.yanzma);
            et_get_yan_code.setTextColor(0xffffffff);
        }
    }


    @Override
    protected void onDestroy() {
        if (time != null) {
            time.cancel();
        }
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
                    et_get_yan_code.setBackgroundResource(R.drawable.yanzma);
                    et_get_yan_code.setTextColor(0xffffffff);
                } else {
                    et_get_yan_code.setBackgroundResource(R.drawable.gray_invite_code);
                    et_get_yan_code.setTextColor(0xff939393);
                }
            }
        });
    }

    /*获取用户信息*/
    private void getConfigurationData(String member_id) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("field", Constant.USER_DATA_PARA);
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.APPCONFIGURATION + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), member_id))
                .addHeader("x-userid", member_id)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("用户信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                BaseUserBean bean = GsonUtil.GsonToBean(response.toString(), BaseUserBean.class);
                                if (bean == null) return;
                                BaseUserBean.BaseUserData result = bean.getResult();
                                String avatar = result.getAvatar();/*用户头像*/
                                String fans = result.getFans();/*用户下级数量*/
                                String gender = result.getGender();/*用户性别*/
                                String invite_code = result.getInvite_code();/*邀请码*/
                                String user_id = result.getMember_id();/*用户id*/
                                String member_name = result.getMember_name();/*用户名*/
                                String member_role = result.getMember_role();/*用户角色*/
                                String phone = result.getPhone();/*用户电话号码（账号）*/
                                String wechat = result.getWechat();/*用户微信号*/
                                String pwechat = result.getPwechat();/*用户父微信号（邀请人微信号）*/
                                String balance = result.getBalance();/*用户可提现余额*/
                                String credits = result.getCredits();/*盒子余额*/
                                PreferUtils.putString(getApplicationContext(), "userImg", avatar);/*存储头像*/
                                PreferUtils.putString(getApplicationContext(), "member_id", user_id);/*存储用户id*/
                                PreferUtils.putString(getApplicationContext(), "sex", gender);/*存储性别*/
                                PreferUtils.putString(getApplicationContext(), "phoneNum", phone);/*存储电话号码*/
                                if (EmjoyAndTeShuUtil.containsEmoji(member_name)) {
                                    PreferUtils.putString(getApplicationContext(), "userName", "果冻" + num);/*存储用户名*/
                                } else if (EmjoyAndTeShuUtil.isLetter(member_name) == false && EmjoyAndTeShuUtil.isDigit(member_name) == false && EmjoyAndTeShuUtil.isContainChinese(member_name) == false) {
                                    PreferUtils.putString(getApplicationContext(), "userName", "果冻" + num);/*存储用户名*/
                                } else {
                                    PreferUtils.putString(getApplicationContext(), "userName", member_name);/*存储用户名*/
                                }
                                PreferUtils.putString(getApplicationContext(), "wchatname", wechat);/*存储微信号*/
                                PreferUtils.putString(getApplicationContext(), "invite_code", invite_code);/*存储邀请码*/
                                PreferUtils.putString(getApplicationContext(), "member_role", member_role);/*存储用户等级*/
                                PreferUtils.putString(getApplicationContext(), "son_count", fans);/*存储下级个数*/
                                PreferUtils.putString(getApplicationContext(), "pwechat", pwechat);/*存储父微信号*/
                                PreferUtils.putString(getApplicationContext(), "balance", balance);/*存储可提现余额*/
                                PreferUtils.putString(getApplicationContext(), "credits", credits);/*存储盒子余额*/
                                PreferUtils.putBoolean(getApplicationContext(), "isLogin", true);
                                EventBus.getDefault().post("loginSuccess");
                                Intent intent = new Intent();
                                intent.setAction("wchatloginfinish");
                                sendBroadcast(intent);
                                finish();
                            } else {
                                ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

}
