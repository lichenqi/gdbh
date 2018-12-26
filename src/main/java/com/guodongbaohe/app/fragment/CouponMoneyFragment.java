package com.guodongbaohe.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.TiXianRecordActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponMoneyFragment extends android.support.v4.app.Fragment {
    private View view;
    @BindView(R.id.withdraw_desposit)
    TextView withdraw_desposit;
    @BindView(R.id.et_real_name)
    EditText et_real_name;
    @BindView(R.id.et_alipay_num)
    EditText et_alipay_num;
    @BindView(R.id.et_money)
    EditText et_money;
    @BindView(R.id.tv_ketiixan_money)
    TextView tv_ketiixan_money;
    String real_name, alipay_num, money, member_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.couponmoneyfragment, container, false);

            ButterKnife.bind(this, view);
            member_id = PreferUtils.getString(getContext(), "member_id");
        }
        return view;
    }

    @OnClick({R.id.withdraw_desposit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdraw_desposit:
                real_name = et_real_name.getText().toString().trim();
                alipay_num = et_alipay_num.getText().toString().trim();
                money = et_money.getText().toString().trim();
                if (TextUtils.isEmpty(real_name)) {
                    ToastUtils.showToast(getContext(), "请输入真姓名");
                    return;
                }
                if (TextUtils.isEmpty(alipay_num)) {
                    ToastUtils.showToast(getContext(), "请输入支付宝账号");
                    return;
                }
                if (TextUtils.isEmpty(money)) {
                    ToastUtils.showToast(getContext(), "请输入要提现金额");
                    return;
                }
                getData();
                break;
        }
    }

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("realname", real_name);
        map.put("alipay", alipay_num);
        map.put("money", money);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.WITHDRAW_DSPOSIT + param)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .addHeader("APPKEY", Constant.APPKEY)
                .addHeader("x-userid", member_id)
                .addHeader("VERSION", VersionUtil.getVersionCode(getActivity().getApplicationContext()))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("提现", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                ToastUtils.showToast(getContext(), "提现成功");
                                Intent intent = new Intent(getContext(), TiXianRecordActivity.class);
                                startActivity(intent);
                            } else {
                                String result = jsonObject.getString("result");
                                ToastUtils.showToast(getContext(), result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }
}