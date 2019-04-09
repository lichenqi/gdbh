package com.guodongbaohe.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.GBossH5Activity;
import com.guodongbaohe.app.activity.GFriendToBossActivity;
import com.guodongbaohe.app.activity.GVipToFriendActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.MakeMoneyBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeMoneyFragment extends Fragment {
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    private View view;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.nestedscrollview)
    NestedScrollView nestedscrollview;
    /*用户头像*/
    @BindView(R.id.circleimageview)
    CircleImageView circleimageview;
    /*用户角色*/
    @BindView(R.id.user_level)
    TextView user_level;
    /*用户昵称*/
    @BindView(R.id.name)
    TextView name;
    /*升级说明*/
    @BindView(R.id.update_label)
    TextView update_label;
    /*身份说明*/
    @BindView(R.id.identity_label)
    TextView identity_label;
    /*购买返利相关*/
    @BindView(R.id.re_gmfl)
    RelativeLayout re_gmfl;
    @BindView(R.id.tv_one)
    TextView tv_one;
    @BindView(R.id.tv_one_bili)
    TextView tv_one_bili;
    /*赚取佣金相关*/
    @BindView(R.id.re_zqyj)
    RelativeLayout re_zqyj;
    @BindView(R.id.tv_two)
    TextView tv_two;
    @BindView(R.id.tv_two_bili)
    TextView tv_two_bili;
    /*果冻商学院相关*/
    @BindView(R.id.re_dgsxy)
    RelativeLayout re_dgsxy;
    @BindView(R.id.tv_three)
    TextView tv_three;
    @BindView(R.id.tv_three_bili)
    TextView tv_three_bili;
    /*多级提成相关*/
    @BindView(R.id.re_djtc)
    RelativeLayout re_djtc;
    @BindView(R.id.tv_four)
    TextView tv_four;
    @BindView(R.id.tv_four_bili)
    TextView tv_four_bili;
    /*获得宝盒相关*/
    @BindView(R.id.re_hdbh)
    RelativeLayout re_hdbh;
    @BindView(R.id.tv_five)
    TextView tv_five;
    @BindView(R.id.tv_five_bili)
    TextView tv_five_bili;
    /*晋升总裁*/
    @BindView(R.id.re_jszc)
    RelativeLayout re_jszc;
    @BindView(R.id.tv_six)
    TextView tv_six;
    @BindView(R.id.tv_six_bili)
    TextView tv_six_bili;
    /*升级按钮*/
    @BindView(R.id.tv_open_vip)
    TextView tv_open_vip;
    /*普通用户和vip用户布局*/
    @BindView(R.id.ll_user_and_vip)
    LinearLayout ll_user_and_vip;
    @BindView(R.id.tv_left_one)
    TextView tv_left_one;
    @BindView(R.id.tv_left_two)
    TextView tv_left_two;
    @BindView(R.id.tv_left_three)
    TextView tv_left_three;
    @BindView(R.id.tv_right_one)
    TextView tv_right_one;
    @BindView(R.id.tv_right_two)
    TextView tv_right_two;
    @BindView(R.id.tv_right_three)
    TextView tv_right_three;
    /*合伙人和总裁布局*/
    @BindView(R.id.re_hehuoren_buju)
    RelativeLayout re_hehuoren_buju;
    @BindView(R.id.hehuoren_and_zcai_bg)
    ImageView hehuoren_and_zcai_bg;
    @BindView(R.id.iv_income_tubiao)
    TextView iv_income_tubiao;
    @BindView(R.id.tv_total_income)
    TextView tv_total_income;
    @BindView(R.id.tv_invite_code)
    TextView tv_invite_code;
    String son_count, member_role, userImg, userName, invite_code, validity;
    Intent intent;
    /*合伙人显示*/
    @BindView(R.id.tv_hehren_view)
    TextView tv_hehren_view;
    String upgrade_partner_vips, upgrade_boss_partners;
    Context context;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "headimgChange":
                /*用户头像和用户名更改*/
                userImgAnduserNameChange();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户升级成功调用用户信息接口*/
                getUserData();
                break;
            case Constant.LOGINSUCCESS:
                /*用户登成功之后*/
                getUserData();
                userImgAnduserNameChange();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.makemoneyfragment, container, false);
            ButterKnife.bind(this, view);
            EventBus.getDefault().register(this);
            context = MyApplication.getInstance();
            /*vip要升级合伙人需要的人数*/
            upgrade_partner_vips = PreferUtils.getString(context, "upgrade_partner_vips");
            /*合伙人升级总裁需要的人数*/
            upgrade_boss_partners = PreferUtils.getString(context, "upgrade_boss_partners");
            /*用户信息接口*/
            getUserData();
            /*滑动标题渐变*/
            initNestedScrollView();
            /*初始化用户名和用户头像*/
            userImgAnduserNameChange();
            /*监听刷新控件*/
            initRefresh();
        }
        return view;
    }

    private void initRefresh() {
        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        getUserData();
                        swiperefreshlayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    /*用户头像和用户名更改*/
    private void userImgAnduserNameChange() {
        userImg = PreferUtils.getString(context, "userImg");
        userName = PreferUtils.getString(context, "userName");
        member_role = PreferUtils.getString(context, "member_role");
        son_count = PreferUtils.getString(context, "son_count");
        if (TextUtils.isEmpty(userImg)) {
            circleimageview.setImageResource(R.mipmap.user_moren_logo);
        } else {
            Glide.with(context).load(userImg).into(circleimageview);
        }
        name.setText(userName);
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            user_level.setText("总裁");
            tv_hehren_view.setVisibility(View.INVISIBLE);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            user_level.setText("合伙人");
            tv_hehren_view.setVisibility(View.VISIBLE);
            tv_hehren_view.setText("邀请" + upgrade_boss_partners + "个合伙人，获得升级总裁权限");
        } else {
            user_level.setText("VIP会员");
            tv_hehren_view.setVisibility(View.VISIBLE);
            tv_hehren_view.setText("购买智慧大脑软件，赠送合伙人权限");
        }
    }

    @OnClick({R.id.tv_open_vip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_open_vip:
                if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                    /*总裁*/
                    intent = new Intent(context, GBossH5Activity.class);
                    startActivity(intent);
                } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                    /*合伙人升级总裁*/
                    intent = new Intent(context, GFriendToBossActivity.class);
                    startActivity(intent);
                } else {
                    /*VIP升级合伙人*/
                    intent = new Intent(context, GVipToFriendActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void getUserData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(context, "member_id"));
        map.put("field", Constant.USER_DATA_PARA);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.USER_BASIC_INFO + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(context, "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, PreferUtils.getString(context, "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("用户信息数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                BaseUserBean userDataBean = GsonUtil.GsonToBean(response.toString(), BaseUserBean.class);
                                if (userDataBean != null) {
                                    BaseUserBean.BaseUserData result = userDataBean.getResult();
                                    member_role = result.getMember_role();/*用户等级*/
                                    son_count = result.getFans();/*下级数量*/
                                    invite_code = result.getInvite_code();/*邀请码*/
                                    /*合伙人到期时间*/
                                    validity = result.getValidity();
                                    PreferUtils.putString(context, "member_role", member_role);
                                    PreferUtils.putString(context, "son_count", son_count);
                                    /*其他全部数据初始化*/
                                    OtherDataChange();
                                    userImgAnduserNameChange();
                                }
                            } else {
                                initDataAndNoNet();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        initDataAndNoNet();
                    }
                });
    }


    private void OtherDataChange() {
        initImageView();
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            /*总裁*/
            memberRoleSecond();
            getTotalData();
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            /*合伙人*/
            memberRoleFirst();
            getTotalData();
        } else {
            /*赚钱接口数据*/
            getData();
            /*Vip用户*/
            haveSonData();
        }
    }

    private void initImageView() {
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            ll_user_and_vip.setVisibility(View.GONE);
            re_hehuoren_buju.setVisibility(View.VISIBLE);
            hehuoren_and_zcai_bg.setImageResource(R.mipmap.zongcai_bg);
            iv_income_tubiao.setTextColor(0xffffffff);
            tv_invite_code.setText("邀请ID: " + invite_code);
            tv_invite_code.setTextColor(0xffffffff);
            tv_total_income.setTextColor(0xffffffff);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            ll_user_and_vip.setVisibility(View.GONE);
            re_hehuoren_buju.setVisibility(View.VISIBLE);
            hehuoren_and_zcai_bg.setImageResource(R.mipmap.hehuorenbuju);
            iv_income_tubiao.setTextColor(0xff7A5B2D);
            tv_invite_code.setText("邀请ID: " + invite_code);
            tv_invite_code.setTextColor(0xff7A5B2D);
            tv_total_income.setTextColor(0xff7A5B2D);
        } else {
            ll_user_and_vip.setVisibility(View.GONE);
            re_hehuoren_buju.setVisibility(View.VISIBLE);
            hehuoren_and_zcai_bg.setImageResource(R.mipmap.vip_zhuantu);
            iv_income_tubiao.setTextColor(0xff444444);
            tv_invite_code.setText("邀请ID: " + invite_code);
            tv_invite_code.setTextColor(0xff444444);
            tv_total_income.setTextColor(0xff444444);
        }
    }

    /*初始化数据和无网络时*/
    private void initDataAndNoNet() {
        member_role = PreferUtils.getString(context, "member_role");
        invite_code = PreferUtils.getString(context, "invite_code");
        son_count = PreferUtils.getString(context, "son_count");
        OtherDataChange();
    }

    /*合伙人角色*/
    private void memberRoleFirst() {
        update_label.setText("升级总裁享受以下特权");
        identity_label.setText("立即升级， 享受最高权益");
        tv_one.setText("购物省钱");
        tv_one_bili.setText("海量优惠券");
        tv_two.setText("商品佣金");
        tv_two_bili.setText("获得90%返佣");
        tv_three.setText("官方培训");
        tv_three_bili.setText("定期开课");
        tv_four.setText("智慧大脑");
        tv_four_bili.setText("自动群发软件");
        tv_five.setText("团队奖金");
        tv_five_bili.setText("奖45%");
        tv_six.setText("总裁身份");
        tv_six_bili.setText("永不过期");
        tv_open_vip.setText("立即升级， 获得最高佣金 >");
    }

    /*总裁角色*/
    private void memberRoleSecond() {
        update_label.setText("总裁享受最高特权");
        identity_label.setText("您的身份永不过期");
        tv_one.setText("购物省钱");
        tv_one_bili.setText("海量优惠券");
        tv_two.setText("商品佣金");
        tv_two_bili.setText("获得最高佣金");
        tv_three.setText("官方培训");
        tv_three_bili.setText("定期开课");
        tv_four.setText("智慧大脑");
        tv_four_bili.setText("自动群发软件");
        tv_five.setText("团队奖金");
        tv_five_bili.setText("最高45%");
        tv_six.setText("总裁身份");
        tv_six_bili.setText("永不过期");
        tv_open_vip.setText("您已是总裁 !");
    }

    /*Vip角色*/
    private void haveSonData() {
        update_label.setText("升级合伙人享受以下特权");
        identity_label.setText("立即升级，开始赚钱");
        tv_one.setText("购物省钱");
        tv_one_bili.setText("海量优惠券");
        tv_two.setText("分享赚钱");
        tv_two_bili.setText("获得更高佣金");
        tv_three.setText("官方培训");
        tv_three_bili.setText("定期开课");
        tv_four.setText("智慧大脑");
        tv_four_bili.setText("自动群发软件");
        tv_five.setText("团队奖金");
        tv_five_bili.setText("奖35%");
        tv_six.setText("合伙人身份");
        tv_six_bili.setText("永不过期");
        tv_open_vip.setText("立即升级，获得更高佣金 >");
    }

    /*普通用户角色*/
    private void touristData() {
        update_label.setText("VIP享受以下特权");
        identity_label.setText("立即升级,开始赚钱");
        tv_one.setText("购物省钱");
        tv_one_bili.setText("海量优惠券");
        tv_two.setText("分享赚钱");
        tv_two_bili.setText("获得佣金");
        tv_three.setText("官方培训");
        tv_three_bili.setText("定期开课");
        tv_four.setText("智慧大脑");
        tv_four_bili.setText("自动群发软件");
        tv_five.setText("团队奖金");
        tv_five_bili.setText("奖25%");
        tv_six.setText("VIP身份");
        tv_six_bili.setText("永不过期");
        tv_open_vip.setText("立即升级 >");
    }

    MakeMoneyBean bean;
    String day_counts, month_counts, total_income;

    /*总裁和合伙人用的接口数据*/
    private void getTotalData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(context, "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.MAKEMONEY + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(context, "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, PreferUtils.getString(context, "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("总裁和合伙人赚钱数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                bean = GsonUtil.GsonToBean(response.toString(), MakeMoneyBean.class);
                                if (bean != null) {
                                    MakeMoneyBean.MakeMoneyData result = bean.getResult();
                                    total_income = result.getTotal_income();/*总裁 和 合伙人 用的总收入*/
                                    BigDecimal bg3 = new BigDecimal(Double.valueOf(total_income));
                                    double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    tv_total_income.setText("¥" + d_money);
                                }
                            } else {
                                ToastUtils.showToast(context, Constant.NONET);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(context, Constant.NONET);
                    }
                });
    }

    /*普通用户和vip用的接口数据*/
    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(context, "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.MAKEMONEY + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(context, "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, PreferUtils.getString(context, "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("赚钱数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                bean = GsonUtil.GsonToBean(response.toString(), MakeMoneyBean.class);
                                if (bean != null) {
                                    MakeMoneyBean.MakeMoneyData result = bean.getResult();
                                    /*vip和普通用户收入*/
                                    String total_income = result.getTotal_income();
                                    BigDecimal bg3 = new BigDecimal(Double.valueOf(total_income));
                                    double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    tv_total_income.setText("¥" + d_money);
                                    day_counts = result.getToday_vip();
                                    month_counts = result.getMonth_vip();
                                    String month_boss = result.getMonth_boss();
                                    String today_boss = result.getToday_boss();
                                    String month_partner = result.getMonth_partner();
                                    String today_partner = result.getToday_partner();
                                    if (Constant.COMMON_USER_LEVEL.contains(member_role)) {/*普通看vip数据*/
                                        tv_left_one.setText("今日有多少人成为VIP");
                                        tv_left_two.setText(day_counts + "人");
                                        tv_left_three.setText("他们都升级成为了VIP");

                                        tv_right_one.setText("本月有多少人成为VIP");
                                        tv_right_two.setText(month_counts + "人");
                                        tv_right_three.setText("他们都升级成为了VIP");
                                    } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {/*vip看合伙人数据*/
                                        tv_left_one.setText("今日有多少人成为合伙人");
                                        tv_left_two.setText(today_partner + "人");
                                        tv_left_three.setText("他们都升级成为了合伙人");

                                        tv_right_one.setText("本月有多少人成为合伙人");
                                        tv_right_two.setText(month_partner + "人");
                                        tv_right_three.setText("他们都升级成为了合伙人");
                                    }
                                }
                            } else {
                                ToastUtils.showToast(context, Constant.NONET);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(context, Constant.NONET);
                    }
                });
    }

    int parentHeight;

    private void initNestedScrollView() {
        ViewTreeObserver viewTreeObserver = title.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                parentHeight = title.getHeight();
                nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY <= 0) {
                            title.setVisibility(View.GONE);
                        } else if (scrollY >= 500) {
                            title.setVisibility(View.VISIBLE);
                            title.getBackground().setAlpha(220);
                        }
                    }
                });
            }
        });
    }

}