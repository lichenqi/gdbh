package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ChackYQ;
import com.guodongbaohe.app.bean.WchatInviteCodePeopleBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*邀请码界面*/
public class InvitationCodeActivity extends BaseActivity {
    @BindView(R.id.et_invited_code)
    EditText et_invited_code;
    @BindView(R.id.tv_sure)
    TextView tv_sure;
    String phone;

    @Override
    public int getContainerView() {
        return R.layout.invitationcodeactivity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        setMiddleTitle("邀请码");
        /*判断手机号是否锁粉过来*/
        checkPhoneIsSuoFen();
    }

    /*判断手机号是否锁粉过来*/
    private void checkPhoneIsSuoFen() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("phone", phone);
        map.put("unionid", "");
        String mapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(mapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.INVITE_CONTACT_CHECK + "?" + param)
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
                        Log.i("锁粉查询", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                Object result_obj = jsonObject.get("result");
                                if (result_obj.toString().equals("false")) {
                                    Log.i("类型", "字符串");
                                } else {
                                    Log.i("类型", "实体类");
                                    WchatInviteCodePeopleBean bean = GsonUtil.GsonToBean(response.toString(), WchatInviteCodePeopleBean.class);
                                    if (bean == null) return;
                                    String invite_code = bean.getResult().getInvite_code();
                                    et_invited_code.setText(invite_code);
                                    et_invited_code.setSelection(invite_code.toString().trim().length());
                                    et_invited_code.setInputType(InputType.TYPE_NULL);
                                }
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

    Dialog dialog;

    @OnClick({R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure:
                String code = et_invited_code.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.showToast(getApplicationContext(), "邀请码不能为空");
                    return;
                }
                verificationCodeData(code);
                break;
        }
    }

    Dialog loadingDialog;
    String result;

    private void verificationCodeData(final String code) {
        long timelineStr = System.currentTimeMillis() / 1000;
        loadingDialog = DialogUtil.createLoadingDialog(InvitationCodeActivity.this, "验证中...");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("account", code);
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        final String mapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(mapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.JY_NUMBER + "?" + param)
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
                        Log.i("邀请码", response.toString());
                        DialogUtil.closeDialog(loadingDialog);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                final ChackYQ chackYQ = GsonUtil.GsonToBean(response.toString(), ChackYQ.class);
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                dialog = new Dialog(InvitationCodeActivity.this, R.style.transparentFrameWindowStyle);
                                dialog.setContentView(R.layout.querenyaoqing);
                                Window window = dialog.getWindow();
                                window.setGravity(Gravity.CENTER | Gravity.CENTER);
                                TextView sure = (TextView) dialog.findViewById(R.id.sure);
                                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                                CircleImageView imageView = (CircleImageView) dialog.findViewById(R.id.touxiang);
                                TextView user_name = (TextView) dialog.findViewById(R.id.user_name);
                                String avatar = chackYQ.getResult().getAvatar();
                                String member_name = chackYQ.getResult().getMember_name();
                                if (TextUtils.isEmpty(avatar)) {
                                    imageView.setImageResource(R.mipmap.user_moren_logo);
                                } else {
                                    Glide.with(getApplicationContext()).load(avatar).into(imageView);
                                }
                                user_name.setText(member_name);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                sure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), FirstUserLoginActivity.class);
                                        intent.putExtra("phone", phone);
                                        intent.putExtra("invite_code", chackYQ.getResult().getInvite_code());
                                        startActivityForResult(intent, 100);
                                    }
                                });
                                dialog.show();
                            } else {
                                result = jsonObject.getString("result");
                                ToastUtils.showToast(getApplicationContext(), result);
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
