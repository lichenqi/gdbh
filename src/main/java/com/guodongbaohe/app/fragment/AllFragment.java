package com.guodongbaohe.app.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.BaseH5Activity;
import com.guodongbaohe.app.activity.KesalanPathActivity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.NinePinkageActivity;
import com.guodongbaohe.app.activity.PinZheMakeMoneyActivity;
import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.activity.ShopRangingClassicActivity;
import com.guodongbaohe.app.activity.SuperMakeActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.adapter.HomeClassicAdapter;
import com.guodongbaohe.app.adapter.HomeListAdapter;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.guodongbaohe.app.bean.BuyUserBean;
import com.guodongbaohe.app.bean.FiveMoKuaiBean;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.bean.XinShouJiaoBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.guodongbaohe.app.view.MarqueeTextView;
import com.guodongbaohe.app.view.UPMarqueeView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*上新界面*/
@SuppressLint("ValidFragment")
public class AllFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private View view;
    @BindView(R.id.ll_parent)
    RelativeLayout ll_parent;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.re_notice)
    RelativeLayout re_notice;
    @BindView(R.id.tv_notice)
    MarqueeTextView tv_notice;
    @BindView(R.id.notice_ca)
    ImageView notice_ca;
    @BindView(R.id.to_top)
    ImageView to_top;
    private String url;
    private int pageNum = 1;
    List<HomeListBean.ListData> list = new ArrayList<>();
    HomeListAdapter adapter;
    private boolean isLoop = true;
    int height;
    String cate_id;
    private TabLayout tablayout;
    private RelativeLayout re_search_title;
    String notice_url;

    public AllFragment(TabLayout tablayout, RelativeLayout re_search_title) {
        this.tablayout = tablayout;
        this.re_search_title = re_search_title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            height = getArguments().getInt("height");
            cate_id = getArguments().getString("cate_id");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.allfragment, container, false);
            ButterKnife.bind(this, view);
            initRecyclerview();
            getListData();
            String notice_title = PreferUtils.getString(getContext(), "notice_title");
            notice_url = PreferUtils.getString(getContext(), "notice_url");
            if (TextUtils.isEmpty(notice_title)) {
                re_notice.setVisibility(View.INVISIBLE);
            } else {
                re_notice.setVisibility(View.VISIBLE);
                re_notice.getBackground().setAlpha(220);
                tv_notice.setTextColor(0xff8c5727);
                tv_notice.setSelected(true);
                tv_notice.setText(PreferUtils.getString(getContext(), "notice_title"));
            }
        }
        return view;
    }

    @OnClick({R.id.notice_ca, R.id.to_top, R.id.re_notice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notice_ca:
                re_notice.setVisibility(View.INVISIBLE);
                break;
            case R.id.to_top:
                xrecycler.scrollToPosition(0);
                break;
            case R.id.re_notice:
                if (!TextUtils.isEmpty(notice_url)) {
                    intent = new Intent(getContext(), XinShouJiaoChengActivity.class);
                    intent.putExtra("url", notice_url);
                    startActivity(intent);
                }
                break;
        }
    }

    /*新手教程*/
    private void getXinShuoData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "xinshou");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
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
                        Log.i("新手教程", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                XinShouJiaoBean jiaoBean = GsonUtil.GsonToBean(response.toString(), XinShouJiaoBean.class);
                                if (jiaoBean == null) return;
                                final List<XinShouJiaoBean.XinShouJiaoData> result = jiaoBean.getResult();
                                Glide.with(getContext()).load(result.get(0).getImage()).into(iv_xinshou);
                                iv_xinshou.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        intent = new Intent(getContext(), BaseH5Activity.class);
                                        intent.putExtra("url", result.get(0).getUrl());
                                        startActivity(intent);
                                    }
                                });
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

    List<FiveMoKuaiBean.FiveMoKuaiData> mokuaiList;

    private void getFiveMoKuaiData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "navig_app");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
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
                        Log.i("五大模块数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                FiveMoKuaiBean bean = GsonUtil.GsonToBean(response.toString(), FiveMoKuaiBean.class);
                                if (bean == null) return;
                                mokuaiList = bean.getResult();
                                initClassicRecycler();
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

    /*列表数据*/
    private void getListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("limit", "12");
        map.put("supid", cate_id);
        map.put("page", String.valueOf(pageNum));
        String param = ParamUtil.getMapParam(map);
        Log.i("列表数据参数", Constant.BASE_URL + Constant.SHOP_LIST + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.SHOP_LIST + "?" + param)
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
                        Log.i("列表数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                HomeListBean bean = GsonUtil.GsonToBean(response.toString(), HomeListBean.class);
                                if (bean == null) return;
                                List<HomeListBean.ListData> result = bean.getResult();
                                if (result.size() == 0) {
                                    xrecycler.setNoMore(true);
                                    xrecycler.refreshComplete();
                                    xrecycler.loadMoreComplete();
                                } else {
                                    boolean isLogin = PreferUtils.getBoolean(getContext(), "isLogin");
                                    String son_count = PreferUtils.getString(getContext(), "son_count");
                                    String member_role = PreferUtils.getString(getContext(), "member_role");
                                    for (HomeListBean.ListData listData : result) {
                                        listData.setLogin(isLogin);
                                        listData.setSon_count(son_count);
                                        listData.setMember_role(member_role);
                                    }
                                    if (pageNum == 1) {
                                        list.clear();
                                        list.addAll(result);
                                        adapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll(result);
                                        adapter.notifyDataSetChanged();
                                        xrecycler.loadMoreComplete();
                                    }
                                }
                            } else {
                                xrecycler.refreshComplete();
                                xrecycler.loadMoreComplete();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getContext(), Constant.NONET);
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                });
    }

    /*首页轮播图接口*/
    List<BannerDataBean.BannerList> banner_result;
    BannerDataBean bannerDataBean;

    private void getBannerData() {
        url = Constant.BASE_URL + Constant.BANNER + "/";
        MyApplication.getInstance().getMyOkHttp().post().url(url).tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), ""))
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("banner数据", response.toString());
                        bannerDataBean = GsonUtil.GsonToBean(response.toString(), BannerDataBean.class);
                        if (bannerDataBean == null) return;
                        banner_result = bannerDataBean.getResult();
                        indicators = new ImageView[banner_result.size()];
                        for (int i = 0; i < banner_result.size(); i++) {
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_cycle_viewpager_indicator, null);
                            ImageView iv = view.findViewById(R.id.image_indicator);
                            indicators[i] = iv;
                            llpoint.addView(view);
                        }
                        setIndicator(0);
                        viewpager.setAdapter(new MyPagerAdapter());
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getActivity(), Constant.NONET);
                    }
                });
    }

    List<View> views;

    /*随机用户佣金接口*/
    private void getBuyData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", "100");
        map.put("rows", "100");
        String mapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(mapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GOODSDETAIL_BUY_NUMS + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-userid", "100")
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), "100"))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("用户数量接口返回", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                BuyUserBean bean = GsonUtil.GsonToBean(response.toString(), BuyUserBean.class);
                                if (bean == null || bean.getResult().size() == 0) return;
                                List<BuyUserBean.BuyUser> userList = bean.getResult();
                                views = new ArrayList<>();
                                for (int i = 0; i < userList.size(); i++) {
                                    View view = LayoutInflater.from(getContext()).inflate(R.layout.item_home_news_scroll, null);
                                    TextView name = view.findViewById(R.id.name);
                                    TextView time = view.findViewById(R.id.time);
                                    TextView tmoney = view.findViewById(R.id.money);
                                    String member_name = userList.get(i).getMember_name();
                                    String order_time = userList.get(i).getOrder_time();
                                    String money = userList.get(i).getMoney();
                                    name.setText(member_name);
                                    time.setText("在" + order_time + "分钟前赚了");
                                    tmoney.setText(money + "元");
                                    views.add(view);
                                }
                                upmarqueeview.setViews(views);
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

    View headView;

    private void initRecyclerview() {
        ViewTreeObserver viewTreeObserver = xrecycler.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                xrecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = xrecycler.getHeight();
                Log.i("这个高度", height + "");
            }
        });

