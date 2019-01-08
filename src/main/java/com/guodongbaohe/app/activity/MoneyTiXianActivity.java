package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.MyWalletBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.keyboardutil.KeyboardVisibilityEvent;
import com.guodongbaohe.app.keyboardutil.KeyboardVisibilityEventListener;
import com.guodongbaohe.app.keyboardutil.Unregistrar;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoneyTiXianActivity extends BaseActivity {
    TextView tv_right_name;
    Unregistrar mUnregistrar;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    /*可提现金额*/
    @BindView(R.id.tv_total_money)
    TextView tv_total_money;
    /*待审核提现金额*/
    @BindView(R.id.tv_wait_money)
    TextView tv_wait_money;
    /*真实姓名输入*/
    @BindView(R.id.et_name)
    EditText et_name;
    /*支付宝账号输入*/
    @BindView(R.id.et_alipay_num)
    EditText et_alipay_num;
    /*修改信息按钮*/
    @BindView(R.id.edit_xinxi)
    TextView edit_xinxi;
    /*输入提现金额*/
    @BindView(R.id.et_input_money)
    EditText et_input_money;
    /*全部提现按钮*/
    @BindView(R.id.all_tixian)
    TextView all_tixian;
    /*提现按钮*/
    @BindView(R.id.withdraw_desposit)
    TextView withdraw_desposit;
    String member_id, total_money;
    /*最低支付宝提现金额，最低银行卡提现金额*/
    String min_withdraw_alipay, min_withdraw_card;

    @Override
    public int getContainerView() {
        return R.layout.moneytixianactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        /*监听键盘开关状态*/
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyBoardState(isOpen);
            }
        });
        /*设置软键盘为关闭*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setMiddleTitle("支付宝提现");
        setRightTitle("提现记录");
        setRightTVVisible();
        tv_right_name = (TextView) findViewById(R.id.tv_right_name);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        min_withdraw_alipay = PreferUtils.getString(getApplicationContext(), "min_withdraw_alipay");
        min_withdraw_card = PreferUtils.getString(getApplicationContext(), "min_withdraw_card");
        total_money = getIntent().getStringExtra("total_money");
        tv_total_money.setText("¥" + total_money);
        tv_right_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TiXianRecordActivity.class);
                startActivity(intent);
            }
        });
        et_input_money.setHint("最低提现金额" + min_withdraw_alipay + "元");
    }

    @Override
    protected void onResume() {
        /*账户信息*/
        getMyPayData();
        /*获取待审核提现金额*/
        getWaitMoney();
        /*获取app配置信息是为了获取金额*/
        getBalanceData();
        super.onResume();
    }

    private void getBalanceData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("field", "balance,credits");
        String param = ParamUtil.getQianMingMapParam(map);
        final String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.APPCONFIGURATION + "?" + mapParam)
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
                        Log.i("用户余额", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                BaseUserBean bean = GsonUtil.GsonToBean(response.toString(), BaseUserBean.class);
                                if (bean == null) return;
                                total_money = bean.getResult().getBalance();
                                String credits = bean.getResult().getCredits();
                                tv_total_money.setText("¥" + total_money);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                });
    }

    private void getWaitMoney() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.GETWAITMONEY + "?" + mapParam)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                String result = jsonObject.getString("result");
                                tv_wait_money.setText("¥" + result);
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

    String alipay, realname, bank_branch, bank_card, bank_name;

    private void getMyPayData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.MY_WALLE_DATA + "?" + mapParam)
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
                        Log.i("账号信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                MyWalletBean bean = GsonUtil.GsonToBean(response.toString(), MyWalletBean.class);
                                if (bean == null) return;
                                MyWalletBean.MyWalletData result = bean.getResult();
                                alipay = result.getAlipay();
                                realname = result.getRealname();
                                bank_branch = result.getBank_branch();
                                bank_card = result.getBank_card();
                                bank_name = result.getBank_name();
                                setNameInputStatus();
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

    /*设置姓名输入框状态*/
    private void setNameInputStatus() {
        if (TextUtils.isEmpty(realname)) {
            et_name.setHint("请输入简体中文");
            et_name.setFocusable(true);
        } else {
            et_name.setText(realname);
            et_name.setFocusable(false);
        }
        if (TextUtils.isEmpty(alipay)) {
            et_alipay_num.setHint("请输入正确的支付宝账号");
            et_alipay_num.setFocusable(true);
        } else {
            et_alipay_num.setText(alipay);
            et_alipay_num.setFocusable(false);
        }
    }


    @OnClick({R.id.withdraw_desposit, R.id.edit_xinxi, R.id.all_tixian})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdraw_desposit:
                String name = et_name.getText().toString().trim();
                String alipay_num = et_alipay_num.getText().toString().trim();
                String tiixan_money = et_input_money.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(getApplicationContext(), "真实姓名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(alipay_num)) {
                    ToastUtils.showToast(getApplicationContext(), "支付宝账号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(tiixan_money)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入提现金额");
                    return;
                }
                if (Double.valueOf(tiixan_money) < Double.valueOf(min_withdraw_alipay)) {
                    ToastUtils.showToast(getApplicationContext(), "提现金额不能小于" + min_withdraw_alipay + "元");
                    return;
                }
                if (Double.valueOf(tiixan_money) > Double.valueOf(total_money)) {
                    ToastUtils.showToast(getApplicationContext(), "提现金额不能超过当前账户可提现余额");
                    return;
                }
                if (Double.valueOf(tiixan_money) >= Double.valueOf(min_withdraw_card)) {
                    intent = new Intent(getApplicationContext(), BankTiXianActivity.class);
                    intent.putExtra("total_money", total_money);
                    startActivity(intent);
                } else {
                    getData(name, alipay_num, tiixan_money);
                }
                break;
            case R.id.edit_xinxi:
                intent = new Intent(getApplicationContext(), EditAlipayDataActivity.class);
                startActivity(intent);
                break;
            case R.id.all_tixian:
                et_input_money.setText(total_money);
                et_input_money.setSelection(total_money.length());
                break;
        }
    }

    Intent intent;
    Dialog loadingDialog;

    private void getData(String real_name, String alipay_num, String money) {
        loadingDialog = DialogUtil.createLoadingDialog(MoneyTiXianActivity.this, "正在申请...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("money", money);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.WITHDRAW_DSPOSIT + param)
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
                        Log.i("提现", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                ToastUtils.showToast(getApplicationContext(), "提现成功");
                                EventBus.getDefault().post(Constant.TIIXANSUCCESS);
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

    private void isKeyBoardState(boolean isOpen) {
        if (isOpen) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //设置ScrollView滚动到顶部
                    // scrollView.fullScroll(ScrollView.FOCUS_UP);
                    //设置ScrollView滚动到顶部
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        mUnregistrar.unregister();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.TIIXANSUCCESS:
                finish();
                break;
        }
    }
}
