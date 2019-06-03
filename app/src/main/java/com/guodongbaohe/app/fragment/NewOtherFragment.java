package com.guodongbaohe.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.NewSecondClassicActivity;
import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.adapter.NinePinkageAdapter;
import com.guodongbaohe.app.adapter.OtherCommonHeadAdapter;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

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

public class NewOtherFragment extends Fragment {
    private View view;
    int which_position;
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
    //分类参数
    private String type = "";
    //颜色值变化
    private int colorId = 1;
    private List<HomeListBean.ListData> list = new ArrayList<>();
    NinePinkageAdapter ninePinkageAdapter;
    private String cate_id, label;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            which_position = arguments.getInt( "which_position" );
            cate_id = arguments.getString( "cate_id" );
            label = arguments.getString( "label" );
            Log.i( "分类id", cate_id + "  " + which_position + "  label  " + label );
        }
        super.onCreate( savedInstanceState );
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                //用户退出
                pageNum = 1;
                getListData();
                userLevelHeadChange();
                userLevelChange();
                break;
            case Constant.LOGINSUCCESS:
                //登录成功
                pageNum = 1;
                getListData();
                userLevelHeadChange();
                userLevelChange();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                //用户等级升级成功
                pageNum = 1;
                getListData();
                userLevelHeadChange();
                userLevelChange();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    //佣金和人气切换（初始布局）
    private void userLevelChange() {
        if (PreferUtils.getBoolean( context, "isLogin" )) {
            String member_role = PreferUtils.getString( context, "member_role" );
            if (Constant.COMMON_USER_LEVEL.contains( member_role )) {
                //普通用户
                tv_renqi.setText( "人气" );
            } else {
                //vip及以上
                tv_renqi.setText( "佣金" );
            }
        } else {
            tv_renqi.setText( "人气" );
        }
    }

    //佣金和人气切换（头部布局）
    private void userLevelHeadChange() {
        if (PreferUtils.getBoolean( context, "isLogin" )) {
            String member_role = PreferUtils.getString( context, "member_role" );
            if (Constant.COMMON_USER_LEVEL.contains( member_role )) {
                //普通用户
                renqi.setText( "人气" );
            } else {
                //vip及以上
                renqi.setText( "佣金" );
            }
        } else {
            renqi.setText( "人气" );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate( R.layout.newotherfragment, container, false );
            ButterKnife.bind( this, view );
            context = MyApplication.getInstance();
            EventBus.getDefault().register( this );
            initView();
            getListData();
            userLevelChange();
        }
        return view;
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

    CommonBean commonBean;
    List<CommonBean.CommonResult> result;

    private void getHeadData() {
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.GOODS_CATES )
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
                        Log.i( "头部数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                commonBean = GsonUtil.GsonToBean( response.toString(), CommonBean.class );
                                if (commonBean == null) return;
                                result = commonBean.getResult();
                                if (result.size() > 0) {
                                    final List<CommonBean.CommonSecond> childList = result.get( which_position ).getChild();
                                    otherCommonHeadAdapter = new OtherCommonHeadAdapter( childList );
                                    recyclerview.setAdapter( otherCommonHeadAdapter );
                                    otherCommonHeadAdapter.setonclicklistener( new OnItemClick() {
                                        @Override
                                        public void OnItemClickListener(View view, int position) {
                                            Intent intent = new Intent( context, NewSecondClassicActivity.class );
                                            intent.putExtra( "name", childList.get( position ).getName() );
                                            intent.putExtra( "cate_id", childList.get( position ).getCate_id() );
                                            intent.putExtra( "parent_id", result.get( which_position ).getCate_id() );
                                            startActivity( intent );
                                        }
                                    } );
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

    private void initView() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new GridLayoutManager( context, 2 ) );
        XRecyclerViewUtil.setView( xrecycler );
        ninePinkageAdapter = new NinePinkageAdapter( context, list );
        xrecycler.setAdapter( ninePinkageAdapter );
        View view = LayoutInflater.from( context ).inflate( R.layout.other_head_view, null );
        xrecycler.addHeaderView( view );
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getListData();
                getHeadData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getListData();
            }
        } );
        initHeadView( view );
        ninePinkageAdapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 2;
                Intent intent = new Intent( context, ShopDetailActivity.class );
                intent.putExtra( "goods_id", list.get( pos ).getGoods_id() );
                intent.putExtra( "cate_route", list.get( pos ).getCate_route() );/*类目名称*/
                intent.putExtra( "cate_category", list.get( pos ).getCate_category() );
                intent.putExtra( "attr_price", list.get( pos ).getAttr_price() );
                intent.putExtra( "attr_prime", list.get( pos ).getAttr_prime() );
                intent.putExtra( "attr_ratio", list.get( pos ).getAttr_ratio() );
                intent.putExtra( "sales_month", list.get( pos ).getSales_month() );
                intent.putExtra( "goods_name", list.get( pos ).getGoods_name() );/*长标题*/
                intent.putExtra( "goods_short", list.get( pos ).getGoods_short() );/*短标题*/
                intent.putExtra( "seller_shop", list.get( pos ).getSeller_shop() );/*店铺姓名*/
                intent.putExtra( "goods_thumb", list.get( pos ).getGoods_thumb() );/*单图*/
                intent.putExtra( "goods_gallery", list.get( pos ).getGoods_gallery() );/*多图*/
                intent.putExtra( "coupon_begin", list.get( pos ).getCoupon_begin() );/*开始时间*/
                intent.putExtra( "coupon_final", list.get( pos ).getCoupon_final() );/*结束时间*/
                intent.putExtra( "coupon_surplus", list.get( pos ).getCoupon_surplus() );/*是否有券*/
                intent.putExtra( "coupon_explain", list.get( pos ).getGoods_slogan() );/*推荐理由*/
                intent.putExtra( "attr_site", list.get( pos ).getAttr_site() );/*天猫或者淘宝*/
                intent.putExtra( "coupon_total", list.get( pos ).getCoupon_total() );
                intent.putExtra( "coupon_id", list.get( pos ).getCoupon_id() );
                intent.putExtra( Constant.SHOP_REFERER, "local" );/*商品来源*/
                intent.putExtra( Constant.GAOYONGJIN_SOURCE, list.get( pos ).getSource() );/*高佣金来源*/
                startActivity( intent );
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
                if (i > 1200) {
                    to_top.setVisibility( View.VISIBLE );
                } else {
                    to_top.setVisibility( View.GONE );
                }
            }
        } );
    }

    OtherCommonHeadAdapter otherCommonHeadAdapter;
    TextView zuixin, xiaoliang, tv_jiage, renqi;
    int classicHeight, totalHeight;
    LinearLayout ll_head_parent;
    RecyclerView recyclerview;

    private void initHeadView(View view) {
        ll_head_parent = (LinearLayout) view.findViewById( R.id.ll_head_parent );
        recyclerview = (RecyclerView) view.findViewById( R.id.recyclerview );
        recyclerview.setHasFixedSize( true );
        recyclerview.setLayoutManager( new GridLayoutManager( context, 4 ) );
        getHeadData();
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

        classicHeight = DensityUtils.dip2px( context, 205 );
        totalHeight = DensityUtils.dip2px( context, 245 );

        xrecycler.addOnScrollListener( new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                View firstVisiableChildView = layoutManager.findViewByPosition( position );
                if (firstVisiableChildView == null) return;
                int itemHeight = firstVisiableChildView.getHeight();
                int scollYDistance = (position) * itemHeight - firstVisiableChildView.getTop();
                Log.i( "看看高度", scollYDistance + "   " + totalHeight + "  " + classicHeight );
                if (scollYDistance - totalHeight >= classicHeight) {
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
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
            }
        } );
    }

    private void setNewColor() {
        type = "";
        colorId = 1;
        zuixin.setTextColor( 0xfff6c15b );
        xiaoliang.setTextColor( 0xff000000 );
        tv_jiage.setTextColor( 0xff000000 );
        renqi.setTextColor( 0xff000000 );
        pageNum = 1;
        xrecycler.refresh();
    }

    private void setXiaoliangColor() {
        type = "sales";
        colorId = 2;
        zuixin.setTextColor( 0xff000000 );
        xiaoliang.setTextColor( 0xfff6c15b );
        tv_jiage.setTextColor( 0xff000000 );
        renqi.setTextColor( 0xff000000 );
        pageNum = 1;
        xrecycler.refresh();
    }

    private void setPriceColor() {
        type = "price";
        colorId = 3;
        zuixin.setTextColor( 0xff000000 );
        xiaoliang.setTextColor( 0xff000000 );
        tv_jiage.setTextColor( 0xfff6c15b );
        renqi.setTextColor( 0xff000000 );
        pageNum = 1;
        xrecycler.refresh();
    }

    private void setRenqiColor() {
        type = "commission";
        colorId = 4;
        zuixin.setTextColor( 0xff000000 );
        xiaoliang.setTextColor( 0xff000000 );
        tv_jiage.setTextColor( 0xff000000 );
        renqi.setTextColor( 0xfff6c15b );
        pageNum = 1;
        xrecycler.refresh();
    }

    private void getListData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "sort", type );
        map.put( "supid", cate_id );
        map.put( "label", label );
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "limit", "12" );
        String mapParam = ParamUtil.getMapParam( map );
        Log.i( "列表数据参数其他", Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam )
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
                                HomeListBean bean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (bean == null) return;
                                List<HomeListBean.ListData> result = bean.getResult();
                                if (result.size() == 0) {
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
                                        ninePinkageAdapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll( result );
                                        ninePinkageAdapter.notifyDataSetChanged();
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
                        ToastUtils.showToast( context, Constant.NONET );
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                } );
    }
}