//        ViewGroup.LayoutParams layoutParams = xrecycler.getLayoutParams();
//        layoutParams.height = height;
//        xrecycler.setLayoutParams(layoutParams);

        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        XRecyclerViewUtil.setView(xrecycler);
        headView = LayoutInflater.from(getContext()).inflate(R.layout.home_head_view, null);
        xrecycler.addHeaderView(headView);
        adapter = new HomeListAdapter(getContext(), list);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getListData();
                if (bannerDataBean == null) {
                    getBannerData();
                }
                getBuyData();
                getFiveMoKuaiData();
                getXinShuoData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getListData();
            }
        });
        initbannerview();
        adapter.setOnClickListener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 2;
                intent = new Intent(getContext(), ShopDetailActivity.class);
                intent.putExtra("goods_id", list.get(pos).getGoods_id());
                intent.putExtra("cate_route", list.get(pos).getCate_route());/*类目名称*/
                intent.putExtra("cate_category", list.get(pos).getCate_category());/*类目id*/
                intent.putExtra("attr_price", list.get(pos).getAttr_price());
                intent.putExtra("attr_prime", list.get(pos).getAttr_prime());
                intent.putExtra("attr_ratio", list.get(pos).getAttr_ratio());
                intent.putExtra("sales_month", list.get(pos).getSales_month());
                intent.putExtra("goods_name", list.get(pos).getGoods_name());/*长标题*/
                intent.putExtra("goods_short", list.get(pos).getGoods_short());/*短标题*/
                intent.putExtra("seller_shop", list.get(pos).getSeller_shop());/*店铺姓名*/
                intent.putExtra("goods_thumb", list.get(pos).getGoods_thumb());/*单图*/
                intent.putExtra("goods_gallery", list.get(pos).getGoods_gallery());/*多图*/
                intent.putExtra("coupon_begin", list.get(pos).getCoupon_begin());/*开始时间*/
                intent.putExtra("coupon_final", list.get(pos).getCoupon_final());/*结束时间*/
                intent.putExtra("coupon_surplus", list.get(pos).getCoupon_surplus());/*是否有券*/
                intent.putExtra("coupon_explain", list.get(pos).getGoods_slogan());/*推荐理由*/
                intent.putExtra("attr_site", list.get(pos).getAttr_site());/*天猫或者淘宝*/
                startActivity(intent);
            }
        });
        xrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = recyclerView.computeVerticalScrollOffset();
                if (i > 1200) {
                    to_top.setVisibility(View.VISIBLE);
                    int black_color = Color.parseColor("#000000");
                    setColorChange(black_color);
                } else {
                    to_top.setVisibility(View.GONE);
                    String currentColor = PreferUtils.getString(getContext(), "currentColor");
                    if (!TextUtils.isEmpty(currentColor)) {
                        setColorChange(Color.parseColor(currentColor));
                    }
                }
            }
        });
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                pageNum = 1;
                getListData();
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                pageNum = 1;
                getListData();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                pageNum = 1;
                getListData();
                break;
            case "timeStart":
                initTimer();
                break;
            case "timeStop":
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (task != null) {
                    task.cancel();
                    task = null;
                }
                break;
        }
    }

    Intent intent;

    private void initClassicRecycler() {
        RecyclerView recyclerview = headView.findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getContext(), mokuaiList.size());
        recyclerview.setLayoutManager(manager);
        HomeClassicAdapter adapter = new HomeClassicAdapter(getContext(), mokuaiList);
        recyclerview.setAdapter(adapter);
        adapter.setOnClickListener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                String url = mokuaiList.get(position).getUrl();
                switch (url) {
                    case "jkj":
                        /*9.9包邮*/
                        intent = new Intent(getContext(), NinePinkageActivity.class);
                        intent.putExtra("title", mokuaiList.get(position).getTitle());
                        startActivity(intent);
                        break;
                    case "fqb":
                        /*疯抢榜*/
                        intent = new Intent(getContext(), ShopRangingClassicActivity.class);
                        intent.putExtra("title", mokuaiList.get(position).getTitle());
                        startActivity(intent);
                        break;
                    case "jhs":
                        /*聚划算*/
                        intent = new Intent(getContext(), KesalanPathActivity.class);
                        intent.putExtra("title", mokuaiList.get(position).getTitle());
                        startActivity(intent);
                        break;
                    case "tqg":
                        /*淘抢购*/
                        intent = new Intent(getContext(), SuperMakeActivity.class);
                        intent.putExtra("title", mokuaiList.get(position).getTitle());
                        startActivity(intent);
                        break;
                    case "pzz":
                        /*拼着赚*/
                        if (PreferUtils.getBoolean(getContext(), "isLogin")) {
                            intent = new Intent(getContext(), PinZheMakeMoneyActivity.class);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                        }
                        break;
                }
            }
        });
    }

    ViewPager viewpager;
    LinearLayout llpoint;
    private ImageView[] indicators;
    View view_color;
    UPMarqueeView upmarqueeview;
    ImageView iv_xinshou;

    private void initbannerview() {
        view_color = headView.findViewById(R.id.view_color);
        viewpager = headView.findViewById(R.id.viewpager);
        llpoint = headView.findViewById(R.id.llpoint);
        upmarqueeview = headView.findViewById(R.id.upmarqueeview);
        iv_xinshou = headView.findViewById(R.id.iv_xinshou);
        getBannerData();
        getBuyData();
        getFiveMoKuaiData();
        getXinShuoData();
        viewpager.addOnPageChangeListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 20;
        llpoint.setLayoutParams(params);
        initTimer();
        setOnTouchView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchView() {
        //通过监听onTouch事件，设置一个标签isLoop;手指按下时isLoop = false,手指抬起后isLoop = true;
        viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isLoop = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isLoop = true;
                        break;
                }
                return false;
            }
        });
    }

    /*设置指示器*/
    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(R.drawable.indicator_unselected1);
        }
        indicators[selectedPosition % indicators.length].setBackgroundResource(R.drawable.indicator_selected1);
    }

    public class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return banner_result == null ? 0 : Integer.MAX_VALUE;
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
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.banner_viewpager, container, false);
            RoundedImageView iv = view.findViewById(R.id.iv);
            Glide.with(getContext()).load(banner_result.get(position % banner_result.size()).getImage()).into(iv);
            container.addView(view);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = banner_result.get(position % banner_result.size()).getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        if (url.contains("http")) {
                            intent = new Intent(getContext(), BaseH5Activity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        } else {
                            getShopBasicData(url);
                        }
                    }
                }
            });
            return view;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        String s = "#" + banner_result.get(position % banner_result.size()).getTitle();
        PreferUtils.putString(getContext(), "currentColor", s);
        int i = Color.parseColor(s);
        view_color.setBackgroundColor(i);
        ll_parent.setBackgroundColor(i);
        tablayout.setBackgroundColor(i);
        re_search_title.setBackgroundColor(i);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            /*状态栏*/
            window.setStatusBarColor(i);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private Timer timer;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentItem = viewpager.getCurrentItem();
            currentItem++;
            viewpager.setCurrentItem(currentItem);
        }
    };

    TimerTask task;

    private void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (isLoop) {
                    handler.sendEmptyMessage(0);
                }
            }
        };
        if (isLoop) {
            timer.schedule(task, 3000, 5000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        EventBus.getDefault().unregister(this);
    }

    private void setColorChange(int colorChange) {
        view_color.setBackgroundColor(colorChange);
        ll_parent.setBackgroundColor(colorChange);
        tablayout.setBackgroundColor(colorChange);
        re_search_title.setBackgroundColor(colorChange);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            /*状态栏*/
            window.setStatusBarColor(colorChange);
        }
    }

    /*商品详情头部信息*/
    private void getShopBasicData(String shopId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("goods_id", shopId);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean(response.toString(), ShopBasicBean.class);
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                Intent intent = new Intent(getContext(), ShopDetailActivity.class);
                                intent.putExtra("goods_id", result.getGoods_id());
                                intent.putExtra("cate_route", result.getCate_route());/*类目名称*/
                                intent.putExtra("cate_category", result.getCate_category());/*类目id*/
                                intent.putExtra("attr_price", result.getAttr_price());
                                intent.putExtra("attr_prime", result.getAttr_prime());
                                intent.putExtra("attr_ratio", result.getAttr_ratio());
                                intent.putExtra("sales_month", result.getSales_month());
                                intent.putExtra("goods_name", result.getGoods_name());/*长标题*/
                                intent.putExtra("goods_short", result.getGoods_short());/*短标题*/
                                intent.putExtra("seller_shop", result.getSeller_shop());/*店铺姓名*/
                                intent.putExtra("goods_thumb", result.getGoods_thumb());/*单图*/
                                intent.putExtra("goods_gallery", result.getGoods_gallery());/*多图*/
                                intent.putExtra("coupon_begin", result.getCoupon_begin());/*开始时间*/
                                intent.putExtra("coupon_final", result.getCoupon_final());/*结束时间*/
                                intent.putExtra("coupon_surplus", result.getCoupon_surplus());/*是否有券*/
                                intent.putExtra("coupon_explain", result.getGoods_slogan());/*推荐理由*/
                                intent.putExtra("attr_site", result.getAttr_site());/*天猫或者淘宝*/
                                startActivity(intent);
                            } else {
                                String result = jsonObject.getString("result");
                                ToastUtils.showToast(getContext(), result);
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
