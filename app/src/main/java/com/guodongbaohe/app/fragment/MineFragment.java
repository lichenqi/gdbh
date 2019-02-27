package com.guodongbaohe.app.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.AboutUsActivity;
import com.guodongbaohe.app.activity.GCollectionActivity;
import com.guodongbaohe.app.activity.GGfwChatActivity;
import com.guodongbaohe.app.activity.GetTokenActivity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.MoneyTiXianActivity;
import com.guodongbaohe.app.activity.MyIncomeingActivity;
import com.guodongbaohe.app.activity.MyOrderActivity;
import com.guodongbaohe.app.activity.MyTeamActivity;
import com.guodongbaohe.app.activity.PersonalActivity;
import com.guodongbaohe.app.activity.TaoBaoH5Activity;
import com.guodongbaohe.app.activity.TaobaoShoppingCartActivity;
import com.guodongbaohe.app.activity.TuanDuiJinTieActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.BeiAnBean;
import com.guodongbaohe.app.bean.MineDataBean;
import com.guodongbaohe.app.bean.VersionBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.DataCleanManager;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.IOUtils;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    @BindView(R.id.gd_collect_jia)
    RelativeLayout gd_collect_jia;
    /*收藏夹个数*/
    @BindView(R.id.tv_collect_num)
    TextView tv_collect_num;
    /*手机令牌*/
    @BindView(R.id.gd_lingpai_rl)
    RelativeLayout gd_lingpai_rl;
    /*令牌号*/
    @BindView(R.id.tv_lingpai_num)
    TextView tv_lingpai_num;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    AlibcLogin alibcLogin;
    /*官方微信*/
    @BindView(R.id.guanfangweixin)
    RelativeLayout guanfangweixin;
    /*版本更新*/
    @BindView(R.id.re_version_update)
    RelativeLayout re_version_update;
    private final int VERSIONCODE_RESULT = 100;
    @BindView(R.id.qudao_id_webview)
    WebView qudao_id_webview;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*提示用户打开储存权限*/
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERSIONCODE_RESULT);
        }
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
            case Constant.COLLECT_LIST_CHANGE:
                initCollectData();
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
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            iv_user_level.setText("总裁");
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            iv_user_level.setText("合伙人");
        } else {
            if (Constant.COMMON_USER_LEVEL.contains(member_role)) {
                iv_user_level.setText("普通会员");
            } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
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
            R.id.re_wechat, R.id.iv_set, R.id.re_xinshou_jiaocheng, R.id.re_question, R.id.re_taobao_gwuche, R.id.re_taobao_order
            , R.id.gd_collect_jia, R.id.guanfangweixin, R.id.re_version_update, R.id.gd_lingpai_rl})
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
                cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("Label", invite_code);
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getContext(), "邀请ID复制成功");
                ClipContentUtil.getInstance(getContext()).putNewSearch(invite_code);//保存记录到数据库
                break;
            case R.id.re_user_bg:
