package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.BaseSearchAdapter;
import com.guodongbaohe.app.adapter.RelevanceAdapter;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.bean.FuzzyData;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.itemdecoration.VipItemDecoration;
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
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchResultActivity extends BigBaseActivity {
    /*返回键*/
    @BindView(R.id.iv_back)
    ImageView iv_back;
    /*搜索框*/
    @BindView(R.id.re_search)
    RelativeLayout re_search;
    /*搜索词*/
    @BindView(R.id.tv_search_name)
    TextView tv_search_name;
    /*综合*/
    @BindView(R.id.tv_paixu)
    TextView tv_paixu;
    /*销量*/
    @BindView(R.id.tv_xiaolinag)
    TextView tv_xiaolinag;
    /*价格*/
    @BindView(R.id.tv_price)
    TextView tv_price;
    /*佣金*/
    @BindView(R.id.tv_yongjin)
    TextView tv_yongjin;
    /*列表布局*/
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    /*无数据界面*/
    @BindView(R.id.nodata)
    RelativeLayout nodata;
    @BindView(R.id.nodata_name)
    TextView nodata_name;
    @BindView(R.id.switch_one)
    Switch switch_one;
    @BindView(R.id.to_top)
    ImageView to_top;
    /*关联搜索*/
    @BindView(R.id.relevance_recycler)
    RecyclerView relevance_recycler;
    private int pageNum = 1;
    /*综合字段*/
    private String type = "";
    String keyword;
    String son_count, member_role;
    /*测试全网数据集合*/
    List<AllNetBean.AllNetData> list = new ArrayList<>();
    BaseSearchAdapter adapter;
    Intent intent;
    /*优惠券显示字段*/
    private String has_coupon = "false";
    boolean isLogin;
    int space;
    int search_type;

    @Override
    protected void onResume() {
        super.onResume();
        userLevelChange();
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        if (msg.equals( Constant.SEARCH_BACK )) {
            finish();
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
        setContentView( R.layout.searchresultactivity );
        ButterKnife.bind( this );
        EventBus.getDefault().register( this );
        keyword = getIntent().getStringExtra( "keyword" );
        search_type = getIntent().getIntExtra( "search_type", 0 );
        isLogin = PreferUtils.getBoolean( getApplicationContext(), "isLogin" );
        son_count = PreferUtils.getString( getApplicationContext(), "son_count" );
        member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
        tv_search_name.setText( keyword );
        getAllNet();
        initXrecycler();
        initSwitch();
        relevanceView();
    }

    /*关联搜索布局*/
    private void relevanceView() {
        space = DensityUtils.dip2px( getApplicationContext(), 10 );
        relevance_recycler.setHasFixedSize( true );
        LinearLayoutManager manager = new LinearLayoutManager( getApplicationContext() );
        manager.setOrientation( LinearLayoutManager.HORIZONTAL );
        relevance_recycler.setLayoutManager( manager );
        RelevanceData();
    }

    /*关联搜索数据*/
    private void RelevanceData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "q", keyword );
        map.put( "code", "utf-8" );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.FUZZY_DATA + param ).tag( this )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "模糊搜索数据", response.toString() );
                        FuzzyData fuzzyData = GsonUtil.GsonToBean( response.toString(), FuzzyData.class );
                        if (fuzzyData == null) return;
                        final List<List<String>> relevance_list = fuzzyData.getResult();
                        if (relevance_list.size() > 0) {
                            relevance_recycler.addItemDecoration( new VipItemDecoration( space, relevance_list.size() ) );
                            relevance_recycler.setVisibility( View.VISIBLE );
                            RelevanceAdapter adapter = new RelevanceAdapter( getApplicationContext(), relevance_list );
                            relevance_recycler.setAdapter( adapter );
                            adapter.setonclicklistener( new OnItemClick() {
                                @Override
                                public void OnItemClickListener(View view, int position) {
                                    keyword = relevance_list.get( position ).get( 0 );
                                    tv_search_name.setText( keyword );
                                    pageNum = 1;
                                    xrecycler.refresh();
                                }
                            } );
                        } else {
                            relevance_recycler.setVisibility( View.GONE );
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    private void initSwitch() {
        switch_one.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_coupon = "true";
                    pageNum = 1;
                    xrecycler.refresh();
                } else {
                    has_coupon = "false";
                    pageNum = 1;
                    xrecycler.refresh();
                }
            }
        } );
    }

    @OnClick({R.id.tv_paixu, R.id.tv_xiaolinag, R.id.tv_price, R.id.tv_yongjin, R.id.iv_back, R.id.re_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_paixu:/*综合*/
                tv_paixu.setTextColor( 0xfff6c15b );
                tv_xiaolinag.setTextColor( 0xff000000 );
                tv_price.setTextColor( 0xff000000 );
                tv_yongjin.setTextColor( 0xff000000 );
                type = "";
                pageNum = 1;
                xrecycler.refresh();
                break;
            case R.id.tv_xiaolinag:/*销量*/
                tv_paixu.setTextColor( 0xff000000 );
                tv_xiaolinag.setTextColor( 0xfff6c15b );
                tv_price.setTextColor( 0xff000000 );
                tv_yongjin.setTextColor( 0xff000000 );
                type = "sales";
                pageNum = 1;
                xrecycler.refresh();
                break;
            case R.id.tv_price:/*价格*/
                tv_paixu.setTextColor( 0xff000000 );
                tv_xiaolinag.setTextColor( 0xff000000 );
                tv_price.setTextColor( 0xfff6c15b );
                tv_yongjin.setTextColor( 0xff000000 );
                type = "price";
                pageNum = 1;
                xrecycler.refresh();
                break;
            case R.id.tv_yongjin:
                tv_paixu.setTextColor( 0xff000000 );
                tv_xiaolinag.setTextColor( 0xff000000 );
                tv_price.setTextColor( 0xff000000 );
                tv_yongjin.setTextColor( 0xfff6c15b );
                type = "commission";
                pageNum = 1;
                xrecycler.refresh();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.re_search:
                intent = new Intent( getApplicationContext(), TransitionSearchActivity.class );
                intent.putExtra( "keyword", keyword );
                startActivity( intent );
                overridePendingTransition( R.anim.ap2, R.anim.ap1 );
                finish();
                break;
        }
    }

    private void getAllNet() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "sort", type );
        map.put( "coupon", has_coupon );
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "q", keyword );
        map.put( "type", String.valueOf( search_type ) );
        final String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.GOODS_NEW_SEARCH + "?" + param )
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
                        Log.i( "搜索数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int aReturn = jsonObject.getInt( "status" );
                            if (aReturn >= 0) {
                                AllNetBean bean = GsonUtil.GsonToBean( response.toString(), AllNetBean.class );
                                if (bean == null) return;
                                List<AllNetBean.AllNetData> result = bean.getResult();
                                if (pageNum == 1 && result.size() == 0) {
                                    nodata.setVisibility( View.VISIBLE );
                                    nodata_name.setText( "没有搜到商品，试试其他关键字" );
                                    xrecycler.setVisibility( View.GONE );
                                    xrecycler.setNoMore( true );
                                    xrecycler.refreshComplete();
                                    xrecycler.loadMoreComplete();
                                } else {
                                    nodata.setVisibility( View.GONE );
                                    xrecycler.setVisibility( View.VISIBLE );
                                    for (AllNetBean.AllNetData listData : result) {
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
                                String result = jsonObject.getString( "result" );
                                if (pageNum == 1) {
                                    if (result.equals( "无结果" )) {
                                        nodata.setVisibility( View.VISIBLE );
                                        nodata_name.setText( "没有搜到商品，试试其他关键字" );
                                        xrecycler.setVisibility( View.GONE );
                                    } else {
                                        ToastUtils.showToast( getApplicationContext(), result );
                                    }
                                }
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

    private void initXrecycler() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        XRecyclerViewUtil.setView( xrecycler );
        adapter = new BaseSearchAdapter( getApplicationContext(), list );
        xrecycler.setAdapter( adapter );
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getAllNet();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getAllNet();
            }
        } );
        adapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 1;
                intent = new Intent( getApplicationContext(), ShopDetailActivity.class );
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
                intent.putExtra( Constant.SHOP_REFERER, "search" );/*商品来源*/
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
        to_top.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xrecycler.scrollToPosition( 0 );
            }
        } );
    }

    /*佣金和人气切换（初始布局）*/
    private void userLevelChange() {
        if (PreferUtils.getBoolean( getApplicationContext(), "isLogin" )) {
            String member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
            if (Constant.COMMON_USER_LEVEL.contains( member_role )) {
                /*普通用户*/
                tv_yongjin.setText( "人气" );
            } else {
                /*vip及以上*/
                tv_yongjin.setText( "佣金" );
            }
        } else {
            tv_yongjin.setText( "人气" );
        }
    }

}
