package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.bean.ConfigurationBean;
import com.guodongbaohe.app.bean.NoticeBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.NetUtil;
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
import butterknife.OnClick;

public class GuideActivity extends BigBaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.jump)
    TextView jump;
    @BindView(R.id.llpoint)
    LinearLayout llpoint;
    private int[] guides = {R.mipmap.guide_one, R.mipmap.guide_two, R.mipmap.guide_three, R.mipmap.guide_four};
    private ImageView[] indicators;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guideactivity);
        ButterKnife.bind(this);
        indicators = new ImageView[guides.length];
        for (int i = 0; i < guides.length; i++) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_cycle_viewpager_indicator, null);
            ImageView iv = (ImageView) view.findViewById(R.id.image_indicator);
            indicators[i] = iv;
            llpoint.addView(view);
        }
        setIndicator(0);
        viewpager.setAdapter(new GuideAdapter());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == 3) {
                    jump.setVisibility(View.VISIBLE);
                } else {
                    jump.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /*获取头部分类标题*/
        getClassicHeadTitle();
        /*获取小提示数据*/
        getNoticeData();
        /*获取app配置信息*/
        getPeiZhiData();
    }

    /*设置指示器*/
    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(R.drawable.guide_unchoose);
        }
        indicators[selectedPosition % indicators.length].setBackgroundResource(R.drawable.guide_choose);
    }

    private class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return guides == null ? 0 : guides.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.guide_item, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            iv.setImageResource(guides[position]);
            container.addView(view);
            return view;
        }
    }

    @OnClick({R.id.jump})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jump:
                if (NetUtil.getNetWorkState(GuideActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
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
