package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.JhsAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.FourIVBean;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NinePinkageActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.ll_hover)
    LinearLayout ll_hover;
    @BindView(R.id.tv_newly)
    TextView tv_newly;
    @BindView(R.id.tv_sale)
    TextView tv_sale;
    @BindView(R.id.tv_renqi)
    TextView tv_renqi;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.to_top)
    ImageView to_top;
    private int pageNum = 1;
    private List<HomeListBean.ListData> list = new ArrayList<>();
    JhsAdapter adapter;
    int height, head_view_hight;
    /*分类参数*/
    private String type = "";
    /*颜色值变化*/
    private int colorId = 1;

    @Override
    public int getContainerView() {
        return R.layout.ninepinkageactivity;
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                xrecycler.refresh();
                userLevelChange();
                userLevelHeadChange();
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                xrecycler.refresh();
                userLevelChange();
                userLevelHeadChange();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                xrecycler.refresh();
                userLevelChange();
                userLevelHeadChange();
                break;
        }
    }

    /*佣金和人气切换（初始布局）*/
    private void userLevelChange() {
        if (PreferUtils.getBoolean( getApplicationContext(), "isLogin" )) {
            String member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
            if (Constant.COMMON_USER_LEVEL.contains( member_role )) {
                /*普通用户*/
                tv_renqi.setText( "人气" );
            } else {
                /*vip及以上*/
                tv_renqi.setText( "佣金" );
            }
        } else {
            tv_renqi.setText( "人气" );
        }
    }

    /*佣金和人气切换（头部布局）*/
    private void userLevelHeadChange() {
        if (PreferUtils.getBoolean( getApplicationContext(), "isLogin" )) {
            String member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
            if (Constant.COMMON_USER_LEVEL.contains( member_role )) {
                /*普通用户*/
                renqi.setText( "人气" );
            } else {
                /*vip及以上*/
                renqi.setText( "佣金" );
            }
        } else {
            renqi.setText( "人气" );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        EventBus.getDefault().register( this );
        String title = getIntent().getStringExtra( "title" );
        getData();
        setMiddleTitle( title );
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        XRecyclerViewUtil.setView( xrecycler );
        initView();
        userLevelChange();
    }

    @OnClick({R.id.tv_newly, R.id.tv_sale, R.id.tv_price, R.id.tv_renqi, R.id.to_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_newly:
                xrecycler.scrollToPosition( 0 );
                setNewColor();
                break;
            case R.id.tv_sale:
                xrecycler.scrollToPosition( 0 );
                setXiaoliangColor();
                break;
            case R.id.tv_price:
                xrecycler.scrollToPosition( 0 );
                setPriceColor();
                break;
            case R.id.tv_renqi:
                xrecycler.scrollToPosition( 0 );
                setRenqiColor();
                break;
            case R.id.to_top:
                xrecycler.scrollToPosition( 0 );
                break;
        }
    }

    RoundedImageView iv_head;

    private void initView() {
        height = DensityUtils.dip2px( getApplicationContext(), 180 );
        head_view_hight = DensityUtils.dip2px( getApplicationContext(), 220 );
        adapter = new JhsAdapter( list, getApplicationContext() );
        xrecycler.setAdapter( adapter );
        View view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.nine_head_view, null );
        iv_head = (RoundedImageView) view.findViewById( R.id.iv_head );
        xrecycler.addHeaderView( view );
        initHeadView( view );
        getFourHeadIVData();
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getData();
            }
        } );
        xrecycler.addOnScrollListener( new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                int i = recyclerView.computeVerticalScrollOffset();
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                View firstVisiableChildView = layoutManager.findViewByPosition( position );
                int itemHeight = firstVisiableChildView.getHeight();
                int scollYDistance = (position) * itemHeight - firstVisiableChildView.getTop();
                Log.i( "滑动", scollYDistance + "  banner高度  " + height + " 头部高度   " + head_view_hight );
                if (scollYDistance - head_view_hight >= height) {
                    ll_hover.setVisibility( View.VISIBLE );
                    switch (colorId) {
                        case 1:
                            tv_newly.setTextColor( 0xfff6c15b );
                            tv_sale.setTextColor( 0xff000000 );
                            tv_price.setTextColor( 0xff000000 );
                            tv_renqi.setTextColor( 0xff000000 );
                            break;
                        case 2:
                            tv_newly.setTextColor( 0xff000000 );
                            tv_sale.setTextColor( 0xfff6c15b );
                            tv_price.setTextColor( 0xff000000 );
                            tv_renqi.setTextColor( 0xff000000 );
                            break;
                        case 3:
                            tv_newly.setTextColor( 0xff000000 );
                            tv_sale.setTextColor( 0xff000000 );
                            tv_price.setTextColor( 0xfff6c15b );
                            tv_renqi.setTextColor( 0xff000000 );
                            break;
                        case 4:
                            tv_newly.setTextColor( 0xff000000 );
                            tv_sale.setTextColor( 0xff000000 );
                            tv_price.setTextColor( 0xff000000 );
                            tv_renqi.setTextColor( 0xfff6c15b );
                            break;
                    }
                } else {
                    ll_hover.setVisibility( View.GONE );
                }
                if (i > 1200) {
                    to_top.setVisibility( View.VISIBLE );
                } else {
                    to_top.setVisibility( View.GONE );
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
            }
        } );
        adapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 2;
                HomeListBean.ListData listData = list.get( pos );
                JumpToShopDetailUtil.start2ShopDetailOfListBean( getApplicationContext(), listData );
            }
        } );
    }

    TextView zuixin, xiaoliang, tv_jiage, renqi;

    private void initHeadView(View view) {
        zuixin = (TextView) view.findViewById( R.id.zuixin );
        xiaoliang = (TextView) view.findViewById( R.id.xiaoliang );
        tv_jiage = (TextView) view.findViewById( R.id.tv_jiage );
        renqi = (TextView) view.findViewById( R.id.renqi );
        zuixin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor();
            }
        } );
        xiaoliang.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setXiaoliangColor();
            }
        } );
        tv_jiage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPriceColor();
            }
        } );
        renqi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRenqiColor();
            }
        } );
        userLevelHeadChange();
    }

    private void setNewColor() {
        type = "";
        colorId = 1;
        zuixin.setTextColor( 0xfff6c15b );
        xiaoliang.setTextColor( 0xff000000 );
        tv_jiage.setTextColor( 0xff000000 );
        renqi.setTextColor( 0xff000000 );
        pageNum = 1;
        getData();
    }

    private void setXiaoliangColor() {
        type = "sales";
        colorId = 2;
        zuixin.setTextColor( 0xff000000 );
        xiaoliang.setTextColor( 0xfff6c15b );
        tv_jiage.setTextColor( 0xff000000 );
        renqi.setTextColor( 0xff000000 );
        pageNum = 1;
        getData();
    }

    private void setPriceColor() {
        type = "price";
        colorId = 3;
        zuixin.setTextColor( 0xff000000 );
        xiaoliang.setTextColor( 0xff000000 );
        tv_jiage.setTextColor( 0xfff6c15b );
        renqi.setTextColor( 0xff000000 );
        pageNum = 1;
        getData();
    }

    private void setRenqiColor() {
        type = "commission";
        colorId = 4;
        zuixin.setTextColor( 0xff000000 );
        xiaoliang.setTextColor( 0xff000000 );
        tv_jiage.setTextColor( 0xff000000 );
        renqi.setTextColor( 0xfff6c15b );
        pageNum = 1;
        getData();
    }

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "sort", type );
        map.put( "price", "9.9" );
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "limit", "12" );
        String mapParam = ParamUtil.getMapParam( map );
        Log.i( "9.9包邮接口", Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                HomeListBean bean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (bean == null) return;
                                List<HomeListBean.ListData> result = bean.getResult();
                                if (result.size() == 0) {
                                    xrecycler.setNoMore( true );
                                    xrecycler.refreshComplete();
                                    xrecycler.loadMoreComplete();
                                } else {
                                    boolean isLogin = PreferUtils.getBoolean( getApplicationContext(), "isLogin" );
                                    String son_count = PreferUtils.getString( getApplicationContext(), "son_count" );
                                    String member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
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
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                } );
    }


    /*四大模块头部图片数据*/
    List<FourIVBean.FourIVData> four_iv_list;

    private void getFourHeadIVData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "type", "button" );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.BANNER + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "四大模块头部图片数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                FourIVBean bean = GsonUtil.GsonToBean( response.toString(), FourIVBean.class );
                                if (bean == null) return;
                                four_iv_list = bean.getResult();
                                for (int i = 0; i < four_iv_list.size(); i++) {
                                    if (four_iv_list.get( i ).getUrl().equals( "jkj" )) {
                                        Glide.with( getApplicationContext() ).load( four_iv_list.get( i ).getImage() ).into( iv_head );
                                        return;
                                    }
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

}
