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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*银行卡提现界面*/
public class BankTiXianActivity extends BaseActivity {
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.tv_total_money)
    TextView tv_total_money;
    @BindView(R.id.et_bank_num)
    EditText et_bank_num;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.edit_xinxi)
    TextView edit_xinxi;
    @BindView(R.id.et_input_money)
    EditText et_input_money;
    @BindView(R.id.withdraw_desposit)
    TextView withdraw_desposit;
    Intent intent;
    String member_id, min_withdraw_card;
    Unregistrar mUnregistrar;
    /*开户支行输入*/
    @BindView(R.id.et_kaihuzhihang)
    EditText et_kaihuzhihang;
    /*全部提现按钮*/
    @BindView(R.id.all_tixian)
    TextView all_tixian;
    String total_money;
    /*小提示*/
    @BindView(R.id.tv_tishi)
    TextView tv_tishi;

    @Override
    public int getContainerView() {
        return R.layout.banktixianactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        /*监听键盘开关状态*/
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                isKeyBoardState(isOpen);
            }
        });
        /*设置软键盘为关闭*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        min_withdraw_card = PreferUtils.getString(getApplicationContext(), "min_withdraw_card");
        Intent intent = getIntent();
        total_money = intent.getStringExtra("total_money");
        setMiddleTitle("银行卡提现");
        tv_total_money.setText("¥" + total_money);
        et_input_money.setHint("最低提现金额" + min_withdraw_card + "元");
        tv_tishi.setText("单笔金额达到" + min_withdraw_card + "及以上");
    }

    @Override
    protected void onResume() {
        /*账户信息*/
        getMyPayData();
        super.onResume();
    }

    String bank_branch, bank_card, bank_name;

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

    private void setNameInputStatus() {
        if (TextUtils.isEmpty(bank_card)) {
            et_bank_num.setHint("请输入银行卡号");
            et_bank_num.setFocusable(true);
        } else {
            et_bank_num.setText(bank_card);
            et_bank_num.setFocusable(false);
        }
        if (TextUtils.isEmpty(bank_name)) {
            et_name.setHint("请输入开户名");
            et_name.setFocusable(true);
        } else {
            et_name.setText(bank_name);
            et_name.setFocusable(false);
        }
        if (TextUtils.isEmpty(bank_branch)) {
            et_kaihuzhihang.setHint("请输入开户支行名称");
            et_kaihuzhihang.setFocusable(true);
        } else {
            et_kaihuzhihang.setText(bank_branch);
            et_kaihuzhihang.setFocusable(false);
        }
    }

    @OnClick({R.id.withdraw_desposit, R.id.edit_xinxi, R.id.all_tixian})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdraw_desposit:
                String bank_num = et_bank_num.getText().toString().trim();
                String kaihu_name = et_name.getText().toString().trim();
                String input_money = et_input_money.getText().toString().trim();
                String kaihuhang = et_kaihuzhihang.getText().toString().trim();
                if (TextUtils.isEmpty(bank_num)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入银行卡卡号");
                    return;
                }
                if (TextUtils.isEmpty(kaihuhang)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入开户支行名称");
                    return;
                }
                if (TextUtils.isEmpty(kaihu_name)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入开户名");
                    return;
                }
                if (TextUtils.isEmpty(input_money)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入提现金额");
                    return;
                }
                if (Double.valueOf(input_money) < Double.valueOf(min_withdraw_card)) {
                    ToastUtils.showToast(getApplicationContext(), "银行卡提现金额不能小于" + min_withdraw_card + "元");
                    return;
                }
                if (Double.valueOf(input_money) > Double.valueOf(total_money)) {
                    ToastUtils.showToast(getApplicationContext(), "银行卡提现金额不能大于当前账户总额");
                    return;
                }
                getData(bank_num, kaihu_name, input_money);
                break;
            case R.id.edit_xinxi:
                intent = new Intent(getApplicationContext(), BankXinXiEditActviity.class);
                startActivity(intent);
                break;
            case R.id.all_tixian:
                et_input_money.setText(total_money);
                et_input_money.setSelection(total_money.length());
                break;
        }
    }

    Dialog loadingDialog;

    private void getData(String bank_num, String kaihu_name, String input_money) {
        loadingDialog = DialogUtil.createLoadingDialog(BankTiXianActivity.this, "正在申请...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("money", input_money);
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
                        Log.i("银行卡提现", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                EventBus.getDefault().post(Constant.TIIXANSUCCESS);
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
        super.onDestroy();
    }
}
