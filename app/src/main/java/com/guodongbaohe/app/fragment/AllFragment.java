package com.guodongbaohe.app.fragment;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.guodongbaohe.app.common.RectBannerLoader;
import com.guodongbaohe.app.common.RoundBannerLoader;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.gridview.DecoratorViewPager;
import com.guodongbaohe.app.gridview.GViewPagerAdapter;
import com.guodongbaohe.app.gridview.GridViewAdapter;
import com.guodongbaohe.app.gridview.MultiGridView;
import com.guodongbaohe.app.gridview.ZeroPointsGoodsActivity;
import com.guodongbaohe.app.itemdecoration.HorizontalItem;
import com.guodongbaohe.app.itemdecoration.HotItem;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.BannerOnClickListener;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.guodongbaohe.app.view.MarqueeTextView;
import com.guodongbaohe.app.view.UPMarqueeView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
    int height;
    String cate_id;
    private RelativeLayout re_tablayout_parent;
    private RelativeLayout re_search_title;
    private RelativeLayout re_parent_title;
    private RelativeLayout re_space_line;
    String notice_url, is_index_activity;
    Context context;
    private Banner viewpager_banner;
    private Banner viewpager_xin;
    View view_color;
    UPMarqueeView upmarqueeview;
    Intent intent;
    public static int item_grid_num = 10;//每一页中GridView中item的数量
    public static int number_columns = 5;//gridview一行展示的数目
    /*首页轮播数据*/
    List<BannerDataBean.BannerList> banner_result;
    BannerDataBean bannerDataBean;
    String member_id;
    /*新手教程数据*/
    BannerDataBean xinshoujiaochengbean;

    /*用户可见和不可见方法设置*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint( isVisibleToUser );
        if (isVisibleToUser) {
            if (viewpager_banner != null) {
                viewpager_banner.startAutoPlay();
            }
        } else {
            if (viewpager_banner != null) {
                viewpager_banner.stopAutoPlay();
            }
        }
    }

    public AllFragment() {
    }

    public AllFragment(RelativeLayout re_tablayout_parent, RelativeLayout re_search_title, RelativeLayout re_parent_title, RelativeLayout re_space_line) {
        this.re_tablayout_parent = re_tablayout_parent;
        this.re_search_title = re_search_title;
        this.re_parent_title = re_parent_title;
        this.re_space_line = re_space_line;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register( this );
        if (getArguments() != null) {
            height = getArguments().getInt( "height" );
            cate_id = getArguments().getString( "cate_id" );
        }
        super.onCreate( savedInstanceState );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate( R.layout.allfragment, container, false );
            ButterKnife.bind( this, view );
            context = MyApplication.getInstance();
            String notice_title = PreferUtils.getString( context, "notice_title" );
            notice_url = PreferUtils.getString( context, "notice_url" );
            is_index_activity = PreferUtils.getString( context, "is_index_activity" );
            initRecyclerview();
            getListData( 0 );
            if (TextUtils.isEmpty( notice_title )) {
                re_notice.setVisibility( View.INVISIBLE );
            } else {
                re_notice.setVisibility( View.VISIBLE );
                re_notice.getBackground().setAlpha( 220 );
                tv_notice.setTextColor( 0xff8c5727 );
                tv_notice.setSelected( true );
                tv_notice.setText( PreferUtils.getString( context, "notice_title" ) );
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
        map.put( "type", "index_activity_test" );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.BANNER + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "查看数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                themeBean = GsonUtil.GsonToBean( response.toString(), ThemeBean.class );
                                if (themeBean == null) {
                                    ll_theme_parent.setVisibility( View.GONE );
                                    return;
                                }
                                theme_list = themeBean.getResult();
                                if (theme_list.size() == 0) {
                                    ll_theme_parent.setVisibility( View.GONE );
                                    return;
                                }
                                /*主题头部图片*/
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get( i ).getUrl().equals( "center" )) {
                                        Glide.with( context ).load( theme_list.get( i ).getImage() ).into( iv_theme );
                                        head_theme_data = theme_list.get( i );
                                    }
                                }
                                setHeadListener( head_theme_data );/*头部点击*/
                                /*布局背景图*/
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get( i ).getUrl().equals( "background" )) {
                                        Glide.with( context ).load( theme_list.get( i ).getImage() ).into( iv_list_bg );
                                    }
                                }
                                /*横向列表数据显示*/
                                horizontalList = new ArrayList<>();
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get( i ).getUrl().equals( "horizontal" )) {
                                        ThemeBean.ThemeData themeData = theme_list.get( i );
                                        HomeHorizontalListBean bean = new HomeHorizontalListBean();
                                        bean.setExtend( themeData.getExtend() );
                                        bean.setImage( themeData.getImage() );
                                        bean.setSort( themeData.getSort() );
                                        bean.setTitle( themeData.getTitle() );
                                        bean.setUrl( themeData.getUrl() );
                                        bean.setType( themeData.getType() );
                                        horizontalList.add( bean );
                                    }
                                }
                                homeHorizontalAdapter = new HomeHorizontalAdapter( context, horizontalList );
                                recyclerview_horizontal.setAdapter( homeHorizontalAdapter );
                                setHomeHorizontalAdapterListener();
                                /*竖直列表数据显示*/
                                verticalList = new ArrayList<>();
                                for (int i = 0; i < theme_list.size(); i++) {
                                    if (theme_list.get( i ).getUrl().equals( "vertical" )) {
                                        ThemeBean.ThemeData themeData = theme_list.get( i );
                                        HomeHorizontalListBean bean = new HomeHorizontalListBean();
                                        bean.setExtend( themeData.getExtend() );
                                        bean.setImage( themeData.getImage() );
                                        bean.setSort( themeData.getSort() );
                                        bean.setTitle( themeData.getTitle() );
                                        bean.setUrl( themeData.getUrl() );
                                        bean.setType( themeData.getType() );
                                        verticalList.add( bean );
                                    }
                                }
                                homeVerticalAdapter = new HomeVerticalAdapter( context, verticalList );
                                recyclerview_vertical.setAdapter( homeVerticalAdapter );
                                setHomeVerticalAdapterListener();
                                ViewGroup.LayoutParams layoutParams = iv_list_bg.getLayoutParams();
                                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                                int widthPixels = displayMetrics.widthPixels;
                                int second_hight = (widthPixels - DensityUtils.dip2px( context, 24 )) / 3 / 25 * 37 * verticalList.size() / 3;
                                int first_height = (widthPixels - DensityUtils.dip2px( context, 18 )) / 2 * 47 / 100 * 2;
                                int space_height;
                                if (verticalList.size() / 3 == 1) {
                                    space_height = DensityUtils.dip2px( getContext(), 32 );
                                } else if (verticalList.size() == 0) {
                                    space_height = DensityUtils.dip2px( getContext(), 26 );
                                } else {
                                    space_height = DensityUtils.dip2px( getContext(), 38 );
                                }
                                layoutParams.height = second_hight + first_height + space_height;
                                iv_list_bg.setLayoutParams( layoutParams );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                } );
    }

    /*24小时排行榜*/
    HomeListBean hoursHotBean;

    private void getHoursHotListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "page", "1" );
        map.put( "rank", "day" );
        map.put( "limit", "20" );
        final String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.RANKINGLIST + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                hoursHotBean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (hoursHotBean == null) return;
                                final List<HomeListBean.ListData> hoursList = hoursHotBean.getResult();
                                if (hoursList.size() == 0) return;
                                HoursHortAdapter hoursHortAdapter = new HoursHortAdapter( context, hoursList );
                                recyclerview_hours_hot.setAdapter( hoursHortAdapter );
                                hoursHortAdapter.setonclicklistener( new OnItemClick() {
                                    @Override
                                    public void OnItemClickListener(View view, int pos) {
                                        HomeListBean.ListData listData = hoursList.get( pos );
                                        JumpToShopDetailUtil.start2ShopDetailOfListBean( context, listData );
                                    }
                                } );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                } );
    }

    @OnClick({R.id.notice_ca, R.id.to_top, R.id.re_notice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notice_ca:
                re_notice.setVisibility( View.INVISIBLE );
                break;
            case R.id.to_top:
                xrecycler.scrollToPosition( 0 );
                break;
            case R.id.re_notice:
                if (!TextUtils.isEmpty( notice_url )) {
                    intent = new Intent( context, XinShouJiaoChengActivity.class );
                    intent.putExtra( "url", notice_url );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity( intent );
                }
                break;
        }
    }

    private void getXinShuoData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "type", "xinshou" );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.BANNER + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "新手教程", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                xinshoujiaochengbean = GsonUtil.GsonToBean( response.toString(), BannerDataBean.class );
                                if (xinshoujiaochengbean == null) return;
                                if (xinshoujiaochengbean.getResult().size() == 0) return;
                                viewpager_xin.setOnBannerListener( new BannerOnClickListener( getContext(), xinshoujiaochengbean.getResult() ) );
                                viewpager_xin.setImages( xinshoujiaochengbean.getResult() );
                                viewpager_xin.setImageLoader( new RectBannerLoader() );
                                viewpager_xin.start();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                } );

    }

    /*列表数据*/
    private void getListData(int mode) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "limit", "20" );
        if (mode == 0) {
            map.put( "supid", cate_id );
        }
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "label", "today" );
        String param = ParamUtil.getMapParam( map );
        Log.i( "列表数据参数其他0", Constant.BASE_URL + Constant.SHOP_LIST + "?" + param );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.SHOP_LIST + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "列表数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                HomeListBean bean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (bean == null) return;
                                List<HomeListBean.ListData> result = bean.getResult();
                                if (result.size() == 0) {
                                    getListData( -1 );
                                    xrecycler.setNoMore( true );
                                    xrecycler.refreshComplete();
                                    xrecycler.loadMoreComplete();
                                } else {
                                    boolean isLogin = PreferUtils.getBoolean( context, "isLogin" );
                                    String son_count = PreferUtils.getString( context, "son_count" );
                                    String member_role = PreferUtils.getString( context, "member_role" );
                                    for (HomeListBean.ListData listData : result) {
                                        listData.setLogin( isLogin );
                                        listData.setSon_count( son_count );
                                        listData.setMember_role( member_role );
                                    }
                                    if (pageNum == 1) {
                                        list.clear();
                                        list.addAll( result );
                                        adapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll( result );
                                        adapter.notifyDataSetChanged();
                                        xrecycler.loadMoreComplete();
                                    }
                                }
                            } else if (jsonObject.getInt( "status" ) == -1003) {/*超出最大页码*/
                                getListData( -1 );
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
                        ToastUtils.showToast( context, Constant.NONET );
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                } );
    }

    private void getBannerData() {
        member_id = PreferUtils.getString( context, "member_id" );
        if (TextUtils.isEmpty( member_id )) {
            member_id = "";
        }
        url = Constant.BASE_URL + Constant.BANNER + "/";
        MyApplication.getInstance().getMyOkHttp().post().url( url ).tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", member_id )
                .tag( this )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "banner数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                bannerDataBean = GsonUtil.GsonToBean( response.toString(), BannerDataBean.class );
                                if (bannerDataBean == null) return;
                                banner_result = bannerDataBean.getResult();
                                if (banner_result.size() == 0) return;
                                viewpager_banner.setOnBannerListener( new BannerOnClickListener( getContext(), banner_result ) );
                                viewpager_banner.setImageLoader( new RoundBannerLoader() ).setImages( banner_result ).start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getActivity(), Constant.NONET );
                    }
                } );
    }

    List<View> views;

    /*随机用户佣金接口*/
    private void getBuyData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", "100" );
        map.put( "rows", "100" );
        String mapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( mapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GOODSDETAIL_BUY_NUMS + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-userid", "100" )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "100" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "用户数量接口返回", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                BuyUserBean bean = GsonUtil.GsonToBean( response.toString(), BuyUserBean.class );
                                if (bean == null || bean.getResult().size() == 0) return;
                                List<BuyUserBean.BuyUser> userList = bean.getResult();
                                views = new ArrayList<>();
                                for (int i = 0; i < userList.size(); i++) {
                                    View view = LayoutInflater.from( context ).inflate( R.layout.item_home_news_scroll, null );
                                    TextView name = (TextView) view.findViewById( R.id.name );
                                    TextView time = (TextView) view.findViewById( R.id.time );
                                    TextView tmoney = (TextView) view.findViewById( R.id.money );
                                    String member_name = userList.get( i ).getMember_name();
                                    String order_time = userList.get( i ).getOrder_time();
                                    String money = userList.get( i ).getMoney();
                                    name.setText( member_name );
                                    time.setText( "在" + order_time + "分钟前赚了" );
                                    tmoney.setText( money + "元" );
                                    views.add( view );
                                }
                                upmarqueeview.setViews( views );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    View headView;

    private void initRecyclerview() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( context ) );
        XRecyclerViewUtil.setView( xrecycler );
        headView = LayoutInflater.from( context ).inflate( R.layout.home_head_view, null );
        xrecycler.addHeaderView( headView );
        initbannerview();
        adapter = new HomeListAdapter( context, list );
        xrecycler.setAdapter( adapter );
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getListData( 0 );/*列表数据*/
                if (bannerDataBean == null) {
                    getBannerData();/*轮播图数据*/
                }
                getBuyData();/*实时报播数据*/
                if (newScreenBean == null) {
                    getNewClassicData();/*分屏数据*/
                }
                if (xinshoujiaochengbean == null) {
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
                getListData( 0 );
            }
        } );
        adapter.setOnClickListener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 2;
                HomeListBean.ListData listData = list.get( pos );
                JumpToShopDetailUtil.start2ShopDetailOfListBean( context, listData );
            }
        } );
        xrecycler.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                int i = recyclerView.computeVerticalScrollOffset();
                if (dy != 0 && i > 3000) {
                    to_top.setVisibility( View.VISIBLE );
                } else {
                    to_top.setVisibility( View.GONE );
                }
            }
        } );
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                pageNum = 1;
                getListData( 0 );
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                pageNum = 1;
                getListData( 0 );
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                pageNum = 1;
                getListData( 0 );
                break;
            case Constant.BANNER_IS_STOP_PLAY:
                if (viewpager_banner != null) {
                    viewpager_banner.stopAutoPlay();
                }
                break;
            case Constant.BANNER_IS_START_PLAY:
                if (viewpager_banner != null) {
                    viewpager_banner.startAutoPlay();
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
        view_color = headView.findViewById( R.id.view_color );
        viewpager_banner = headView.findViewById( R.id.viewpager_banner );
        upmarqueeview = headView.findViewById( R.id.upmarqueeview );
        viewpager_xin = headView.findViewById( R.id.viewpager_xin );
        screen_viewpager = headView.findViewById( R.id.screen_viewpager );
        screen_point = headView.findViewById( R.id.screen_point );
        /*活动主题大布局*/
        ll_theme_parent = headView.findViewById( R.id.ll_theme_parent );
        iv_list_bg = headView.findViewById( R.id.iv_list_bg );
        iv_theme = headView.findViewById( R.id.iv_theme );
        if (TextUtils.isEmpty( is_index_activity )) {
            ll_theme_parent.setVisibility( View.GONE );
        } else {
            if (is_index_activity.equals( "no" )) {
                ll_theme_parent.setVisibility( View.GONE );
            } else {
                ll_theme_parent.setVisibility( View.VISIBLE );
            }
        }
        /*横向布局*/
        recyclerview_horizontal = headView.findViewById( R.id.recyclerview_horizontal );
        recyclerview_horizontal.setHasFixedSize( true );
        recyclerview_horizontal.setLayoutManager( new GridLayoutManager( context, 2 ) );
        int space = DensityUtils.dip2px( context, 6 );
        recyclerview_horizontal.addItemDecoration( new HorizontalItem( space ) );
        /*竖直布局*/
        recyclerview_vertical = headView.findViewById( R.id.recyclerview_vertical );
        recyclerview_vertical.setHasFixedSize( true );
        recyclerview_vertical.setLayoutManager( new GridLayoutManager( context, 3 ) );
        recyclerview_vertical.addItemDecoration( new HorizontalItem( space ) );
        /*24小时热播榜*/
        recyclerview_hours_hot = headView.findViewById( R.id.recyclerview_hours_hot );
        recyclerview_hours_hot.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( context );
        linearLayoutManager.setOrientation( LinearLayoutManager.HORIZONTAL );
        recyclerview_hours_hot.setLayoutManager( linearLayoutManager );
        space = DensityUtils.dip2px( context, 10 );
        recyclerview_hours_hot.addItemDecoration( new HotItem( space ) );
        getBannerData();/*轮播图*/
        getBuyData();/*实时报播*/
        getNewClassicData();/*分屏数据*/
        getXinShuoData();/*新手教程*/
        getThemeData();/*主题活动数据*/
        getHoursHotListData();/*24小时热播榜*/
        viewpager_banner.setOnPageChangeListener( this );
        initViewPagerXin();
        adapters = new GViewPagerAdapter();
        screen_viewpager.setAdapter( adapters );
    }

    private void initViewPagerXin() {
        screen_viewpager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator_grid( position );
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        } );
    }

    String first_color, second_color;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        first_color = "#" + banner_result.get( position % banner_result.size() ).getTitle();
        if (position % banner_result.size() == banner_result.size() - 1) {
            second_color = "#" + banner_result.get( 0 ).getTitle();
        } else {
            second_color = "#" + banner_result.get( position % banner_result.size() + 1 ).getTitle();
        }
        int one_color = Color.parseColor( first_color );
        int two_color = Color.parseColor( second_color );
        int evaluate = (Integer) argbEvaluator.evaluate( positionOffset, one_color, two_color );
        view_color.setBackgroundColor( evaluate );
        ll_parent.setBackgroundColor( evaluate );
        if (re_tablayout_parent != null) {
            re_tablayout_parent.setBackgroundColor( evaluate );
        }
        if (re_search_title != null) {
            re_search_title.setBackgroundColor( evaluate );
        }
        if (re_parent_title != null) {
            re_parent_title.setBackgroundColor( evaluate );
        }
        if (re_space_line != null) {
            re_space_line.setBackgroundColor( evaluate );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getActivity() != null) {
                Window window = getActivity().getWindow();
                if (window != null) {
                    window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
                    /*状态栏*/
                    window.setStatusBarColor( evaluate );
                }
            }
        }

        if (!TextUtils.isEmpty( first_color )) {
            if (first_color.length() == 7 && first_color.substring( 0, 1 ).equals( "#" )) {
                PreferUtils.putString( context, "currentColor", first_color );
            } else {
                PreferUtils.putString( context, "currentColor", "#000000" );
            }
        } else {
            PreferUtils.putString( context, "currentColor", "#000000" );
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    /*商品详情头部信息*/
    private void getShopBasicData(String shopId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "goods_id", shopId );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                JumpToShopDetailUtil.start2ActivityOfHeadBean( context, result );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    /*首页分屏数据*/
    NewBannerBean newScreenBean;
    List<NewBannerBean.ResultBean> screenList;

    private void getNewClassicData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "type", "navig_app_page" );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.BANNER + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "新版分屏数据类型", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                newScreenBean = GsonUtil.GsonToBean( response.toString(), NewBannerBean.class );
                                if (newScreenBean == null) return;
                                screenList = newScreenBean.getResult();
                                if (screenList.size() == 0) return;
                                initScreenDataView();
                                if (screenList.size() > 10) {
                                    screen_point.setVisibility( View.VISIBLE );
                                } else {
                                    screen_point.setVisibility( View.GONE );
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                    }
                } );
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
            bean.title = screenList.get( i ).getTitle();
            bean.image = screenList.get( i ).getImage();
            bean.url = screenList.get( i ).getUrl();
            bean.extend = screenList.get( i ).getExtend();
            bean.type = screenList.get( i ).getType();
            dataList.add( bean );
        }
        //计算viewpager一共显示几页
        int pageSize = dataList.size() % item_grid_num == 0 ? dataList.size() / item_grid_num : dataList.size() / item_grid_num + 1;
        indicator_grid = new ImageView[pageSize];
        for (int i = 0; i < pageSize; i++) {
            View view = LayoutInflater.from( context ).inflate( R.layout.view_grid_viewpager, null );
            ImageView iv = (ImageView) view.findViewById( R.id.image_indicator );
            indicator_grid[i] = iv;
            screen_point.addView( view );
        }
        setIndicator_grid( 0 );
        for (int i = 0; i < pageSize; i++) {
            gridView = new MultiGridView( context );
            GridViewAdapter adapter = new GridViewAdapter( dataList, i );
            gridView.setNumColumns( number_columns );
            gridView.setSelector( new ColorDrawable( Color.TRANSPARENT ) );// 去掉默认点击背景
            gridView.setAdapter( adapter );
            gridList.add( gridView );
        }
        adapters.add( gridList );
    }

    private void setIndicator_grid(int selectedPosition) {
        for (int i = 0; i < indicator_grid.length; i++) {
            indicator_grid[i].setBackgroundResource( R.mipmap.grid_unselect );
        }
        indicator_grid[selectedPosition % indicator_grid.length].setBackgroundResource( R.mipmap.grid_select );
    }

    private void setHeadListener(final ThemeBean.ThemeData head_theme_data) {
        iv_theme.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferUtils.getBoolean( context, "isLogin" )) {
                    String type = head_theme_data.getType();
                    String extend = head_theme_data.getExtend();
                    String title = head_theme_data.getTitle();
                    switch (type) {
                        case "xinshou":
                            /*新手教程界面*/
                            intent = new Intent( context, XinShouJiaoChengActivity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "tmall":
                            /*淘宝天猫会场活动*/
                            intent = new Intent( context, TaoBaoAndTianMaoUrlActivity.class );
                            intent.putExtra( "url", extend );
                            intent.putExtra( "title", title );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "normal":
                            /*普通链接*/
                            intent = new Intent( context, BaseH5Activity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "app_theme":
                            /*app主题*/
                            intent = new Intent( context, BaseH5Activity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "local_goods":
                            /*实例商品到商品详情*/
                            getShopBasicData( extend );
                            break;
                        case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                            intent = new Intent( context, TaobaoTianMaoHolidayOfActivity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "ldms":/*0点秒杀*/
                            intent = new Intent( context, ZeroPointsGoodsActivity.class );
                            intent.putExtra( "goods_type", "ldms" );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "gysp":/*高佣金商品*/
                            intent = new Intent( context, ZeroPointsGoodsActivity.class );
                            intent.putExtra( "goods_type", "gysp" );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                    }
                } else {
                    startToLoginActivity();
                }
            }
        } );
    }

    private void setHomeHorizontalAdapterListener() {
        homeHorizontalAdapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                if (PreferUtils.getBoolean( context, "isLogin" )) {
                    String type = horizontalList.get( position ).getType();
                    String title = horizontalList.get( position ).getTitle();
                    String extend = horizontalList.get( position ).getExtend();
                    switch (type) {
                        case "xinshou":
                            /*新手教程界面*/
                            intent = new Intent( context, XinShouJiaoChengActivity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "tmall":
                            /*淘宝天猫会场活动*/
                            intent = new Intent( context, TaoBaoAndTianMaoUrlActivity.class );
                            intent.putExtra( "url", extend );
                            intent.putExtra( "title", title );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "normal":
                            /*普通链接*/
                            intent = new Intent( context, BaseH5Activity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "app_theme":
                            /*app主题*/
                            intent = new Intent( context, BaseH5Activity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "local_goods":
                            /*实例商品到商品详情*/
                            getShopBasicData( extend );
                            break;
                        case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                            intent = new Intent( context, TaobaoTianMaoHolidayOfActivity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "ldms":/*0点秒杀*/
                            intent = new Intent( context, ZeroPointsGoodsActivity.class );
                            intent.putExtra( "goods_type", "ldms" );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "gysp":/*高佣金商品*/
                            intent = new Intent( context, ZeroPointsGoodsActivity.class );
                            intent.putExtra( "goods_type", "gysp" );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                    }
                } else {
                    startToLoginActivity();
                }
            }
        } );
    }

    private void setHomeVerticalAdapterListener() {
        homeVerticalAdapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                if (PreferUtils.getBoolean( context, "isLogin" )) {
                    String type = verticalList.get( position ).getType();
                    String title = verticalList.get( position ).getTitle();
                    String extend = verticalList.get( position ).getExtend();
                    switch (type) {
                        case "xinshou":
                            /*新手教程界面*/
                            intent = new Intent( context, XinShouJiaoChengActivity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "tmall":
                            /*淘宝天猫会场活动*/
                            intent = new Intent( context, TaoBaoAndTianMaoUrlActivity.class );
                            intent.putExtra( "url", extend );
                            intent.putExtra( "title", title );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "normal":
                            /*普通链接*/
                            intent = new Intent( context, BaseH5Activity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "app_theme":
                            /*app主题*/
                            intent = new Intent( context, BaseH5Activity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "local_goods":
                            /*实例商品到商品详情*/
                            getShopBasicData( extend );
                            break;
                        case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                            intent = new Intent( context, TaobaoTianMaoHolidayOfActivity.class );
                            intent.putExtra( "url", extend );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "ldms":/*0点秒杀*/
                            intent = new Intent( context, ZeroPointsGoodsActivity.class );
                            intent.putExtra( "goods_type", "ldms" );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                        case "gysp":/*高佣金商品*/
                            intent = new Intent( context, ZeroPointsGoodsActivity.class );
                            intent.putExtra( "goods_type", "gysp" );
                            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity( intent );
                            break;
                    }
                } else {
                    startToLoginActivity();
                }
            }
        } );
    }

    /*跳转到登录界面*/
    private void startToLoginActivity() {
        intent = new Intent( context, LoginAndRegisterActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

}
