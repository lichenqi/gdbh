package com.guodongbaohe.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.SearchActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.PaiHangBangBean;
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

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewRangingListFragment extends Fragment {
    private View view;
    @BindView(R.id.circleimageview)
    CircleImageView circleimageview;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.name_type)
    TextView name_type;
    @BindView(R.id.name_id)
    TextView name_id;
    @BindView(R.id.total_income)
    TextView total_income;
    @BindView(R.id.baohe_num)
    TextView baohe_num;
    @BindView(R.id.fans_num)
    TextView fans_num;
    @BindView(R.id.city_num)
    TextView city_num;
    String userImg, userName, member_role, invite_code, son_count;
    @BindView(R.id.tuandui_income)
    TextView tuandui_income;
    @BindView(R.id.tuanduipaiming)
    TextView tuanduipaiming;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    boolean isLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.newranginglistfragment, container, false);
            ButterKnife.bind(this, view);
            EventBus.getDefault().register(this);
            isLogin = PreferUtils.getBoolean(getContext(), "isLogin");
            if (isLogin) {
                /*获取排行基本数据*/
                getData();
                /*初始化用户基本信息*/
                initUserData();
            } else {
                /*游客数据显示*/
                touristDataView();
            }
            /*监听刷新控件*/
            initRefresh();
        }
        return view;
    }

    /*游客数据显示*/
    private void touristDataView() {
        circleimageview.setImageResource(R.mipmap.user_moren_logo);
        name.setText("果冻宝盒");
        name_type.setText("普通用户");
        name_id.setText("");
        total_income.setText("---");
        baohe_num.setText("---");
        fans_num.setText("---");
        city_num.setText("---");
        tuandui_income.setText("---");
        tuanduipaiming.setText("---");
    }

    private void initRefresh() {
        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        /*获取用户基本信息*/
                        if (PreferUtils.getBoolean(getContext(), "isLogin")) {
                            getUserData();
                        }
                        swiperefreshlayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
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
                                    initUserData();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "loginSuccess":
                initUserData();
                getData();
                break;
            case "headimgChange":
                initUserData();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                initUserData();
                break;
            case Constant.LOGIN_OUT:
                touristDataView();
                break;
        }
    }

    private void initUserData() {
        userImg = PreferUtils.getString(getContext(), "userImg");
        userName = PreferUtils.getString(getContext(), "userName");
        invite_code = PreferUtils.getString(getContext(), "invite_code");
        member_role = PreferUtils.getString(getContext(), "member_role");
        son_count = PreferUtils.getString(getContext(), "son_count");
        if (TextUtils.isEmpty(userImg)) {
            circleimageview.setImageResource(R.mipmap.user_moren_logo);
        } else {
            Glide.with(getContext()).load(userImg).into(circleimageview);
        }
        name.setText(userName);
        name_id.setText("邀请ID: " + invite_code);
        if (member_role.equals("2")) {
            name_type.setText("总裁");
        } else if (member_role.equals("1")) {
            name_type.setText("合伙人");
        } else {
            if (son_count.equals("0")) {
                name_type.setText("普通会员");
            } else {
                name_type.setText("VIP");
            }
        }
    }

    private void getData() {
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
                .url(Constant.BASE_URL + Constant.RANKING_LIST + "?" + param)
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
                        Log.i("首页排行榜", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                PaiHangBangBean bean = GsonUtil.GsonToBean(response.toString(), PaiHangBangBean.class);
                                if (bean == null) return;
                                PaiHangBangBean.PaiHangBangData result = bean.getResult();
                                String income_rank = result.getIncome_rank();
                                if (income_rank.equals("0")) {
                                    total_income.setText("9999+");
                                } else {
                                    total_income.setText(result.getIncome_rank());
                                }
                                String fans_rank = result.getFans_rank();
                                if (fans_rank.equals("0")) {
                                    fans_num.setText("9999+");
                                } else {
                                    fans_num.setText(fans_rank);
                                }
                                String credits_rank = result.getCredits_rank();
                                if (credits_rank.equals("0")) {
                                    baohe_num.setText("9999+");
                                } else {
                                    baohe_num.setText(credits_rank);
                                }
                                String balance_rank = result.getBalance_rank();
                                if (balance_rank.equals("0")) {
                                    city_num.setText("9999+");
                                } else {
                                    city_num.setText(balance_rank);
                                }
                                tuandui_income.setText(result.getTeam_income());
                                String team_income_rank = result.getTeam_income_rank();
                                if (team_income_rank.equals("0")) {
                                    tuanduipaiming.setText("9999+");
                                } else {
                                    tuanduipaiming.setText(team_income_rank);
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

    Intent intent;

    @OnClick({R.id.ll_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_search:
                intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
        }
    }
}
