package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAlipayDataActivity extends BaseActivity {
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_alipay_num)
    EditText et_alipay_num;
    @BindView(R.id.save)
    TextView save;
    String member_id, alipay_num, realname;

    @Override
    public int getContainerView() {
        return R.layout.editalipaydataactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("支付宝信息修改");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        Intent intent = getIntent();
        alipay_num = intent.getStringExtra("alipay_num");
        realname = intent.getStringExtra("realname");
        if (!TextUtils.isEmpty(realname)) {
             et_name.setText(realname);
             et_name.setSelection(realname.length());
        } else {
            et_name.setHint("请输入真实姓名");
        }
        if (!TextUtils.isEmpty(alipay_num)) {
            et_alipay_num.setText(alipay_num);
            et_alipay_num.setSelection(alipay_num.length());
        } else {
            et_alipay_num.setHint("请输入支付宝账号");
        }
    }

    @OnClick({R.id.save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                String name = et_name.getText().toString().trim();
                String num = et_alipay_num.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(getApplicationContext(), "真实姓名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(num)) {
                    ToastUtils.showToast(getApplicationContext(), "支付宝账号不能为空");
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("realname", name);
                map.put("alipay", num);
                try {
                    JSONObject jsonObject = new JSONObject(map.toString());
                    saveData(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    Dialog loadingDialog;

    private void saveData(String struct) {
        loadingDialog = DialogUtil.createLoadingDialog(EditAlipayDataActivity.this, "正在保存...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("struct", struct);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.EDITALIPAYDATA + "?" + param)
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
                        DialogUtil.closeDialog(loadingDialog);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                ToastUtils.showToast(getApplicationContext(), "修改成功");
                                finish();
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
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }
}
