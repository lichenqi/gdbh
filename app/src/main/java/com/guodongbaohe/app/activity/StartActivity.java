package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.app_status.AppStatus;
import com.guodongbaohe.app.app_status.AppStatusManager;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.bean.ConfigurationBean;
import com.guodongbaohe.app.bean.NoticeBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.SpUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StartActivity extends AppCompatActivity {
    Intent intent;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.re_parent)
    RelativeLayout re_parent;
    private TimeCount countdownTime = new TimeCount(3000, 1000);
    boolean isFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.advertisementactivity);
        ButterKnife.bind(this);
        /*获取头部分类标题*/
        getClassicHeadTitle();
        /*获取小提示数据*/
        getNoticeData();
        /*获取app配置信息*/
        getPeiZhiData();
        isFirst = PreferUtils.getBoolean(getApplicationContext(), "isFirst");
        countdownTime.start();
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
        re_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
        time.getBackground().setAlpha(150);
    }

    private void toMainActivity() {
        if (!isFirst) {
            intent = new Intent(getApplicationContext(), GuideActivity.class);
            //app状态改为正常
            AppStatusManager.getInstance().setAppStatus(AppStatus.STATUS_NORMAL);
            startActivity(intent);
            PreferUtils.putBoolean(getApplicationContext(), "isFirst", true);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            //app状态改为正常
            AppStatusManager.getInstance().setAppStatus(AppStatus.STATUS_NORMAL);
            startActivity(intent);
        }
        finish();
    }

    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setText("跳过\n" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            toMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTime != null) {
            countdownTime.cancel();
        }
    }

    List<CommonBean.CommonResult> result_list;

    private void getClassicHeadTitle() {
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GOODS_CATES)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            Log.i("数据啊", response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                CommonBean bean = GsonUtil.GsonToBean(response.toString(), CommonBean.class);
                                if (bean == null) return;
                                result_list = bean.getResult();
                                SpUtil.putList(getApplicationContext(), "head_title_list", result_list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        Toast.makeText(getApplicationContext(), Constant.NONET, Toast.LENGTH_LONG);
                    }
                });
    }

    private void getNoticeData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "notice");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.NOTICE + param)
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
                        Log.i("小提示数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                NoticeBean bean = GsonUtil.GsonToBean(response.toString(), NoticeBean.class);
                                if (bean == null) return;
                                String title = bean.getResult().getTitle();
                                PreferUtils.putString(getApplicationContext(), "notice_title", title);
                                PreferUtils.putString(getApplicationContext(), "notice_url", bean.getResult().getUrl());
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

    private void getPeiZhiData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.APPPEIZHIDATA)
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
                        Log.i("app配置信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                ConfigurationBean bean = GsonUtil.GsonToBean(response.toString(), ConfigurationBean.class);
                                if (bean == null) return;
                                /*H5地址*/
                                ConfigurationBean.PageBean http_list = bean.getPage();
                                Gson gson = new Gson();
                                String http_list_data = gson.toJson(http_list);
                                //保存h5地址信息
                                PreferUtils.putString(getApplicationContext(), "http_list_data", http_list_data);
                                /*邀请说明*/
                                String invite_friends = bean.getResult().getInvite_friends();
                                /*最低支付宝提现金额*/
                                String min_withdraw_alipay = bean.getResult().getMin_withdraw_alipay();
                                /*最低银行卡提现金额*/
                                String min_withdraw_card = bean.getResult().getMin_withdraw_card();
                                /*比例*/
                                String tax_rate = bean.getResult().getTax_rate();
                                String share_friends_title = bean.getResult().getShare_friends_title();
                                String short_title = bean.getResult().getShort_title();
                                /*订单说明*/
                                String order_new = bean.getResult().getOrder_new();
                                /*常见问题*/
                                String question = bean.getResult().getQuestion();
                                /*新手教程*/
                                String course = bean.getResult().getCourse();
                                /*提交应用审核字段是否需要邀请码*/
                                String online_switch_android = bean.getResult().getOnline_switch_android();
                                /*vip邀请人数限制字段*/
                                String upgrade_invite_num = bean.getResult().getUpgrade_invite_num();
                                /*存储比例数据*/
                                PreferUtils.putString(getApplicationContext(), "tax_rate", tax_rate);
                                /*存储二维码信息*/
                                PreferUtils.putString(getApplicationContext(), "share_friends_title", share_friends_title);
                                /*存储邀请标题*/
                                PreferUtils.putString(getApplicationContext(), "short_title", short_title);
                                /*存储最低提现金额*/
                                PreferUtils.putString(getApplicationContext(), "min_withdraw_alipay", min_withdraw_alipay);
                                /*存储最低银行卡提现金额*/
                                PreferUtils.putString(getApplicationContext(), "min_withdraw_card", min_withdraw_card);
                                /*存储新手教程*/
                                PreferUtils.putString(getApplicationContext(), "order_new", order_new);
                                /*存储常见问题*/
                                PreferUtils.putString(getApplicationContext(), "question", question);
                                /*存储vip邀请人数字段*/
                                PreferUtils.putString(getApplicationContext(), "upgrade_invite_num", upgrade_invite_num);
                                /*新手教程*/
                                PreferUtils.putString(getApplicationContext(), "course", course);
                                /*存储分享说明*/
                                PreferUtils.putString(getApplicationContext(), "invite_friends", invite_friends);
                                PreferUtils.putString(getApplicationContext(), "online_switch_android", online_switch_android);
                                /*存储用户协议*/
                                PreferUtils.putString(getApplicationContext(), "agreement", bean.getResult().getAgreement());
                                /*存储分享说明*/
                                PreferUtils.putString(getApplicationContext(), "share_goods", bean.getResult().getShare_goods());
                                PreferUtils.putString(getApplicationContext(), "about_us", bean.getResult().getAbout_us());
                                /*存储详情是否显示优惠券*/
                                PreferUtils.putString(getApplicationContext(), "shopdetail_show_cpupon", bean.getResult().getIs_show_coupon());
                                /*存储商品首页是否显示优惠券*/
                                PreferUtils.putString(getApplicationContext(), "shop_home_show_coupon", bean.getResult().getIs_show_ratio());
                                /*存储商品详情点击分享赚和购买返弹窗显示*/
                                PreferUtils.putString(getApplicationContext(), "is_pop_window", bean.getResult().getIs_pop_window());
                                /*存储普通用户到VIP需要的邀请人数字段*/
                                PreferUtils.putString(getApplicationContext(), "upgrade_vip_invite", bean.getResult().getUpgrade_vip_invite());
                                /*对付华为 oppo上线开关*/
                                PreferUtils.putString(getApplicationContext(), "money_upgrade_switch", bean.getResult().getMoney_upgrade_switch());
                                /*商品详情页是否显示弹框*/
                                PreferUtils.putString(getApplicationContext(), "is_show_money_vip", bean.getResult().getIs_show_money_vip());
                                PreferUtils.putString(getApplicationContext(), "is_pop_window_vip", bean.getResult().getIs_pop_window_vip());
                                /*Android启动引导登录*/
                                PreferUtils.putString(getApplicationContext(), "start_guide_to_login", bean.getResult().getStart_guide_to_login());
                                /*vip要升级合伙人需要的人数*/
                                PreferUtils.putString(getApplicationContext(), "upgrade_partner_vips", bean.getResult().getUpgrade_partner_vips());
                                /*合伙人升级总裁需要的人数*/
                                PreferUtils.putString(getApplicationContext(), "upgrade_boss_partners", bean.getResult().getUpgrade_boss_partners());
                                /*令牌说明*/
                                PreferUtils.putString(getApplicationContext(), "app_token_desc", bean.getResult().getApp_token_desc());
                                /*存储首页主题活动状态*/
                                PreferUtils.putString(getApplicationContext(), "is_index_activity", bean.getResult().getIs_index_activity());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 仿返回键退出界面,但不销毁，程序仍在后台运行
//            moveTaskToBack(false); // 关键的一行代码
            return false;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            //非默认值
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

}
