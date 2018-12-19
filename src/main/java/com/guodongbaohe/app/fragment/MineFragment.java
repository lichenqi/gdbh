package com.guodongbaohe.app.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.AboutUsActivity;
import com.guodongbaohe.app.activity.MoneyTiXianActivity;
import com.guodongbaohe.app.activity.MyIncomeingActivity;
import com.guodongbaohe.app.activity.MyOrderActivity;
import com.guodongbaohe.app.activity.MyTeamActivity;
import com.guodongbaohe.app.activity.PersonalActivity;
import com.guodongbaohe.app.activity.TaoBaoH5Activity;
import com.guodongbaohe.app.activity.TuanDuiJinTieActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.bean.MineDataBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DataCleanManager;
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

public class MineFragment extends Fragment {
    private View view;
    @BindView(R.id.re_clean_cache)
    RelativeLayout re_clean_cache;
    @BindView(R.id.tv_cache_size)
    TextView tv_cache_size;
    @BindView(R.id.circleimageview)
    CircleImageView circleimageview;
    Intent intent;
    @BindView(R.id.re_order)
    RelativeLayout re_order;
    @BindView(R.id.re_aboutus)
    RelativeLayout re_aboutus;
    @BindView(R.id.re_my_department)
    RelativeLayout re_my_department;
    @BindView(R.id.re_incomeing)
    RelativeLayout re_incomeing;
    @BindView(R.id.re_invite_award)
    RelativeLayout re_invite_award;
    private String userImg, userName, member_role, invite_code, son_count, balance, pwechat, credits;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.iv_user_level)
    TextView iv_user_level;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_fuzhi_anniu)
    TextView tv_fuzhi_anniu;
    @BindView(R.id.tv_total_show)
    TextView tv_total_show;
    @BindView(R.id.tv_today_yongjin_show)
    TextView tv_today_yongjin_show;
    @BindView(R.id.tv_daozhang)
    TextView tv_daozhang;
    @BindView(R.id.tv_ketiixan)
    TextView tv_ketiixan;
    @BindView(R.id.re_user_bg)
    RelativeLayout re_user_bg;
    /*可提现*/
    @BindView(R.id.re_withdraw_deposit)
    RelativeLayout re_withdraw_deposit;
    /*邀请人微信*/
    @BindView(R.id.tv_pwechat)
    TextView tv_pwechat;
    @BindView(R.id.re_wechat)
    RelativeLayout re_wechat;
    ClipboardManager cm;
    ClipData mClipData;
    @BindView(R.id.re_tuandui)
    RelativeLayout re_tuandui;
    @BindView(R.id.tv_tuandui)
    TextView tv_tuandui;
    @BindView(R.id.iv_set)
    ImageView iv_set;
    @BindView(R.id.re_xinshou_jiaocheng)
    RelativeLayout re_xinshou_jiaocheng;
    @BindView(R.id.re_question)
    RelativeLayout re_question;
    @BindView(R.id.re_taobao_gwuche)
    RelativeLayout re_taobao_gwuche;
    @BindView(R.id.re_taobao_order)
    RelativeLayout re_taobao_order;
    private ClipBean clipBean;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    AlibcLogin alibcLogin;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.mine_fragment, container, false);
            ButterKnife.bind(this, view);
            EventBus.getDefault().register(this);
            alibcLogin = AlibcLogin.getInstance();
            /*清理缓存*/
            getCacheData();
            /*初始化用户一些基本数据*/
            initView();
            /*我的界面接口数据*/
            getMineData();
            /*刷新控件*/
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
                        /*获取用户基本信息*/
                        getUserData();
                        getMineData();
                        swiperefreshlayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    BigDecimal bg3;

    /*我的界面相关数据*/
    private void getMineData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getContext(), "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MINE_DATA + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getContext(), "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), PreferUtils.getString(getContext(), "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("我的数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                MineDataBean mineDataBean = GsonUtil.GsonToBean(response.toString(), MineDataBean.class);
                                if (mineDataBean != null) {
                                    MineDataBean.MineData result = mineDataBean.getResult();
                                    String total = result.getTotal();/*累计总收入*/
                                    String month = result.getMonth();/*本月预估收入*/
                                    String today = result.getToday();/*今日预估收入*/
                                    bg3 = new BigDecimal(Double.valueOf(total));
                                    double total_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    tv_total_show.setText(String.valueOf(total_money));
                                    bg3 = new BigDecimal(Double.valueOf(month));
                                    double month_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    tv_today_yongjin_show.setText(String.valueOf(month_money));
                                    bg3 = new BigDecimal(Double.valueOf(today));
                                    double today_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    tv_daozhang.setText(String.valueOf(today_money));
                                }
                            } else {
                                ToastUtils.showToast(getContext(), Constant.NONET);
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

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGINSUCCESS:
                getMineData();
                initView();
                break;
            case "headimgChange":
                initView();
                break;
            case Constant.TIIXANSUCCESS:
                /*专门查询用户余额和团队津贴接口*/
                getBalanceData();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                initView();
                break;
        }
    }

    private void initView() {
        userImg = PreferUtils.getString(getContext(), "userImg");
        userName = PreferUtils.getString(getContext(), "userName");
        member_role = PreferUtils.getString(getContext(), "member_role");
        invite_code = PreferUtils.getString(getContext(), "invite_code");
        son_count = PreferUtils.getString(getContext(), "son_count");
        balance = PreferUtils.getString(getContext(), "balance");
        pwechat = PreferUtils.getString(getContext(), "pwechat");
        credits = PreferUtils.getString(getContext(), "credits");
        if (TextUtils.isEmpty(userImg)) {
            circleimageview.setImageResource(R.mipmap.user_moren_logo);
        } else {
            Glide.with(getContext()).load(userImg).into(circleimageview);
        }
        tv_name.setText(userName);
        if (member_role.equals("2")) {
            iv_user_level.setText("总裁");
        } else if (member_role.equals("1")) {
            iv_user_level.setText("合伙人");
        } else {
            if (son_count.equals("0")) {
                iv_user_level.setText("普通会员");
            } else {
                iv_user_level.setText("VIP");
            }
        }
        tv_id.setText("邀请ID: " + invite_code);
        tv_ketiixan.setText("¥" + balance);
        tv_tuandui.setText("¥" + credits);
        tv_pwechat.setText(pwechat);
    }

    private void getCacheData() {
        try {
            String totalCacheSize = DataCleanManager.getTotalCacheSize(getContext());
            bg3 = new BigDecimal(Double.valueOf(totalCacheSize) / 10);
            double cache = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            tv_cache_size.setText(String.valueOf(cache));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.re_clean_cache, R.id.circleimageview, R.id.re_order, R.id.re_aboutus, R.id.re_my_department, R.id.re_tuandui,
            R.id.re_incomeing, R.id.re_invite_award, R.id.tv_fuzhi_anniu, R.id.re_user_bg, R.id.re_withdraw_deposit,
            R.id.re_wechat, R.id.iv_set, R.id.re_xinshou_jiaocheng, R.id.re_question, R.id.re_taobao_gwuche, R.id.re_taobao_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_clean_cache:
                cleanCacheDialog();
                break;
            case R.id.circleimageview:
                intent = new Intent(getContext(), PersonalActivity.class);
                startActivity(intent);
                break;
            case R.id.re_order:
                intent = new Intent(getContext(), MyOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.re_xinshou_jiaocheng:
                intent = new Intent(getContext(), XinShouJiaoChengActivity.class);
                intent.putExtra("url", PreferUtils.getString(getContext(), "course"));
                startActivity(intent);
                break;
            case R.id.re_question:
                intent = new Intent(getContext(), XinShouJiaoChengActivity.class);
                intent.putExtra("url", PreferUtils.getString(getContext(), "question"));
                startActivity(intent);
                break;
            case R.id.re_aboutus:
                intent = new Intent(getContext(), AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.re_my_department:
                intent = new Intent(getContext(), MyTeamActivity.class);
                startActivity(intent);
                break;
            case R.id.re_incomeing:
                intent = new Intent(getContext(), MyIncomeingActivity.class);
                startActivity(intent);
                break;
            case R.id.re_invite_award:
                intent = new Intent(getContext(), YaoQingFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_fuzhi_anniu:
                clipBean = new ClipBean();
                cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("Label", invite_code);
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getContext(), "邀请ID复制成功");
                clipBean.setTitle(invite_code);
                clipBean.save();
                break;
            case R.id.re_user_bg:
                break;
            case R.id.re_withdraw_deposit:
                intent = new Intent(getContext(), MoneyTiXianActivity.class);
                intent.putExtra("total_money", balance);
                startActivity(intent);
                break;
            case R.id.re_wechat:
                if (!TextUtils.isEmpty(pwechat)) {
                    clipBean = new ClipBean();
                    cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipData = ClipData.newPlainText("Label", pwechat);
                    cm.setPrimaryClip(mClipData);
                    ToastUtils.showToast(getContext(), "邀请人微信号复制成功");
                    clipBean.setTitle(pwechat);
                    clipBean.save();
                } else {
                    ToastUtils.showToast(getContext(), "暂无邀请人微信号");
                }
                break;
            case R.id.re_tuandui:
                intent = new Intent(getContext(), TuanDuiJinTieActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_set:
                intent = new Intent(getContext(), PersonalActivity.class);
                startActivity(intent);
                break;
            case R.id.re_taobao_gwuche:
                /*判断淘宝是否授权*/
                alibcLogin.showLogin(getActivity(), new AlibcLoginCallback() {

                    @Override
                    public void onSuccess() {
                        intent = new Intent(getContext(), TaoBaoH5Activity.class);
                        intent.putExtra("type", "1");
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        ToastUtils.showToast(getContext(), "淘宝授权失败");
                    }
                });
                break;
            case R.id.re_taobao_order:
                /*判断淘宝是否授权*/
                alibcLogin.showLogin(getActivity(), new AlibcLoginCallback() {

                    @Override
                    public void onSuccess() {
                        intent = new Intent(getContext(), TaoBaoH5Activity.class);
                        intent.putExtra("type", "2");
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        ToastUtils.showToast(getContext(), "淘宝授权失败");
                    }
                });
                break;
        }
    }

    private void getBalanceData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getContext(), "member_id"));
        map.put("field", "balance,credits");
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.APPCONFIGURATION + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getContext(), "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), PreferUtils.getString(getContext(), "member_id")))
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
                                balance = bean.getResult().getBalance();
                                credits = bean.getResult().getCredits();
                                tv_ketiixan.setText("¥" + balance);
                                tv_tuandui.setText("¥" + credits);
                                PreferUtils.putString(getContext(), "balance", balance);
                                PreferUtils.putString(getContext(), "credits", credits);
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

    private void cleanCacheDialog() {
        final Dialog dialog = new Dialog(getContext(), R.style.activitydialog);
        dialog.setContentView(R.layout.clean_cache_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView dismiss = dialog.findViewById(R.id.dismiss);
        TextView sure = dialog.findViewById(R.id.sure);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DataCleanManager.clearAllCache(getContext());
                getCacheData();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        getBalanceData();
        super.onResume();
    }

    private void getUserData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getContext(), "member_id"));
        map.put("field", Constant.USER_DATA_PARA);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MINEDATA + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getContext(), "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), PreferUtils.getString(getContext(), "member_id")))
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
                                    member_role = result.getMember_role();
                                    son_count = result.getFans();
                                    PreferUtils.putString(getContext(), "member_role", member_role);
                                    PreferUtils.putString(getContext(), "son_count", son_count);
                                    initView();
                                }
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
