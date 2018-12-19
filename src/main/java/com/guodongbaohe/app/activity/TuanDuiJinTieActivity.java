package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.DecimalDigitsInputFilter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TuanDuiJinTieActivity extends BaseActivity {
    @BindView(R.id.withdraw_desposit)
    TextView withdraw_desposit;
    @BindView(R.id.all_income)
    TextView all_income;
    @BindView(R.id.et_input_money)
    EditText et_input_money;
    @BindView(R.id.tv_total_money)
    TextView tv_total_money;
    String credits;

    @Override
    public int getContainerView() {
        return R.layout.tuanduijintieactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("团队奖金");
        credits = PreferUtils.getString(getApplicationContext(), "credits");
        tv_total_money.setText("¥" + credits);
        et_input_money.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(1)});
    }

    @OnClick({R.id.withdraw_desposit, R.id.all_income})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.withdraw_desposit:
                String trim = et_input_money.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入团队奖金");
                    return;
                }
                if (Double.valueOf(trim) < 1) {
                    ToastUtils.showToast(getApplicationContext(), "最低团队奖金数不能小于1");
                    return;
                }
                if (Double.valueOf(trim) > Double.valueOf(credits)) {
                    ToastUtils.showToast(getApplicationContext(), "输入的团队奖金不能超过当前账户总数");
                    return;
                }
                payData(trim);
                break;
            case R.id.all_income:
                et_input_money.setText(credits);
                et_input_money.setSelection(credits.length());
                break;
        }
    }

    private void payData(String trim) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("ex_credit", trim);
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.BAOHETOYONGJIN + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String result = jsonObject.getString("result");
                            ToastUtils.showToast(getApplicationContext(), result);
                            if (jsonObject.getInt("status") >= 0) {
                                /*获取最新的团队信息*/
                                EventBus.getDefault().post(Constant.TIIXANSUCCESS);
                                finish();
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
