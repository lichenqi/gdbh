package com.guodongbaohe.app.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.BaseH5Activity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.activity.TaoBaoAndTianMaoUrlActivity;
import com.guodongbaohe.app.activity.TaobaoTianMaoHolidayOfActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.adapter.HomeHorizontalAdapter;
import com.guodongbaohe.app.adapter.HomeListAdapter;
import com.guodongbaohe.app.adapter.HomeVerticalAdapter;
import com.guodongbaohe.app.adapter.HoursHortAdapter;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.guodongbaohe.app.bean.BuyUserBean;
import com.guodongbaohe.app.bean.HomeHorizontalListBean;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.bean.NewBanDataBean;
import com.guodongbaohe.app.bean.NewBannerBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.bean.ThemeBean;
import com.guodongbaohe.app.bean.XinShouJiaoBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.gridview.DecoratorViewPager;
import com.guodongbaohe.app.gridview.GViewPagerAdapter;
import com.guodongbaohe.app.gridview.GridViewAdapter;
import com.guodongbaohe.app.gridview.MultiGridView;
import com.guodongbaohe.app.itemdecoration.HorizontalItem;
import com.guodongbaohe.app.itemdecoration.HotItem;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
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
    private boolean isXinLoop = true;
    int height;
    String cate_id;
    private RelativeLayout re_tablayout_parent;
    private RelativeLayout re_search_title;
    String notice_url, is_index_activity;
    Context context;
    ViewPager viewpager, viewpager_xin;
    LinearLayout llpoint, llpoint_xin;
    private ImageView[] indicators;
    private ImageView[] indicators_xin;
    View view_color;
    UPMarqueeView upmarqueeview;
    Intent intent;
    public static int item_grid_num = 10;//每一页中GridView中item的数量
    public static int number_columns = 5;//gridview一行展示的数目

    public AllFragment() {

    }

    public AllFragment(RelativeLayout re_tablayout_parent, RelativeLayout re_search_title) {
        this.re_tablayout_parent = re_tablayout_parent;
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
            context = MyApplication.getInstance();
            String notice_title = PreferUtils.getString(context, "notice_title");
            notice_url = PreferUtils.getString(context, "notice_url");
            is_index_activity = PreferUtils.getString(context, "is_index_activity");
            initRecyclerview();
            getListData();
            if (TextUtils.isEmpty(notice_title)) {
                re_notice.setVisibility(View.INVISIBLE);
            } else {
                re_notice.setVisibility(View.VISIBLE);
                re_notice.getBackground().setAlpha(220);
                tv_notice.setTextColor(0xff8c5727);
                tv_notice.setSelected(true);
                tv_notice.setText(PreferUtils.getString(context, "notice_title"));
            }
        }
        return view;
    }

    ThemeBean themeBean;
    List<ThemeBean.ThemeData> theme_list;
    List<HomeHorizontalListBean> horizontalList;
    List<HomeHorizontalListBean> verticalList;
    ThemeBean.ThemeData head_theme_data;
    HomeHorizontalAdapter homeHorizontalAdapter;
    HomeVerticalAdapter homeVerticalAdapter;

    private void getThemeData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "index_activity");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("查看数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                themeBean = GsonUtil.GsonToBean(response.toString(), ThemeBean.class);
                                if (themeBean == null) {
                                    ll_theme_parent.setVisibility(View.GONE);
                                    return;
                                }
                                theme_list = themeBean.getResult();
                                /*主题头部图片*/
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get(i).getUrl().equals("center")) {
                                        Glide.with(context).load(theme_list.get(i).getImage()).into(iv_theme);
                                        head_theme_data = theme_list.get(i);
                                    }
                                }
                                setHeadListener(head_theme_data);/*头部点击*/
                                /*布局背景图*/
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get(i).getUrl().equals("background")) {
//                                        Glide.with(context).load(theme_list.get(i).getImage()).into(iv_list_bg);
                                    }
                                }
                                /*横向列表数据显示*/
                                horizontalList = new ArrayList<>();
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get(i).getUrl().equals("horizontal")) {
                                        ThemeBean.ThemeData themeData = theme_list.get(i);
                                        HomeHorizontalListBean bean = new HomeHorizontalListBean();
                                        bean.setExtend(themeData.getExtend());
                                        bean.setImage(themeData.getImage());
                                        bean.setSort(themeData.getSort());
                                        bean.setTitle(themeData.getTitle());
                                        bean.setUrl(themeData.getUrl());
                                        bean.setType(themeData.getType());
                                        horizontalList.add(bean);
                                    }
                                }
                                homeHorizontalAdapter = new HomeHorizontalAdapter(context, horizontalList);
                                recyclerview_horizontal.setAdapter(homeHorizontalAdapter);
                                setHomeHorizontalAdapterListener();
                                /*竖直列表数据显示*/
                                verticalList = new ArrayList<>();
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get(i).getUrl().equals("vertical")) {
                                        ThemeBean.ThemeData themeData = theme_list.get(i);
                                        HomeHorizontalListBean bean = new HomeHorizontalListBean();
                                        bean.setExtend(themeData.getExtend());
                                        bean.setImage(themeData.getImage());
                                        bean.setSort(themeData.getSort());
                                        bean.setTitle(themeData.getTitle());
                                        bean.setUrl(themeData.getUrl());
                                        bean.setType(themeData.getType());
                                        verticalList.add(bean);
                                    }
                                }
                                homeVerticalAdapter = new HomeVerticalAdapter(context, verticalList);
                                recyclerview_vertical.setAdapter(homeVerticalAdapter);
                                setHomeVerticalAdapterListener();
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

    /*24小时排行榜*/
    HomeListBean hoursHotBean;

    private void getHoursHotListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("page", "1");
        map.put("rank", "day");
        map.put("limit", "20");
        final String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.RANKINGLIST + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                hoursHotBean = GsonUtil.GsonToBean(response.toString(), HomeListBean.class);
                                if (hoursHotBean == null) return;
                                final List<HomeListBean.ListData> hoursList = hoursHotBean.getResult();
                                if (hoursList.size() == 0) return;
                                HoursHortAdapter hoursHortAdapter = new HoursHortAdapter(context, hoursList);
                                recyclerview_hours_hot.setAdapter(hoursHortAdapter);
                                hoursHortAdapter.setonclicklistener(new OnItemClick() {
                                    @Override
                                    public void OnItemClickListener(View view, int pos) {
                                        intent = new Intent(context, ShopDetailActivity.class);
                                        intent.putExtra("goods_id", hoursList.get(pos).getGoods_id());
                                        intent.putExtra("cate_route", hoursList.get(pos).getCate_route());/*类目名称*/
                                        intent.putExtra("cate_category", hoursList.get(pos).getCate_category());
                                        intent.putExtra("attr_price", hoursList.get(pos).getAttr_price());
                                        intent.putExtra("attr_prime", hoursList.get(pos).getAttr_prime());
                                        intent.putExtra("attr_ratio", hoursList.get(pos).getAttr_ratio());
                                        intent.putExtra("sales_month", hoursList.get(pos).getSales_month());
                                        intent.putExtra("goods_name", hoursList.get(pos).getGoods_name());/*长标题*/
                                        intent.putExtra("goods_short", hoursList.get(pos).getGoods_short());/*短标题*/
                                        intent.putExtra("seller_shop", hoursList.get(pos).getSeller_shop());/*店铺姓名*/
                                        intent.putExtra("goods_thumb", hoursList.get(pos).getGoods_thumb());/*单图*/
                                        intent.putExtra("goods_gallery", hoursList.get(pos).getGoods_gallery());/*多图*/
                                        intent.putExtra("coupon_begin", hoursList.get(pos).getCoupon_begin());/*开始时间*/
                                        intent.putExtra("coupon_final", hoursList.get(pos).getCoupon_final());/*结束时间*/
                                        intent.putExtra("coupon_surplus", hoursList.get(pos).getCoupon_surplus());/*是否有券*/
                                        intent.putExtra("coupon_explain", hoursList.get(pos).getGoods_slogan());/*推荐理由*/
                                        intent.putExtra("attr_site", hoursList.get(pos).getAttr_site());/*天猫或者淘宝*/
                                        intent.putExtra("coupon_total", hoursList.get(pos).getCoupon_total());
                                        intent.putExtra("coupon_id", hoursList.get(pos).getCoupon_id());/*优惠券id*/
                                        intent.putExtra(Constant.SHOP_REFERER, "local");/*商品来源*/
                                        intent.putExtra(Constant.GAOYONGJIN_SOURCE, hoursList.get(pos).getSource());/*高佣金来源*/
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    intent = new Intent(context, XinShouJiaoChengActivity.class);
                    intent.putExtra("url", notice_url);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
        }
    }

    /*新手教程*/
    List<XinShouJiaoBean.XinShouJiaoData> xin_list;
    XinShouJiaoBean jiaoBean;

    private void getXinShuoData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "xinshou");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("新手教程", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                jiaoBean = GsonUtil.GsonToBean(response.toString(), XinShouJiaoBean.class);
                                if (jiaoBean == null) return;
                                xin_list = jiaoBean.getResult();
                                if (xin_list.size() == 0) return;
                                indicators_xin = new ImageView[xin_list.size()];
                                for (int i = 0; i < xin_list.size(); i++) {
                                    View view = LayoutInflater.from(context).inflate(R.layout.view_cycle_viewpager_indicator, null);
                                    ImageView iv = (ImageView) view.findViewById(R.id.image_indicator);
                                    indicators_xin[i] = iv;
                                    llpoint_xin.addView(view);
                                }
                                setIndicatorXin(0);
                                viewpager_xin.setAdapter(new XinShouJiaoAdapter());
                                if (xin_list.size() > 1) {
                                    initXinTimer();
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
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, ""))
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
                                    boolean isLogin = PreferUtils.getBoolean(context, "isLogin");
                                    String son_count = PreferUtils.getString(context, "son_count");
                                    String member_role = PreferUtils.getString(context, "member_role");
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
                        ToastUtils.showToast(context, Constant.NONET);
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                });
    }

    /*首页轮播图接口*/
    List<BannerDataBean.BannerList> banner_result;
    BannerDataBean bannerDataBean;
    String member_id;

    private void getBannerData() {
        member_id = PreferUtils.getString(context, "member_id");
        if (TextUtils.isEmpty(member_id)) {
            member_id = "";
        }
        url = Constant.BASE_URL + Constant.BANNER + "/";
        MyApplication.getInstance().getMyOkHttp().post().url(url).tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", member_id)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("banner数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                bannerDataBean = GsonUtil.GsonToBean(response.toString(), BannerDataBean.class);
                                if (bannerDataBean == null) return;
                                banner_result = bannerDataBean.getResult();
                                if (banner_result.size() == 0) return;
                                indicators = new ImageView[banner_result.size()];
                                for (int i = 0; i < banner_result.size(); i++) {
                                    View view = LayoutInflater.from(context).inflate(R.layout.view_cycle_viewpager_indicator, null);
                                    ImageView iv = (ImageView) view.findViewById(R.id.image_indicator);
                                    indicators[i] = iv;
                                    llpoint.addView(view);
                                }
                                setIndicator(0);
                                viewpager.setAdapter(new MyPagerAdapter());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-userid", "100")
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, "100"))
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
                                    View view = LayoutInflater.from(context).inflate(R.layout.item_home_news_scroll, null);
                                    TextView name = (TextView) view.findViewById(R.id.name);
                                    TextView time = (TextView) view.findViewById(R.id.time);
                                    TextView tmoney = (TextView) view.findViewById(R.id.money);
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
                        ToastUtils.showToast(context, Constant.NONET);
                    }
                });
    }

    View headView;

    private void initRecyclerview() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(context));
        XRecyclerViewUtil.setView(xrecycler);
        headView = LayoutInflater.from(context).inflate(R.layout.home_head_view, null);
        xrecycler.addHeaderView(headView);
        initbannerview();
        adapter = new HomeListAdapter(context, list);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getListData();/*列表数据*/
                if (bannerDataBean == null) {
                    getBannerData();/*轮播图数据*/
                }
                getBuyData();/*实时报播数据*/
                if (newScreenBean == null) {
                    getNewClassicData();/*分屏数据*/
                }
                if (jiaoBean == null) {
                    getXinShuoData();/*新手教程数据*/
                }
                if (themeBean == null) {
                    getThemeData();/*主题活动数据*/
                }
                if (hoursHotBean == null) {
                    getHoursHotListData();/*24小时热播榜数据*/
                }
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getListData();
            }
        });
        adapter.setOnClickListener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 2;
                intent = new Intent(context, ShopDetailActivity.class);
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
                intent.putExtra("coupon_total", list.get(pos).getCoupon_total());/*券数量*/
                intent.putExtra("coupon_id", list.get(pos).getCoupon_id());/*优惠券id*/
                intent.putExtra(Constant.SHOP_REFERER, "local");/*商品来源*/
                intent.putExtra(Constant.GAOYONGJIN_SOURCE, list.get(pos).getSource());/*高佣金来源*/
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                if (dy != 0 && i > 3000) {
                    to_top.setVisibility(View.VISIBLE);
                    int black_color = Color.parseColor("#000000");
                    setColorChange(black_color);
                    PreferUtils.putString(context, "scrollToWhere", "bottom");
                } else {
                    PreferUtils.putString(context, "scrollToWhere", "top");
                    to_top.setVisibility(View.GONE);
                    String currentColor = PreferUtils.getString(context, "currentColor");
                    if (!TextUtils.isEmpty(currentColor)) {
                        if (currentColor.length() == 7 && currentColor.substring(0, 1).equals("#")) {
                            setColorChange(Color.parseColor(currentColor));
                        } else {
                            setColorChange(Color.parseColor("#000000"));
                        }
                    } else {
                        setColorChange(Color.parseColor("#000000"));
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

    DecoratorViewPager screen_viewpager;
    LinearLayout screen_point;
    private GViewPagerAdapter adapters;
    ImageView iv_theme, iv_list_bg;
    RecyclerView recyclerview_horizontal;
    RecyclerView recyclerview_vertical;
    RecyclerView recyclerview_hours_hot;
    LinearLayout ll_theme_parent;

    private void initbannerview() {
        view_color = headView.findViewById(R.id.view_color);
        viewpager = (ViewPager) headView.findViewById(R.id.viewpager);
        llpoint = (LinearLayout) headView.findViewById(R.id.llpoint);
        upmarqueeview = (UPMarqueeView) headView.findViewById(R.id.upmarqueeview);
        viewpager_xin = (ViewPager) headView.findViewById(R.id.viewpager_xin);
        llpoint_xin = (LinearLayout) headView.findViewById(R.id.llpoint_xin);
        screen_viewpager = (DecoratorViewPager) headView.findViewById(R.id.screen_viewpager);
        screen_point = (LinearLayout) headView.findViewById(R.id.screen_point);
        /*活动主题大布局*/
        ll_theme_parent = (LinearLayout) headView.findViewById(R.id.ll_theme_parent);
        if (TextUtils.isEmpty(is_index_activity)) {
            ll_theme_parent.setVisibility(View.GONE);
        } else {
            if (is_index_activity.equals("no")) {
                ll_theme_parent.setVisibility(View.GONE);
            } else {
                ll_theme_parent.setVisibility(View.VISIBLE);
            }
        }
        iv_theme = (ImageView) headView.findViewById(R.id.iv_theme);
        iv_list_bg = (ImageView) headView.findViewById(R.id.iv_list_bg);
        /*横向布局*/
        recyclerview_horizontal = (RecyclerView) headView.findViewById(R.id.recyclerview_horizontal);
        recyclerview_horizontal.setHasFixedSize(true);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(context, 2));
        int space = DensityUtils.dip2px(context, 10);
        recyclerview_horizontal.addItemDecoration(new HorizontalItem(space));
        /*竖直布局*/
        recyclerview_vertical = (RecyclerView) headView.findViewById(R.id.recyclerview_vertical);
        recyclerview_vertical.setHasFixedSize(true);
        recyclerview_vertical.setLayoutManager(new GridLayoutManager(context, 4));
        space = DensityUtils.dip2px(context, 10);
        recyclerview_vertical.addItemDecoration(new HorizontalItem(space));
        /*24小时热播榜*/
        recyclerview_hours_hot = (RecyclerView) headView.findViewById(R.id.recyclerview_hours_hot);
        recyclerview_hours_hot.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview_hours_hot.setLayoutManager(linearLayoutManager);
        space = DensityUtils.dip2px(context, 10);
        recyclerview_hours_hot.addItemDecoration(new HotItem(space));
        getBannerData();/*轮播图*/
        getBuyData();/*实时报播*/
        getNewClassicData();/*分屏数据*/
        getXinShuoData();/*新手教程*/
        getThemeData();/*主题活动数据*/
        getHoursHotListData();/*24小时热播榜*/
        viewpager.addOnPageChangeListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 20;
        llpoint.setLayoutParams(params);
        llpoint_xin.setLayoutParams(params);
        initTimer();
        setOnTouchView();
        initViewPagerXin();
        adapters = new GViewPagerAdapter();
        screen_viewpager.setAdapter(adapters);
    }

    private void initViewPagerXin() {
        viewpager_xin.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicatorXin(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        screen_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator_grid(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    /*设置指示器*/
    private void setIndicatorXin(int selectedPosition) {
        for (int i = 0; i < indicators_xin.length; i++) {
            indicators_xin[i].setBackgroundResource(R.drawable.indicator_unselected1);
        }
        indicators_xin[selectedPosition % indicators_xin.length].setBackgroundResource(R.drawable.indicator_selected1);
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
            View view = LayoutInflater.from(context).inflate(R.layout.banner_viewpager, container, false);
            RoundedImageView iv = (RoundedImageView) view.findViewById(R.id.iv);
            Glide.with(context).load(banner_result.get(position % banner_result.size()).getImage()).into(iv);
            container.addView(view);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferUtils.getBoolean(context, "isLogin")) {
                        String url = banner_result.get(position % banner_result.size()).getUrl();/*跳转地址*/
                        String type = banner_result.get(position % banner_result.size()).getType();/*跳转类型*/
                        String extend = banner_result.get(position % banner_result.size()).getExtend();/*轮播图标题*/
                        if (!TextUtils.isEmpty(type)) {
                            switch (type) {
                                case "normal":
                                    /*普通链接地址*/
                                    intent = new Intent(context, BaseH5Activity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "tmall":
                                    /*淘宝天猫会场地址*/
                                    intent = new Intent(context, TaoBaoAndTianMaoUrlActivity.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("title", extend);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "local_goods":
                                    /*本地商品进商品详情*/
                                    getShopBasicData(url);
                                    break;
                                case "xinshou":
                                    /*新手教程主题*/
                                    intent = new Intent(context, XinShouJiaoChengActivity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "app_theme":
                                    /*app主题*/
                                    intent = new Intent(context, BaseH5Activity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                                    intent = new Intent(context, TaobaoTianMaoHolidayOfActivity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    } else {
                        startToLoginActivity();
                    }
                }
            });
            return view;
        }
    }

    /*新手教程适配器*/
    private class XinShouJiaoAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return xin_list == null ? 0 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.xinshoujiao, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            Glide.with(context).load(xin_list.get(position % xin_list.size()).getImage()).into(iv);
            container.addView(view);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferUtils.getBoolean(context, "isLogin")) {
                        String url = xin_list.get(position % xin_list.size()).getUrl();/*跳转地址*/
                        String type = xin_list.get(position % xin_list.size()).getType();/*跳转类型*/
                        String title = xin_list.get(position % xin_list.size()).getTitle();/*标题*/
                        if (!TextUtils.isEmpty(type)) {
                            switch (type) {
                                case "xinshou":
                                    /*新手教程界面*/
                                    intent = new Intent(context, XinShouJiaoChengActivity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "tmall":
                                    /*淘宝天猫会场活动*/
                                    intent = new Intent(context, TaoBaoAndTianMaoUrlActivity.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("title", title);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "normal":
                                    /*普通链接*/
                                    intent = new Intent(context, BaseH5Activity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "app_theme":
                                    /*app主题*/
                                    intent = new Intent(context, BaseH5Activity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                case "local_goods":
                                    /*实例商品到商品详情*/
                                    getShopBasicData(url);
                                    break;
                                case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                                    intent = new Intent(context, TaobaoTianMaoHolidayOfActivity.class);
                                    intent.putExtra("url", url);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    } else {
                        startToLoginActivity();
                    }
                }
            });
            return view;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        String s = "#" + banner_result.get(position % banner_result.size()).getTitle();
        int i;
        if (!TextUtils.isEmpty(s)) {
            if (s.length() == 7 && s.substring(0, 1).equals("#")) {
                PreferUtils.putString(context, "currentColor", s);
                i = Color.parseColor(s);
            } else {
                PreferUtils.putString(context, "currentColor", "#000000");
                i = Color.parseColor("#000000");
            }
        } else {
            PreferUtils.putString(context, "currentColor", "#000000");
            i = Color.parseColor("#000000");
        }
        if (!TextUtils.isEmpty(i + "")) {
            view_color.setBackgroundColor(i);
            ll_parent.setBackgroundColor(i);
            if (re_tablayout_parent != null) {
                re_tablayout_parent.setBackgroundColor(i);
            }
            if (re_search_title != null) {
                re_search_title.setBackgroundColor(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                /*状态栏*/
                window.setStatusBarColor(i);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        setIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


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

    @SuppressLint("HandlerLeak")
    private Handler xinHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentItem = viewpager_xin.getCurrentItem();
            currentItem++;
            viewpager_xin.setCurrentItem(currentItem);
        }
    };


    TimerTask task, xinTask;
    private Timer timer, xinTimer;

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
            timer.schedule(task, 5000, 9000);
        }
    }

    private void initXinTimer() {
        xinTimer = new Timer();
        xinTask = new TimerTask() {
            @Override
            public void run() {
                if (isXinLoop) {
                    xinHandler.sendEmptyMessage(0);
                }
            }
        };
        if (isXinLoop) {
            xinTimer.schedule(xinTask, 4000, 8000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (xinTimer != null) {
            xinTimer.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    private void setColorChange(int colorChange) {
        if (!TextUtils.isEmpty(colorChange + "")) {
            view_color.setBackgroundColor(colorChange);
            ll_parent.setBackgroundColor(colorChange);
            if (re_tablayout_parent != null) {
                re_tablayout_parent.setBackgroundColor(colorChange);
            }
            if (re_search_title != null) {
                re_search_title.setBackgroundColor(colorChange);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                /*状态栏*/
                window.setStatusBarColor(colorChange);
            }
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
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, ""))
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
                                Intent intent = new Intent(context, ShopDetailActivity.class);
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
                                intent.putExtra("coupon_total", result.getCoupon_total());
                                intent.putExtra("coupon_id", result.getCoupon_id());
                                intent.putExtra(Constant.SHOP_REFERER, "local");/*商品来源*/
                                intent.putExtra(Constant.GAOYONGJIN_SOURCE, result.getSource());/*高佣金来源*/
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                String result = jsonObject.getString("result");
                                ToastUtils.showToast(context, result);
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

    /*首页分屏数据*/
    NewBannerBean newScreenBean;
    List<NewBannerBean.ResultBean> screenList;

    private void getNewClassicData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "navig_app_page");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(context))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(context, ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("新版分屏数据类型", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                newScreenBean = GsonUtil.GsonToBean(response.toString(), NewBannerBean.class);
                                if (newScreenBean == null) return;
                                screenList = newScreenBean.getResult();
                                if (screenList.size() == 0) return;
                                initScreenDataView();
                                if (screenList.size() > 10) {
                                    screen_point.setVisibility(View.VISIBLE);
                                } else {
                                    screen_point.setVisibility(View.GONE);
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

    private List<NewBanDataBean> dataList = new ArrayList<>();
    private List<GridView> gridList = new ArrayList<>();
    private ImageView[] indicator_grid;
    MultiGridView gridView;

    /*分屏初始化*/
    private void initScreenDataView() {
        if (dataList.size() > 0) {
            dataList.clear();
        }
        if (gridList.size() > 0) {
            gridList.clear();
        }
        //初始化数据
        for (int i = 0; i < screenList.size(); i++) {
            NewBanDataBean bean = new NewBanDataBean();
            bean.title = screenList.get(i).getTitle();
            bean.image = screenList.get(i).getImage();
            bean.url = screenList.get(i).getUrl();
            bean.extend = screenList.get(i).getExtend();
            bean.type = screenList.get(i).getType();
            dataList.add(bean);
        }
        //计算viewpager一共显示几页
        int pageSize = dataList.size() % item_grid_num == 0 ? dataList.size() / item_grid_num : dataList.size() / item_grid_num + 1;
        indicator_grid = new ImageView[pageSize];
        for (int i = 0; i < pageSize; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_grid_viewpager, null);
            ImageView iv = (ImageView) view.findViewById(R.id.image_indicator);
            indicator_grid[i] = iv;
            screen_point.addView(view);
        }
        setIndicator_grid(0);
        for (int i = 0; i < pageSize; i++) {
            gridView = new MultiGridView(context);
            GridViewAdapter adapter = new GridViewAdapter(dataList, i);
            gridView.setNumColumns(number_columns);
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉默认点击背景
            gridView.setAdapter(adapter);
            gridList.add(gridView);
        }
        adapters.add(gridList);
    }

    private void setIndicator_grid(int selectedPosition) {
        for (int i = 0; i < indicator_grid.length; i++) {
            indicator_grid[i].setBackgroundResource(R.mipmap.grid_unselect);
        }
        indicator_grid[selectedPosition % indicator_grid.length].setBackgroundResource(R.mipmap.grid_select);
    }

    private void setHeadListener(final ThemeBean.ThemeData head_theme_data) {
        iv_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferUtils.getBoolean(context, "isLogin")) {
                    String type = head_theme_data.getType();
                    String extend = head_theme_data.getExtend();
                    String title = head_theme_data.getTitle();
                    switch (type) {
                        case "xinshou":
                            /*新手教程界面*/
                            intent = new Intent(context, XinShouJiaoChengActivity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "tmall":
                            /*淘宝天猫会场活动*/
                            intent = new Intent(context, TaoBaoAndTianMaoUrlActivity.class);
                            intent.putExtra("url", extend);
                            intent.putExtra("title", title);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "normal":
                            /*普通链接*/
                            intent = new Intent(context, BaseH5Activity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "app_theme":
                            /*app主题*/
                            intent = new Intent(context, BaseH5Activity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "local_goods":
                            /*实例商品到商品详情*/
                            getShopBasicData(extend);
                            break;
                        case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                            intent = new Intent(context, TaobaoTianMaoHolidayOfActivity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                    }
                } else {
                    startToLoginActivity();
                }
            }
        });
    }

    private void setHomeHorizontalAdapterListener() {
        homeHorizontalAdapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                if (PreferUtils.getBoolean(context, "isLogin")) {
                    String type = horizontalList.get(position).getType();
                    String title = horizontalList.get(position).getTitle();
                    String extend = horizontalList.get(position).getExtend();
                    switch (type) {
                        case "xinshou":
                            /*新手教程界面*/
                            intent = new Intent(context, XinShouJiaoChengActivity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "tmall":
                            /*淘宝天猫会场活动*/
                            intent = new Intent(context, TaoBaoAndTianMaoUrlActivity.class);
                            intent.putExtra("url", extend);
                            intent.putExtra("title", title);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "normal":
                            /*普通链接*/
                            intent = new Intent(context, BaseH5Activity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "app_theme":
                            /*app主题*/
                            intent = new Intent(context, BaseH5Activity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "local_goods":
                            /*实例商品到商品详情*/
                            getShopBasicData(extend);
                            break;
                        case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                            intent = new Intent(context, TaobaoTianMaoHolidayOfActivity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                    }
                } else {
                    startToLoginActivity();
                }
            }
        });
    }

    private void setHomeVerticalAdapterListener() {
        homeVerticalAdapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                if (PreferUtils.getBoolean(context, "isLogin")) {
                    String type = verticalList.get(position).getType();
                    String title = verticalList.get(position).getTitle();
                    String extend = verticalList.get(position).getExtend();
                    switch (type) {
                        case "xinshou":
                            /*新手教程界面*/
                            intent = new Intent(context, XinShouJiaoChengActivity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "tmall":
                            /*淘宝天猫会场活动*/
                            intent = new Intent(context, TaoBaoAndTianMaoUrlActivity.class);
                            intent.putExtra("url", extend);
                            intent.putExtra("title", title);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "normal":
                            /*普通链接*/
                            intent = new Intent(context, BaseH5Activity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "app_theme":
                            /*app主题*/
                            intent = new Intent(context, BaseH5Activity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case "local_goods":
                            /*实例商品到商品详情*/
                            getShopBasicData(extend);
                            break;
                        case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                            intent = new Intent(context, TaobaoTianMaoHolidayOfActivity.class);
                            intent.putExtra("url", extend);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                    }
                } else {
                    startToLoginActivity();
                }
            }
        });
    }

    /*跳转到登录界面*/
    private void startToLoginActivity() {
        intent = new Intent(context, LoginAndRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
