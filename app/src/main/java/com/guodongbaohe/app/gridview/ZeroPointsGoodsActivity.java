package com.guodongbaohe.app.gridview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

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

public class ZeroPointsGoodsActivity extends BaseActivity {

    private int pageNUm = 1;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.to_top)
    ImageView to_top;
    List<HomeListBean.ListData> list = new ArrayList<>();
    JhsAdapter adapter;
    String goods_type;
    private String goods_url;


    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                xrecycler.refresh();
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                xrecycler.refresh();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                xrecycler.refresh();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    @Override
    public int getContainerView() {
        return R.layout.commonactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        EventBus.getDefault().register( this );
        Intent intent = getIntent();
        goods_type = intent.getStringExtra( "goods_type" );
        if (goods_type.equals( "ldms" )) {
            setMiddleTitle( "0点秒杀" );
            goods_url = Constant.GOODS_ZERO;
        } else {
            setMiddleTitle( "高佣金商品" );
            goods_url = Constant.GAOYONGJINGOODS;
        }
        getData();
        setView();
    }

    RoundedImageView iv_head;

    private void setView() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        XRecyclerViewUtil.setView( xrecycler );
        adapter = new JhsAdapter( list, getApplicationContext() );
        xrecycler.setAdapter( adapter );
        View view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.base_head_img, null );
        xrecycler.addHeaderView( view );
        iv_head = (RoundedImageView) view.findViewById( R.id.iv_head );
        getFourHeadIVData();
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNUm = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                pageNUm++;
                getData();
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
        to_top.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xrecycler.scrollToPosition( 0 );
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

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "page", String.valueOf( pageNUm ) );
        map.put( "limit", "20" );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + goods_url + "?" + mapParam )
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
                        Log.i( "商品数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                HomeListBean nineBean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (nineBean == null) return;
                                List<HomeListBean.ListData> result = nineBean.getResult();
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
                                    if (pageNUm == 1) {
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
                                if (goods_type.equals( "ldms" )) {
                                    for (int i = 0; i < four_iv_list.size(); i++) {
                                        if (four_iv_list.get( i ).getUrl().equals( "ldms" )) {
                                            Glide.with( getApplicationContext() ).load( four_iv_list.get( i ).getImage() ).into( iv_head );
                                            return;
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < four_iv_list.size(); i++) {
                                        if (four_iv_list.get( i ).getUrl().equals( "gysp" )) {
                                            Glide.with( getApplicationContext() ).load( four_iv_list.get( i ).getImage() ).into( iv_head );
                                            return;
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

                    }
                } );
    }
}
