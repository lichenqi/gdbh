package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.alipay.PayResult;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ConfigurationBean;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartnerTopUpActivity extends BaseActivity {
    String member_id, son_count;
    /*升级按钮*/
    @BindView(R.id.update)
    TextView update;
    /*第一个付款金额*/
    @BindView(R.id.tv_first_money)
    TextView tv_first_money;
    /*白色按钮*/
    @BindView(R.id.iv_white)
    ImageView iv_white;
    /*邀请的人数*/
    @BindView(R.id.tv_invited_people)
    TextView tv_invited_people;
    /*第二个付款金额*/
    @BindView(R.id.tv_invited_money)
    TextView tv_invited_money;
    /*邀请好友立减*/
    @BindView(R.id.shezi_invite_people_num)
    TextView shezi_invite_people_num;

    @Override
    public int getContainerView() {
        return R.layout.partnertopupactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("合伙人升级");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        Intent intent = getIntent();
        son_count = intent.getStringExtra("son_count");
        /*获取app配置信息*/
        getAPPPeizhiData();
        tv_invited_people.setText("您已邀请了" + son_count + "人");
    }

    @OnClick({R.id.update, R.id.iv_white})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update:
                payData();
                break;
            case R.id.iv_white:
                ToastUtils.showToast(getApplicationContext(), "您未达到开通条件");
                break;
        }
    }

    JSONObject jsonObject;
    Dialog loadingDialog;
    String sn;

    private void payData() {
        loadingDialog = DialogUtil.createLoadingDialog(PartnerTopUpActivity.this, "加载中...");
        long timelineStr = System.currentTimeMillis() / 1000;
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("method", "alipay");
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.PAY_DATA + "?" + mapParam)
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
                        Log.i("支付", response.toString());
                        try {
                            jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                String amount = result.getString("trades[amount]");
                                String appid = result.getString("trades[appid]");
                                sn = result.getString("trades[sn]");
                                String username = result.getString("trades[username]");
                                String userid = result.getString("trades[userid]");
                                getOrderInfo(amount, appid, sn, userid, username);
                            } else {
                                String result = jsonObject.getString("result");
                                DialogUtil.closeDialog(loadingDialog);
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

    String resultOrderInfo;

    private void getOrderInfo(String amount, String appid, String sn, String userid, String username) {
        HashMap<String, String> map = new HashMap<>();
        map.put("trades[amount]", amount);
        map.put("trades[appid]", appid);
        map.put("trades[sn]", sn);
        map.put("trades[method]", "alipay");
        map.put("trades[userid]", userid);
        map.put("trades[username]", username);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        Log.i("参数", qianMingMapParam);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.PAY_ORDER_NO + qianMingMapParam)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        DialogUtil.closeDialog(loadingDialog);
                        Log.i("订单信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            resultOrderInfo = jsonObject.getString("result");
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(PartnerTopUpActivity.this);
                                    Map<String, String> result = alipay.payV2(resultOrderInfo, true);
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = result;
                                    alipayHandler.sendMessage(msg);
                                }
                            };
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
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

    @SuppressLint("HandlerLeak")
    private Handler alipayHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        /*校验支付成功*/
                        checkSNData();
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtils.showToast(getApplicationContext(), "支付成功");
                        Intent intent = new Intent(getApplicationContext(), PaySuccessActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showToast(getApplicationContext(), "支付失败");
                    }
                    break;
                }
            }
        }
    };

    private void checkSNData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sn", sn);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + "payment/validate?" + param)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("教研", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                    }
                });

    }

    private void getAPPPeizhiData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.APPPEIZHIDATA)
                .tag(this)
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
                        Log.i("app配置", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                ConfigurationBean bean = GsonUtil.GsonToBean(response.toString(), ConfigurationBean.class);
                                if (bean == null) return;
                                String upgrade_partner_invite = bean.getResult().getUpgrade_partner_invite();
                                String upgrade_partner = bean.getResult().getUpgrade_partner();
                                String upgrade_invite_num = bean.getResult().getUpgrade_invite_num();
                                tv_first_money.setText("¥" + upgrade_partner);
                                tv_invited_money.setText("¥" + upgrade_partner_invite);
                                shezi_invite_people_num.setText("邀请" + upgrade_invite_num + "个好友立减100元");
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
