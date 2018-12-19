package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.MineShouYiBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyIncomeingActivity extends BaseActivity {
    @BindView(R.id.ketixian)
    TextView ketixian;
    @BindView(R.id.leijidaozhang)
    TextView leijidaozhang;
    @BindView(R.id.yitixian)
    TextView yitixian;
    @BindView(R.id.weijiesuan)
    TextView weijiesuan;
    @BindView(R.id.tv_yugusyi)
    TextView tv_yugusyi;
    @BindView(R.id.day_yjin)
    TextView day_yjin;
    @BindView(R.id.day_baohe)
    TextView day_baohe;
    @BindView(R.id.month_yugu)
    TextView month_yugu;
    @BindView(R.id.month_yongjin)
    TextView month_yongjin;
    @BindView(R.id.monthbaohe)
    TextView monthbaohe;
    @BindView(R.id.tv_tixian_button)
    TextView tv_tixian_button;
    /*提现记录*/
    @BindView(R.id.re_tixian_record)
    RelativeLayout re_tixian_record;
    TextView tv_right_name;
    /*常见问题*/
    @BindView(R.id.re_question)
    RelativeLayout re_question;
    /*上月结算收入*/
    @BindView(R.id.shangyuejiesuan_yuan)
    TextView shangyuejiesuan_yuan;
    /*上月商品佣金*/
    @BindView(R.id.shangyue_yongjin_yuan)
    TextView shangyue_yongjin_yuan;
    /*上月团队奖金*/
    @BindView(R.id.shangyue_tuanduijiangjin_yuan)
    TextView shangyue_tuanduijiangjin_yuan;

    @Override
    public int getContainerView() {
        return R.layout.myincomeingactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tv_right_name = findViewById(R.id.tv_right_name);
        setMiddleTitle("收入详情");
        setRightTVVisible();
        setRightTitle("明细");
        getData();
        initListener();
    }

    private void initListener() {
        tv_right_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ShouRuMingXiActivity.class);
                startActivity(intent);
            }
        });
    }

    Intent intent;

    @OnClick({R.id.tv_tixian_button, R.id.re_tixian_record, R.id.re_question})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_tixian_button:
                if (!TextUtils.isEmpty(balance)) {
                    intent = new Intent(getApplicationContext(), MoneyTiXianActivity.class);
                    intent.putExtra("total_money", balance);
                    startActivity(intent);
                }
                break;
            case R.id.re_tixian_record:
                intent = new Intent(getApplicationContext(), TiXianRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.re_question:
                intent = new Intent(getApplicationContext(), XinShouJiaoChengActivity.class);
                intent.putExtra("url", "http://app.mopland.com/question/index");
                startActivity(intent);
                break;
        }
    }

    String balance;

    /*我的收益数据*/
    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MINESHOUYIDATA + "?" + param)
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
                        Log.i("我的收益", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                MineShouYiBean mineShouYiBean = GsonUtil.GsonToBean(response.toString(), MineShouYiBean.class);
                                MineShouYiBean.MineShouYiBeanData result = mineShouYiBean.getResult();
                                MineShouYiBean.MonthData month = result.getMonth();
                                MineShouYiBean.YesterdayData yesterday = result.getYesterday();
                                MineShouYiBean.ProfitData profit = result.getProfit();
                                MineShouYiBean.LastMonth last_month = result.getLast_month();
                                balance = profit.getBalance();
                                ketixian.setText(balance);/*可提现*/
                                leijidaozhang.setText(profit.getTotal());/*累计到账*/
                                yitixian.setText(profit.getAlready());/*已提现*/
                                weijiesuan.setText(profit.getUnsettled());/*未结算*/
                                /*昨日日报*/
                                tv_yugusyi.setText(yesterday.getIncome());/*预估收入*/
                                day_yjin.setText(yesterday.getDeduct_money());/*商品佣金*/
                                day_baohe.setText(yesterday.getCredit());/*团队奖金*/
                                /*本月月报*/
                                month_yugu.setText(month.getIncome());/*预估收入*/
                                month_yongjin.setText(month.getDeduct_money());/*商品佣金*/
                                monthbaohe.setText(month.getCredit());/*团队奖金*/
                                /*上月月报*/
                                String settle_money = last_month.getSettle_money();/*商品佣金*/
                                String income = last_month.getIncome();/*结算收入*/
                                String credit = last_month.getCredit();/*团队奖金*/
                                shangyuejiesuan_yuan.setText(income);
                                shangyue_yongjin_yuan.setText(settle_money);
                                shangyue_tuanduijiangjin_yuan.setText(credit);
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
