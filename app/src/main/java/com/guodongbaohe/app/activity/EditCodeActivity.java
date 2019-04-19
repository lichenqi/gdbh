package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditCodeActivity extends BaseActivity {
    @BindView(R.id.ed_name)
    EditText ed_name;
    @BindView(R.id.sure)
    TextView sure;
    String invite_code;
    String member_id;
    String member_role;

    @Override
    public int getContainerView() {
        return R.layout.editcode_xml;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("修改邀请码");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        invite_code = PreferUtils.getString(getApplicationContext(), "invite_code");
        if (!TextUtils.isEmpty(invite_code)) {
            ed_name.setText(invite_code);
            ed_name.setSelection(invite_code.length());
        }
    }

    Dialog dialog;

    public void getEditCode(final String code) {
        dialog = DialogUtil.createLoadingDialog(EditCodeActivity.this, "等待中");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("code", code);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MODIFY_CODE + "?" + param)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), PreferUtils.getString(getApplicationContext(), "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        DialogUtil.closeDialog(dialog, EditCodeActivity.this);
                        Log.i("编辑结果", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                PreferUtils.putString(getApplicationContext(), "invite_code", code);/*存储邀请码*/
                                EventBus.getDefault().post(Constant.EDITUSERINFO);
                                finish();
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
                        DialogUtil.closeDialog(dialog, EditCodeActivity.this);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    @OnClick({R.id.sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sure:
                String content = ed_name.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入邀请码");
                    return;
                }
                getEditCode(content);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        DialogUtil.closeDialog(dialog, EditCodeActivity.this);
        super.onDestroy();
    }
}