//                intent = new Intent(getContext(), BaiChuanActivity.class);
//                startActivity(intent);
                break;
            case R.id.re_withdraw_deposit:
                intent = new Intent(getContext(), MoneyTiXianActivity.class);
                intent.putExtra("total_money", balance);
                startActivity(intent);
                break;
            case R.id.re_wechat:
                if (!TextUtils.isEmpty(pwechat)) {
                    cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipData = ClipData.newPlainText("Label", pwechat);
                    cm.setPrimaryClip(mClipData);
                    ToastUtils.showToast(getContext(), "邀请人微信号复制成功");
                    ClipContentUtil.getInstance(getContext()).putNewSearch(pwechat);//保存记录到数据库
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
                intent = new Intent(getContext(), TaobaoShoppingCartActivity.class);
                startActivity(intent);
                break;
            case R.id.re_taobao_order:
                intent = new Intent(getContext(), TaoBaoH5Activity.class);
                startActivity(intent);
                break;
            case R.id.gd_collect_jia:
                intent = new Intent(getContext(), GCollectionActivity.class);
                startActivity(intent);
                break;
            case R.id.guanfangweixin:
                intent = new Intent(getContext(), GGfwChatActivity.class);
                startActivity(intent);
                break;
            case R.id.re_version_update:
                getVersionCodeData();
                break;
            case R.id.gd_lingpai_rl://手机令牌
                intent = new Intent(getContext(), GetTokenActivity.class);
                startActivity(intent);
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
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.USER_BASIC_INFO + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getContext(), "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getH5VersionCode(getContext()))
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
        TextView dismiss = (TextView) dialog.findViewById(R.id.dismiss);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
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
        initCollectData();
        /*初始化阿里百川*/
        initAliBaiApi();
        /*初始化渠道*/
        initQuDaoWebview();
        String string = PreferUtils.getString(getContext(), "member_role");
        if (!string.equals("0")) {
            gd_lingpai_rl.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    private void initAliBaiApi() {

    }

    /*收藏夹显示*/
    private void initCollectData() {
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
                .url(Constant.BASE_URL + Constant.USER_BASIC_INFO + "?" + param)
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
                            } else {
                                String special = jsonObject.getString("special");
                                if (!TextUtils.isEmpty(special)) {
                                    if (special.equals("logout")) {
                                        /*非法账号*/
                                        String result = jsonObject.getString("result");
                                        startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                                        if (!TextUtils.isEmpty(result)) {
                                            ToastUtils.showToast(getContext(), result);
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
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }

    String download, title, desc;

    /*版本升级接口*/
    private void getVersionCodeData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.VERSIONUPDATE)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), ""))
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
                                desc = result.getDesc();
                                title = result.getTitle();
                                download = result.getDownload();
                                String version = result.getVersion();
                                Integer localCode = Integer.valueOf(VersionUtil.getAndroidNumVersion(getContext()).replace(".", "").trim());
                                if (Integer.valueOf(version) > localCode) {
                                    versionUpdataDialog();
                                } else {
                                    ToastUtils.showToast(getContext(), "已是最新版本!");
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
        final Dialog dialog = new Dialog(getContext(), R.style.activitydialog);
        dialog.setContentView(R.layout.version_update_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView tv_one = (TextView) dialog.findViewById(R.id.tv_one);
        TextView tv_two = (TextView) dialog.findViewById(R.id.tv_two);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        tv_one.setText(title);
        tv_two.setText(desc);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.cancel);
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERSIONCODE_RESULT);
                } else {
                    IOUtils.rmoveFile("gdbh.apk");
                    ToastUtils.showToast(getContext(), "可在通知栏查看下载进度");
                    downLoadApk();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /*7.0兼容*/
    private void installAPK() {
        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gdbh.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(getContext(), Constant.FILEPROVIDER, apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    DownloadManager downloadManager;
    long downloadId;

    public void downLoadApk() {
        IOUtils.rmoveFile("gdbh.apk");
        //使用系统下载类
        downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
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
        getContext().registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
                    Toast.makeText(getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VERSIONCODE_RESULT:
                /*安装apk权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "版本升级需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "版本升级需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    IOUtils.rmoveFile("gdbh.apk");
                    ToastUtils.showToast(getContext(), "可在通知栏查看下载进度");
                    downLoadApk();
                }
                break;
        }
    }

    /*初始化渠道*/
    String js, taobao_nick;

    private void initQuDaoWebview() {
        AlibcLogin instance = AlibcLogin.getInstance();
        taobao_nick = instance.getSession().nick;
        if (TextUtils.isEmpty(taobao_nick)) return;
        WebSettings settings = qudao_id_webview.getSettings();
        qudao_id_webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        qudao_id_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!TextUtils.isEmpty(js)) {
                    qudao_id_webview.loadUrl("javascript:" + js);
                    Log.i("聚到地址", js);
                }
            }

        });
        /*检查是否需要备案*/
        checkIsNeedBeian();
        qudao_id_webview.addJavascriptInterface(new DemoJavascriptInterface(), "Guodong");
    }

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void saved(String msg) {
            Log.i("回调消息", msg + "  1  ");
            /*保存接口*/
            saveMsg(msg);
        }
    }

    /*检查是否需要备案*/
    String note, script;

    private void checkIsNeedBeian() {
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
                .url(Constant.BASE_URL + Constant.BEIANCHECK + "?" + param)
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
                        Log.i("是否需要备案", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                            } else {
                                BeiAnBean beiAnBean = GsonUtil.GsonToBean(response.toString(), BeiAnBean.class);
                                if (beiAnBean == null) return;
                                if (beiAnBean.getSpecial().equals("beian")) {
                                    note = beiAnBean.getResult().getNote();
                                    String url = beiAnBean.getResult().getUrl();
                                    script = beiAnBean.getResult().getScript();
                                    /*js文件*/
                                    js = "var ns = document.createElement('script');";
                                    js += "ns.src = '" + script + "'; ns.onload = function(){ Beian.init( '" + note + "' ); };";
                                    js += "document.body.appendChild(ns);";
                                    qudao_id_webview.loadUrl(url);
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

    private void saveMsg(String msg) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getContext(), "member_id"));
        map.put("account_name", taobao_nick);
        map.put("explain", msg);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.SAVEBEIAN + "?" + param)
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
                        Log.i("保存结果", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        Log.i("保存结果错误", error_msg);
                    }
                });
    }

}
