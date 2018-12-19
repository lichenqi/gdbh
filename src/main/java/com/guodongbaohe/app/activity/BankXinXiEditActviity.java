package com.guodongbaohe.app.activity;

import android.app.Dialog;
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

/*银行卡信息修改界面*/
public class BankXinXiEditActviity extends BaseActivity {
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_kaihu_zhihang)
    EditText et_kaihu_zhihang;
    @BindView(R.id.et_kaihu_name)
    EditText et_kaihu_name;
    @BindView(R.id.save)
    TextView save;
    String member_id;

    @Override
    public int getContainerView() {
        return R.layout.bankxinxieditactviity;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("银行卡信息修改");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
    }

    @OnClick({R.id.save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                String bank_num = et_name.getText().toString().trim();
                String kaihuhang = et_kaihu_zhihang.getText().toString().trim();
                String kaihuname = et_kaihu_name.getText().toString().trim();
                if (TextUtils.isEmpty(bank_num)) {
                    ToastUtils.showToast(getApplicationContext(), "银行卡号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(kaihuhang)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入开户支行");
                    return;
                }
                if (TextUtils.isEmpty(kaihuname)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入开户姓名");
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("bank_card", bank_num);
                map.put("bank_branch", kaihuhang);
                map.put("bank_name", kaihuname);
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
        loadingDialog = DialogUtil.createLoadingDialog(BankXinXiEditActviity.this, "正在保存...");
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
