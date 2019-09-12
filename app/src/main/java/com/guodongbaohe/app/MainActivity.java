package com.guodongbaohe.app;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.guodongbaohe.app.activity.BaseH5Activity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.StartActivity;
import com.guodongbaohe.app.activity.TaoBaoAndTianMaoUrlActivity;
import com.guodongbaohe.app.activity.TaoBaoWebViewActivity;
import com.guodongbaohe.app.activity.TaobaoTianMaoHolidayOfActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.app_status.AppStatus;
import com.guodongbaohe.app.app_status.AppStatusManager;
import com.guodongbaohe.app.base_activity.OriginalActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.ConfigurationBean;
import com.guodongbaohe.app.bean.NewYearsBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.bean.VersionBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.fragment.LieBiaoFenLeiFragment;
import com.guodongbaohe.app.fragment.MineFragment;
import com.guodongbaohe.app.fragment.NewHomeFragment;
import com.guodongbaohe.app.fragment.NewIdentityLimitsFragment;
import com.guodongbaohe.app.fragment.SendCircleFragment;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.CleanDataUtil;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.EmjoyAndTeShuUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.IOUtils;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends OriginalActivity {
    @BindView(R.id.fl_container)
    FrameLayout fl_container;
    @BindView(R.id.ll_home)
    LinearLayout ll_home;
    @BindView(R.id.iv_home)
    ImageView iv_home;
    @BindView(R.id.tv_home)
    TextView tv_home;
    @BindView(R.id.ll_circle)
    LinearLayout ll_circle;
    @BindView(R.id.iv_circle)
    ImageView iv_circle;
    @BindView(R.id.tv_circle)
    TextView tv_circle;
    @BindView(R.id.ll_money)
    LinearLayout ll_money;
    @BindView(R.id.iv_money)
    ImageView iv_money;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.ll_ticket)
    LinearLayout ll_ticket;
    @BindView(R.id.iv_ticket)
    ImageView iv_ticket;
    @BindView(R.id.tv_ticket)
    TextView tv_ticket;
    @BindView(R.id.ll_mine)
    LinearLayout ll_mine;
    @BindView(R.id.iv_mine)
    ImageView iv_mine;
    @BindView(R.id.tv_mine)
    TextView tv_mine;
    private List<ImageView> imageViewList = new ArrayList<>();
    private List<TextView> textViewList = new ArrayList<>();
    private int curIndex = 0;
    private int[] noseIds = {R.drawable.home_seleted, R.drawable.nine_selete, R.drawable.big_choose, R.drawable.sort_selete, R.drawable.me_seleted};
    private int[] seIds = {R.drawable.home, R.drawable.nine_no, R.drawable.choose, R.drawable.sort, R.drawable.me};
    private Fragment currentFragment;
    int size, normal_size, normal_iv_size;
    int num;
    String local_version, start_guide_to_login;
    Dialog dialog;
    /*我是李晨奇*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYVLE) {
            /*跳转到闪屏页*/
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        start_guide_to_login = PreferUtils.getString(getApplicationContext(), "start_guide_to_login");
        num = (int) ((Math.random() * 9 + 1) * 10000000);
        imageViewList.add(iv_home);
        imageViewList.add(iv_circle);
        imageViewList.add(iv_money);
        imageViewList.add(iv_ticket);
        imageViewList.add(iv_mine);

        textViewList.add(tv_home);
        textViewList.add(tv_circle);
        textViewList.add(tv_money);
        textViewList.add(tv_ticket);
        textViewList.add(tv_mine);

        size = DensityUtils.dip2px(getApplicationContext(), 58);
        normal_size = DensityUtils.dip2px(getApplicationContext(), 50);
        normal_iv_size = DensityUtils.dip2px(getApplicationContext(), 20);
        replaceFragment(curIndex);

        if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
            /*获取用户信息*/
            getConfigurationData();
            /*首页广告弹窗(2小时弹框一次)*/
            long currentTimeMillis = System.currentTimeMillis();
            String firstDialogTime = PreferUtils.getString(getApplicationContext(), "firstDialogTime");
            if (!TextUtils.isEmpty(firstDialogTime)) {
                if ((currentTimeMillis - Long.valueOf(firstDialogTime)) > 7200000) {
                    getDialogData();
                }
            } else {
                getDialogData();
            }
        }
        /*获取app配置信息*/
        getPeiZhiData();
        /*版本升级接口*/
        local_version = VersionUtil.getAndroidNumVersion(getApplicationContext());
        getVersionCodeData();

        /*android登录开关按钮*/
        if (!TextUtils.isEmpty(start_guide_to_login)) {
            if (start_guide_to_login.equals("yes") && !PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String flag_main = PreferUtils.getString(getApplicationContext(), "flag_main");
        if (TextUtils.equals(flag_main, "1")) {
            ll_home.performClick();
            PreferUtils.putString(getApplicationContext(), "flag_main", "0");
        }
    }

    private void getConfigurationData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("field", Constant.USER_DATA_PARA);
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.USER_BASIC_INFO + "?" + mapParam)
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
                        Log.i("用户信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                BaseUserBean bean = GsonUtil.GsonToBean(response.toString(), BaseUserBean.class);
                                if (bean == null) return;
                                BaseUserBean.BaseUserData result = bean.getResult();
                                String avatar = result.getAvatar();/*用户头像*/
                                String fans = result.getFans();/*用户下级数量*/
                                String gender = result.getGender();/*用户性别*/
                                String invite_code = result.getInvite_code();/*邀请码*/
                                String user_id = result.getMember_id();/*用户id*/
                                String member_name = result.getMember_name();/*用户名*/
                                String member_role = result.getMember_role();/*用户角色*/
                                String phone = result.getPhone();/*用户电话号码（账号）*/
                                String wechat = result.getWechat();/*用户微信号*/
                                String pwechat = result.getPwechat();/*用户父微信号（邀请人微信号）*/
                                String balance = result.getBalance();/*可提现余额*/
                                String credits = result.getCredits();/*盒子余额*/
                                PreferUtils.putString(getApplicationContext(), "userImg", avatar);/*存储头像*/
                                PreferUtils.putString(getApplicationContext(), "member_id", user_id);/*存储用户id*/
                                PreferUtils.putString(getApplicationContext(), "sex", gender);/*存储性别*/
                                PreferUtils.putString(getApplicationContext(), "phoneNum", phone);/*存储电话号码*/
                                if (EmjoyAndTeShuUtil.containsEmoji(member_name)) {
                                    PreferUtils.putString(getApplicationContext(), "userName", "果冻" + num);/*存储用户名*/
                                } else {
                                    PreferUtils.putString(getApplicationContext(), "userName", member_name);/*存储用户名*/
                                }
                                PreferUtils.putString(getApplicationContext(), "wchatname", wechat);/*存储微信号*/
                                PreferUtils.putString(getApplicationContext(), "invite_code", invite_code);/*存储邀请码*/
                                PreferUtils.putString(getApplicationContext(), "member_role", member_role);/*存储用户等级*/
                                PreferUtils.putString(getApplicationContext(), "son_count", fans);/*存储下级个数*/
                                PreferUtils.putString(getApplicationContext(), "pwechat", pwechat);/*存储父微信号*/
                                PreferUtils.putString(getApplicationContext(), "balance", balance);/*存储可提现余额*/
                                PreferUtils.putString(getApplicationContext(), "credits", credits);/*存储盒子余额*/
                            } else {
                                String special = jsonObject.getString("special");
                                if (!TextUtils.isEmpty(special)) {
                                    if (special.equals("logout")) {
                                        /*非法账号*/
                                        String result = jsonObject.getString("result");
                                        startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
                                        if (!TextUtils.isEmpty(result)) {
                                            ToastUtils.showToast(getApplicationContext(), result);
                                        }
                                    }
                                }
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

    int home_color;

    @OnClick({R.id.ll_home, R.id.ll_circle, R.id.ll_money, R.id.ll_ticket, R.id.ll_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:/*首页*/
                EventBus.getDefault().post(Constant.BANNER_IS_START_PLAY);
                setHomeClickColor();
                setStatusColor(0);
                setBackSize();
                curIndex = 0;
                replaceFragment(curIndex);
                break;
            case R.id.ll_circle:/*排行榜*/
                EventBus.getDefault().post(Constant.BANNER_IS_STOP_PLAY);
                setStatusColor(1);
                setBackSize();
                curIndex = 1;
                replaceFragment(curIndex);
                break;
            case R.id.ll_money:/*赚钱*/
                EventBus.getDefault().post(Constant.BANNER_IS_STOP_PLAY);
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    setStatusColor(2);
                    ViewGroup.LayoutParams layoutParams = ll_money.getLayoutParams();
                    layoutParams.height = size;
                    ll_money.setLayoutParams(layoutParams);
                    ViewGroup.LayoutParams layoutParams1 = iv_money.getLayoutParams();
                    layoutParams1.height = size;
                    layoutParams1.width = size;
                    iv_money.setLayoutParams(layoutParams1);
                    curIndex = 2;
                    replaceFragment(curIndex);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
                }
                break;
            case R.id.ll_ticket:/*发圈*/
                EventBus.getDefault().post(Constant.BANNER_IS_STOP_PLAY);
                setStatusColor(3);
                setBackSize();
                curIndex = 3;
                replaceFragment(curIndex);
                break;
            case R.id.ll_mine:/*我的*/
                EventBus.getDefault().post(Constant.BANNER_IS_STOP_PLAY);
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    setStatusColor(4);
                    setBackSize();
                    curIndex = 4;
                    replaceFragment(curIndex);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
                }
                break;
        }
    }

    /*点击首页按钮设置颜色变化*/
    String currentColor;

    private void setHomeClickColor() {
        currentColor = PreferUtils.getString(getApplicationContext(), "currentColor");
        if (!TextUtils.isEmpty(currentColor)) {
            if (currentColor.length() == 7 && currentColor.substring(0, 1).equals("#")) {
                home_color = Color.parseColor(currentColor);
            } else {
                home_color = Color.parseColor("#000000");
            }
        } else {
            home_color = Color.parseColor("#000000");
        }
    }

    private void replaceFragment(int tag) {

        for (int i = 0; i < imageViewList.size(); i++) {
            ImageView imageView = imageViewList.get(i);
            TextView textView = textViewList.get(i);
            if (i == curIndex) {
                imageView.setBackgroundResource(noseIds[i]);
                textView.setTextColor(0xff333333);
                initAnimation(imageView);
                continue;
            }
            imageView.setBackgroundResource(seIds[i]);
            textView.setTextColor(0xff666666);
        }

        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commitAllowingStateLoss();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(tag));
        if (currentFragment == null) {
            switch (tag) {
                case 0:
                    currentFragment = new NewHomeFragment();
                    break;
                case 1:
                    currentFragment = new LieBiaoFenLeiFragment();
                    break;
                case 2:
                    currentFragment = new NewIdentityLimitsFragment();
                    break;
                case 3:
                    currentFragment = new SendCircleFragment();
                    break;
                case 4:
                    currentFragment = new MineFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container, currentFragment, String.valueOf(tag)).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(currentFragment).commitAllowingStateLoss();
        }
    }

    private void initAnimation(ImageView iv) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_home_tab);
        iv.startAnimation(animation);
    }

    private void setBackSize() {
        ViewGroup.LayoutParams layoutParams = ll_money.getLayoutParams();
        layoutParams.height = normal_size;
        ll_money.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = iv_money.getLayoutParams();
        layoutParams1.height = normal_iv_size;
        layoutParams1.width = normal_iv_size;
        iv_money.setLayoutParams(layoutParams1);
    }

    private void setStatusColor(int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            /*状态栏*/
            switch (mode) {
                case 0:
                    window.setStatusBarColor(home_color);
                    break;
                case 1:
                    window.setStatusBarColor(0xff1a1a1a);
                    break;
                case 2:
                    window.setBackgroundDrawableResource(R.drawable.jianbian_zqian);
                    window.setStatusBarColor(0xff1a1a1a);
                    break;
                case 3:
                    window.setBackgroundDrawableResource(R.mipmap.faquan_bg);
                    window.setStatusBarColor(0xff1a1a1a);
                    break;
                case 4:
                    window.setStatusBarColor(0xff1a1a1a);
                    break;
            }
        }
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(int color) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String loginout = intent.getStringExtra("loginout");
        if (!TextUtils.isEmpty(loginout)) {
            if (loginout.equals("loginout")) {/*首页*/
                EventBus.getDefault().post(Constant.BANNER_IS_START_PLAY);
                setHomeClickColor();
                setStatusColor(0);
                setBackSize();
                curIndex = 0;
                replaceFragment(curIndex);
            } else if (loginout.equals("newidentitylimitsfragment")) {/*用户升级页*/
                setStatusColor(2);
                ViewGroup.LayoutParams layoutParams = ll_money.getLayoutParams();
                layoutParams.height = size;
                ll_money.setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParams1 = iv_money.getLayoutParams();
                layoutParams1.height = size;
                layoutParams1.width = size;
                iv_money.setLayoutParams(layoutParams1);
                curIndex = 2;
                replaceFragment(curIndex);
            }
        }
    }

    ConfigurationBean.PageBean http_list;

    private void getPeiZhiData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.APPPEIZHIDATA).tag(this)
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
                                http_list = bean.getPage();
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
                                /*存储应用宝上线隐藏邀请码登录注册功能*/
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

    String download, title, desc, is_update;

    /*版本升级接口*/
    private void getVersionCodeData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.VERSIONUPDATE)
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
                        Log.i("版本信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                VersionBean versionBean = GsonUtil.GsonToBean(response.toString(), VersionBean.class);
                                if (versionBean == null) return;
                                VersionBean.VersionData result = versionBean.getResult();
                                is_update = result.getIs_update();/*是否强制更新标识 no 代表随意；yes 代表强制更新*/
                                desc = result.getDesc();
                                title = result.getTitle();
                                download = result.getDownload();
                                String version = result.getVersion();
                                Integer localCode = Integer.valueOf(local_version.replace(".", "").trim());
                                if (Integer.valueOf(version) > localCode) {
                                    versionUpdataDialog();
                                }
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

    /*版本升级弹窗*/
    private void versionUpdataDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.activitydialog);
        dialog.setContentView(R.layout.version_update_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView tv_one = (TextView) dialog.findViewById(R.id.tv_one);
        TextView tv_two = (TextView) dialog.findViewById(R.id.tv_two);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        tv_one.setText(title);
        tv_two.setText(desc);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.cancel);
        if (!TextUtils.isEmpty(is_update) && is_update.equals("yes")) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                } else {
                    ToastUtils.showToast(getApplicationContext(), "可在通知栏查看下载进度");
                    downLoadApk();
                }
            }
        });
        if (!TextUtils.isEmpty(is_update) && is_update.equals("yes")) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        } else {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
        }
        dialog.show();
    }

    /*7.0兼容*/
    private void installAPK() {
        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gdbh.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), Constant.FILEPROVIDER, apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        CleanDataUtil.clearAllCache(getApplicationContext());
        startActivity(intent);
    }

    DownloadManager downloadManager;
    long downloadId;

    public void downLoadApk() {
        /*下载之前先删除之前存在的apk*/
        IOUtils.rmoveFile("gdbh.apk");
        //使用系统下载类
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(download);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedOverRoaming(false);
        //创建目录下载
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "gdbh.apk");
        // 把id保存好，在接收者里面要用
        downloadId = downloadManager.enqueue(request);
        //设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //机型适配
        request.setMimeType("application/vnd.android.package-archive");
        //通知栏显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("果冻宝盒App");
        request.setDescription("正在下载中...");
        request.setVisibleInDownloadsUi(true);
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    /*检查下载状态*/
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    Log.i("下载中", "下载中");
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    installAPK();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2699:/*apk安装权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "版本升级需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "版本升级需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "可在通知栏查看下载进度");
                    downLoadApk();
                }
                break;
        }
    }

    /*首页弹框图片数据*/
    List<NewYearsBean.ResultBean> four_iv_list;
    Dialog dialogs;
    Intent intent;
    long firstDialogTime;

    private void getDialogData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "start_pop_window");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
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
                        Log.i("当前时间首页弹窗", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                NewYearsBean bean = GsonUtil.GsonToBean(response.toString(), NewYearsBean.class);
                                if (bean == null) return;
                                four_iv_list = bean.getResult();
                                firstDialogTime = System.currentTimeMillis();
                                PreferUtils.putString(getApplicationContext(), "firstDialogTime", String.valueOf(firstDialogTime));
                                if (four_iv_list.size() > 0) {
                                    dialogs = new Dialog(MainActivity.this, R.style.transparentFrameWindowStyle);
                                    dialogs.setContentView(R.layout.g_newyears);
                                    Window window = dialogs.getWindow();
                                    window.setGravity(Gravity.CENTER | Gravity.CENTER);
                                    final ImageView sure = (ImageView) dialogs.findViewById(R.id.shouye_dialog);
                                    ImageView cancel = (ImageView) dialogs.findViewById(R.id.close_dialog_im);
                                    LinearLayout close_dialog_lv = (LinearLayout) dialogs.findViewById(R.id.close_dialog_lv);
                                    if (sure == null || cancel == null) return;
                                    Glide.with(getApplicationContext()).load(four_iv_list.get(0).getImage()).into(sure);
                                    Glide.with(getApplicationContext()).load(R.mipmap.close_dialog).into(cancel);
                                    sure.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String url = four_iv_list.get(0).getUrl();/*跳转地址*/
                                            String type = four_iv_list.get(0).getType();/*跳转类型*/
                                            String extend = four_iv_list.get(0).getExtend();/*标题*/
                                            if (!TextUtils.isEmpty(type)) {
                                                switch (type) {
                                                    case "normal":
                                                        /*会员升级类型*/
                                                        intent = new Intent(MainActivity.this, TaoBaoWebViewActivity.class);
                                                        intent.putExtra("url", url);
                                                        startActivity(intent);
                                                        break;
                                                    case "tmall":
                                                        /*淘宝天猫会场地址*/
                                                        intent = new Intent(MainActivity.this, TaoBaoAndTianMaoUrlActivity.class);
                                                        intent.putExtra("url", url);
                                                        intent.putExtra("title", extend);
                                                        startActivity(intent);
                                                        break;
                                                    case "local_goods":
                                                        /*本地商品进商品详情*/
                                                        getShopBasicData(url);
                                                        break;
                                                    case "xinshou":
                                                        /*新手教程主题*/
                                                        intent = new Intent(getApplicationContext(), XinShouJiaoChengActivity.class);
                                                        intent.putExtra("url", url);
                                                        startActivity(intent);
                                                        break;
                                                    case "app_theme":
                                                        /*app主题*/
                                                        intent = new Intent(getApplicationContext(), BaseH5Activity.class);
                                                        intent.putExtra("url", url);
                                                        startActivity(intent);
                                                        break;
                                                    case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                                                        intent = new Intent(getApplicationContext(), TaobaoTianMaoHolidayOfActivity.class);
                                                        intent.putExtra("url", url);
                                                        startActivity(intent);
                                                        break;
                                                }
                                            }
                                            dialogs.dismiss();
                                        }
                                    });
                                    close_dialog_lv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogs.dismiss();
                                        }
                                    });
                                    dialogs.show();
                                }
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

    /*商品详情头部信息*/
    private void getShopBasicData(String shopId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("goods_id", shopId);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param)
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
                            if (jsonObject.getInt("status") >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean(response.toString(), ShopBasicBean.class);
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                JumpToShopDetailUtil.start2ActivityOfHeadBean(getApplicationContext(), result);
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
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(getApplicationContext(), "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
